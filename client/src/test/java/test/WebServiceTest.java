package test;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
/**
 * webService测试
 * @author a_Q
 *
 */
public class WebServiceTest {
	public static void main(String[] args){

		try {
			// webService wsdl地址
			String wsdlUrl = "http://192.168.0.100:8080/msc-web/webService/prescription?wsdl";
			//webService中的nameSpace
			String nameSpaceUri = "http://ws.qy.wondersgroup.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);

			//设置调用方法名
			call.setOperationName(new QName(nameSpaceUri, "saveCF"));

			//设置参数及类型
			call.addParameter("arg0", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			//设置返回值类型
			call.setReturnClass(String.class);
			String sign = "xxxx";
			
			String data = "";
			
			
			String result = (String) call.invoke(new Object[] { sign });

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
