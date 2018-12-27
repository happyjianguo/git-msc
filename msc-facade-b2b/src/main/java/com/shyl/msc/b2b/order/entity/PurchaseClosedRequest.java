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

/**
 * 采购订单结案申请
 * 
 * 
 */
@Entity
@Table(name = "t_order_purchase_c_request")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PurchaseClosedRequest extends Order {
	private static final long serialVersionUID = -76076139021650607L;
	
	/**
	 * 状态
	 */
	public enum Status {
		unaudit("未审核"),
		agree("同意结案"),
		disagree("不同意结案");
		private String name;
		private Status(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	public enum ClosedType {
		orderClosed("订单结案"),
		orderDetailClosed("订单明细结案");
		private String name;
		private ClosedType(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 采购订单编号
	 */
	private String purchaseOrderCode;
	/**
	 * 结案申请人
	 */
	private String closedMan;
	/**
	 * 结案申请时间
	 */
	private Date closedRequestDate;
	/**
	 * 状态
	 */
	private Status status;
	/**
	 * 原因
	 */
	private String reason;
	/**
	 * 答复
	 */
	private String reply;
	/**
	 * 结案类型
	 */
	private ClosedType closedType;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_purchase_c_request_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=30)
	public String getPurchaseOrderCode() {
		return purchaseOrderCode;
	}
	public void setPurchaseOrderCode(String purchaseOrderCode) {
		this.purchaseOrderCode = purchaseOrderCode;
	}
	@Column(length=60)
	public String getClosedMan() {
		return closedMan;
	}
	public void setClosedMan(String closedMan) {
		this.closedMan = closedMan;
	}
	public Date getClosedRequestDate() {
		return closedRequestDate;
	}
	public void setClosedRequestDate(Date closedRequestDate) {
		this.closedRequestDate = closedRequestDate;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	@Column(length=300)
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	@Column(length=300)
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	public ClosedType getClosedType() {
		return closedType;
	}
	public void setClosedType(ClosedType closedType) {
		this.closedType = closedType;
	}
}