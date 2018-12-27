package com.shyl.msc.webService.impl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.springframework.transaction.annotation.Transactional;

import com.shyl.msc.webService.IStockWebService;

/**
 * 库存实现类
 * @author a_Q
 *
 */
@WebService(serviceName="stockWebService",portName="stockPort", targetNamespace="http://webservice.msc.shyl.com/")
public class StockWebService extends BaseWebService implements IStockWebService {

	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		// TODO Auto-generated method stub
		return null;
	}
}
