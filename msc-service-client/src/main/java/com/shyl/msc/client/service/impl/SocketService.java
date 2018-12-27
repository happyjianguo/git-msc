package com.shyl.msc.client.service.impl;

import org.springframework.stereotype.Service;

import com.shyl.msc.client.service.ISocketService;
import com.shyl.msc.socket.server.MinaServerHandler;

@Service
public class SocketService implements ISocketService {

	@Override
	public void messageSentAll(Object message) throws Exception {
		MinaServerHandler.messageSentAll(message);
	}

	@Override
	public void messageSentSingle(String key, Object message) throws Exception {
		MinaServerHandler.messageSentSingle(key, message);
	}

}
