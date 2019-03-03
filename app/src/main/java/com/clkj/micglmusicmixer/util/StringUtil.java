package com.clkj.micglmusicmixer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析字符串工具类
 * @author json_data
 *
 */
public class StringUtil {

	
	/**
	 * 拆分字符为","和"=" ,然后把结果交给Map
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
		 * 拆分字符为"," ,然后把结果交给String []
		 * @param str
		 * @return
		 */
		 public static String [] convertStrToArray(String str){  

		        String[] strArray = null;  
		        strArray = str.split(","); 
		       return strArray;
		    }   
}
