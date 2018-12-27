package test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class FastJson {
	 public static void main(String[] args) {  
		 	try {
				JSONObject jo = new JSONObject();
				Integer i = 0;
				jo.put("aaa", i);
				jo.put("bbb", "3232ss");
				jo.put("ccc", "");
				jo.put("detail", "222");
				String data = jo.toJSONString();
				
				JSONObject j = JSON.parseObject(data);
				System.out.println("----"+j.getString("xxx"));
				
				//String detail = jo.getString("detail");//��ϸ
				//List<JSONObject> list = JSON.parseArray(detail, JSONObject.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }  
}
