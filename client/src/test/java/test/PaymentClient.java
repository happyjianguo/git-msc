package test;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
/**
 * GPO发送发票测试
 * @author a_Q
 *
 */
public class PaymentClient {
	public static void main(String[] args){

		try {
			String wsdlUrl = "http://localhost:8080/msc-web/webService/payment?wsdl";
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
			String dataType = "1";
			String data = "";
			String iocode = "1";
			
			JSONObject jo = new JSONObject();
			jo.put("yybm", "455755610");			//医院编码
			jo.put("gysbm", "30000001");				//GPO编码
			jo.put("psqybm", "30000001");			//配送企业编码
			jo.put("fksj", "2016-06-24 11:00:00");	//付款时间
			jo.put("fkfs", 1);						//付款方式
			jo.put("fkje", 99*34.9);					//付款金额
			jo.put("jsdbh", "J02016062400000001");			//结算单编号
			jo.put("yqfkdbh", "20160624001");		//药企付款单编号
			
			data = JSON.toJSONString(jo);
			System.out.println("data====="+data);
			sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
			
			
			String result = (String) call.invoke(new Object[] { sign,dataType,data });

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
