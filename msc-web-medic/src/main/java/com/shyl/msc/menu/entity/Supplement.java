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

/** 药品补充信息（包装） */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_menu_supplement")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Supplement extends BaseEntity {

	
	public String productName;

	public String model;
	
	public String packDesc;
	
	public String producerName;
	
	public String authorizeNo;
	
	public Integer extId;

	public String getProductName() {
		return productName;
	}

	public String getModel() {
		return model;
	}

	public String getPackDesc() {
		return packDesc;
	}

	public String getProducerName() {
		return producerName;
	}

	public String getAuthorizeNo() {
		return authorizeNo;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}

	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}

	public void setAuthorizeNo(String authorizeNo) {
		this.authorizeNo = authorizeNo;
	}
	public Integer getExtId() {
		return extId;
	}

	public void setExtId(Integer extId) {
		this.extId = extId;
	}

}