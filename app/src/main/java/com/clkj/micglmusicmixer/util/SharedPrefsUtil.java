package com.clkj.micglmusicmixer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences工具�? * @author json_data
 *
 */
public class SharedPrefsUtil {
	public final static String SETTING = "MicGlmusicMixer";  
	

	/**
	 *  麦克风音量大小
	 */
	public final static String MIC_PROGRESS="mic_progress";
	
	/**
	 *  混响音量大小
	 */
	public final static String HUNXIANG_PROGRESS="hunxiang_progress";
	/**
	 *  高音音量大小
	 */
	public final static String GAOYIN_PROGRESS="gaoyin_progress";
	/**
	 *  低音音量大小
	 */
	public final static String DIYIN_PROGRESS="diyin_progress";
    public static final String YANCHI_PROGRESS ="yanchi_progress";
    public static final String MODE = "which_mode";

    public static void putValue(Context context,String key, int value) {  
         Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();  
         sp.putInt(key, value);  
         sp.commit();  
    }  
    
    
    public static void putValue(Context context,String key, boolean value) {  
         Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();  
         sp.putBoolean(key, value);  
         sp.commit();  
    }  
    public static void putValue(Context context,String key, String value) {  
         Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();  
         sp.putString(key, value);  
         sp.commit();  
    }  
    public static int getValue(Context context,String key, int defValue) {  
        SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);  
        int value = sp.getInt(key, defValue);  
        return value;  
    }  
    public static boolean getValue(Context context,String key, boolean defValue) {  
        SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);  
        boolean value = sp.getBoolean(key, defValue);  
        return value;  
    }  
    public static String getValue(Context context,String key, String defValue) {  
        SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);  
        String value = sp.getString(key, defValue);  
        return value;  
    }  
}
