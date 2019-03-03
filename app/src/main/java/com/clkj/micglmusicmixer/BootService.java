package com.clkj.micglmusicmixer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BootService extends Service {

	
	private static String TAG="BootService";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(TAG, "This is bootservice,onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "This is bootservice,onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

}
