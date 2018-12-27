package com.shyl.jmeter.client;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.Constants;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;

public class GoodsClient {

	public String send(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		jo.put("yybm", "455755610");
		jo.put("jls", "1");
		JSONArray jo_arr = new JSONArray();
		JSONObject jod = new JSONObject();
		jod.put("sxh", "1");
		jod.put("yyypbm", "YY78557");	
		jod.put("ypmc", "消结安胶囊");
		jod.put("tym", "1000");	
		jod.put("jxmc", "银谷制药有限责任公司");
		jod.put("scqymc", "云南良方制药有限公司");
		jod.put("gg", "0.38g");
		jod.put("bzdw", "盒");
		jod.put("bzgg", "24粒/盒");
		jod.put("ypbm", "78557");
		jod.put("sfjy", "0");
		
		jo_arr.add(jod);
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_goods, Constants.nameSpaceUri, "send", sendData);
		return result;
	}
}
