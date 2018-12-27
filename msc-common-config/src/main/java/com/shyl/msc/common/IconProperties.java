package com.shyl.msc.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class IconProperties {
	/**  是否使用CA **/
	public static String ICONCOUNT = "";
	public static List<String> ICONLIST = new ArrayList();
	
	static {
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = IconProperties.class.getResourceAsStream("icon_css.properties");
			prop.load(in);
			ICONCOUNT = prop.getProperty("ICONCOUNT")==null?"":prop.getProperty("ICONCOUNT").trim();
			for (int i = 0; i < Integer.parseInt(ICONCOUNT); i++) {
				String iconI = prop.getProperty(i+"")==null?"":prop.getProperty(i+"").trim();
				if(!iconI.equals("")){
					ICONLIST.add(iconI);
					System.out.println(i+":"+iconI);
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static void main(String[] args) {
		
	}
	
}
