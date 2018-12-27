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
public class GoodsHospitalClient {
	public static void main(String[] args){

		try {
			String wsdlUrl = "http://localhost:8080/msc-web/webService/goods?wsdl";
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
			String iocode = "5";
			JSONObject jo = new JSONObject();
			jo.put("yybm", "455755610");
			jo.put("jls", "1");
			JSONArray jo_arr = new JSONArray();
			JSONObject jod = new JSONObject();
			jod.put("sxh", "1");
			jod.put("yyypbm", "YY78557");	
			jod.put("ypmc", "消结安胶囊");
			jod.put("tym", "1000");	
			jod.put("jxmc", "银谷制药有限责任公司");
			jod.put("scqymc", "云南良方制药有限公司");
			jod.put("gg", "0.38g");
			jod.put("bzdw", "盒");
			jod.put("bzgg", "24粒/盒");
			jod.put("ypbm", "78557");
			jod.put("sfjy", "0");
			
			jo_arr.add(jod);
			jo.put("mx", jo_arr);
			data = JSON.toJSONString(jo);
			sign = SHA1.getMessageDigest(iocode + data, "SHA-1");
			
			
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
