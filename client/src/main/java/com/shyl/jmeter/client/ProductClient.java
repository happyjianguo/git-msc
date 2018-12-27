package com.shyl.jmeter.client;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.shyl.util.Constants;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;

public class ProductClient {

	/**
	 * 2.1 GPO药品信息下载
	 * @param iocode
	 * @param dataType
	 * @param data
	 * @return
	 * @throws ServiceException 
	 * @throws RemoteException 
	 */
	public String GetToCom(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		jo.put("gpobm", "30000001");			//GPO编码
		jo.put("scgxsj", "2016-01-12 00:00:00");//上次更新时间
		if(StringUtils.isEmpty(data)){
			data = jo.toJSONString();
		}
		if(StringUtils.isEmpty(dataType)){
			dataType = "1";//默认为json格式
		}
		if(StringUtils.isEmpty(iocode)){
			iocode = "1";
		}
		System.out.println("data="+data);
		String sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
		System.out.println("sign="+sign);
		String [] sendData = new String[] {sign,dataType,data };

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_product, Constants.nameSpaceUri, "getToCom", sendData);
		return result;
	}
	
	/**
	 * 2.1 医院药品信息下载
	 * @param iocode
	 * @param dataType
	 * @param data
	 * @return
	 * @throws ServiceException 
	 * @throws RemoteException 
	 */
	public String getToHis(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		jo.put("yybm", "455755610");
		jo.put("scgxsj", "2016-01-01 00:00:00");
		if(StringUtils.isEmpty(data)){
			data = jo.toJSONString();
		}
		if(StringUtils.isEmpty(dataType)){
			dataType = "1";//默认为json格式
		}
		if(StringUtils.isEmpty(iocode)){
			iocode = "59296069";
		}
		System.out.println("data="+data);
		String sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
		System.out.println("sign="+sign);
		String [] sendData = new String[] {sign,dataType,data };

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_product, Constants.nameSpaceUri, "getToHis", sendData);
		return result;
	}
	
	
}
