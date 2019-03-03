package com.clkj.micglmusicmixer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.clkj.micglmusicmixer.update.ParseXmlService;
import com.clkj.micglmusicmixer.util.LogUtil;
import com.clkj.micglmusicmixer.util.ParseXmlUtil;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

/**
 * 广陵散调音台2.0版本
 * 
 * @author json_data
 * 
 */
public class GlmusicApplication extends Application {
	private static Context mContext = null;

	/**
	 * 是否要打印日誌
	 */
	public static boolean isDebug = true;
	/**
	 * 打开Mixer_server服务的名称
	 */
	public static final String mPropMixerServerName = "mixer_service";
	/**
	 * Localhost 的Unix域名
	 */
	public static final String LOCAL_SOCKET_NAME = "mixer_socket";
	/**
	 * 下载并存放音效数据文件所在的临时目录
	 */
//	public static final String soundEffectDataTempDir = "/data/soundBar";
	public static final String soundEffectDataTempDir = "/system/etc";
// huangxing open  U01。
	public static final int VendorID = 3190; // 0x0c76 //3468
	public static final int ProductID = 8215; // 0x2015 //308

// huangxing mask	
//	public static final int VendorID = 3190; // test
//	public static final int ProductID = 308; // 
//	
	
	
	private static final String TAG = "GlmusicApplication";

	public AudioManager mAudioManager = null;

	public static final int displayTime=5000;

	public static int mic_def_volumeProgress = 5;
	public static int yanchi_def_progress = 5;
	public static int hunxiang_def_progress =5;
	public static int gaoyin_def_progress = 5;
	public static int diyin_def_progress = 5;


	public static int whichmode = 8;

	public static int diyin_progress_max = 10;
	public static int sys_progress_max = 15;
	public static int gaoyin_progress_max = 10;
	public static int hunxiang_progress_max = 10;
	public static int mic_volumeProgress_max = 10;
	public static int yanchi_progress_max = 10;
	/**
	 * 调用调音台显示的键值
	 */
	public static String displaySoundConsoleKey = "44";
	/**
	 * 隐藏调音台的键值
	 */
	public static String hideSoundConsoleKey = "9e";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.mContext = this;
		LogUtil.d("决胜21点");

	}

	public static Context getContext() {
		return mContext;
	}

	

}
