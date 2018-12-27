package test;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.alibaba.fastjson.JSONObject;

public class ActivemqTest {
	public static void main(String[] args){

		try {
			
			//String wsdlUrl = "http://localhost:8080/msc-web/webService/deliveryOrder?wsdl";
			String wsdlUrl = "http://localhost:8080/msc-webservice-pe/webService/test?wsdl";
			String nameSpaceUri = "http://webservice.msc.shyl.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);

			call.setOperationName(new QName(nameSpaceUri, "save"));

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
			js.put("name", "455755610");		

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
