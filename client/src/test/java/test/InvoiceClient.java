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
 * GPO发送发票测试
 * @author a_Q
 *
 */
public class InvoiceClient {
	public static void main(String[] args){

		try {
			String wsdlUrl = "http://localhost:8080/msc-web/webService/invoice?wsdl";
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
			String no = "P02016062400000001"; 
			
			JSONObject jo = new JSONObject();
			jo.put("yybm", "455755610");		//医院编码
			jo.put("gysbm", "30000001");		//GPO编码
			jo.put("fph", "333999911");			//发票号
			jo.put("fprq", "2016-06-24");//发票日期
			jo.put("hszje", 34.9*99);	//含税总金额
			jo.put("bhszje", 34.9*99/1.05);	//含税总金额
			jo.put("sfch", "0");		//是否冲红
			jo.put("fpbz", "xxxx");			//发票备注
			jo.put("stdbh", no);			//发票备注
			jo.put("jls", "1");			//记录数
			JSONArray jo_arr = new JSONArray();
			JSONObject jod = new JSONObject();
			jod.put("sxh", "1");			//顺序号
			jod.put("ypbm", "78557");			//药品编码
			jod.put("spsl", 99);			//商品数量
			jod.put("hsdj", 34.9);		//含税单价
			jod.put("bhsdj", 34.9/1.05);		//含税单价
			jod.put("hsje", 34.9*99);		//含税单价
			jod.put("bhsje", 34.9*99/1.05);		//含税单价
			jod.put("stdmxbh", no+"-0001");		
			jod.put("sl", 5);			//税率
			jo_arr.add(jod);
			
			jo.put("mx", jo_arr);
			
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
