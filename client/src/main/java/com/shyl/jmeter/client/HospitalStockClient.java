package com.shyl.jmeter.client;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.CSVUtil;
import com.shyl.util.Constants;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;

import jodd.util.StringUtil;

public class HospitalStockClient {
	public String send(String iocode,String dataType, String data, String yybm)
			throws RemoteException, IOException, ServiceException{
		if (StringUtil.isEmpty(yybm)) {
			yybm="455755610";
		}
		if(StringUtils.isEmpty(data)){
			InputStream is = this.getClass().getResourceAsStream("/resources/InOutBoundClient.csv");

			List<Map<String, Object>> lineList = CSVUtil.csvToList(is);
			
			JSONObject jo = new JSONObject();
			jo.put("yybm", yybm);			//医院编码
			jo.put("kcrq", "2016-10-19");
			jo.put("jls", lineList.size());						//记录数
			

			JSONArray jo_arr = new JSONArray();
			int i=1;
			for(Map<String, Object> info : lineList) {
				JSONObject jod = new JSONObject();
				jod.put("sxh", i);					//顺序号
				jod.put("ypbm", info.get("YPBM"));				//药品编码
				jod.put("qckcsl", 1000);				//期初库存数量
				jod.put("qckcje", 1000*34.9);			//期初库存金额
				jod.put("qmkcsl", 500);					//期末库存数量
				jod.put("qmkcje", 500*34.9);			//期末库存金额
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_hospitalStock, Constants.nameSpaceUri, "send", sendData);
		return result;
	}
}
