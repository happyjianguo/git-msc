package com.shyl.jmeter.client;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.shyl.util.Constants;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;

public class PaymentClient {
	public String send(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		jo.put("yybm", "455755610");			//医院编码
		jo.put("gysbm", "30000001");				//GPO编码
		jo.put("psqybm", "30000001");			//配送企业编码
		jo.put("fksj", "2016-06-24 11:00:00");	//付款时间
		jo.put("fkfs", 1);						//付款方式
		jo.put("fkje", 99*34.9);					//付款金额
		jo.put("jsdbh", "K2016081800000001");			//结算单编号
		jo.put("yqfkdbh", "20160624001");		//药企付款单编号
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_payment, Constants.nameSpaceUri, "send", sendData);
		return result;
	}
}
