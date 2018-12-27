package com.shyl.msc.set.entity;

import java.util.Date;

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
import com.shyl.common.entity.BasicEntity;

/** 企业 证照注册申请*/
@Entity
@Table(name = "t_set_company_cert_reg")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompanyCertReg extends BasicEntity {
	private static final long serialVersionUID = -7385223160198861529L;
	public enum AuditStatus {
		create("待发送"),
		send("审核中"),
		pass("审核通过"),
		back("已退回");
		
		private String name;
		private AuditStatus(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	
	/**
	 * 证照公司
	 */
	private Company company;
	/**
	 * 申报公司
	 */
	private Company declarant;
	/** 
	 * 证照类型(attribute)
	 */
	private String typeCode;
	/** 
	 * 证照类型名称
	 */
	private String typeName;
	/**
	 * 证照代码
	 */
	private String code;
	/**
	 * 证照名称
	 */
	private String name;
	/**
	 * 发证日期
	 */
	private String issueDate;
	/**
	 * 发证部门
	 */
	private String dept;
	/**
	 * 证照状态
	 */
	private Integer Status;
	/**
	 * 证照图片路径
	 */
	private String imagePath;
	/**
	 * 证照范围
	 */
	private String scope;
	/**
	 * 证照有效期截止
	 */
	private String validDate;
	/**
	 * 备注
	 */
	private String note;
	/**
	 * 审核状态
	 */
	private AuditStatus auditStatus;
	/**
	 * 发送时间
	 */
	private Date sendTime;
	/**
	 * 审核时间
	 */
	private Date auditTime;
	/**
	 * 审核意见
	 */
	private String auditNote;
	
	/** 主键*/	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_company_cert_reg_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="companyId")
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="declarantId")
	public Company getDeclarant() {
		return declarant;
	}
	public void setDeclarant(Company declarant) {
		this.declarant = declarant;
	}
	@Column(length=10)
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	@Column(length=100)
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	@Column(length=50,nullable = false,updatable = false)
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
	@Column(length=20)
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	@Column(length=50)
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public Integer getStatus() {
		return Status;
	}
	public void setStatus(Integer status) {
		Status = status;
	}
	@Column(length=100)
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	@Column(length=100)
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	@Column(length=20)
	public String getValidDate() {
		return validDate;
	}
	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}
	@Column(length=200)
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public AuditStatus getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public Date getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}
	@Column(length=200)
	public String getAuditNote() {
		return auditNote;
	}
	public void setAuditNote(String auditNote) {
		this.auditNote = auditNote;
	}
	

}