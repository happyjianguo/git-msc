package test;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class JmeterTest extends AbstractJavaSamplerClient {
	private String a;  
	private String b;  
	private String filename;  
	private SampleResult results;

	// 设置传入的参数，可以设置多个，已设置的参数会显示到Jmeter的参数列表中
	public Arguments getDefaultParameters() {
		System.out.println("********1*************");
		Arguments params = new Arguments();
		params.addArgument("filename", "0");//设置参数，并赋予默认值0  
		params.addArgument("a", "0");//设置参数，并赋予默认值0  
		params.addArgument("b", "0");//设置参数，并赋予默认值0  
		return params;
	}

	// 初始化方法，实际运行时每个线程仅执行一次，在测试方法运行前执行
	public void setupTest(JavaSamplerContext arg0) {
		System.out.println("********2*************");
		results = new SampleResult();
	}

	// 测试执行的循环体，根据线程数和循环次数的不同可执行多次
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {
		b = arg0.getParameter("b"); // 获取在Jmeter中设置的参数值  
		a = arg0.getParameter("a"); // 获取在Jmeter中设置的参数值  
		filename = arg0.getParameter("filename"); // 获取在Jmeter中设置的参数值  
		results.sampleStart();// jmeter 开始统计响应时间标记  
		try {
			System.out.println("********3*************");
			new GetToComProductClient().aaa();
			results.setSuccessful(true);  
		} catch (Exception e) {
			results.setSuccessful(false);  
		} finally {
			results.sampleEnd(); // 事务的终点
		}
		return results;
	}

	// 结束方法，实际运行时每个线程仅执行一次，在测试方法运行结束后执行
	public void teardownTest(JavaSamplerContext arg0) {
		System.out.println("********4*************");
	}
	
	public static void main(String[] args) {  
		Arguments params = new Arguments();  
		params.addArgument("a", "0");//设置参数，并赋予默认值0  
		params.addArgument("b", "0");//设置参数，并赋予默认值0  
        JavaSamplerContext arg0 = new JavaSamplerContext(params);  
        JmeterTest test = new JmeterTest();  
        test.setupTest(arg0);  
        test.runTest(arg0);  
        test.teardownTest(arg0);  
    }  
}