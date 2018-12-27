package com.shyl.msc.dm.entity;

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

/**
 * 药品备案明细
 * 
 */
@Entity
@Table(name = "t_dm_product_register_detail")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ProductRegisterDetail extends BasicEntity {
	private static final long serialVersionUID = 4401916733979249605L;
	/**
	 * 药品名称
	 */
	private String productName;
	/**
	 * 通用名
	 */
	private String genericName;
	/**
	 * 剂型名称
	 */
	private String dosageFormName;
	/**
	 * 规格
	 */
	private String model;
	/**
	 * 包装规格
	 */
	private String packDesc;
	/**
	 * 生产企业
	 */
	private String producerName;
	/**
	 * 国药准字
	 */
	private String authorizeNo;
	/**
	 * 药品备案
	 */
	private ProductRegister productRegister;
	
	/** 主键*/
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_product_register_d_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=100)
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	@Column(length = 100)
	public String getGenericName() {
		return genericName;
	}
	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}
	@Column(length = 50,nullable=false)
	public String getDosageFormName() {
		return dosageFormName;
	}
	public void setDosageFormName(String dosageFormName) {
		this.dosageFormName = dosageFormName;
	}
	@Column(length = 200,nullable=false)
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	@Column(length = 50,nullable=false)
	public String getPackDesc() {
		return packDesc;
	}
	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}
	@Column(length=200)
	public String getProducerName() {
		return producerName;
	}
	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	@Column(length = 100)
	public String getAuthorizeNo() {
		return authorizeNo;
	}
	public void setAuthorizeNo(String authorizeNo) {
		this.authorizeNo = authorizeNo;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="productRegisterId")
	public ProductRegister getProductRegister() {
		return productRegister;
	}
	public void setProductRegister(ProductRegister productRegister) {
		this.productRegister = productRegister;
	}
}