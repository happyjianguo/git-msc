package com.shyl.jmeter.client;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.Constants;
import com.shyl.util.NumberUtil;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;

public class ReturnsOrderClient {
	public String send(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject js = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject js_arr = new JSONObject();
		String no = "P2016081500000001";
		js.put("yybm", "455755610");
		js.put("psdbm", "01");
		js.put("gysbm", "30000001");
		js.put("psqybm", "30000001");
		js.put("thrq", "2016-06-24");
		js.put("yqthdbh", NumberUtil.getRandomNum(10));
		js.put("ddbh", no);
		js.put("thfqr", "王喜");
		js.put("thfqsj", "2016-06-24 00:00:00");
		js.put("thsj", "2016-06-24 00:00:00");
		js.put("jylx", "1");
		js.put("jls", "1");
		
		js_arr.put("sxh", "1");
		js_arr.put("ypbm", "78557");
		js_arr.put("scph", "1");
		js_arr.put("thsl", 1);
		js_arr.put("thdj", 34.9);
		js_arr.put("thyy", "破损");
		js_arr.put("scrq", "2016-06-24");
		js_arr.put("yxrq", "2016-06-29");
		js_arr.put("psdmxbh", no+"-0001");
		
		
		arr.add(js_arr);
		js.put("mx", arr);

		if(StringUtils.isEmpty(data)){
			data = js.toJSONString();
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_returnsOrder, Constants.nameSpaceUri, "send", sendData);
		return result;
	}
}
