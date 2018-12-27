package com.shyl.util;

import java.math.BigDecimal;
import java.util.Random;

public class NumberUtil {
	/**
	 * 补零算法
	 * @param str
	 * @param strLength
	 * @return
	 */
	public static String addZeroForNum(String str, int strLength) {
	     int strLen = str.length();
	     StringBuffer sb = null;
	     while (strLen < strLength) {
	           sb = new StringBuffer();
	           sb.append("0").append(str);// 左(前)补0
	        // sb.append(str).append("0");//右(后)补0
	           str = sb.toString();
	           strLen = str.length();
	     }
	     return str;
	 }
	
	public static String getRandomNum(int strLength) {  
	      
	    Random rm = new Random();  
	    double pross = rm.nextDouble();
	    String result = pross+"";
	    String [] r = result.split("\\.");
	    if(r.length > 1){
	    	result = r[1];
	    }else{
	    	result = r[0];
	    }
	    if(result.length() >= strLength){
	    	result = result.substring(0, strLength);
	    }else{
		    result = addZeroForNum(result, strLength);
	    }
	    return result;  
	}
	
	/**
	 * 保留2位小数
	 * @param d
	 * @return
	 */
	public static Double scale2(Double d){
		BigDecimal bg = new BigDecimal(d);
		return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	}
	
	public static void main(String[] args) {
		System.out.println("=="+getRandomNum(10));
	}
}
