package com.shyl.msc.b2b.judge.entity;

import java.math.BigDecimal;
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
 * 供应商服务评价
 */
@Entity
@Table(name = "t_judge_serviceJ")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceJudge extends BasicEntity {
	private static final long serialVersionUID = 8999057192563600418L;
	/**
	 * 状态
	 */
	public enum Status {
		unaudit("未审核"),
		agree("同意"),
		disagree("不同意");
		private String name;
		private Status(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/** 代码*/
	private String code;
	/** 名称 */
	private String name;
	/** 描述 */
	private String describe;
	/** 扣分 **/
	private BigDecimal deduct;
	/** 审核日期 **/
	private Date auditDate;
	/** 审核人 **/
	private String auditor;
	/** 状态 */
	private Status status;
	


	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_judge_serviceJ_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=10)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(length=100)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(length=200)
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}

	@Column(precision=16, scale=2)
	public BigDecimal getDeduct() {
		return deduct;
	}
	
	public void setDeduct(BigDecimal deduct) {
		this.deduct = deduct;
	}
	public Date getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}
	@Column(length=100)
	public String getAuditor() {
		return auditor;
	}
	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	

}
