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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;

/**
 * 交易商品 
 * 
 */
@Entity
@Table(name = "t_dm_goods")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Goods extends BasicEntity {
	private static final long serialVersionUID = 4401916733979249605L;
	/**
	 * 产品
	 */
	private Product product;
	/**
	 * 药品编码
	 */
	private String productCode;
	/**
	 * 医院编码
	 */
	private String hospitalCode;
	/**
	 * 库存上限
	 */
	private Integer stockUpLimit = 0;
	/**
	 * 库存下限
	 */
	private Integer stockDownLimit = 0;
	/**
	 * 库存数量
	 */
	private BigDecimal stockNum = new BigDecimal(0);
	/**
	 * 库存金额
	 */
	private BigDecimal stockSum = new BigDecimal(0);
	/**
	 * 是否禁用(1是，0否)
	 */
	private Integer isDisabled =0;


	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_goods_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "productId",nullable=false)
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	@Column(length=50)
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	@Column(length=50)
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	public Integer getStockUpLimit() {
		return stockUpLimit;
	}
	public void setStockUpLimit(Integer stockUpLimit) {
		this.stockUpLimit = stockUpLimit;
	}
	public Integer getStockDownLimit() {
		return stockDownLimit;
	}
	public void setStockDownLimit(Integer stockDownLimit) {
		this.stockDownLimit = stockDownLimit;
	}
	@Column(precision=16, scale=0)
	public BigDecimal getStockNum() {
		return stockNum;
	}
	public void setStockNum(BigDecimal stockNum) {
		this.stockNum = stockNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getStockSum() {
		return stockSum;
	}
	public void setStockSum(BigDecimal stockSum) {
		this.stockSum = stockSum;
	}
	public Integer getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}	
	
	
}