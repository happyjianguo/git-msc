package com.shyl.msc.menu.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BaseEntity;
import com.shyl.common.entity.BasicEntity;

/** 抓取药品数据 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_menu_product_data")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ProductData extends BaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String productName;
	
	public String englishName;
	
	private String tradeName;
	
	public String authorizeNo;
	
	public String oldAuthorizeNo;
	
	public String splitAuthorizeNo;
	
	public String producerName;
	
	public String producerEName;
	
	public String dosageFormName;
	
	public String model;
	
	public String splitProducerName;
	
	public String packDesc;
	
	public Integer type;
	
	public Integer extId;
	
	public String drugType;
	
	public String standardCode;

	public String getAuthorizeNo() {
		return authorizeNo;
	}
	public String getOldAuthorizeNo() {
		return oldAuthorizeNo;
	}
	public String getSplitAuthorizeNo() {
		return splitAuthorizeNo;
	}
	public String getProducerName() {
		return producerName;
	}
	public String getProducerEName() {
		return producerEName;
	}
	public String getProductName() {
		return productName;
	}
	public String getDosageFormName() {
		return dosageFormName;
	}
	public String getModel() {
		return model;
	}
	public String getSplitProducerName() {
		return splitProducerName;
	}
	public String getPackDesc() {
		return packDesc;
	}
	public String getStandardCode() {
		return standardCode;
	}
	public void setAuthorizeNo(String authorizeNo) {
		this.authorizeNo = authorizeNo;
	}
	public void setOldAuthorizeNo(String oldAuthorizeNo) {
		this.oldAuthorizeNo = oldAuthorizeNo;
	}
	public void setSplitAuthorizeNo(String splitAuthorizeNo) {
		this.splitAuthorizeNo = splitAuthorizeNo;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	public void setProducerEName(String producerEName) {
		this.producerEName = producerEName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public void setDosageFormName(String dosageFormName) {
		this.dosageFormName = dosageFormName;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public void setSplitProducerName(String splitProducerName) {
		this.splitProducerName = splitProducerName;
	}
	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}
	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}

	public String getEnglishName() {
		return englishName;
	}

	public Integer getType() {
		return type;
	}

	public Integer getExtId() {
		return extId;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setExtId(Integer extId) {
		this.extId = extId;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
		this.drugType = drugType;
	}

}