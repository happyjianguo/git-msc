/***********************************************************************
 * Module:  RegulationOrg.java
 * Author:  Administrator
 * Purpose: Defines the Class RegulationOrg
 ***********************************************************************/

package com.shyl.msc.set.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.shyl.common.entity.BasicEntity;

/** 监管机构 */
@Entity
@Table(name = "t_set_regulationorg")
public class RegulationOrg extends BasicEntity {
	private static final long serialVersionUID = 5727756375186900750L;
	/**
	 * 编码
	 */
	private String code;
	/**
	 * 单位名称
	 */
	private String fullName;
	/**
	 * 单位简称
	 */
	private String shortName;
	/**
	 * 拼音简称
	 */
	private String pinyin;
	/**
	 * 五笔简称
	 */
	private String wbcode;
	/**
	 * 所在地区
	 */
	private Long regionCode;
	/**
	 * 机构隶属关系
	 */
	private Integer reportType;
	/**
	 * 归档文件序号
	 */
	private String archNo;
	/**
	 * 通讯地址
	 */
	private String postAddr;
	/**
	 * 邮政编码
	 */
	private String postCode;
	/**
	 * 电话号码
	 */
	private String telephone;
	/**
	 * 传真
	 */
	private String fax;
	/**
	 * 电子邮箱
	 */
	private String email;
	/**
	 * 网址
	 */
	private String website;
	/**
	 * 备 注
	 */
	private String notes;
	/**
	 * 是否禁用(1是，0否)
	 */
	private Integer isDisabled;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_regulationorg_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=20,unique = true, nullable = false,updatable = false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(length=100)
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	@Column(length=50)
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	@Column(length=50)
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	@Column(length=50)
	public String getWbcode() {
		return wbcode;
	}
	public void setWbcode(String wbcode) {
		this.wbcode = wbcode;
	}
	public Long getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(Long regionCode) {
		this.regionCode = regionCode;
	}
	public Integer getReportType() {
		return reportType;
	}
	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}
	@Column(length=20)
	public String getArchNo() {
		return archNo;
	}
	public void setArchNo(String archNo) {
		this.archNo = archNo;
	}
	@Column(length=50)
	public String getPostAddr() {
		return postAddr;
	}
	public void setPostAddr(String postAddr) {
		this.postAddr = postAddr;
	}
	@Column(length=20)
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	@Column(length=20)
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	@Column(length=20)
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	@Column(length=50)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Column(length=50)
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	@Column(length=100)
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Integer getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}


}