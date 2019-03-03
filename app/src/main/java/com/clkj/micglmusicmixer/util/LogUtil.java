package com.clkj.micglmusicmixer.util;


import com.clkj.micglmusicmixer.GlmusicApplication;

import android.util.Log;

/**
 * log打印工具类
 * @author json_data
 *
 */
public class LogUtil {
   
	private LogUtil()  
    {  
        /* cannot be instantiated */  
        throw new UnsupportedOperationException("cannot be instantiated");  
    }  
    private static boolean isDebug = GlmusicApplication.isDebug;// 是否需要打印bug，可以在application的onCreate函数里面初始化  
    private static final String TAG = "GlmusicApplication";  
  
    // 下面四个是默认tag的函数  
    public static void i(String msg)  
    {  
        if (isDebug)  
            Log.i(TAG, msg);  
    }  
  
    public static void d(String msg)  
    {  
        if (isDebug)  
            Log.d(TAG, msg);  
    }  
  
    public static void e(String msg)  
    {  
        if (isDebug)  
            Log.e(TAG, msg);  
    }  
  
    public static void v(String msg)  
    {  
        if (isDebug)  
            Log.v(TAG, msg);  
    }  
  
    // 下面是传入自定义tag的函数  
    public static void i(String tag, String msg)  
    {  
        if (isDebug)  
            Log.i(tag, msg);  
    }  
  
    public static void d(String tag, String msg)  
    {  
        if (isDebug)  
            Log.i(tag, msg);  
    }  
  
    public static void e(String tag, String msg)  
    {  
        if (isDebug)  
            Log.e(tag, msg);  
    }  
  
    public static void v(String tag, String msg)  
    {  
        if (isDebug)  
            Log.i(tag, msg);  
    }  
	
}
