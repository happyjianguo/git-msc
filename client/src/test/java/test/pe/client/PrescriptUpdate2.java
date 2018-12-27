package test.pe.client;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.alibaba.fastjson.JSONObject;
import com.shyl.util.SHA1;

public class PrescriptUpdate2 {
	public static void main(String[] args){
		try {
			
			//String wsdlUrl = "http://localhost:8080/msc-webservice-pe/webService/prescript?wsdl";
			String wsdlUrl = "http://115.236.19.147:48080/msc-webservice-pe/webService/prescript?wsdl";
			String nameSpaceUri = "http://webservice.msc.shyl.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);

			call.setOperationName(new QName(nameSpaceUri, "updateStatusA"));

			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.setReturnClass(String.class);


			String sign = "";
			String data = "";
			String iocode = "02894471";//
			JSONObject js = new JSONObject();
			
			js.put("yybm", "NJ00001");//
			js.put("cfh", "C2016122100000001");
			js.put("cfzt", "3");
			
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
