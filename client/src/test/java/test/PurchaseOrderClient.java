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
 * GPO上传订单
 * @author a_Q
 *
 */
public class PurchaseOrderClient {
	public static void main(String[] args){

		try {
			//String wsdlUrl = "http://localhost:8080/msc-web/webService/purchaseOrder?wsdl";
			String wsdlUrl = "http://localhost:8080/msc-web/webService/purchaseOrder?wsdl";
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
			String data = "";
			
			JSONObject jo = new JSONObject();
			jo.put("ddjhbh", "X02016062400000001");//订单计划编号
			jo.put("yqddbh", "02016062400000001");	//药企订单编号
			jo.put("gysbm", "30000001");				//供应商编码
			jo.put("psqybm", "30000001");			//配送企业编码
			jo.put("yybm", "455755610");			//医院编码
			jo.put("psdbm", "01");					//配送点编码
			jo.put("yqphsj", "2016-06-28 11:22:33");//要求配货日期
			jo.put("jjcd", "1");					//紧急程度
			jo.put("jylx", 1);					
			jo.put("dcpsbs", 0);					//多次配送标识
			
			
			jo.put("jls", 1);						//记录数
			JSONArray jo_arr = new JSONArray();
			JSONObject jod = new JSONObject();
			jod.put("sxh", "1");							//顺序号
			jod.put("ddjhmxbh", "X02016062400000001-0001");	//订单计划明细编号
			jod.put("yqddmxbh", "02016062400000001-0001");	//药企订单明细编号
			jod.put("ypbm", "78557");							//药品编码
			jod.put("cgsl", 100);							//采购数量
			jod.put("cgdj", 34.9);							//采购单价
			jod.put("bzsm", "好的马上送到");						//备注说明
			jo_arr.add(jod);
			
			jo.put("mx", jo_arr);
			
			data = JSON.toJSONString(jo);
			System.out.println("data===="+data);
			sign = SHA1.getMessageDigest("1"+data, "SHA-1");
			
			
			String result = (String) call.invoke(new Object[] { sign,"1",data });

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
