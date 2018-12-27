/***********************************************************************
 * Module:  PrescriptProduct.java
 * Author:  Administrator
 * Purpose: Defines the Class PrescriptProduct
 ***********************************************************************/

package com.shyl.msc.dm.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;


/**
 * 外配处方药品
 */
@Entity
@Table(name = "t_dm_prescriptproduct")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class PrescriptProduct extends BasicEntity {
	private static final long serialVersionUID = 1L;	
	/**
	 * 药品编码
	 */
	private Product product;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_prescriptproduct_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "productId",nullable=false)
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}

	
}