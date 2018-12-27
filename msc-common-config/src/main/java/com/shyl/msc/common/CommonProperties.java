package com.shyl.msc.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommonProperties {
	/**  SYSNAME **/
	public static String  SYSNAME= "";
	/** FIRSTCLASSNAME **/
	public static String FIRSTCLASSNAME = "";
	/**  ISSAAS **/
	public static String  ISSAAS= "";
	/**  CANAME_JS **/
	public static String  CANAME_JS= "";
	
	/**  pe被调平台代码 **/
	public static String PLATFORM_NO = "";
	/**  b2b访问pe的iocode **/
	public static String IOCODE_B2B_TO_PE = "";
	/**  订单消息webservice接口 **/
	public static String WS_BEB_ORDERMSG = "";
	/**  订单计划webservice接口 **/
	public static String WS_B2B_PURCHASEORDERPLAN = "";
	/**  webService命名空间 **/
	public static String WS_TARGETNAMESPACE = "";
	/**  是否合并处方生成订单计划 **/
	public static String IS_MERGE_PRESCRIPT_TO_ORDER = "";
	/**  PE供应商编码 **/
	public static String PE_GYS_CODE = "";
	/**  患者识别类型 **/
	public static String PATIENT_DISTINGUISH_TYPE = "";
	
	/**  深圳平台暂时标志符 **/
	public static String IS_TO_SZ_PROJECT = "";
	/**  saas平台代码集 **/
	public static String DB_PROJECTCODE = "";
	/**  saas平台代码名称集 **/
	public static String DB_PROJECTNAME = "";
	/**  saas平台数据源集 **/
	public static String DB_PROJECTDS = "";
	/**  saas平台主数据源 **/
	public static String MAIN_PROJECTDS = "";
	/**  单点登录appname **/
	public static String APPNAME = "";
	/** 单点登录协议码  **/
	public static String SECURITY = "";
	/** 单点登录地址  **/
	public static String SSOURL = "";
	
	
	static {
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = CommonProperties.class.getResourceAsStream("/public_user.properties");
			prop.load(in);

			FIRSTCLASSNAME = prop.getProperty("FIRSTCLASSNAME","").trim();
			SYSNAME = prop.getProperty("SYSNAME","").trim();
			ISSAAS = prop.getProperty("ISSAAS","").trim();
			CANAME_JS = prop.getProperty("CANAME_JS","").trim();
			
			PLATFORM_NO = prop.getProperty("PLATFORM_NO","").trim();
			IOCODE_B2B_TO_PE = prop.getProperty("IOCODE_B2B_TO_PE","").trim();
			WS_BEB_ORDERMSG = prop.getProperty("WS_BEB_ORDERMSG","").trim();
			WS_B2B_PURCHASEORDERPLAN = prop.getProperty("WS_B2B_PURCHASEORDERPLAN","").trim();
			WS_TARGETNAMESPACE = prop.getProperty("WS_TARGETNAMESPACE","").trim();
			IS_MERGE_PRESCRIPT_TO_ORDER = prop.getProperty("IS_MERGE_PRESCRIPT_TO_ORDER","").trim();
			PE_GYS_CODE = prop.getProperty("PE_GYS_CODE","").trim();
			PATIENT_DISTINGUISH_TYPE = prop.getProperty("PATIENT_DISTINGUISH_TYPE","").trim();
			
			IS_TO_SZ_PROJECT = prop.getProperty("IS_TO_SZ_PROJECT","").trim();
			DB_PROJECTCODE = prop.getProperty("DB_PROJECTCODE","").trim();
			DB_PROJECTNAME = prop.getProperty("DB_PROJECTNAME","").trim();
			DB_PROJECTDS = prop.getProperty("DB_PROJECTDS","").trim();
			MAIN_PROJECTDS = prop.getProperty("MAIN_PROJECTDS","").trim();
			APPNAME = prop.getProperty("APPNAME","").trim();
			SECURITY = prop.getProperty("SECURITY","").trim();
			SSOURL = prop.getProperty("SSOURL","").trim();
			
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
	
	
	
}
