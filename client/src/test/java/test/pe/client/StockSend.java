package test.pe.client;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.SHA1;

public class StockSend {
	public static void main(String[] args){
		try {
			
			//String wsdlUrl = "http://115.236.19.147:48080/msc-webservice-pe/webService/prescript?wsdl";
			String wsdlUrl = "http://localhost:8080/msc-webservice-pe/webService/stock?wsdl";
			String nameSpaceUri = "http://webservice.msc.shyl.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);

			call.setOperationName(new QName(nameSpaceUri, "send"));

			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.setReturnClass(String.class);


			String sign = "";
			String data = "";
			String iocode = "1111";
			JSONObject js = new JSONObject();
			
			js.put("psdbm", "122101213N13");
			js.put("kcrq", "2016-11-10");
			js.put("xtdm", "1111");
			js.put("jls", "2");
			
			JSONArray array = new JSONArray();
			JSONObject mx = new JSONObject();
			mx.put("sxh", "1");
			mx.put("ypbm", "100001");
			mx.put("qmkcsl", "20");
			mx.put("qmkcje", "576");
			array.add(mx);
			
			mx = new JSONObject();
			mx.put("sxh", "2");
			mx.put("ypbm", "100007");
			mx.put("qmkcsl", "200");
			mx.put("qmkcje", "57446");
			array.add(mx);
			
			js.put("mx", array);
			data = js.toJSONString();
			System.out.println("data="+data);
			sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
			
			String result = (String) call.invoke(new Object[] {sign,"1",data });

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
