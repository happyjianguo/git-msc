package test;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DelivertOrderTest {
	public static void main(String[] args){

		try {
			
			//String wsdlUrl = "http://localhost:8080/msc-web/webService/deliveryOrder?wsdl";
			String wsdlUrl = "http://localhost:8080/msc-web/webService/deliveryOrder?wsdl";
			String nameSpaceUri = "http://webservice.msc.shyl.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);

			call.setOperationName(new QName(nameSpaceUri, "send"));

			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING,
					ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING,
					ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING,
					ParameterMode.IN);
			call.setReturnClass(String.class);


			String sign = "";
			String dataType = "1";
			String data = "";
			JSONObject js = new JSONObject();
			JSONArray arr = new JSONArray();
			JSONObject js_arr = new JSONObject();
			String no = "D02016062400000001";
			js.put("yybm", "455755610");
			js.put("psdbm", "01");
			js.put("gysbm", "30000001");
			js.put("psqybm", "30000001");
			js.put("yqpsdbh", "P02016062400000001");
			js.put("psdtxm", "P02016062400000001");
			js.put("jylx", "1");
			
			js.put("cjsj", "2016-06-24 00:00:00");
			js.put("sdsj", "2016-06-27 00:00:00");
			js.put("jls", "1");
			js.put("ddbh", no);
			
			js_arr.put("sxh", "1");
			js_arr.put("ypbm", "78557");
			js_arr.put("scph", "1");
			js_arr.put("scrq", "2016-04-27");
			js_arr.put("yxrq", "2016-07-28");
			js_arr.put("dj", 34.9);
			js_arr.put("pssl", 100);
			js_arr.put("ddmxbh", no+"-0001");
			arr.add(js_arr);
			js.put("mx", arr);

			data = js.toJSONString();
			System.out.println("data="+data);
			sign = SHA1.getMessageDigest("1"+data, "SHA-1");
			System.out.println("sign="+sign);
			
			String result = (String) call.invoke(new Object[] {sign,dataType,data });

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
