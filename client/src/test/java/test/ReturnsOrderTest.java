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

public class ReturnsOrderTest {
	public static void main(String[] args){

		try {
			
			// WSDL��ַ
			//String wsdlUrl = "http://192.168.168.105:8080/msc-sz/webService/product?wsdl";
			//String wsdlUrl = "http://192.168.168.105:8080/msc-sz/webService/deliveryOrder?wsdl";
			String wsdlUrl = "http://localhost:8080/msc-web/webService/returnsOrder?wsdl";
			// ��������Ӧ��WSDL�е�namespace
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
			String no = "D02016062400000001";

			String sign = "";
			String dataType = "1";
			String data = "";
			String iocode = "1";
			JSONObject js = new JSONObject();
			JSONArray arr = new JSONArray();
			JSONObject js_arr = new JSONObject();
			js.put("yybm", "455755610");
			js.put("psdbm", "01");
			js.put("gysbm", "30000001");
			js.put("psqybm", "30000001");
			js.put("thrq", "2016-06-24");
			js.put("ddbh", no);
			js.put("thfqr", "王喜");
			js.put("thfqsj", "2016-06-24 00:00:00");
			js.put("thsj", "2016-06-24 00:00:00");
			js.put("jylx", "1");
			js.put("jls", "1");
			
			js_arr.put("sxh", "1");
			js_arr.put("ypbm", "78557");
			js_arr.put("scph", "1");
			js_arr.put("thsl", 1);
			js_arr.put("thdj", 34.9);
			js_arr.put("thyy", "破损");
			js_arr.put("scrq", "2016-06-24");
			js_arr.put("yxrq", "2016-06-29");
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
