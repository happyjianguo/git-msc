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
 * GPO订单计划反馈上传
 * @author a_Q
 *
 */
public class PurchaseOrderPlanFedBackClient {
	public static void main(String[] args){

		try {
			String wsdlUrl = "http://localhost:8080/msc-web/webService/purchaseOrderPlan?wsdl";
			String nameSpaceUri = "http://webservice.msc.shyl.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);

			call.setOperationName(new QName(nameSpaceUri, "fedback"));
			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING,ParameterMode.IN);
			call.setReturnClass(String.class);
			
			String sign = "";
			String dataType = "1";
			String data = "";
			String ddjhbh = "X02016062400000001";
			
			JSONObject jo = new JSONObject();
			jo.put("gysbm", "30000001");		//GPO编码
			jo.put("ddjhbh", ddjhbh);			//订单编号
			jo.put("zt", 0);
			jo.put("jls", 3);

			JSONArray jo_arr = new JSONArray();
			JSONObject jod = new JSONObject();
			jod.put("sxh", 1);			//顺序号
			jod.put("ddjhmxbh", "X02016062400000001-0001");		//订单计划明细编号
			jod.put("cljg", 0);			//处理结果
			jod.put("bz", "11111xxx");		//备注说明
			jo_arr.add(jod);
			
			jo.put("mx", jo_arr);
			data = JSON.toJSONString(jo);
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
