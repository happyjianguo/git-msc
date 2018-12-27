package test.pe.client;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.alibaba.fastjson.JSONObject;
import com.shyl.util.SHA1;

public class PatientSave {
	public static void main(String[] args){
		try {
			
			String wsdlUrl = "http://115.236.19.147:48080/msc-webservice-pe/webService/patient?wsdl";
			String nameSpaceUri = "http://webservice.msc.shyl.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);

			call.setOperationName(new QName(nameSpaceUri, "save"));

			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.setReturnClass(String.class);


			String sign = "";
			String data = "";
			String iocode = "61";
			JSONObject js = new JSONObject();
			
			js.put("yybm", "PDY310384");
			js.put("hzxm", "陈静");
			js.put("sfzh", "332626197609300020");
			js.put("xb", "女");
			js.put("csrq", "1976-09-30");
			js.put("dhhm", "0574-86888122");
			js.put("sjhm", "13788889999");
			js.put("jzdz", "宁波市北仑区长江路88号");
			js.put("jkkh", "300000222220002");
			js.put("sbkh", "300000222220002");
			js.put("ybkh", "300000222220002");
			
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
