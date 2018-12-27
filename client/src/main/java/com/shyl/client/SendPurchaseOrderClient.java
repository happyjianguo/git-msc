package com.shyl.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.CSVUtil;
import com.shyl.util.SHA1;
/**
 * 医院上传订单测试
 * @author a_Q
 *
 */
public class SendPurchaseOrderClient {
	public static void main(String[] args){
		try {
			String wsdlUrl = "http://localhost:8080/msc-web/webService/purchaseOrder?wsdl";
			String nameSpaceUri = "http://webservice.msc.shyl.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);

			call.setOperationName(new QName(nameSpaceUri, "send"));
			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.setReturnClass(String.class);
			
			String sign = "";
			String data = "";
			String dataType = "1";
			String iocode = "";
			
			Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();
			List<Map<String, Object>> lineList = CSVUtil.csvToList("E:/data/test/PurchaseOrder.csv");
			for(Map<String, Object> m:lineList){
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				String key = m.get("yybm")+"-"+m.get("psdbm")+"-"+m.get("yqphsj")
						+"-"+m.get("jjcd")+"-"+m.get("dcpsbs")
						+"-"+m.get("ddjhbh")+"-"+m.get("yqddbh")
						+"-"+m.get("gpobm")+"-"+m.get("psqybm");
				if(map.get(key)!=null){
					list = map.get(key);
					list.add(m);
				}else{
					list.add(m);
				}
				map.put(key, list);
			}
			
			for(String key: map.keySet()){
				JSONObject jo = null;
				JSONArray jo_arr = new JSONArray();
				List<Map<String, Object>> line = map.get(key);
				for(Map<String, Object> m:line){
					if(jo == null){
						iocode = m.get("iocode").toString();
						jo = new JSONObject();
						jo.put("ddjhbh", m.get("ddjhbh"));			//订单计划编号
						jo.put("yqddbh", m.get("yqddbh"));			//药企订单编码
						jo.put("gpobm", m.get("gpobm"));			//GPO编码
						jo.put("psqybm", m.get("psqybm"));			//配送企业编码
						jo.put("yybm", m.get("yybm"));			//医院编码
						
						jo.put("psdbm", m.get("psdbm"));		//配送点编码
						jo.put("yqphsj", m.get("yqphsj"));		//要求配货日期
						jo.put("jjcd", m.get("jjcd"));			//紧急程度
						jo.put("dcpsbs", m.get("dcpsbs"));		//多次配送标识
						jo.put("jls", line.size());				//记录数
					}
					JSONObject jod = new JSONObject();
					jod.put("sxh", m.get("sxh"));				//顺序号
					jod.put("ypbm", m.get("ypbm"));				//药品编码
					jod.put("cgjldw", m.get("cgjldw"));			//采购计量单位
					jod.put("cgsl", m.get("cgsl"));				//采购数量
					jod.put("bzsm", m.get("bzsm"));				//备注说明
					jod.put("ddjhmxbh", m.get("ddjhmxbh"));				//订单明细编号
					jod.put("yqddmxbh", m.get("yqddmxbh"));				//订单明细编号
					jod.put("cgdj", m.get("cgdj"));				//订单明细编号
					
					
					jo_arr.add(jod);
				}
				jo.put("mx", jo_arr);
				data = JSON.toJSONString(jo);
				System.out.println("iocode===="+iocode);
				System.out.println("data===="+data);
				sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
				String result = (String) call.invoke(new Object[] { sign, dataType, data });
				System.out.println(result);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
