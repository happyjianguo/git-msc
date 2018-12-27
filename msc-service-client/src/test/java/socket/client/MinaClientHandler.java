package socket.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSONObject;

/**
 * 客户端事件处理类
 * 
 * @author yan
 */
public class MinaClientHandler extends IoHandlerAdapter {

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		System.out.println("服务端关闭了连接");
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		//Message msg = (Message)message;
		//System.out.println("----"+msg.getMsg()+"-"+msg.getSuccess());
		System.out.println("客户端接收到的消息: " + JSONObject.toJSONString(message));
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("客户端连接上服务器");
	}
}