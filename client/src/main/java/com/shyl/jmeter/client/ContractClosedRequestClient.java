package com.shyl.jmeter.client;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.Constants;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;

public class ContractClosedRequestClient {
	public String send(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		jo.put("yythsqdbh", "20160704-01");
		jo.put("psdbh", "P02016062500000002");
		jo.put("yybm", "455755610");			//医院编码455755442   //PDY310384
		jo.put("psdbm", "01");			//配送点编码
		jo.put("thfqr", "张药师");
		jo.put("thfqsj", "2016-06-29 15:00:00");	//要求配货日期
		jo.put("jls", 1);				//记录数
		JSONArray jo_arr = new JSONArray();
		JSONObject jod = new JSONObject();
		jod.put("sxh", "1");			//顺序号
		jod.put("psdmxbh", "P02016062500000002-0001");
		jod.put("ypbm", "91191");			//药品编码
		jod.put("scph", "0001");			//
		jod.put("scrq", "2016-06-21");		//
		jod.put("yxrq", "2016-06-30");
		jod.put("thsl", "20");			//
		jod.put("thyy", "包装坏了");			//
		jo_arr.add(jod);
		jod = new JSONObject();
		jod.put("sxh", "2");			//顺序号
		jod.put("psdmxbh", "P02016062500000002-0000");
		jod.put("ypbm", "69533");			//药品编码
		jod.put("scph", "0001");			//
		jod.put("scrq", "2016-06-27");			//
		jod.put("yxrq", "2016-06-29");
		jod.put("thsl", "10");			//
		jod.put("thyy", "xx");			//
		jo_arr.add(jod);
		/*jod = new JSONObject();
		jod.put("sxh", "3");			//顺序号
		jod.put("cgjhdbh", "999888822-001");
		jod.put("ypbm", "60043");			//药品编码
		jod.put("cgsl", 20);			//采购数量
		jod.put("bzsm", "");		//备注说明
		jo_arr.add(jod);*/
		jo.put("mx", jo_arr);
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_contractClosedRequest, Constants.nameSpaceUri, "send", sendData);
		return result;
	}
	
	public String get(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		jo.put("gpobm", "40003333");	//GPO编码
		jo.put("gpolx", "1");	//GPO编码
		jo.put("cxkssj", "2016-06-12 20:30:20");			//
		jo.put("cxjssj", "2016-12-12 20:30:20");	
		if(StringUtils.isEmpty(data)){
			data = jo.toJSONString();
		}
		if(StringUtils.isEmpty(dataType)){
			dataType = "1";//默认为json格式
		}
		if(StringUtils.isEmpty(iocode)){
			iocode = "11";
		}
		System.out.println("data="+data);
		String sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
		System.out.println("sign="+sign);
		String [] sendData = new String[] {sign,dataType,data };

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_contractClosedRequest, Constants.nameSpaceUri, "get", sendData);
		return result;
	}
	
	public String fedback(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		jo.put("gysbm", "100001");	//GPO编码
		jo.put("ddjasqdbh", "C2016081800000001");			//
		jo.put("zt", 1);
		jo.put("sm", "好的同意你了");	
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_contractClosedRequest, Constants.nameSpaceUri, "fedback", sendData);
		return result;
	}
}
