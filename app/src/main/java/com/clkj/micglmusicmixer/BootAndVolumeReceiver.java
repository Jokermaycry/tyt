package com.clkj.micglmusicmixer;

import java.io.IOException;

import com.clkj.micglmusicmixer.hid.UsbHid;
import com.clkj.micglmusicmixer.util.LogUtil;
import com.clkj.micglmusicmixer.util.SharedPrefsUtil;
import com.clkj.micglmusicmixer.util.SomeValues;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BootAndVolumeReceiver extends BroadcastReceiver {

	private Context mContext;
	private static String TAG = "BootAndVolumeReceiver";
	/**
	 * 开机广播
	 */
	private static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

	/**
	 * 监听音量改变广播
	 */
	private static final String VOLUME_UPDATE_ACTION = "android.media.VOLUME_CHANGED_ACTION";

	/**
	 * 请求usb授权广播
	 */
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

	/**
	 * 打开调音台的广播
	 */
	private static final String OPEN_GLMUSICMIXER_ACTION="com.clkj.action.OPEN_GLMUSICMIXER_ACTION";

	
	UsbHid myUsbHid;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext = context;
		String action = intent.getAction();
		
		if (ACTION_USB_PERMISSION.equals(action)) {
			synchronized (this) {
				UsbDevice device = (UsbDevice) intent
						.getParcelableExtra(UsbManager.EXTRA_DEVICE);

				if (intent.getBooleanExtra(
						UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
					if (device != null) {
						// call method to set up device communication
						Log.d(TAG, "permission  for device " + device);
						

						myUsbHid = UsbHid.getInstance(mContext);
						if (SomeValues.volumeWeatherOnView.equals("0")) {

							LogUtil.i(TAG,
									"Receive volumeDialog show cmd，going to start volume dialog");
							if (myUsbHid.OpenDevice(GlmusicApplication.VendorID,
									GlmusicApplication.ProductID)) {// 如果打开设备失败

								if (SomeValues.volumeWeatherOnView.equals("0")) {

									if (mContext == null) {
										return;
									}

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

							} else {
								Toast.makeText(
										mContext,
										mContext.getResources().getString(
												R.string.no_usbdevice), 2000).show();// no_usbdevice
							}
						} else {
							LogUtil.i(TAG, "但调音台对话框存在");
							
							
							
						}
						
						
						
						
					}
				} else {
					Log.d(TAG, "permission denied for device " + device);
				}
			}
		}
		
		
		
		/*if (action.equals(BOOT_ACTION)) {// 开机广播

			Intent mBootIntent = new Intent(mContext, BootService.class);
			mBootIntent.putExtra("volume_flag", "boot");
			mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(mBootIntent);
			LogUtil.i(TAG, "接收到开机广播，准备启动MicMixerUI服务");

		}*/
		
		/*if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

			myUsbHid = UsbHid.getInstance(mContext);
			if (myUsbHid.OpenDevice(GlmusicApplication.VendorID,
					GlmusicApplication.ProductID)) {// 如果打开设备成功
				
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
						if (myUsbHid.OpenDevice(GlmusicApplication.VendorID,
								GlmusicApplication.ProductID)) {

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

						} else {
							LogUtil.d(TAG, "打开USB设备失败");
						}

					}

				}).start();

				
			} else {
				Toast.makeText(
						mContext,
						mContext.getResources().getString(
								R.string.no_usbdevice), 2000).show();// no_usbdevice
		
				LogUtil.d(TAG, "打开USB设备失败");
				}
			
			
			LogUtil.i(TAG, "Usb device attached");
		} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

			LogUtil.i(TAG, "Usb device attached");
		}*/
		
		
		if (action.equals(VOLUME_UPDATE_ACTION)||action.equals(OPEN_GLMUSICMIXER_ACTION)) {
			
			LogUtil.i(TAG,
					"Volume changed  BroadcastReceiver Or OPEN_GLMUSICMIXER_ACTION");
			myUsbHid = UsbHid.getInstance(mContext);
			if (SomeValues.volumeWeatherOnView.equals("0")) {

				LogUtil.i(TAG,
						"Receive volumeDialog show cmd，going to start volume dialog");
				if (myUsbHid.OpenDevice(GlmusicApplication.VendorID,
						GlmusicApplication.ProductID)) {// 如果打开设备失败

					if (SomeValues.volumeWeatherOnView.equals("0")) {

						if (mContext == null) {
							return;
						}

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

				} else {
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.no_usbdevice), 2000).show();// no_usbdevice
				}
			} else {
				LogUtil.i(TAG, "但调音台对话框存在");
				
				
				
			}

		}

	}

	
}
