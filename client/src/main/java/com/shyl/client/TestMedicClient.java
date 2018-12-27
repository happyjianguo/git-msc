package com.shyl.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.SHA1;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestMedicClient{
	public String send(List<Map<String, Object>> mapList) {
		try{

			String wsdlUrl = "http://117.78.48.251/testmedicshop/webservice/goods?wsdl";
			wsdlUrl = "http://127.0.0.1:8080/medic/webservice/goods?wsdl";
			String nameSpaceUri = "http://webS、ervice.medicshop.shyl.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);

			call.setOperationName(new QName(nameSpaceUri, "send"));
			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.setReturnClass(String.class);

			String sign = "";
			String data = "";
			String dataType = "1";
			String iocode = "";
			String result = "";
			JSONObject js = new JSONObject();
			js.put("ptdm","01");
			js.put("zbbm","002001");
			JSONArray array = new JSONArray();
			/*for(Map<String,Object> map : mapList){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ypbm",map.get("code"));
				jsonObject.put("ypmc",map.get("name"));
				jsonObject.put("tym",map.get("genericName"));
				jsonObject.put("pinyin",map.get("pinyin"));
				jsonObject.put("dw",map.get("unit"));
				jsonObject.put("gg",map.get("model"));
				jsonObject.put("jxmc",map.get("dosageFormName"));
				jsonObject.put("bzgg",map.get("packDesc"));
				jsonObject.put("sccj",map.get("producerName"));
				jsonObject.put("cd",map.get("original"));
				jsonObject.put("gyzz",map.get("authorizeNo"));
				jsonObject.put("bzm",map.get("standardCode"));
				jsonObject.put("txm",map.get("barcode"));
				jsonObject.put("cclx",map.get("storageType"));
				array.add(jsonObject);
			}*/
			js.put("ypmx",array);
//			data = js.toJSONString();
			data = "{\"ptdm\":\"01\",\"zbbm\":\"002\",\"ypmx\":[{\"ypbm\":\"77777777777\",\"ypmc\":\"理会12222111\",\"tym\":\"理会\",\"dw\":\"g\",\"gg\":\"g/5\"}]}";
			sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
			result = (String) call.invoke(new Object[] { sign, dataType, data });
			System.out.println(result);
			return result;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}


	public static void main(String[] args) {
		TestMedicClient t = new TestMedicClient();
		t.send(new ArrayList<Map<String, Object>>());
	}
}
