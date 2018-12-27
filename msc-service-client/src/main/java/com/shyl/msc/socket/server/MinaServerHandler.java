package com.shyl.msc.socket.server;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSONObject;
import com.shyl.sys.dto.Message;

/**
 * mina服务端的的事件处理类
 * 
 * @author a_Q
 */
public class MinaServerHandler extends IoHandlerAdapter {

    private static  Map<String,IoSession> ioSessions = Collections.synchronizedMap(new HashMap<String,IoSession>());

	private final static Log log = LogFactory.getLog(MinaServerHandler.class);
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		log.error("异常:"+cause);
		session.close(true);
	}
	
	/**
	 * 服务端接收消息
	 */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		log.info("Received:"+JSONObject.toJSONString(message));
		//TODO 
		Message msg = new Message();
		msg.setSuccess(true);
		msg.setMsg("成功返回");
		msg.setData("hello Y");
		session.write(JSONObject.toJSON(msg));
	}
	
	/**
	 * 客户端连接的会话创建
	 */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		InetSocketAddress isa = (InetSocketAddress) session.getRemoteAddress();
		log.info("客户端:" + isa.getAddress().getHostAddress() + ":"
				+ isa.getPort() + " 连接进来了");
		ioSessions.put(isa.getAddress().getHostAddress(), session);
	}

	/**
	 * 客户端关闭会话
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		InetSocketAddress isa = (InetSocketAddress) session.getRemoteAddress();
		log.info("客户端: " + isa + " 关闭了连接");
		ioSessions.remove(isa.getAddress().getHostAddress());
	}
	
	/**
	 * 向所有客户端发消息
	 * @param message
	 * @throws Exception
	 */
	public static void messageSentAll(Object message) throws Exception {
		// 对所有客户端发送消息
		log.info("Send:"+JSONObject.toJSONString(message));
		for (Iterator<String> it = ioSessions.keySet().iterator(); it.hasNext();) {
			IoSession ss = ioSessions.get(it.next());
			ss.write(message);
		}
    }
	
	/**
	 * 向单个客户端发消息
	 * @param key - 客户端IP
	 * @param message
	 * @throws Exception
	 */
	public static void messageSentSingle(String key, Object message) throws Exception {
		//向客户端发送的数据
		log.info("Send:"+JSONObject.toJSONString(message));
		IoSession ioSession = ioSessions.get(key);
		if(ioSession != null)
			ioSession.write(message);
    }
	
	/**
	 * 向单个客户端发消息
	 * @param ioSession - 
	 * @param message
	 * @throws Exception
	 */
	public static void messageSentSingle(IoSession ioSession, Object message) throws Exception {
		//向客户端发送的数据
		log.info("Send:"+JSONObject.toJSONString(message));
		ioSession.write(message);
    }

	/**
	 * 获得客户端会话
	 * @param key
	 * @return
	 */
	public static IoSession getIosession(String key){
		return ioSessions.get(key);
	}
}
