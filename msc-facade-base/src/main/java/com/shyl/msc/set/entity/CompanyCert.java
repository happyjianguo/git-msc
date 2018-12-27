package com.shyl.msc.set.entity;

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

/** 企业 证照*/
@Entity
@Table(name = "t_set_company_cert")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompanyCert extends BasicEntity {
	private static final long serialVersionUID = -7385223160198861529L;
	
	/**
	 * 证照公司
	 */
	private Company company;
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
	
	/** 主键*/	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_company_cert_s")
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
	

}