package com.shyl.jmeter.test;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class BaseJmeterClient {

	public SampleResult results;
	public String iocode;
	public String dataType;
	public String data;
	public String yybm;

	// 设置传入的参数，可以设置多个，已设置的参数会显示到Jmeter的参数列表中
	public Arguments getDefaultParameters() {
		Arguments params = new Arguments();
		params.addArgument("iocode", "");// 设置参数，并赋予默认值空
		params.addArgument("dataType", "1");// 设置参数，并赋予默认值1
		params.addArgument("data", "");// 设置参数，并赋予默认值空
		params.addArgument("yybm", "");// 设置参数，并赋予默认值空
		return params;
	}

	// 初始化方法，实际运行时每个线程仅执行一次，在测试方法运行前执行
	public void setupTest(JavaSamplerContext arg0) {
		results = new SampleResult();
	}
	

	// 结束方法，实际运行时每个线程仅执行一次，在测试方法运行结束后执行
	public void teardownTest(JavaSamplerContext arg0) {
	}
}
