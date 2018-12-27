package com.shyl.msc.b2b.order.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;
/**
 * 报文 
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_order_datagram")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Datagram extends BasicEntity{
	private static final long serialVersionUID = -3434239894330200398L;
	public enum DatagramType {		
		/** 药品基本信息下载(医院) */
		product_getToHis,	
		/** 药品基本信息下载 (GPO)*/
		product_getToCom,	
		/** 配送点下载 */
		warehouse_getToCom,	
		/** 价格上传 */
		productPrice_send,	
		/** 订单计划上传 */
		purchaseOrderPlan_send,	
		/** 订单计划下载 */
		purchaseOrderPlan_get,		
		/** 订单计划反馈上传 */
		purchaseOrderPlan_fedback,	
		/** 订单上传 */
		purchaseOrder_send,
		/** 配送单上传 */
		deliveryOrder_send,	
		/** 配送单下载 */
		deliveryOrder_get,
		/** 配送单清单下载 */
		deliveryOrder_getList,
		/** 退货单上传 */
		returnsOrder_send,	
		/** 入库单上传 */
		inoutbound_send,	
		/** 入库单下载 */
		inoutbound_get,		
		/** 发票上传*/
		invoice_send,		
		/** 库存上传*/
		hospitalStock_send,	
		/**药品映射关系信息上传*/
		goodsHospital_send,
		/** 结算单上传*/
		settlement_send,
		/** 付款单上传*/
		payment_send,
		/** 药品基本信息下载 (患者)*/
		product_getToPe,
		/** 药品价格信息下载(医院) */
		goodsPrice_getToHis,
		/** 退货申请单下载*/
		returnsRequest_get,
		/** 退货申请单反馈上传*/
		returnsRequest_fedback,
		/** 退货申请单上传*/
		returnsRequest_send,
		/** 结案申请单下载*/
		purchaserClosedRequest_get,
		/** 结案申请单反馈上传*/
		purchaserClosedRequest_fedback,
		/** 结案申请单上传*/
		purchaserClosedRequest_send,
		/** 三方合同下载 */
		contract_get,
		/** 三方合同签章回传 */
		contract_fedback,
		/** 三方合同结案申请下载 */
		contractClosedRequest_get,
		/** 三方合同结案申请反馈上传 */
		contractClosedRequest_fedback,
		
		orderMsg_get,
		/** 医院报量下载 */
		hospitalPlan_get,
		/** 圣格灵的消息传递 */
		sglSigned_send,
		/** 供应商库存上传 */
		vendorStock_send,
		/** 根据日期获取药品信息 */
		product_getAll,
		/** 药品映射关系信息下载*/
		goodsHospital_get,
		/** 订单计划取消 */
		purchaseOrderPlan_cancel,
		/** 根据条件药品信息下载 */
		product_get,
		/** 发票上传*/
		invoice_get,
		/** 企业信息下载*/
		company_get,
		/** 订单计划医院取消项下载 */
		purchaseOrderPlan_getCancel,
		/** 供应商药品供应关系*/
		productDetail_get,
		/** 交易发票上传*/
		tradeInvoice_send,
		/**单据发票上传*/
		attachmentUpload_send,
		/**  对照关系查询 **/
		goodsHospital_getAll,
		/** 医院信息下载*/
		hospital_get
	}
	/**
	 * 报文类型
	 */
	private DatagramType datagramType;
	/**
	 * 发送日期
	 */
	private Date sendDate;
	/**
	 * 发送方编码
	 */
	private String senderCode;
	/**
	 * 发送方名称
	 */
	private String senderName;
	/**
	 * ip地址
	 */
	private String ip;
	/**
	 * 数据格式
	 */
	private Integer dataType;
	/**
	 * 数据
	 */
	private String data;
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_datagram_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public DatagramType getDatagramType() {
		return datagramType;
	}
	public void setDatagramType(DatagramType datagramType) {
		this.datagramType = datagramType;
	}
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	@Column(length=50)
	public String getSenderCode() {
		return senderCode;
	}
	public void setSenderCode(String senderCode) {
		this.senderCode = senderCode;
	}
	@Column(length=100)
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	@Column(length=50)
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getDataType() {
		return dataType;
	}
	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}
	@Column(length=200000)
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	
}
