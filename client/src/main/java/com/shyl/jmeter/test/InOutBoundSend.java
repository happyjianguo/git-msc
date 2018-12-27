package com.shyl.jmeter.test;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shyl.jmeter.client.InOutBoundClient;

public class InOutBoundSend extends BaseJmeterClient implements JavaSamplerClient {

	// 测试执行的循环体，根据线程数和循环次数的不同可执行多次
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {
		iocode = arg0.getParameter("iocode"); // 获取在Jmeter中设置的参数值
		dataType = arg0.getParameter("dataType"); // 获取在Jmeter中设置的参数值
		data = arg0.getParameter("data"); // 获取在Jmeter中设置的参数值
		yybm = arg0.getParameter("yybm"); // 获取在Jmeter中设置的参数值
		results.sampleStart();// jmeter 开始统计响应时间标记
		try {
			String result = new InOutBoundClient().send(iocode,dataType, data, yybm);
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
		select a.hospitalcode as yybm,a.warehousecode as psdbm,a.code as psdbh,
		b.productcode as ypbm,b.batchcode as scph,1 as crksl,b.code as PSDMXBH
		 from t_order_deliveryorder a,t_order_deliveryorder_detail b
		where a.id=b.deliveryorderid and a.code='P2016101400000002';
		 */
		Arguments params = new Arguments();
		params.addArgument("iocode", "");// 设置参数，并赋予默认值
		params.addArgument("dataType", "1");// 设置参数，并赋予默认值1
		params.addArgument("data", "");// 设置参数，并赋予默认值
		params.addArgument("yybm", "");// 设置参数，并赋予默认值
		JavaSamplerContext arg0 = new JavaSamplerContext(params);
		InOutBoundSend test = new InOutBoundSend();
		test.setupTest(arg0);
		test.runTest(arg0);
		test.teardownTest(arg0);
	}

	
}