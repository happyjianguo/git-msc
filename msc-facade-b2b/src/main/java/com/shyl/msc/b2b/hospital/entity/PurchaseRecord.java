package com.shyl.msc.b2b.hospital.entity;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;
import com.shyl.msc.dm.entity.Product;

/**
 * 采购记录上报
 * 
 *
 */
@Entity
@Table(name = "t_hospital_purchaserecord")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class PurchaseRecord extends BasicEntity {
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
	 * 药交产品Id
	 */
	private String productTranId;
	/**
	 * 采购平台
	 */
	private String platform;
	/**
	 * 采购数量
	 */
	private BigDecimal num;
	/**
	 * 采购金额
	 */
	private BigDecimal amt;

	@Id
	@SequenceGenerator(name = "generator", sequenceName = "t_hospital_purchaserecord_s")
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "productId", nullable = false)
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Column(length = 100)
	public String getProductTranId() {
		return productTranId;
	}

	public void setProductTranId(String productTranId) {
		this.productTranId = productTranId;
	}

	@Column(length = 10)
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	@Column(precision = 16, scale = 3)
	public BigDecimal getNum() {
		return num;
	}

	public void setNum(BigDecimal num) {
		this.num = num;
	}

	@Column(precision = 16, scale = 3)
	public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

}
