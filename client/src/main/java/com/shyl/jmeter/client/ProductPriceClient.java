package com.shyl.jmeter.client;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.Constants;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;

public class ProductPriceClient {
	public String send(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		jo.put("gpobm", "30000001");		//GPO编码
		jo.put("jls", "1");			//记录数
		JSONArray jo_arr = new JSONArray();
		JSONObject jod = new JSONObject();
		jod.put("sxh", "1");		//顺序号
		jod.put("ypbm", "78557");//药品编码
		jod.put("yybm", "455755610");
		jod.put("gysbm", "30000001");//配送企业编码
		jod.put("zbj", 34.9);		//中标价
		jod.put("cjj", 34.9);		//成交价
		jod.put("sfjy", 0);			//是否禁用
		jod.put("yxqq", "2016-01-01");
		jod.put("yxqz", "2016-12-31");
		jod.put("jylx", 1);
		
		jo_arr.add(jod);
		jo.put("mx", jo_arr);
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_productPrice, Constants.nameSpaceUri, "send", sendData);
		return result;
	}
}
