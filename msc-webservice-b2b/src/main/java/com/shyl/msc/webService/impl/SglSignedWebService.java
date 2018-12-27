package com.shyl.msc.webService.impl;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.shyl.common.util.GridFSDAO;
import com.shyl.msc.b2b.order.entity.Datagram.DatagramType;
import com.shyl.msc.b2b.plan.entity.Contract;
import com.shyl.msc.b2b.plan.service.IContractService;
import com.shyl.sys.entity.AttributeItem;
import com.shyl.sys.service.IAttributeItemService;
import com.shyl.msc.webService.ISglSignedWebService;
import com.shyl.sys.dto.Message;


/**
 * sgl签章实现类
 * @author a_Q
 *
 */
@WebService(serviceName="sglSignedWebService",portName="sglSignedPort", targetNamespace="http://webservice.msc.shyl.com/")
public class SglSignedWebService extends BaseWebService implements ISglSignedWebService {

	@Resource
	IContractService contractService;
	@Resource
	private GridFSDAO gridFSDAO;
	@Resource
	private IAttributeItemService attributeItemService;
	@Resource
	private CuratorFramework curatorFramework;
	
	@Override
	@WebMethod(action="send")
	@WebResult(name="sendResult")
	public String send(@WebParam(name="sign")String sign, @WebParam(name="dataType")String dataType, @WebParam(name="data")String data) {
		Message message = new Message();
		try {
			//第一步检查数据类型是否合法
			message = checkDataType(DatagramType.sglSigned_send, sign, dataType, data);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第二步验证data数据是否合法
			JSONObject converData = getConverData(dataType, data);
			message = checkData(converData);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第三步验证签名
			AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("publicUser", "SGLIOCODE");
			message = checkSign(attributeItem.getField3(), data, sign);
			if(!message.getSuccess()){
				return resultMessage(message, dataType);
			}
			//第五步执行主逻辑
			message = sendMethod(converData);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(false);
			message.setMsg("服务器出错9");
			message.setData("");
		}
		return resultMessage(message, dataType);
	}

	private Message sendMethod(JSONObject converData) {
		Message message = new Message();
		message.setSuccess(false);
		//将base64编码的字符串解码成字节数组
		String contractCode = converData.getString("keyId");
		Contract contract = contractService.findByCode("", contractCode);
		String wj = converData.getString("wj");	//文件
        byte[] bytes = Base64.decodeBase64(wj.getBytes());
        String path = gridFSDAO.saveFile(bytes, UUID.randomUUID().toString()+".pdf", "contract");
		contract.setHospitalSealPath(path);
		contract.setFilePath(path);
		contract.setHospitalConfirmDate(new Date());
		contract.setGpoConfirmDate(new Date());
		contract.setEffectiveDate(new Date());
		contract.setStatus(Contract.Status.hospitalSigned);
		contract.setModifyDate(new Date());
		contractService.update("", contract);
		System.out.println("接收完成path＝"+path);
		message.setMsg("成功返回");
		message.setSuccess(true);
		//触发zk的监听事件
		setData(contract.getId());
		return message;
	}

	@Async
	public void setData(Long id){
		try {
			Stat stat = curatorFramework.checkExists().forPath("/CONTRACT/"+id);
			if(stat != null){
				curatorFramework.setData().forPath("/CONTRACT/"+id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Message checkData(JSONObject converData) {
		AttributeItem attributeItem = attributeItemService.queryByAttrAndItemNo("publicUser", "PLATFORM_NO");
		Message message = new Message();
		message.setSuccess(false);
		String wj = converData.getString("wj");	//文件
		if(StringUtils.isEmpty(wj)){
			message.setMsg("文件不能为空");
			return message;
		}
		String contractCode = converData.getString("keyId");
		if(StringUtils.isEmpty(contractCode)){
			message.setMsg("合同编号不能为空");
			return message;
		}
		Contract contract = contractService.findByCode(attributeItem.getField3(), contractCode);
		if(contract == null){
			message.setMsg("合同编号有误");
			return message;
		}
		message.setSuccess(true);
		return message;
	}
}
