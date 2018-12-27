package com.shyl.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

public class NetWormTools{

	public static String cookieStr;


	/**
     * Post Request
     * @return
     * @throws Exception
     */
    public static String doPost(String path) throws Exception {

        String cookie = cookieStr;
        
        if (StringUtils.isEmpty(cookieStr)) {
        	cookie = "srcurl=" + toHexString(path) + "; path=/; ";
        }
        
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        
        conn.setRequestProperty("Cookie", cookie);
        
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(60000);
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Host", "app1.sfda.gov.cn");
        conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
        
        conn.connect(); 
    	//获取本次cookie信息
    	String newCookie = conn.getHeaderField("Set-Cookie");
    	if (!StringUtils.isEmpty(newCookie) && newCookie.indexOf("JSESSIONID")>=0) {
    		newCookie = newCookie.substring(0, newCookie.indexOf(";"));
			newCookie += "; ";
    	} else {
    		newCookie = "";
    	}
    	Map<String,List<String>> maps = conn.getHeaderFields();
    	Iterator<String> iterator = maps.keySet().iterator();
    	while(iterator.hasNext()) {
    		String key = iterator.next();
    		if ("Set-Cookie".equals(key)) {
        		List<String> list = maps.get(key);
        		for(String info : list) {
        			if (info.indexOf("yunsuo_session_verify")>=0) {
        				info = info.substring(info.indexOf("yunsuo_session_verify"), info.length());
        				info = info.substring(0, info.indexOf(";"));
        				newCookie += info+ "; ";
        			}
        		}
    		}
    	}
    	if (!StringUtils.isEmpty(newCookie)) {
        	cookieStr = newCookie + "srcurl=" + toHexString(path) + "; path=/; ";
    	}
        
        BufferedReader reader=null;
        try {
            
            if (conn.getResponseCode() >= 300) {
            	System.out.println("cookieStr is " + cookieStr);
            	System.out.println("HTTP Request is not success, Response code is " + conn.getResponseCode());
                throw new Exception("HTTP Request is not success, Response code is " + conn.getResponseCode());
            }
            /**
             * 解决部分页面数据返回乱码问题
             */
            reader=new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuffer buffer = new StringBuffer();
            String line=null;
            while((line=reader.readLine())!=null){
            	buffer.append(line);
            }
            return buffer.toString();
        } catch (Exception e) {
			throw e;
		} finally {
            if (reader != null) {
            	reader.close();
            }
            
        }
    }
    
    public static void initSession(){
    	cookieStr = null;
    }
    

    
    public static String stripHtml(String content) { 
    	content = content.replaceAll("<p .*?>", "\r\n"); 
    	content = content.replaceAll("<br\\s*/?>", "\r\n"); 
    	content = content.replaceAll("\\<.*?>", ""); 
    	// content = HTMLDecoder.decode(content); 
    	return content; 
    }
    
    
    public static String strip(String content) { 
    	return content.replaceAll("\\(.{0,}\\)", "").replaceAll("\\(.{0,}\\)", ""); 
	}
    
    public static String toHexString(String s) {
	    String str="";
	    for (int i=0;i<s.length();i++) {
		    int ch = (int)s.charAt(i);
		    String s4 = Integer.toHexString(ch);
		    str = str + s4;
	    }
	    return str;
    } 
}
