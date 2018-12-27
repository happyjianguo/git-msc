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
 * 医院发送订单测试
 * @author a_Q
 *
 */
public class GetDeliveryOrderClient {
	public static void main(String[] args){

		try {	
			String wsdlUrl = "http://6.6.0.28:8082/msc-web/webService/deliveryOrder?wsdl";

			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);
			call.setOperationName(new QName("http://webservice.msc.shyl.com/", "get"));
			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.setReturnClass(String.class);
			
			String sign = "";
			String dataType = "";
			String data = "";
			
			//医院编码为455755610，对应iocode为59296069
			String yybm = "455755610";
			String iocode = "59296069";
			
			//json形式
			dataType = "1";
			JSONObject jo = new JSONObject();
			jo.put("yybm", yybm);	//医院编码
			jo.put("psdtxm", "1111");	//配送单条形码
			jo.put("cxkssj", "2016-01-12 00:00:00");//查询开始时间
			jo.put("cxjssj", "2016-07-12 00:00:00");//查询结束时间	
			data = JSON.toJSONString(jo);
			
			//xml形式
			dataType = "2";
			data = "<data>"
					+ "<yybm type='string'>"+yybm+"</yybm>"
					+ "<psdtxm type='string'>1111</psdtxm>"
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
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

	}
}
