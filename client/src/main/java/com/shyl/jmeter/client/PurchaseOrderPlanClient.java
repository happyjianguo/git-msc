package com.shyl.jmeter.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;
import org.apache.jorphan.collections.Data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.CSVUtil;
import com.shyl.util.Constants;
import com.shyl.util.NumberUtil;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;

public class PurchaseOrderPlanClient {
	public String send(String iocode,String dataType, String data,String yybm) 
			throws RemoteException, ServiceException, IOException {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (StringUtils.isEmpty(yybm)) {
			yybm="455755610";
		}
		if(StringUtils.isEmpty(data)){
			InputStream is = this.getClass().getResourceAsStream("/resources/PurchaseOrderPlanClient.csv");
			 
			
			List<Map<String, Object>> lineList = CSVUtil.csvToList(is);
			
			String s = NumberUtil.getRandomNum(10)+(System.currentTimeMillis()+"").substring(5);
			JSONObject jo = new JSONObject();
			jo.put("yybm", yybm);			//医院编码455755442   //PDY310384
			jo.put("psdbm", yybm+"01");			//配送点编码
			jo.put("yqphsj", format.format(new Date(new Date().getTime()+86400000)));	//要求配货日期
			jo.put("jjcd", "1");			//紧急程度
			jo.put("cgjhdbh", s);
			jo.put("dcpsbs", 0);			//多次配送标识
			jo.put("jhlx", 0);			//计划类型
			jo.put("jls", lineList.size());				//记录数

			JSONArray jo_arr = new JSONArray();
			int i=1;
			for (Map<String, Object> info:lineList) {
				JSONObject jod = new JSONObject();
				
				jod.put("sxh", i);			//顺序号
				jod.put("cgjhdmxbh", s+"-"+1);
				jod.put("gysbm", info.get("gysbm"));
				jod.put("cgdj", info.get("cgdj"));	
				jod.put("ypbm", info.get("ypbm"));			//药品编码
				jod.put("cgsl", info.get("cgsl"));			//采购数量
				jod.put("bzsm", info.get("bzsm"));	//备注说明
				jo_arr.add(jod);
				i++;
				if(i>100) break;
			}
			
			jo.put("mx", jo_arr);
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

		System.out.println("发送时间开始："+format.format(new Date()));
		String result = ServiceUtil.callWebService(Constants.wsdlUrl_purchaseOrderPlan, Constants.nameSpaceUri, "send", sendData);

		System.out.println("发送时间结束："+format.format(new Date()));
		
		return result;
	}
	
	public String get(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		jo.put("gpobm", "30000001");	//GPO编码
		jo.put("cxkssj", "2015-06-12 20:30:20");			//
		jo.put("cxjssj", "2016-08-12 20:30:20");
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_purchaseOrderPlan, Constants.nameSpaceUri, "get", sendData);
		return result;
	}
	
	public String fedback(String iocode,String dataType, String data) throws RemoteException, ServiceException, IOException{
		
		if(StringUtils.isEmpty(data)){
			InputStream is = this.getClass().getResourceAsStream("/resources/PurchaseOrderPlanClient_fedback.csv");
			
			List<Map<String, Object>> lineList = CSVUtil.csvToList(is);
			Map<String, Object> map = lineList.get(0);
			
			JSONObject jo = new JSONObject();
			jo.put("gpobm", "10310");		//GPO编码
			jo.put("gpolx", "1");		//GPO编码
			jo.put("gysbm", map.get("GYSBM"));		//GPO编码
			jo.put("ddjhbh", map.get("DDJHBH"));			//订单编号
			jo.put("zt", 0);
			jo.put("jls", lineList.size());

			JSONArray jo_arr = new JSONArray();
			int i=1;
			for(Map<String, Object> info : lineList) {
				JSONObject jod = new JSONObject();
				jod.put("sxh", i);			//顺序号
				jod.put("ddjhmxbh", info.get("DDJHMXBH"));		//订单计划明细编号
				jod.put("cljg", 0);			//处理结果
				jod.put("bz", "xxx");		//备注说明
				jo_arr.add(jod);
			}
			
			jo.put("mx", jo_arr);
			
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_purchaseOrderPlan, Constants.nameSpaceUri, "fedback", sendData);
		return result;
	}
}
