package com.shyl.msc.dm.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;

/**
 * 药品备案
 * 
 */
@Entity
@Table(name = "t_dm_product_register")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ProductRegister extends BasicEntity {
	private static final long serialVersionUID = 4401916733979249605L;
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
	
	public enum OrgType {
		hospital,
		vendor
	}
	/**
	 * 机构类型
	 */
	private OrgType orgType;
	/**
	 * 机构id
	 */
	private Long orgId;
	/**
	 * 机构名称
	 */
	private String orgName;
	/**
	 * 审核日期
	 */
	private Date auditDate;
	/**
	 * 审核人
	 */
	private String auditor;
	/**
	 * 审核意见
	 */
	private String suggestion;
	/**
	 * 状态
	 */
	private Status status;

	@JsonIgnore
	private Set<ProductRegisterDetail> productRegisterDetails = new HashSet<ProductRegisterDetail>();
	
	/** 主键*/
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_product_register_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public OrgType getOrgType() {
		return orgType;
	}
	public void setOrgType(OrgType orgType) {
		this.orgType = orgType;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	@Column(length=100)
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public Date getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}
	@Column(length=50)
	public String getAuditor() {
		return auditor;
	}
	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}
	@Column(length=200)
	public String getSuggestion() {
		return suggestion;
	}
	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}
	@OneToMany(mappedBy="productRegister", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	public Set<ProductRegisterDetail> getProductRegisterDetails() {
		return productRegisterDetails;
	}
	public void setProductRegisterDetails(Set<ProductRegisterDetail> productRegisterDetails) {
		this.productRegisterDetails = productRegisterDetails;
	}
}