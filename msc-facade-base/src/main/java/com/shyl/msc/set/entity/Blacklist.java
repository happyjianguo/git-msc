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

/** 企业黑名单 */
@Entity
@Table(name = "t_set_blacklist")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Blacklist extends BasicEntity {
	private static final long serialVersionUID = -7385223160198861529L;
	/**
	 * 企业
	 */
	private Company company;
	/**
	 * 加入时间
	 */
	private Date joinDate;
	/**
	 * 加入原因
	 */
	private String joinReason;
	/**
	 * 解除原因
	 */
	private String disabledReason;
	/**
	 * 是否禁用（1是，0否）
	 */
	private Integer isDisabled = 1;
	
	/** 主键*/	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_blacklist_s")
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
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	@Column(length=200)
	public String getJoinReason() {
		return joinReason;
	}
	public void setJoinReason(String joinReason) {
		this.joinReason = joinReason;
	}
	@Column(length=200)
	public String getDisabledReason() {
		return disabledReason;
	}
	public void setDisabledReason(String disabledReason) {
		this.disabledReason = disabledReason;
	}
	public Integer getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}
	

}