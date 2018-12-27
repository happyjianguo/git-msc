package com.shyl.msc.menu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BaseEntity;
import com.shyl.common.entity.BasicEntity;

/** 医疗器械 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_menu_medical_devices")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class MedicalDevices extends BaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 国家药品信息网ID
	 */
	private Integer extId;
	/**
	 * table id 26/27
	 */
	public Integer type;

	/**
	 * 注册证编号
	 */
	private String registCode;
	/**
	 * 注册人名称
	 */
	private String registName;
	/**
	 * 注册人住所
	 */
	private String registAddress;
	/**
	 * 生产地址
	 */
	private String producerAddress;
	/**
	 * 代理人名称
	 */
	private String agentName;
	/**
	 * 代理人住所
	 */
	private String agentAddress;
	/**
	 * 产品名称（中文）
	 */
	private String productName;
	/**
	 * 产品名称(英文)
	 */
	private String englishName;
	/**
	 * 型号、规格
	 */
	private String model;
	/**
	 * 结构和组成
	 */
	private String structure;
	/**
	 * 适用范围
	 */
	private String userRange;
	/**
	 * 售后服务机构
	 */
	private String afterSaleOrg;
	/**
	 * 生产国或地区（中文）
	 */
	private String areaName;
	/**
	 * 生产国或地区（英文）
	 */
	private String areaEName;
	/**
	 * 生产厂商名称（中文）
	 */
	private String producerName;
	/**
	 * 备注
	 */
	private String notes;
	/**
	 * 批准日期
	 */
	private String authorizeDate;
	/**
	 * 有效期至
	 */
	private String validExpiry;
	/**
	 * 产品标准
	 */
	private String productStandard;
	/**
	 * 变更日期
	 */
	private String chageDate;
	/**
	 * 主要组成成分（体外诊断试剂）
	 */
	private String mainContent;
	/**
	 * 预期用途（体外诊断试剂）
	 */
	private String expectUse;
	/**
	 * 产品储存条件及有效期（体外诊断试剂）
	 */
	private String storageCondition;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getExtId() {
		return extId;
	}

	public void setExtId(Integer extId) {
		this.extId = extId;
	}

	

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAreaEName() {
		return areaEName;
	}

	public void setAreaEName(String areaEName) {
		this.areaEName = areaEName;
	}

	public String getRegistCode() {
		return registCode;
	}

	public void setRegistCode(String registCode) {
		this.registCode = registCode;
	}

	public String getRegistName() {
		return registName;
	}

	public void setRegistName(String registName) {
		this.registName = registName;
	}
	
	@Column(length = 500)
	public String getRegistAddress() {
		return registAddress;
	}

	public void setRegistAddress(String registAddress) {
		this.registAddress = registAddress;
	}

	public String getProducerAddress() {
		return producerAddress;
	}

	public void setProducerAddress(String producerAddress) {
		this.producerAddress = producerAddress;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentAddress() {
		return agentAddress;
	}

	public void setAgentAddress(String agentAddress) {
		this.agentAddress = agentAddress;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	@Column(length = 500)
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Column(length = 2000)
	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	public String getUserRange() {
		return userRange;
	}

	public void setUserRange(String userRange) {
		this.userRange = userRange;
	}

	public String getAfterSaleOrg() {
		return afterSaleOrg;
	}

	public void setAfterSaleOrg(String afterSaleOrg) {
		this.afterSaleOrg = afterSaleOrg;
	}

	public String getProducerName() {
		return producerName;
	}

	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	@Column(length = 1500)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getAuthorizeDate() {
		return authorizeDate;
	}

	public void setAuthorizeDate(String authorizeDate) {
		this.authorizeDate = authorizeDate;
	}

	public String getValidExpiry() {
		return validExpiry;
	}

	public void setValidExpiry(String validExpiry) {
		this.validExpiry = validExpiry;
	}

	public String getProductStandard() {
		return productStandard;
	}

	public void setProductStandard(String productStandard) {
		this.productStandard = productStandard;
	}

	public String getChageDate() {
		return chageDate;
	}

	public void setChageDate(String chageDate) {
		this.chageDate = chageDate;
	}
	@Column(length = 500)
	public String getMainContent() {
		return mainContent;
	}

	public void setMainContent(String mainContent) {
		this.mainContent = mainContent;
	}

	public String getExpectUse() {
		return expectUse;
	}

	public void setExpectUse(String expectUse) {
		this.expectUse = expectUse;
	}

	public String getStorageCondition() {
		return storageCondition;
	}

	public void setStorageCondition(String storageCondition) {
		this.storageCondition = storageCondition;
	}
}