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
 * 医院发送入库单测试
 * @author a_Q
 *
 */
public class InOutBoundClient {
	public static void main(String[] args){

		try {
			String wsdlUrl = "http://localhost:8080/msc-web/webService/inOutBound?wsdl";
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
			String dataType="1";
			String data = "";
			String iocode="5";
			String no = "P02016062400000001"; 
			JSONObject jo = new JSONObject();
			jo.put("yybm", "455755610");					//医院编码
			jo.put("psdbm", "01");					//配送点编码
			jo.put("rkdbh", no);	//入库单
			jo.put("psdbh", no);	//配送单编号
			
			jo.put("czsj", "2016-03-26 00:00:00");			//操作日期
			jo.put("czr", "小黑");			//操作人
			jo.put("jls", "1");						//记录数
			JSONArray jo_arr = new JSONArray();
			JSONObject jod = new JSONObject();
			jod.put("sxh", "1");					//顺序号
			jod.put("ypbm", "78557");					//药品编码
			jod.put("crksl", 99);				//出入库数量
			jod.put("psdmxbh", no+"-0001");//配送单明细编号
			jod.put("rkdmxbh", no+"-0001");//配送单明细编号
			jo_arr.add(jod);
			jo.put("mx", jo_arr);
			
			data = JSON.toJSONString(jo);
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
