package com.shyl.msc.b2b.plan.entity;

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
 * 三方合同结案申请
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_plan_contract_closedrequest")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ContractClosedRequest extends BasicEntity{
	private static final long serialVersionUID = -3434239894330200398L;
	/**
	 * 状态
	 */
	public enum Status {
		unaudit("未审核"),
		agree("同意终止"),
		disagree("不同意终止");
		private String name;
		private Status(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 结案对象
	 */
	public enum ClosedObject {
		contract("合同"),
		contractDetail("合同明细");
		private String name;
		private ClosedObject(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 编号
	 */
	private String code;
	/**
	 * 结案对象
	 */
	private ClosedObject closedObject;
	/**
	 * 合同编号
	 */
	private String contractCode;
	/**
	 * 合同明细编号
	 */
	private String contractDetailCode;
	/**
	 * 结案申请人
	 */
	private String closedMan;
	/**
	 * 结案申请时间
	 */
	private Date closedRequestDate;
	/**
	 * 审核人
	 */
	private String auditor;
	/**
	 * 审核时间
	 */
	private Date auditDate;

	/**
	 * 原因
	 */
	private String reason;
	/**
	 * 答复
	 */
	private String reply;
	/**
	 * 状态
	 */
	private Status status;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_contract_closedrequest_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=30)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}	
	public ClosedObject getClosedObject() {
		return closedObject;
	}
	public void setClosedObject(ClosedObject closedObject) {
		this.closedObject = closedObject;
	}
	@Column(length=30)
	public String getContractCode() {
		return contractCode;
	}
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	@Column(length=30)
	public String getContractDetailCode() {
		return contractDetailCode;
	}
	public void setContractDetailCode(String contractDetailCode) {
		this.contractDetailCode = contractDetailCode;
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
	@Column(length=60)
	public String getAuditor() {
		return auditor;
	}
	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}
	public Date getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
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
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
}
