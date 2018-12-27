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
public class GetPurchaseOrderClient {
	public static void main(String[] args){

		try {
			//String wsdlUrl = "http://192.168.168.101:8080/msc-sz/webService/purchaseOrder?wsdl";
			String wsdlUrl = "http://localhost:8080/msc-web/webService/purchaseOrderPlan?wsdl";
			String nameSpaceUri = "http://webservice.msc.shyl.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);

			call.setOperationName(new QName(nameSpaceUri, "get"));
			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.setReturnClass(String.class);
			
			String sign = "";
			String data = "";
			String iocode = "1";
			
			JSONObject jo = new JSONObject();
			jo.put("gysbm", "30000001");	//GPO编码
			jo.put("cxkssj", "2015-06-12 20:30:20");			//
			jo.put("cxjssj", "2016-08-12 20:30:20");	
			
			data = JSON.toJSONString(jo);
			//data = "<o><cxjsrq type='string'>2016-07-12</cxjsrq><cxksrq type='string'>2016-06-12</cxksrq><yybm type='string'>PDY310384</yybm></o>";
			System.out.println("data===="+data);
			sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
			
			
			String result = (String) call.invoke(new Object[] { sign,"1",data });

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
