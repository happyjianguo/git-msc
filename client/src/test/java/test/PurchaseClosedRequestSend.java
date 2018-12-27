package test;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * 医院发送订单测试
 * @author a_Q
 *
 */
public class PurchaseClosedRequestSend {
	public static void main(String[] args){

		try {
			//String wsdlUrl = "http://115.236.19.147:48080/msc-web/webService/purchaseOrderPlan?wsdl";
			//String wsdlUrl = "http://218.18.233.229:8081/msc-web/webService/purchaseOrderPlan?wsdl";
			String wsdlUrl = "http://localhost:8080/msc-web/webService/purchaseClosedRequest?wsdl";
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
			String iocode = "5";
			
			JSONObject jo = new JSONObject();
			jo.put("yythsqdbh", "20160704-01");
			jo.put("psdbh", "P02016062500000002");
			jo.put("yybm", "455755610");			//医院编码455755442   //PDY310384
			jo.put("psdbm", "01");			//配送点编码
			jo.put("thfqr", "张药师");
			jo.put("thfqsj", "2016-06-29 15:00:00");	//要求配货日期
			jo.put("jls", 1);				//记录数
			JSONArray jo_arr = new JSONArray();
			JSONObject jod = new JSONObject();
			jod.put("sxh", "1");			//顺序号
			jod.put("psdmxbh", "P02016062500000002-0001");
			jod.put("ypbm", "91191");			//药品编码
			jod.put("scph", "0001");			//
			jod.put("scrq", "2016-06-21");		//
			jod.put("yxrq", "2016-06-30");
			jod.put("thsl", "20");			//
			jod.put("thyy", "包装坏了");			//
			jo_arr.add(jod);
			jod = new JSONObject();
			jod.put("sxh", "2");			//顺序号
			jod.put("psdmxbh", "P02016062500000002-0000");
			jod.put("ypbm", "69533");			//药品编码
			jod.put("scph", "0001");			//
			jod.put("scrq", "2016-06-27");			//
			jod.put("yxrq", "2016-06-29");
			jod.put("thsl", "10");			//
			jod.put("thyy", "xx");			//
			jo_arr.add(jod);
			/*jod = new JSONObject();
			jod.put("sxh", "3");			//顺序号
			jod.put("cgjhdbh", "999888822-001");
			jod.put("ypbm", "60043");			//药品编码
			jod.put("cgsl", 20);			//采购数量
			jod.put("bzsm", "");		//备注说明
			jo_arr.add(jod);*/
			jo.put("mx", jo_arr);
			
			data = JSON.toJSONString(jo);
			System.out.println("data===="+data);
			sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
			
			
			String result = (String) call.invoke(new Object[] {sign, dataType, data});

			System.out.println(result);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
