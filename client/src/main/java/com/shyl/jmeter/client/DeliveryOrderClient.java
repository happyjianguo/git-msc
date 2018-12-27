package com.shyl.jmeter.client;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.CSVUtil;
import com.shyl.util.Constants;
import com.shyl.util.NumberUtil;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;


public class DeliveryOrderClient {

	public String send(String iocode, String dataType, String data) throws RemoteException, ServiceException, IOException{
		

		if(StringUtils.isEmpty(data)){
			System.out.println(this.getClass().getResource("/DeliveryOrderClient.csv").getPath());
			InputStream is = this.getClass().getResourceAsStream("/DeliveryOrderClient.csv");
			
			List<Map<String, Object>> lineList = CSVUtil.csvToList(is);
			Map<String, Object> map = lineList.get(0);
			
			JSONObject js = new JSONObject();
			JSONArray arr = new JSONArray();
			js.put("yybm", map.get("YYBM"));
			js.put("psdbm", map.get("PSDBM"));
			js.put("gysbm", map.get("GYSBM"));
			js.put("psqybm", map.get("PSQYBM"));
			js.put("pssbm", map.get("PSSBM"));
			js.put("gpobm", "10310");
			
			js.put("yqpsdbh", NumberUtil.getRandomNum(10));
			js.put("psdtxm", NumberUtil.getRandomNum(10));
			js.put("jylx", "0");
			js.put("gpolx", "1");
			
			js.put("cjsj", "2016-08-18 00:00:00");
			js.put("sdsj", "2016-08-20 00:00:00");
			js.put("jls", lineList.size());
			js.put("ddbh", map.get("DDBH"));

			int i=1;
			for(Map<String, Object> info : lineList) {
				JSONObject js_arr = new JSONObject();
				js_arr.put("sxh", i);
				js_arr.put("ypbm", info.get("YPBM"));
				js_arr.put("scph", "1");
				js_arr.put("scrq", "2016-04-27");
				js_arr.put("yxrq", "2016-12-28");
				js_arr.put("dj",  info.get("DJ"));
				js_arr.put("pssl",  info.get("PSSL"));
				js_arr.put("ddmxbh", info.get("DDMXBH"));
				arr.add(js_arr);
				i++;
			}
			js.put("mx", arr);
			
			
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
		Long time0 = new Date().getTime();
		String result = ServiceUtil.callWebService(Constants.wsdlUrl_deliveryOrder, Constants.nameSpaceUri, "send", sendData);
		Long time1 = new Date().getTime();
		System.out.println("处理时间："+(time1-time0));
		return result;
	}
	
	public String get(String iocode,String dataType, String data, String yybm) throws RemoteException, ServiceException{
		
		if(StringUtils.isEmpty(yybm)){
			yybm = "455755610";
		}
		if(StringUtils.isEmpty(data)){
			JSONObject jo = new JSONObject();
			jo.put("yybm", yybm);	//医院编码
			jo.put("psdtxm", "test000001");	//配送单条形码
			data = jo.toJSONString();
		}
		if(StringUtils.isEmpty(dataType)){
			dataType = "1";//默认为json格式
		}
		if(StringUtils.isEmpty(iocode)){
			iocode = "59296069";
		}
		System.out.println("data="+data);
		System.out.println("dataType="+dataType);
		String sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
		System.out.println("sign="+sign);
		String [] sendData = new String[] {sign,dataType,data };

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_deliveryOrder, Constants.nameSpaceUri, "get", sendData);
		return result;
	}
}
