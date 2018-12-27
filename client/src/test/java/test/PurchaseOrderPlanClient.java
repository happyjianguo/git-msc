package test;

import java.rmi.RemoteException;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisProperties;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * 医院发送订单测试
 * @author a_Q
 *
 */
public class PurchaseOrderPlanClient {
	public static void main(String[] args){

			
			String sign = "";
			String data = "";
			String dataType = "1";
			String iocode = "97780314";


			for(int i = 1;i<=10;i++) {
				String jhdh = String.valueOf(new Date().getTime());
				JSONObject jo = new JSONObject();
				jo.put("ptdm", "SZ");			//医院编码455755442   //PDY310384
				jo.put("yybm", "455849246");			//医院编码455755442   //PDY310384
				jo.put("psdbm", "45584924601");			//配送点编码
				jo.put("yqphsj", "2017-11-30 11:22:33");//要求配货日期
				jo.put("jjcd", "1");			//紧急程度
				jo.put("cgjhdbh", jhdh);
				jo.put("jhlx", 1);			//多次配送标识
				jo.put("dcpsbs", 0);			//多次配送标识
				jo.put("gysbm", "13461");

				jo.put("jls", 1);				//记录数
				JSONArray jo_arr = new JSONArray();
				JSONObject jod = new JSONObject();
				/*jod.put("sxh", "1");			//顺序号
				jod.put("cgjhdbh", jhdh+"-0001");
				jod.put("gysbm", "13461");
				jod.put("cgdj", 34.9);
				jod.put("ypbm", "100592");							//药品编码
				jod.put("ypmc", "黄氏响声丸");							//药品编码
				jod.put("cgsl", 1);			//采购数量
				jod.put("bzsm", "马上需要");	//备注说明
				jo_arr.add(jod);
				jod = new JSONObject();*/
				jod.put("sxh", "1");			//顺序号
				jod.put("cgjhdbh", jhdh+"-0002");
				jod.put("gysbm", "13461");
				jod.put("cgdj", 34.9);
				jod.put("ypbm", "100104"); //药品编码
				jod.put("ypmc", "急支糖浆"); //药品编码
				jod.put("cgsl", 1);			//采购数量
				jod.put("bzsm", "马上需要");	//备注说明
				jo_arr.add(jod);

				jo.put("mx", jo_arr);

				data = JSON.toJSONString(jo);
				System.out.println("data===="+data);
				sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
				Thread thread = new Thread(sign, dataType, data);
				thread.run();
			}

	}
}

class Thread implements Runnable {
	private String sign;
	private String dataType;
	private String data;
	public Thread(String sign, String dataType, String data) {
		this.sign = sign;
		this.dataType=dataType;
		this.data=data;
	}
	public  void run() {
		try {
			//AxisProperties.setProperty("axis.socketSecureFactory", "test.MySocketFactory");
			String wsdlUrl = "http://localhost/msc-webservice-b2b/webService/purchaseOrderPlan?wsdl";
			String nameSpaceUri = "http://webservice.msc.shyl.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);

			call.setOperationName(new QName(nameSpaceUri, "send"));
			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.setReturnClass(String.class);
			String result = (String) call.invoke(new Object[] {sign, dataType, data});
			System.out.println(result);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}