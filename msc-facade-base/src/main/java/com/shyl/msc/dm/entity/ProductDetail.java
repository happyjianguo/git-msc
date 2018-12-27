package com.shyl.msc.dm.entity;

import java.math.BigDecimal;

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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;

/**
 * 
 * 医院选定药品
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_dm_product_detail")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductDetail extends BasicEntity {
	private static final long serialVersionUID = -1236398719015974891L;

	/**
	 *  遴选目录
	 */
	private Product product;
	/**
	 *  供应商编码
	 */
	private String vendorCode;
	/**
	 *  供应商名称
	 */
	private String vendorName;
	/**
	 * 医院编码
	 */
	private String hospitalCode;
	/**
	 * 医院名称
	 */
	private String hospitalName;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 数量（临时属性）
	 */
	private String num;
	
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_product_detail_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="productId")
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	@Column(length = 20)
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	@Column(length = 100)
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	@Column(length = 50)
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	@Column(length = 100)
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	@Transient
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
}
