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
 * 医院发送入库单测试
 * @author a_Q
 *
 */
public class GetInOutBoundClient {
	public static void main(String[] args){

		try {
			String wsdlUrl = "http://218.18.233.228:8082/msc-web/webService/inOutBound?wsdl";

			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);
			call.setOperationName(new QName("http://webservice.msc.shyl.com/", "get"));
			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.setReturnClass(String.class);
			
			//GPO编码为30000001，对应iocode为59296069
			String gpobm = "30000001";
			String iocode = "1";
			
			String sign = "";
			String dataType="";
			String data = "";
			
			//json格式
			dataType="1";
			JSONObject jo = new JSONObject();
			jo.put("gpobm", gpobm);					//GPO编码
			jo.put("cxkssj", "2016-01-01 20:29:20");		//查询开始时间
			jo.put("cxjssj", "2016-12-01 20:29:20");	    //查询结束时间	
			data = JSON.toJSONString(jo);
			
			//xml形式
			dataType = "2";
			data = "<data>"
					+ "<gpobm type='string'>"+gpobm+"</yybm>"
					+ "<cxkssj type='string'>2016-01-12 00:00:00</cxjsrq>"
					+ "<cxjssj type='string'>2016-07-12 00:00:00</cxksrq>"
					+ "</data>";
			//加密数据
			sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
			//调用接口并返回结果
			String result = (String) call.invoke(new Object[] {sign,dataType,data});
			//打印返回结果			
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
