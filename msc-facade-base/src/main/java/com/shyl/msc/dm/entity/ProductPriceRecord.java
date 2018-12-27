package com.shyl.msc.dm.entity;

import java.math.BigDecimal;
import java.util.Date;

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
 * 产品价格记录
 * 
 */
@Entity
@Table(name = "t_dm_product_price_record")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ProductPriceRecord extends BasicEntity {
	private static final long serialVersionUID = 4401916733979249605L;
	/**
	 * 类型
	 */
	public enum Type {
		gpo("gpo"),
		notgpo("非gpo");
		private String name;
		private Type(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	
	/**
	 * 产品编码
	 */
	private String productCode;
	/**
	 * 产品名称
	 */
	private String productName;
	/**
	 * gpo编码
	 */
	private String gpoCode;
	/**
	 * gpo名称
	 */
	private String gpoName;
	/**
	 * 供应商编码
	 */
	private String vendorCode;
	/**
	 * 供应商名称
	 */
	private String vendorName;
	/**
	 * 成交价
	 */
	private BigDecimal finalPrice;
	/**
	 * 上次成交价
	 */
	private BigDecimal lastPrice;
	/**
	 * 价格异动次数
	 */
	private Integer priceCount = 1;
	/**
	 * 价格变动时间
	 */
	private Date changeDate ;
	/**
	 * 类型
	 */
	private Type type;
	
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_product_price_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=50)
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	@Column(length=100)
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	@Column(length=20)
	public String getGpoCode() {
		return gpoCode;
	}
	public void setGpoCode(String gpoCode) {
		this.gpoCode = gpoCode;
	}
	@Column(length=100)
	public String getGpoName() {
		return gpoName;
	}
	public void setGpoName(String gpoName) {
		this.gpoName = gpoName;
	}	
	@Column(length=20)
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	@Column(length=100)
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getFinalPrice() {
		return finalPrice;
	}
	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}
	public Date getChangeDate() {
		return changeDate;
	}
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public BigDecimal getLastPrice() {
		return lastPrice;
	}
	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}
	public Integer getPriceCount() {
		return priceCount;
	}
	public void setPriceCount(Integer priceCount) {
		this.priceCount = priceCount;
	}
	
}