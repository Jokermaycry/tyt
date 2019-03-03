package com.clkj.micglmusicmixer.update;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.clkj.micglmusicmixer.GlmusicApplication;

import com.clkj.micglmusicmixer.activity.UpdateActivity;
import com.clkj.micglmusicmixer.util.LogUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.StrictMode;

import android.util.Log;

/*
 * 检测网络状态广播
 */
public class CheckNetReceiver extends BroadcastReceiver {

	private Context mContext;

	private static String TAG = "CheckNetReceiver";

	/**
	 * 检测网络广播连上已经执行了
	 */
	private static boolean receiveHasBeenCome = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

			this.mContext = context;
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkinfo = cm.getActiveNetworkInfo();
			if (networkinfo != null && networkinfo.isConnected()) {// 如果联网状态

				if (receiveHasBeenCome == false) {
					receiveHasBeenCome = true;
					checkNetHandler.sendEmptyMessage(CONNECT);

				} else {
					LogUtil.d(TAG, "检测升级已经执行");
				}

			} else {// 如果断网状态
				receiveHasBeenCome = false;
//				checkNetHandler.sendEmptyMessage(BREAK);
			}
			Log.d("检测网络状态广播", "广播开启");
			// return;
		} else {
			return;
		}

	}

	// added 10.28
	SharedPreferences locNumSharedPreferences;
	SharedPreferences.Editor editor;

	private final int CONNECT = 1;
	private final int BREAK = 2;
	private Handler checkNetHandler = new Handler() {

		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:
				Log.d(TAG, "检测到网络打开");

				if (mContext != null) {
					updateThread = new Thread(updateRunnable);

					if (updateThread.isAlive()) {
						Log.d(TAG, "更新线程已存在");
					} else {
						updateThread.start();
					}
					

				} else {
					Log.d("MainService.mContext", "这个对象是空");
				}

				break;
			case 2:
				if(mContext!=null){
					LogUtil.i("弹出重启系统框");
//					reboot_dialog(MainService.mContext);
				}else{
					Log.d("MainService.mContext", "这个对象是空");
				}
				
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	Thread updateThread;

	Runnable updateRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				update();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/********************************************************************/
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	String httpVersionCode;// 版本信息
	// String url_res_updateInfo;//更新信息
	URL download_url;// apk升级路径

	/**
	 * �?��软件是否有更新版�? *
	 * 
	 * @return
	 */
	private boolean xml_parse() {

		// 把version.xml放到网络上，然后获取文件信息
		InputStream inStream = ParseXmlService.class.getClassLoader()
				.getResourceAsStream("version.xml");
		// 解析XML文件�?由于XML文件比较小，因此使用DOM方式进行解析
		ParseXmlService service = new ParseXmlService();
		try {
			mHashMap = service.parseXml(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void update() throws IOException {

		xml_parse();
		// String ver = getAppVersionName(ReceiveDataService.updateContext);

		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(mContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String versionName = pi.versionName;// 本地app的版本号
		Log.i(TAG, "本地APP版本号：" + versionName);

		try {

			String checkUrl = mHashMap.get("check_url");
			httpVersionCode = sendGet(checkUrl, null);// 请求服务器上的版本号

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			download_url = new URL(mHashMap.get("download_url"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (httpVersionCode == "")
			return;
		else {
			int len = httpVersionCode.indexOf(".");
			httpVersionCode = httpVersionCode.substring(len - 1,
					httpVersionCode.length());
			Log.d(TAG, "httpVersionCode==" + httpVersionCode);
		}
		// string转double
		double ver_dou = Double.parseDouble(versionName);
		double http_dou = Double.parseDouble(httpVersionCode);
		Log.d(TAG, "ver_dou==" + ver_dou + "    http_dou==" + http_dou);
		// 当网络版本高于本地版本，就要进行更新
		if (http_dou > ver_dou) {
			// handler.sendEmptyMessage(1);

			Log.e(TAG, "app需要更新");

			downloadApk();// 下载文件
			

		}
	}
	
	/**
	 * 向指定URL发�?GET方法的请�? *
	 * 
	 * @param url
	 *            发�?请求的URL
	 * @param params
	 *            请求参数，请求参数应该是name1=value1&name2=value2的形式�?
	 * @return URL�?��表远程资源的响应
	 */
	public static String sendGet(String url, String params) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlName = url + "?" + params;
			URL realUrl = new URL(urlName);
			// 打开和URL之间的连�?
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属�?
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// 建立实际的连�?
			conn.connect();
			// 获取�?��响应头字�?
			Map<String, List<String>> map = conn.getHeaderFields();
			// 遍历�?��的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义BufferedReader输入流来读取URL的响�?
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += "\n" + line;
			}
		} catch (Exception e) {
			System.out.println("发�?GET请求出现异常�?" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入�?
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软�?
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 * @date 2012-4-26
	 * @blog http://blog.92coding.com
	 */
	/* 记录进度条数�? */
	private int progress;

	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";

					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) download_url
							.openConnection();
					System.out.println("apk下载地址是：" + download_url);
					conn.connect();
					// 获取文件大小
					int filelength = conn.getContentLength();
					// 创建输入�?
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					int numread = 0;
					do {
						numread = is.read(buf);
						count += numread;
						// 计算进度条位�?
						progress = (int) (((float) count / filelength) * 100);
						// // 更新进度
						// mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// 下载完成
							// mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							LogUtil.d(TAG, "filelength=="+filelength);
							installApk(filelength);
							Log.d(TAG, "最新APP下载完成，准备安装");

							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (numread > 0);// 点击取消就停止下�?
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk(int filelength) {
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		String url1 = apkfile.toString();
		if (!apkfile.exists()) {
			return;
		}
		File mFileName = new File(url1);
//		boolean installFlag = slientInstall(mFileName);

//		if (installFlag == true) {
//			LogUtil.i(TAG, "APP自升级安装成功");

//			checkNetHandler.sendEmptyMessage(2);
			Intent mBootIntent = new Intent(mContext,UpdateActivity.class);
			 mBootIntent.putExtra("updateUrl", url1);
			 mBootIntent.putExtra("updateFlag", "1");
			 Log.d(TAG, "updateFlag="+1);
			 mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		     mBootIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		     mContext.startActivity(mBootIntent);
			

//		} else {
//			LogUtil.i(TAG, "APP自升级安装失败");
//		}

	}

	/**
	 * 静默安装
	 * 
	 * @param file
	 * @return
	 */
	public static boolean slientInstall(File file) {
		boolean result = false;
		Process process = null;
		OutputStream out = null;
		try {
			process = Runtime.getRuntime().exec("su");
			out = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(out);
			dataOutputStream.writeBytes("chmod 777 " + file.getPath() + "\n");
			dataOutputStream
					.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "
							+ file.getPath());
			// 提交命令
			dataOutputStream.flush();
			// 关闭流操作
			dataOutputStream.close();
			out.close();
			int value = process.waitFor();

			// 代表成功
			if (value == 0) {
				result = true;
				System.out.println("安装结果" + result);
			} else if (value == 1) { // 失败
				result = false;
				System.out.println("安装结果" + result);
			} else { // 未知情况
				result = false;
				System.out.println("安装结果" + result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return result;
	}

	

}
