package com.shyl.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.util.CSVUtil;
import com.shyl.util.SHA1;
/**
 * GPO上传退货单
 * @author a_Q
 *
 */
public class SendReturnsOrderClient {
	public static void main(String[] args){

		try {
			
			String wsdlUrl = "http://localhost:8080/msc-web/webService/returnsOrder?wsdl";
			String nameSpaceUri = "http://webservice.msc.shyl.com/";

			Service service = new Service();

			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(wsdlUrl);
			call.setOperationName(new QName(nameSpaceUri, "send"));

			call.addParameter("sign", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.addParameter("dataType", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.addParameter("data", org.apache.axis.Constants.XSD_STRING, ParameterMode.IN);
			call.setReturnClass(String.class);

			String sign = "";
			String dataType = "1";
			String data = "";
			String iocode = "";
			Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();
			List<Map<String, Object>> lineList = CSVUtil.csvToList("E:/data/test/ReturnsOrder.csv");
			for(Map<String, Object> m:lineList){
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				String key = m.get("yybm")+"-"+m.get("psdbm")+"-"+m.get("gpobm")
						+"-"+m.get("psqybm")+"-"+m.get("thsj")+"-"+m.get("ddbh");
				if(map.get(key)!=null){
					list = map.get(key);
					list.add(m);
				}else{
					list.add(m);
				}
				map.put(key, list);
			}
			
			for(String key: map.keySet()){
				JSONObject jo = null;
				JSONArray jo_arr = new JSONArray();
				List<Map<String, Object>> line = map.get(key);
				for(Map<String, Object> m:line){
					if(jo == null){
						iocode = m.get("iocode").toString();
						jo = new JSONObject();
						jo.put("yybm", m.get("yybm"));		//医院编码
						jo.put("psdbm", m.get("psdbm"));	//配送点编码
						jo.put("gpobm", m.get("gpobm"));	//GPO编码
						jo.put("psqybm", m.get("psqybm"));	//配送企业编码
						jo.put("thsj", m.get("thsj"));		//退货日期
						jo.put("ddbh", m.get("ddbh"));		//订单编号
						jo.put("thfqr", m.get("thfqr"));		//退货发起人
						jo.put("thfqsj", m.get("thfqsj"));		//退货发起时间
						jo.put("jls", line.size());			
					}
					JSONObject jod = new JSONObject();		
					jod.put("sxh", m.get("sxh"));			//顺序号
					jod.put("ypbm", m.get("ypbm"));			//药品编码
					jod.put("scph", m.get("scph"));			//生产批号
					jod.put("thsl", m.get("thsl"));			//退货数量
					jod.put("thdj", m.get("thdj"));			//退货单价
					jod.put("thyy", m.get("thyy"));			//退货原因
					jod.put("ddmxbh", m.get("ddmxbh"));		//订单明细编号
					jod.put("thsj", m.get("thfqsj"));		//退货发起时间
					jod.put("scrq", m.get("scrq"));		//生产日期
					jod.put("yxrq", m.get("yxrq"));		//生产日期
					jo_arr.add(jod);
				}
				jo.put("mx", jo_arr);
				data = JSON.toJSONString(jo);
				System.out.println("data===="+data);
				sign = SHA1.getMessageDigest(iocode+data, "SHA-1");
				String result = (String) call.invoke(new Object[] { sign, dataType, data });
				System.out.println(result);
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
