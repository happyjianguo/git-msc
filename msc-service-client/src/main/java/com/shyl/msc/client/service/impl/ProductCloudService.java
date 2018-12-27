package com.shyl.msc.client.service.impl;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shyl.common.entity.PageRequest;
import com.shyl.msc.client.service.IProductCloudService;
import com.shyl.msc.client.util.HttpClientPost;

@Service
public class ProductCloudService implements IProductCloudService {

	@Override
	public JSONObject queryCloudByPage(PageRequest page) {
		Map<String, Object> query = page.getQuery();
		if (query == null) {
			query = new HashMap<String, Object>();
		}
		String productName ="";
		String producerName ="";
		if (query.containsKey("productName")) {
			productName = (String)query.get("productName");
		}
		if (query.containsKey("producerName")) {
			producerName = (String)query.get("producerName");
		}
		String[] parameters = {productName, producerName};
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("username", "chuangye");
		jsonObj.put("password", "P14GlUWCIn09VVkFK5AS");
		jsonObj.put("action", "001");
		jsonObj.put("pageindex", page.getPage());
		jsonObj.put("pagesize", page.getPageSize());
		jsonObj.put("parameters", parameters);
		
		String json = "strJson="+jsonObj.toJSONString();
		
		try {
			//调用云平台参数
			json = HttpClientPost.doPost("http://119.57.170.171:9399/OpenService.asmx/GetData", 
					json.getBytes("UTF-8"), "application/x-www-form-urlencoded; charset=utf-8");
			if (json.indexOf("{")<0) {
				return null;
			}
			if (json.indexOf("\"ROWCOUNT\":\"") <= 0) {
				json = json.replaceAll("\"ROWCOUNT\":","\"ROWCOUNT\":\"");
			}
			if (json.indexOf("{")>0&& json.lastIndexOf("}")>0) {
				//处理数据
				json = json.substring(json.indexOf("{"),json.lastIndexOf("}")+1);
			}
			System.out.println(json);
			json = json.replaceAll("\\\\\"", "\"");
			//格式化
			JSONObject ret = JSONObject.parseObject(json);
			ret.put("total", ret.get("ROWTOTAL"));
			if (ret.containsKey("Table")) {
				ret.put("rows", ret.get("Table"));
			} else {
				ret.put("rows", new JSONArray());
			}
			ret.put("footer", "[]");
			ret.remove("Table");
			ret.remove("ROWTOTAL");
			ret.remove("ROWCOUNT");
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}

}
