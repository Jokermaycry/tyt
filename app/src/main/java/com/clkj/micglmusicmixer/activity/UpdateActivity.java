package com.clkj.micglmusicmixer.activity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.clkj.micglmusicmixer.R;
import com.clkj.micglmusicmixer.util.LogUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.StatFs;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends Activity {

	
	private static String TAG="UpdateActivity";
	
	String updateUrl;
	// String updateInfo;
	String updateFlag;
	
	long fileLength;

	Context mContext;

	private static File mFileName;

	private ProgressBar mypro;
	private Button wanchengBtn;
	private TextView mTextView;

	private int progress = 0;

	ProgressDialog waitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);
		mContext = this;
		Intent intent = this.getIntent();
		updateUrl = intent.getStringExtra("updateUrl");
		mFileName = new File(updateUrl);
		updateFlag = intent.getStringExtra("updateFlag");
		
		
		// updateInfo=intent.getStringExtra("url_res_updateInfo");
		System.out.println("安装地址为：" + updateUrl);

		showNormalDia(updateUrl, updateFlag);

	}

	boolean installFlag=false;//安装失败 
	
	String installFlagStr="failed";//安装结果
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:

				/*waitDialog = ProgressDialog.show(UpdateActivity.this, null,
						mContext.getResources().getString(R.string.updateing), true);
				waitDialog.setCancelable(false);
				setDialogFontSize(waitDialog, 30);
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

							
//							installFlag = slientInstall(mFileName);
							installFlagStr=jingmoInstall(updateUrl);
							LogUtil.i(TAG, "安装结果："+installFlagStr);//成功，Success；失败，Failure [INSTALL_FAILED_INSUFFICIENT_STORAGE]

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (waitDialog != null) {
							waitDialog.dismiss();
							waitDialog = null;
						}
						

						if(installFlagStr.contains("Success")){
							
							PowerManager pManager = (PowerManager) getSystemService(UpdateActivity.POWER_SERVICE);
							pManager.reboot("重启");
							
							LogUtil.e("准备重启系统");
							UpdateActivity.this.finish();
						}else{
							mHandler.sendEmptyMessage(2);
						}
						
						
					}

				}).start();*/
				Log.d(TAG, updateUrl);
				installApk(mContext,updateUrl);
				
				break;

			case 1:
				// mHandler.sendEmptyMessage(0);
				// mypro.setProgress(progress);
				// mTextView.setText(progress+"%");
				Toast.makeText(mContext, mContext.getResources().getString(R.string.updateing_reboot), 3000).show();
				break;

			case 2:
				Toast.makeText(mContext, mContext.getResources().getString(R.string.update_failed), Toast.LENGTH_LONG).show();
				UpdateActivity.this.finish();
				break;

			default:
				break;

			}

		};
	};

	
	
	private String jingmoInstall(String apkAbsolutePath){
		String[] args = { "pm", "install", "-r", apkAbsolutePath };  
		String result = "";  
		ProcessBuilder processBuilder = new ProcessBuilder(args);  
		Process process = null;  
		InputStream errIs = null;  
		InputStream inIs = null;  
		try {  
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		    int read = -1;  
		    process = processBuilder.start();  
		    errIs = process.getErrorStream();  
		    while ((read = errIs.read()) != -1) {  
		        baos.write(read);  
		    }  
		    baos.write('\n');  
		    inIs = process.getInputStream();  
		    while ((read = inIs.read()) != -1) {  
		        baos.write(read);  
		    }  
		    byte[] data = baos.toByteArray();  
		    result = new String(data);  
		} catch (IOException e) {  
		    e.printStackTrace();  
		} catch (Exception e) {  
		    e.printStackTrace();  
		} finally {  
		    try {  
		        if (errIs != null) {  
		            errIs.close();  
		        }  
		        if (inIs != null) {  
		            inIs.close();  
		        }  
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    }  
		    if (process != null) {  
		        process.destroy();  
		    }  
		}  
		return result; 
	}

	
	
	 public long getAvailableInternalMemorySize(){  
	        File path = Environment.getDataDirectory();    
	        StatFs stat = new StatFs(path.getPath());  
	        long blockSize = stat.getBlockSize();  
	        long availableBlocks = stat.getAvailableBlocks();  
	        return availableBlocks*blockSize;  
	    }  
	
	/* 普通的对话框 */
	private void showNormalDia(final String url, String flag) {

		if (flag.equals("1")) {
			AlertDialog.Builder builder = new Builder(mContext);
			builder.setMessage(mContext.getResources().getString(R.string.update_check));

			builder.setTitle(mContext.getResources().getString(R.string.update_title));

			builder.setPositiveButton(mContext.getResources().getString(R.string.update_now), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					mHandler.sendEmptyMessage(0);

					dialog.dismiss();
					UpdateActivity.this.finish();

				}
			});

			builder.setNegativeButton(mContext.getResources().getString(R.string.update_later), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();
					UpdateActivity.this.finish();
				}
			});

			builder.create().show();
		}

	}

	private void updateProgress() {
		new updateProgressThread().start();

	}

	private class updateProgressThread extends Thread {
		@Override
		public void run() {

			for (int i = 1; i < 101; i++) {
				try {
					Thread.sleep(100);
					// mypro.incrementProgressBy(1);
					progress = i;
					mHandler.sendEmptyMessage(1);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			mHandler.sendEmptyMessage(2);

		}
	}
	
	/**
	 * 安装apk常规方法
	 * @param mContext
	 * @param fileName
	 */
	private void installApk(Context mContext,String fileName){
		Intent intent=new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://"+fileName), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
	
	private void setDialogFontSize(Dialog dialog, int size) {
		Window window = dialog.getWindow();
		View view = window.getDecorView();
		setViewFontSize(view, size);
	}

	private void setViewFontSize(View view, int size) {
		if (view instanceof ViewGroup) {
			ViewGroup parent = (ViewGroup) view;
			int count = parent.getChildCount();
			for (int i = 0; i < count; i++) {
				setViewFontSize(parent.getChildAt(i), size);
			}
		} else if (view instanceof TextView) {
			TextView textview = (TextView) view;
			textview.setTextSize(size);
		}
	}

	
	

}
