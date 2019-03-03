package com.clkj.micglmusicmixer;


import com.clkj.micglmusicmixer.VolumeService;
import com.clkj.micglmusicmixer.hid.UsbHid;
import com.clkj.micglmusicmixer.util.LogUtil;
import com.clkj.micglmusicmixer.util.SharedPrefsUtil;
import com.clkj.micglmusicmixer.util.SomeValues;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static String TAG="MainActivity";
	private Context mContext;
	
	UsbHid myUsbHid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		
		mContext=this;
		
		myUsbHid = UsbHid.getInstance(mContext);
		// 去打开设备，如果打开设备成功走下面
		if (myUsbHid.OpenDevice(GlmusicApplication.VendorID,
				GlmusicApplication.ProductID)) {
			
			
			final int mic_volumeProgress = SharedPrefsUtil.getValue(mContext,
					SharedPrefsUtil.MIC_PROGRESS,
					GlmusicApplication.mic_def_volumeProgress);

			final int hunxiang_progress = SharedPrefsUtil.getValue(mContext,
					SharedPrefsUtil.HUNXIANG_PROGRESS,
					GlmusicApplication.hunxiang_def_progress);
			
			
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					LogUtil.d(TAG, "准备初始化上次保存的各个音量");
					

						byte[] sendMic = { (byte) 0x80, (byte) 0xA1,
								(byte) mic_volumeProgress, (byte) 0x00 };
						LogUtil.d(TAG, "I Send: 0x80, 0xA1," + mic_volumeProgress
								+ ", 0x00");

						byte[] recvMic = myUsbHid.UsbHidSendCommand(sendMic,
								sendMic.length);

						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						byte[] sendHunxiang = { (byte) 0x80, (byte) 0xA2,
								(byte) hunxiang_progress, (byte) 0x00 };
						LogUtil.d(TAG, "I Send: 0x80, 0xA2," + hunxiang_progress
								+ ", 0x00");

						byte[] recvHunxiang = myUsbHid.UsbHidSendCommand(
								sendHunxiang, sendHunxiang.length);

					

				}

			}).start();
			
			

			if (SomeValues.volumeWeatherOnView.equals("0")) {

				
				if (mContext != null) {
					LogUtil.i(TAG,
							"Going to open DialogVolume");
					
					Intent mVoIntent = new Intent(mContext,
							VolumeService.class);

					mVoIntent
							.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startService(mVoIntent);

					LogUtil.i(
							TAG,
							"Go to start volume dialog，weatherOnView="
									+ SomeValues.volumeWeatherOnView);
				} else {
					LogUtil.e(TAG, "mContext==null");
				}
			} else {
				LogUtil.e(TAG, "Dialog has been on，weatherOnView="
						+ SomeValues.volumeWeatherOnView);
			}
			
			
		}else{
			
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.no_usbdevice), 2000).show();// no_usbdevice
			
		}
		
		
		
		
		this.finish();
		
		
		
	}
}
