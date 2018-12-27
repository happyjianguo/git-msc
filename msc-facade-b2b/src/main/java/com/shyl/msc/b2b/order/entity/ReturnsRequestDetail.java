package com.shyl.msc.b2b.order.entity;


import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 退货申请单明细 */
@Entity
@Table(name = "t_order_returnsrequest_detail")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReturnsRequestDetail extends OrderDetail {
	private static final long serialVersionUID = 8866547909888950360L;

	/**
	 * 退货申请单
	 */
	private ReturnsRequest returnsRequest;
	/**
	 * 生产批号
	 */
	private String batchCode;
	/**
	 * 生产日期
	 */
	private String batchDate;
	/**
	 * 有效日期
	 */
	private String expiryDate;
	/**
	 * 退货原因
	 */
	private String reason;
	/**
	 * 答复退货数量
	 */
	private BigDecimal replyNum;
	/**
	 * 答复明细
	 */
	private String reply;
	/**
	 *  配送单明细编号
	 */
	private String deliveryOrderDetailCode;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_returnsrequest_d_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="returnsRequestId")	
	public ReturnsRequest getReturnsRequest() {
		return returnsRequest;
	}
	public void setReturnsRequest(ReturnsRequest returnsRequest) {
		this.returnsRequest = returnsRequest;
	}
	@Column(length=100)
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	@Column(length=10)
	public String getBatchDate() {
		return batchDate;
	}
	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}
	@Column(length=10)
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	@Column(length=300)
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getReplyNum() {
		return replyNum;
	}
	public void setReplyNum(BigDecimal replyNum) {
		this.replyNum = replyNum;
	}
	@Column(length=300)
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	@Column(length=30)
	public String getDeliveryOrderDetailCode() {
		return deliveryOrderDetailCode;
	}
	public void setDeliveryOrderDetailCode(String deliveryOrderDetailCode) {
		this.deliveryOrderDetailCode = deliveryOrderDetailCode;
	}
	
}