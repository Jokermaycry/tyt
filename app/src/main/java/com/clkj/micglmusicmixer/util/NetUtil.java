package com.clkj.micglmusicmixer.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 网络工具类
 * @author json_data
 *
 */
public class NetUtil {

	
	/**
	 * 检查当前是否连接
	 * 
	 * @param context
	 * @return true表示当前网络处于连接状态，否则返回false
	 */

	public static boolean isNetConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}

		return false;

	}

	
	/**
	 * 检查当前WIFI是否连接
	 */

	public static boolean isWifiConnected(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context

		.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = cm.getActiveNetworkInfo();

		if (info != null && info.isConnected()

		&& ConnectivityManager.TYPE_WIFI == info.getType()) {

			return true;

		}

		return false;

	}
	 /**
	   * 插网线获取IP
	   */
	  public String getIPAddressLine(Context context) {
		  String comstr = "ifconfig eth0";
	      String ip = execRootCmd(comstr);
	      Log.i("MyTag", "---process ifconfig eth0-----" + ip);
	      final String myip = ip.substring(ip.indexOf("ip") + 2,
	                      ip.indexOf("mask")).trim();
	      return myip;
	  }

	  
	  
	  
	  /**
	   * Get IP address from first non-localhost interface
	   * 
	   * @param mContext
	   * @return address or empty string
	   */
	  public static String getIPAddress(Context context) {
	          WifiManager wifimanage = (WifiManager)context.getSystemService(context.WIFI_SERVICE);// 获取WifiManager
	          // 检查wifi是否开启
	          String myIp="";
	          if (isWifiConnected(context)) {

	                  WifiInfo wifiinfo = wifimanage.getConnectionInfo();

	                  myIp = intToIp(wifiinfo.getIpAddress());
	                  

	          }else  {
	        	  
	        	  String comstr = "ifconfig wlan0";
	        	  String wlanIp = execRootCmd(comstr);
	        	  if(!wlanIp.contains("ip")){
	        		  String comstrEth = "ifconfig eth0";
	                  String ip = execRootCmd(comstrEth);
	                  LogUtil.i( "---process ifconfig eth0-----" + ip);
	                  myIp = ip.substring(ip.indexOf("ip") + 2,
	                                  ip.indexOf("mask")).trim();
	                 
	        	  }else{
	        		  myIp = wlanIp.substring(wlanIp.indexOf("ip") + 2,
	        				  wlanIp.indexOf("mask")).trim();
	        		 
	        	  }
	        	  
	          }
	          LogUtil.i("--current device ip----" + myIp); 
			return myIp;

	  }
	  
	  
	  
	  
	  
	  /*
		 * 获取IP
		 */
		  private static String intToIp(int i) {
	          return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
	                          + "." + ((i >> 24) & 0xFF);
	  }
	  
	  protected static String execRootCmd(String paramString) {
	          DataInputStream dis = null;
	          Runtime r = Runtime.getRuntime();
	          try {
	                  // r.exec("su"); // get root
	                  StringBuilder sb = new StringBuilder();
	                  Process p = r.exec(paramString);
	                  InputStream input = p.getInputStream();
	                  dis = new DataInputStream(input);
	                  String content = null;
	                  while ((content = dis.readLine()) != null) {
	                          sb.append(content).append("\n");
	                  }
	                  // r.exec("exit"); Log.i("UERY", "sb = " + sb.toString());
	                  // localVector.add(sb.toString());
	                  return sb.toString();
	          } catch (IOException e) {
	                  e.printStackTrace();
	          } finally {
	                  if (dis != null) {
	                          try {
	                                  dis.close();
	                          } catch (IOException e) {
	                                  e.printStackTrace();
	                          }
	                  }
	          }
	          return null;
	  }


	
}
