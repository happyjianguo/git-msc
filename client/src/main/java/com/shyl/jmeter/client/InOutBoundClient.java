package com.shyl.jmeter.client;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Date;
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

import jodd.util.StringUtil;

public class InOutBoundClient {
	public String send(String iocode,String dataType, String data, String yybm) throws RemoteException, ServiceException, IOException {
		
		if(StringUtils.isEmpty(data)){
			System.out.println(this.getClass().getResource("/resources/InOutBoundClient_"+yybm+".csv").getPath());
			InputStream is = this.getClass().getResourceAsStream("/resources/InOutBoundClient_"+yybm+".csv");
			
			List<Map<String, Object>> lineList = CSVUtil.csvToList(is);
			Map<String, Object> map = lineList.get(0);
			
			JSONObject jo = new JSONObject();
			if (StringUtil.isEmpty(yybm)) {
				jo.put("yybm", map.get("YYBM"));//医院编码
			} else {
				jo.put("yybm", yybm);					//医院编码
			}
			jo.put("psdbm",map.get("PSDBM"));					//配送点编码
			jo.put("rkdbh", NumberUtil.getRandomNum(10));	//入库单
			jo.put("psdbh", map.get("PSDBH"));	//配送单编号
			
			jo.put("czsj", "2016-08-20 00:00:00");			//操作日期
			jo.put("czr", "小黑");			//操作人
			jo.put("jls", lineList.size());						//记录数
			JSONArray jo_arr = new JSONArray();

			int i=1;
			for(Map<String, Object> info : lineList) {
				JSONObject jod = new JSONObject();
				jod.put("sxh", i);					//顺序号
				jod.put("ypbm", info.get("YPBM"));					//药品编码
				jod.put("scph", info.get("SCPH"));
				jod.put("scrq", "2016-04-27");
				jod.put("yxrq", "2016-12-28");
				jod.put("crksl", info.get("CRKSL"));				//出入库数量
				jod.put("psdmxbh", info.get("PSDMXBH"));//配送单明细编号
				jo_arr.add(jod);
				i++;
				if (i>100) {
					break;
				}
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

		Long time0 = new Date().getTime();
		String result = ServiceUtil.callWebService(Constants.wsdlUrl_inOutBound, Constants.nameSpaceUri, "send", sendData);
		Long time1 = new Date().getTime();
		System.out.println("处理时间："+(time1-time0));
		return result;
	}
	
	public String get(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		jo.put("gpobm", "30000001");					//GPO编码
		jo.put("cxkssj", "2016-01-01 20:29:20");		//查询开始时间
		jo.put("cxjssj", "2016-12-01 20:29:20");	    //查询结束时间	
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_inOutBound, Constants.nameSpaceUri, "get", sendData);
		return result;
	}
}
