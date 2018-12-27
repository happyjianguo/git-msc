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
import com.shyl.util.NumberUtil;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;

public class PurchaseOrderClient {
	public String send(String iocode,String dataType, String data) throws RemoteException, ServiceException, IOException{
		
		if(StringUtils.isEmpty(data)){
			InputStream is = this.getClass().getResourceAsStream("/resources/PurchaseOrderClient.csv");
			
			List<Map<String, Object>> lineList = CSVUtil.csvToList(is);
			Map<String, Object> map = lineList.get(0);
			
			JSONObject jo = new JSONObject();
			String yno = NumberUtil.getRandomNum(10);
			jo.put("ddjhbh", map.get("DDJHBH"));//订单计划编号
			jo.put("yqddbh", yno);	//药企订单编号
			jo.put("gysbm", map.get("GYSBM"));			//供应商编码
			jo.put("yybm", map.get("YYBM"));			//医院编码
			jo.put("psdbm", map.get("PSDBM"));					//配送点编码
			jo.put("yqphsj", "2016-10-28 11:22:33");//要求配货日期
			jo.put("jjcd", "1");					//紧急程度
			jo.put("jylx", 0);					
			jo.put("dcpsbs", 0);					//多次配送标识
			jo.put("gpolx", "1");
			jo.put("gpobm", "10310");
			
			
			jo.put("jls", 1);						//记录数
			JSONArray jo_arr = new JSONArray();
			int i=1;
			for(Map<String, Object> info : lineList) {
				JSONObject jod = new JSONObject();
				jod.put("sxh", i);							//顺序号
				jod.put("ddjhmxbh", info.get("DDJHMXBH"));	//订单计划明细编号
				jod.put("yqddmxbh", yno+"-"+i);	//药企订单明细编号
				jod.put("ypbm", info.get("YPBM"));							//药品编码
				jod.put("cgsl", info.get("CGSL"));							//采购数量
				jod.put("cgdj", info.get("CGDJ"));							//采购单价
				jod.put("bzsm", "");						//备注说明
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_purchaseOrder, Constants.nameSpaceUri, "send", sendData);
		return result;
	}
	
}
