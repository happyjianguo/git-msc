package com.shyl.util;

import java.rmi.RemoteException;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

public class ServiceUtil {

	/**
	 * 访问WebService接口
	 * @param wsdlUrl
	 * @param targetNamespace
	 * @param methodName - 访问方法名
	 * @param data - 所传参数
	 * @return
	 * @throws ServiceException
	 * @throws RemoteException
	 */
	public static String callWebService(String wsdlUrl, String targetNamespace, String methodName, String[] data) throws ServiceException, RemoteException{
		Service service = new Service();

		Call call = (Call) service.createCall();
		call.setTargetEndpointAddress(wsdlUrl);
		call.setOperationName(new QName(targetNamespace, methodName));

		call.addParameter("sign", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
		call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
		call.addParameter("data", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
		call.setReturnClass(String.class);
		call.setTimeout(600000);
		String result = (String) call.invoke(data);
		return result;
	}
}
