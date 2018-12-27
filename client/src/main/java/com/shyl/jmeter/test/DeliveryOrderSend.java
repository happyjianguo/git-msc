package com.shyl.jmeter.test;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shyl.jmeter.client.DeliveryOrderClient;

public class DeliveryOrderSend extends BaseJmeterClient implements JavaSamplerClient {

	// 测试执行的循环体，根据线程数和循环次数的不同可执行多次
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {
		iocode = arg0.getParameter("iocode"); // 获取在Jmeter中设置的参数值
		dataType = arg0.getParameter("dataType"); // 获取在Jmeter中设置的参数值
		data = arg0.getParameter("data"); // 获取在Jmeter中设置的参数值
		results.sampleStart();// jmeter 开始统计响应时间标记
		try {
			DeliveryOrderClient deliveryOrderClient = new DeliveryOrderClient();
			String result = deliveryOrderClient.send(iocode,dataType, data);
			System.out.println("---returnData---"+result);
			JSONObject jObject = JSON.parseObject(result);
			String success = jObject.getString("success");
			if(success.equals("true")){
				results.setSuccessful(true);
			}else{
				results.setSuccessful(false);
			}
			// 被测对象调用
		} catch (Throwable e) {
			results.setSuccessful(false);
			e.printStackTrace();
		} finally {
			results.sampleEnd();// jmeter 结束统计响应时间标记
		}
		return results;
	}

	public static void main(String[] args) {
		/*
		 * 查询订单信息来做配送
		select b.code as ddbh,b.hospitalcode as yybm,b.warehousecode as psdbm,
      	vendorcode as gysbm,vendorcode as psqybm,vendorcode as pssbm,productcode as ypbm,
			price as dj,100 as pssl,a.code as DDMXBH
			from t_order_purchaseorder_detail a,t_order_purchaseorder b
			where a.purchaseorderid=b.id and a.code like '%D2016101400000006%' order by a.code asc
		 */
		Arguments params = new Arguments();
		params.addArgument("iocode", "54");// 设置参数，并赋予默认值
		params.addArgument("dataType", "1");// 设置参数，并赋予默认值1
		params.addArgument("data", "");// 设置参数，并赋予默认值
		JavaSamplerContext arg0 = new JavaSamplerContext(params);
		DeliveryOrderSend test = new DeliveryOrderSend();
		test.setupTest(arg0);
		test.runTest(arg0);
		test.teardownTest(arg0);
	}

	
}