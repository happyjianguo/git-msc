package test.pe.client;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.SHA1;

public class PrescriptSave2 {
	public static void main(String[] args){
		try {
			
			String wsdlUrl = "http://115.236.19.147:48080/msc-webservice-pe/webService/prescript?wsdl";
			//String wsdlUrl = "http://localhost:8080/msc-webservice-pe/webService/prescript?wsdl";
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
			String iocode = "02894471";
			JSONObject js = new JSONObject();
			
			js.put("yybm", "NJ00001");
			js.put("ycfdh", "");
			js.put("yycfh", "AK2999999");
			js.put("jzsj", "2016-08-02 10:30:00");
			js.put("kfsj", "2016-08-02 12:30:22");
			js.put("hzdh", "1");
			js.put("ysxm", "陈刚");
			js.put("jzks", "消化内科");
			js.put("zdsm", "长期胃痛致中度胃病");
			js.put("zs", "该病人长期胃痛");
			js.put("zzms", "一会痛一会不痛");
			js.put("bz", "");
			js.put("psfs", "0");
			//js.put("psdz", "浙江省宁波市北仑区华庭公寓203");
			js.put("psdbm", "NJ00001-01");
			js.put("sjly", "0");
			
			JSONArray array = new JSONArray();
			JSONObject mx = new JSONObject();
			mx.put("sxh", "1");
			mx.put("ypbm", "100006");
			mx.put("sl", "2");
			mx.put("dj", "5.5");
			mx.put("pc", "一日三次，每次1片");
			mx.put("yf", "饭后服用");
			array.add(mx);
			
			mx = new JSONObject();
			mx.put("sxh", "2");
			mx.put("ypbm", "100016");
			mx.put("sl", "2");
			mx.put("dj", "10.5");
			mx.put("pc", "一日三次，每次2片");
			mx.put("yf", "饭后服用");
			array.add(mx);
			
			mx = new JSONObject();
			mx.put("sxh", "3");
			mx.put("ypbm", "100026");
			mx.put("sl", "1");
			mx.put("dj", "10.5");
			mx.put("pc", "一日三次，每次3片");
			mx.put("yf", "饭后服用");
			array.add(mx);
			
			js.put("mx", array);
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
