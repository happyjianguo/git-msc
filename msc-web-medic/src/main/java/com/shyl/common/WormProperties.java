package com.shyl.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Properties;


public class WormProperties {
	
	public static Properties prop = new Properties();

	static {
		InputStream in = null;
		try {
			String filePath = WormProperties.class.getResource("/").getFile()+"worm.properties";
			File newFile = new File(filePath);
			System.out.println(newFile);
			if (!newFile.exists()) {
				newFile.createNewFile();
			}
			in = WormProperties.class.getResourceAsStream("/worm.properties");
			prop.load(in);
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
	
	public static String get(String key, String def) {
		return prop.getProperty(key, def);
	}

	@SuppressWarnings("rawtypes")
	public static void put(String key, String value) {
		FileOutputStream fos = null;
		try {
			String file = WormProperties.class.getResource("/worm.properties").getFile();
			//给配置文件设置值，并保存
			fos = new FileOutputStream(file);
			prop.put(key, value);
			prop.store(fos, key+"修改");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos!= null) {
				try {
					fos.close();
				} catch (IOException e) {}
			}
		}
		
	}
}
