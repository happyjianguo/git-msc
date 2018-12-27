package com.shyl.msc.client.service;

public interface ISocketService {

	/**
	 * 向所有客户端发消息
	 * @param message
	 * @throws Exception
	 */
	public void messageSentAll(Object message) throws Exception;
	
	/**
	 * 向单个客户端发消息
	 * @param key
	 * @param message
	 * @throws Exception
	 */
	public void messageSentSingle(String key, Object message) throws Exception;
}
