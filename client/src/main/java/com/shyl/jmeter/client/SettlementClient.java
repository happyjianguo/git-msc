package com.shyl.jmeter.client;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.Constants;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;

public class SettlementClient {
	public String send(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		jo.put("yybm", "455755610");			//医院编码
		jo.put("gysbm", "30000001");				//GPO编码
		jo.put("psqybm", "30000001");			//配送企业编码
		jo.put("jssj", "2016-06-24 11:00:00");	//结算时间
		jo.put("zqqsrq", "2016-06-24");			//账期起始日期
		jo.put("zqjsrq", "2016-06-24");			//账期结束日期
		jo.put("jszje", 34.9*99);				//结算总金额
		jo.put("yqjsdbh", "20160624001");				//药企结算单编号
		jo.put("jls", "1");			//记录数
		JSONArray jo_arr = new JSONArray();
		JSONObject jod = new JSONObject();
		jod.put("sxh", "1");			//顺序号
		jod.put("yqjsdmxbh", "20160624001-01");	//药企结算单明细编号
		jod.put("fpbh", "F2016081800000001");			//发票编号
		jod.put("jsje", 34.9*99);			//结算金额
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_settlement, Constants.nameSpaceUri, "send", sendData);
		return result;
	}
}
