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
public class ProductPriceClient {
	public static void main(String[] args){

		try {
			String wsdlUrl = "http://localhost:8080/msc-web/webService/productPrice?wsdl";
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
			
			JSONObject jo = new JSONObject();
			jo.put("gysbm", "30000001");		//GPO编码
			jo.put("jls", "2");			//记录数
			JSONArray jo_arr = new JSONArray();
			JSONObject jod = new JSONObject();
			jod.put("sxh", "1");		//顺序号
			jod.put("ypbm", "78557");//药品编码
			jod.put("yybm", "455755610");
			jod.put("psqybm", "30000001");//配送企业编码
			jod.put("zbj", 34.9);		//中标价
			jod.put("cjj", 34.9);		//成交价
			jod.put("sfjy", 0);			//是否禁用
			jod.put("yxqq", "2016-01-01");
			jod.put("yxqz", "2016-12-31");
			jod.put("jylx", 1);
			
			jo_arr.add(jod);
			jo.put("mx", jo_arr);
			
			data = "{\"gysbm\":\"30000001\",\"jls\":24,\"mx\":[{\"sxh\":4988,\"ypbm\":\"113240\",\"yybm\":null,\"psqybm\":null,\"zbj\":55.0,\"cjj\":55.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":4989,\"ypbm\":\"91191\",\"yybm\":null,\"psqybm\":null,\"zbj\":56.0,\"cjj\":56.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":4990,\"ypbm\":\"66699\",\"yybm\":null,\"psqybm\":null,\"zbj\":57.0,\"cjj\":57.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":4991,\"ypbm\":\"124371\",\"yybm\":null,\"psqybm\":null,\"zbj\":58.0,\"cjj\":58.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":4992,\"ypbm\":\"53525\",\"yybm\":null,\"psqybm\":null,\"zbj\":59.0,\"cjj\":59.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":4993,\"ypbm\":\"136133\",\"yybm\":null,\"psqybm\":null,\"zbj\":60.0,\"cjj\":60.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":4994,\"ypbm\":\"71475\",\"yybm\":null,\"psqybm\":null,\"zbj\":61.0,\"cjj\":61.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":4995,\"ypbm\":\"83916\",\"yybm\":null,\"psqybm\":null,\"zbj\":62.0,\"cjj\":62.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":4996,\"ypbm\":\"51909\",\"yybm\":null,\"psqybm\":null,\"zbj\":63.0,\"cjj\":63.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":4997,\"ypbm\":\"66157\",\"yybm\":null,\"psqybm\":null,\"zbj\":64.0,\"cjj\":64.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":4998,\"ypbm\":\"50159\",\"yybm\":null,\"psqybm\":null,\"zbj\":65.0,\"cjj\":65.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":4999,\"ypbm\":\"83594\",\"yybm\":null,\"psqybm\":null,\"zbj\":66.0,\"cjj\":66.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5000,\"ypbm\":\"78532\",\"yybm\":null,\"psqybm\":null,\"zbj\":67.0,\"cjj\":67.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5001,\"ypbm\":\"90592\",\"yybm\":null,\"psqybm\":null,\"zbj\":68.0,\"cjj\":68.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5002,\"ypbm\":\"92231\",\"yybm\":null,\"psqybm\":null,\"zbj\":69.0,\"cjj\":69.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5003,\"ypbm\":\"94853\",\"yybm\":null,\"psqybm\":null,\"zbj\":70.0,\"cjj\":70.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5004,\"ypbm\":\"70607\",\"yybm\":null,\"psqybm\":null,\"zbj\":71.0,\"cjj\":71.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5005,\"ypbm\":\"61218\",\"yybm\":null,\"psqybm\":null,\"zbj\":72.0,\"cjj\":72.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5006,\"ypbm\":\"140889\",\"yybm\":null,\"psqybm\":null,\"zbj\":73.0,\"cjj\":73.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5007,\"ypbm\":\"19798\",\"yybm\":null,\"psqybm\":null,\"zbj\":74.0,\"cjj\":74.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5008,\"ypbm\":\"51081\",\"yybm\":null,\"psqybm\":null,\"zbj\":75.0,\"cjj\":75.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5009,\"ypbm\":\"118465\",\"yybm\":null,\"psqybm\":null,\"zbj\":76.0,\"cjj\":76.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5010,\"ypbm\":\"69533\",\"yybm\":null,\"psqybm\":null,\"zbj\":77.0,\"cjj\":77.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1},{\"sxh\":5011,\"ypbm\":\"129540\",\"yybm\":null,\"psqybm\":null,\"zbj\":78.0,\"cjj\":78.0,\"yxqq\":\"2016-06-25\",\"yxqz\":\"2017-06-25\",\"sfjy\":0,\"jylx\":1}]}";
			//data = JSON.toJSONString(jo);
			System.out.println("data====="+data);
			sign = SHA1.getMessageDigest("1"+data, "SHA-1");
			

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
