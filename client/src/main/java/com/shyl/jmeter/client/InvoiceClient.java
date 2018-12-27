package com.shyl.jmeter.client;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.Constants;
import com.shyl.util.SHA1;
import com.shyl.util.ServiceUtil;

public class InvoiceClient {
	public String send(String iocode,String dataType, String data) throws RemoteException, ServiceException{
		JSONObject jo = new JSONObject();
		String no = "P2016081500000001"; 
		jo.put("yybm", "455755610");		//医院编码
		jo.put("gysbm", "30000001");		//GPO编码
		jo.put("fph", "333999911");			//发票号
		jo.put("fprq", "2016-06-24");//发票日期
		jo.put("hszje", 34.9*99);	//含税总金额
		jo.put("bhszje", 34.9*99/1.05);	//含税总金额
		jo.put("sfch", "0");		//是否冲红
		jo.put("fpbz", "xxxx");			//发票备注
		jo.put("stdbh", no);			//发票备注
		jo.put("jls", "1");			//记录数
		JSONArray jo_arr = new JSONArray();
		JSONObject jod = new JSONObject();
		jod.put("sxh", "1");			//顺序号
		jod.put("ypbm", "78557");			//药品编码
		jod.put("spsl", 1);			//商品数量
		jod.put("hsdj", 34.9);		//含税单价
		jod.put("bhsdj", 34.9/1.05);		//含税单价
		jod.put("hsje", 34.9*99);		//含税单价
		jod.put("bhsje", 34.9*99/1.05);		//含税单价
		jod.put("stdmxbh", no+"-0001");		
		jod.put("sl", 5);			//税率
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

		String result = ServiceUtil.callWebService(Constants.wsdlUrl_invoice, Constants.nameSpaceUri, "send", sendData);
		return result;
	}
}
