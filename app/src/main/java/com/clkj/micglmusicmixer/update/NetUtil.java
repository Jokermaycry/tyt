package com.clkj.micglmusicmixer.update;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.net.ConnectivityManagerCompat;
import android.util.Log;

@SuppressLint("NewApi")
public class NetUtil {

	public NetUtil(Context context) {
		super();
	}

	/*
	 * ��鵱ǰWIFI�Ƿ����ӣ�������˼�����Ƿ����ӣ������ǲ���WIFI
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
	 * ��鵱ǰGPRS�Ƿ����ӣ�������˼�����Ƿ����ӣ������ǲ���GPRS
	 * 
	 * @param context
	 * @return true��ʾ��ǰ���紦������״̬������GPRS�����򷵻�false
	 */

	public static boolean isGprsConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()

		&& ConnectivityManager.TYPE_MOBILE == info.getType()) {
			return true;
		}
		return false;
	}

	/**

	 * 
	 * @param context
	 */

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}

		return false;

	}

	/**
	 * �Դ���ݴ���ʱ����Ҫ���ø÷��������жϣ�����������У�Ӧ����ʾ�û�
	 * 
	 * @param context
	 * @return true��ʾ�������У�false��ʾ������
	 */

	public static boolean isActiveNetworkMetered(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context

		.getSystemService(Context.CONNECTIVITY_SERVICE);

		return ConnectivityManagerCompat.isActiveNetworkMetered(cm);

	}

	public static Intent registerReceiver(Context context,

	ConnectivityChangeReceiver receiver) {

		return context.registerReceiver(receiver,
				ConnectivityChangeReceiver.FILTER);

	}

	public static void unregisterReceiver(Context context,

	ConnectivityChangeReceiver receiver) {

		context.unregisterReceiver(receiver);

	}

	public static abstract class ConnectivityChangeReceiver extends

	BroadcastReceiver {

		public static final IntentFilter FILTER = new IntentFilter(

		ConnectivityManager.CONNECTIVITY_ACTION);

		@Override
		public final void onReceive(Context context, Intent intent) {

			ConnectivityManager cm = (ConnectivityManager) context

			.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo wifiInfo = cm

			.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			NetworkInfo gprsInfo = cm

			.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			// �ж��Ƿ���Connected�¼�

			boolean wifiConnected = false;

			boolean gprsConnected = false;

			if (wifiInfo != null && wifiInfo.isConnected()) {

				wifiConnected = true;

			}

			if (gprsInfo != null && gprsInfo.isConnected()) {
				gprsConnected = true;

			}

			if (wifiConnected || gprsConnected) {

				onConnected();

				return;

			}

			// �ж��Ƿ���Disconnected�¼���ע�⣺�����м�״̬���¼����ϱ���Ӧ�ã��ϱ���Ӱ������

			boolean wifiDisconnected = false;

			boolean gprsDisconnected = false;

			if (wifiInfo == null || wifiInfo != null

			&& wifiInfo.getState() == State.DISCONNECTED) {

				wifiDisconnected = true;

			}

			if (gprsInfo == null || gprsInfo != null

			&& gprsInfo.getState() == State.DISCONNECTED) {

				gprsDisconnected = true;

			}

			if (wifiDisconnected && gprsDisconnected) {

				onDisconnected();

				return;

			}

		}

		protected abstract void onDisconnected();

		protected abstract void onConnected();
	}
	
	
	/*******************************��ȡIP��mac��ʲô��**************************************************************/
	
	/*
	 * ��ȡIP
	 */
	  private String intToIp(int i) {
          return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                          + "." + ((i >> 24) & 0xFF);
  }
  
  protected String execRootCmd(String paramString) {
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

  /**
   * Get IP address from first non-localhost interface
   * 
   * @param ipv4
   *            true=return ipv4, false=return ipv6
   * @return address or empty string
   */
  public String getIPAddressWifi(boolean useIPv4,Context context) {
          WifiManager wifimanage = (WifiManager)context.getSystemService(context.WIFI_SERVICE);// ��ȡWifiManager
          // ���wifi�Ƿ���

          if (wifimanage.isWifiEnabled()) {

                  WifiInfo wifiinfo = wifimanage.getConnectionInfo();

                  String wifiip = intToIp(wifiinfo.getIpAddress());
                  Log.i("MyTag", "-------wifiip----" + wifiip);
                  return wifiip;
          }
//          else {
//                  String comstr = "ifconfig eth0";
//                  String ip = execRootCmd(comstr);
//                  Log.i("MyTag", "---process ifconfig eth0-----" + ip);
//                  final String myip = ip.substring(ip.indexOf("ip") + 2,
//                                  ip.indexOf("mask")).trim();
//                  return myip;
//                  
//          }
		return null;

  }
  
  /*
   * �����߻�ȡIP
   */
  public String getIPAddressLine(boolean useIPv4,Context context) {
	  String comstr = "ifconfig eth0";
      String ip = execRootCmd(comstr);
      Log.i("MyTag", "---process ifconfig eth0-----" + ip);
      final String myip = ip.substring(ip.indexOf("ip") + 2,
                      ip.indexOf("mask")).trim();
      return myip;
  }
	 /* 
*****************************************************************
*                       �Ӻ����ñ������MAC��ַ���������ù�,�������߻��ȡ��wifimac
*****************************************************************                        
*/    
public static String getWangKouMacAddress(){   
  String result = "";     
  String Mac = "";
  result = callCmd("busybox ifconfig","HWaddr");
  
  //���ص�result == null����˵�����粻��ȡ
  if(result==null){
  	return "������?��������";
  }

  if(result.length()>0 && result.contains("HWaddr")==true){
  	Mac = result.substring(result.indexOf("HWaddr")+6, result.length()-1);
  	Log.i("test","Mac:"+Mac+" Mac.length: "+Mac.length());

  	if(Mac.length()>1){
  		Mac = Mac.replaceAll(" ", "");
  		result = "";
  		String[] tmp = Mac.split(":");
  		for(int i = 0;i<tmp.length;++i){
  			result +=tmp[i];
  		}
  	}   	
//	return result.toUpperCase();
  }
  return result;
}   


public static String callCmd(String cmd,String filter) {   
  String result = "";   
  String line = "";   
  try {
  	Process proc = Runtime.getRuntime().exec(cmd);
      InputStreamReader is = new InputStreamReader(proc.getInputStream());   
      BufferedReader br = new BufferedReader (is);   
      
      //ִ������cmd��ֻȡ����к���filter����һ��
      while ((line = br.readLine ()) != null && line.contains(filter)== false) {   
      	//result += line;
      	Log.i("test","line: "+line);
      }
      
      result = line;
      Log.i("test","result: "+result);
  }   
  catch(Exception e) {   
      e.printStackTrace();   
  }   
  return result;   
}
	////////////////////////////////////////////////////////////////

//���IP��ȡ����Mac
public static String getLocalMacAddressFromIp(Context context) {
   String mac_s= "";
  try {
       byte[] mac;
       NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
       mac = ne.getHardwareAddress();
       mac_s = byte2hex(mac);
  } catch (Exception e) {
      e.printStackTrace();
  }
  
   return mac_s;
}


@SuppressLint("NewApi")
public static  String byte2hex(byte[] b) {
    StringBuffer hs = new StringBuffer(b.length);
    String stmp = "";
    int len = b.length;
    for (int n = 0; n < len; n++) {
     stmp = Integer.toHexString(b[n] & 0xFF);
     if (stmp.length() == 1)
      hs = hs.append("0").append(stmp);
     else {
      hs = hs.append(stmp);
     }
    }
    return String.valueOf(hs);
   }

//��ȡ����IP
public static String getLocalIpAddress() {  
      try {  
          for (Enumeration<NetworkInterface> en = NetworkInterface  
                          .getNetworkInterfaces(); en.hasMoreElements();) {  
                      NetworkInterface intf = en.nextElement();  
                     for (Enumeration<InetAddress> enumIpAddr = intf  
                              .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                          InetAddress inetAddress = enumIpAddr.nextElement();  
                          if (!inetAddress.isLoopbackAddress()) {  
                          return inetAddress.getHostAddress().toString();  
                          }  
                     }  
                  }  
              } catch (SocketException ex) {  
                  Log.e("WifiPreference IpAddress", ex.toString());  
              }  
      
           return null;  
}  

/**
 * 
 * ��ȡ���mac��ַ��������mac��ַ�̶�,�Ǿ�һֱ��ȡ�̶�mac
 */
public static String getLocalfinalMacAddress() {  
	String mac = null;
	String result="";
    try{  
        String path="sys/class/net/eth0/address";  
        FileInputStream fis_name = new FileInputStream(path);  
        byte[] buffer_name = new byte[1024*8];  
        int byteCount_name = fis_name.read(buffer_name);  
        if(byteCount_name>0)  
        {  
            mac = new String(buffer_name, 0, byteCount_name, "utf-8");  
        }  
          
        if(mac.length()==0||mac==null){  
            path="sys/class/net/eth0/wlan0";  
            FileInputStream fis = new FileInputStream(path);  
            byte[] buffer = new byte[1024*8];  
            int byteCount = fis.read(buffer);  
            if(byteCount>0)  
            {  
                mac = new String(buffer, 0, byteCount, "utf-8");  
            }  
        }  
          
        if(mac.length()==0||mac==null){  
            return "";  
        }  
    }catch(Exception io){  
          
    }  
    String macStr=mac.trim();  
    if(macStr.length()>1){
    	macStr = macStr.replaceAll(" ", "");
  		result = "";
  		String[] tmp = macStr.split(":");
  		for(int i = 0;i<tmp.length;++i){
  			result +=tmp[i];
  		}
  	}   	
	return result.toUpperCase();
   
}  


}