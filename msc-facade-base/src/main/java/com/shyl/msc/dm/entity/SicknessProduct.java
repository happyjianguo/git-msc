/***********************************************************************
 * Module:  SicknessProduct.java
 * Author:  Administrator
 * Purpose: Defines the Class SicknessProduct
 ***********************************************************************/

package com.shyl.msc.dm.entity;

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
 * 疾病关联药品
 */
@Entity
@Table(name = "t_dm_sicknessproduct")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class SicknessProduct extends BasicEntity {
	private static final long serialVersionUID = 1L;	
	/**
	 * 疾病编码
	 */
	private String sicknessCode;
	/**
	 * 药品编码
	 */
	private String productCode;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_sicknessproduct_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=50)
	public String getSicknessCode() {
		return sicknessCode;
	}
	public void setSicknessCode(String sicknessCode) {
		this.sicknessCode = sicknessCode;
	}
	@Column(length=50)
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	
}