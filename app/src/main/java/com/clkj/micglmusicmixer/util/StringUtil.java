package com.clkj.micglmusicmixer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * �����ַ���������
 * @author json_data
 *
 */
public class StringUtil {

	
	/**
	 * ����ַ�Ϊ","��"=" ,Ȼ��ѽ������Map
	 * @param str
	 * @return
	 */
	 public static Map convertStrToMap(String str){  
	        String[] strArray = null;  
	        strArray = str.split(","); 
	        Map map=new HashMap();
	        for(int i=0;i<strArray.length;i++){
	        	String[] dataArray=strArray[i].split("=");
	        	map.put(dataArray[0].trim(), dataArray[1].trim());
	        	
	        }
	        return map;
	    }   
	 
	   /**
		 * ����ַ�Ϊ"," ,Ȼ��ѽ������String []
		 * @param str
		 * @return
		 */
		 public static String [] convertStrToArray(String str){  

		        String[] strArray = null;  
		        strArray = str.split(","); 
		       return strArray;
		    }   
}
