package com.shyl.msc.b2b.hospital.entity;

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
import com.shyl.msc.dm.entity.Product;
/**
 * 供应药品上报
 * 
 *
 */
@Entity
@Table(name = "t_hospital_supplyproduct")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SupplyProduct extends BasicEntity{
	private static final long serialVersionUID = -3434239894330200398L;
	
	/**
	 * 年月
	 */
	private String month;
	/**
	 * 医院编码
	 */
	private String hospitalCode;
	/**
	 * 医院名称
	 */
	private String hospitalName;
	/**
	 * 药品
	 */
	private Product product;
	/**
	 * 供应商编码
	 */
	private String vendorCode;
	/**
	 * 供应商名称
	 */
	private String vendorName;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_hospital_supplyproduct_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	@Column(length=50)
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	@Column(length=100)
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "productId",nullable=false)
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	@Column(length=20)
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	@Column(length=200)
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	
}
