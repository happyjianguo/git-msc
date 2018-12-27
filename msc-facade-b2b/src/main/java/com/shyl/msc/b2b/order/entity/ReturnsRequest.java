package com.shyl.msc.b2b.order.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 退货申请单 */
@Entity
@Table(name = "t_order_returnsrequest")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ReturnsRequest extends Order {
	private static final long serialVersionUID = 9156179523013703761L;
	/**
	 * 状态
	 */
	public enum Status {
		unaudit("未审核"),
		agree("同意退货"),
		disagree("不同意退货");
		private String name;
		private Status(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 退货发起人
	 */
	private String returnsMan;
	/**
	 * 退货发起时间
	 */
	private Date returnsBeginDate;
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
	 * 退货单明细
	 */
	@JsonIgnore
	private Set<ReturnsRequestDetail> returnsRequestDetails = new HashSet<ReturnsRequestDetail>();
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_returnsrequest_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=20)
	public String getReturnsMan() {
		return returnsMan;
	}
	public void setReturnsMan(String returnsMan) {
		this.returnsMan = returnsMan;
	}
	public Date getReturnsBeginDate() {
		return returnsBeginDate;
	}
	public void setReturnsBeginDate(Date returnsBeginDate) {
		this.returnsBeginDate = returnsBeginDate;
	}
	@Transient	
	public Set<ReturnsRequestDetail> getReturnsRequestDetails() {
		return returnsRequestDetails;
	}
	public void setReturnsRequestDetails(
			Set<ReturnsRequestDetail> returnsRequestDetails) {
		this.returnsRequestDetails = returnsRequestDetails;
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
	

}