package com.shyl.msc.b2b.stock.entity;

import java.math.BigDecimal;

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
 * 供应商药品库存
 */
@Entity
@Table(name = "t_stock_vendor_stock_day")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VendorStockDay extends BasicEntity {
	private static final long serialVersionUID = 8999057192563600418L;
	/** 日期 */
	private String stockDate;
	/** 供应商代码 */
	private String vendorCode;
	/** 供应商名称 */
	private String vendorName;
	/** 产品Code **/
	private String productCode;
	/** 产品名称 */
	private String productName;
	/** 起初库存数量 */
	private BigDecimal beginNum;
	/** 起初库存金额 */
	private BigDecimal beginAmt;
	/** 期末库存数量 */
	private BigDecimal endNum;
	/** 期末库存金额 */
	private BigDecimal endAmt;
	/** 报文id*/
	private Long datagramId;


	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_vendor_stock_day_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=10)
	public String getStockDate() {
		return stockDate;
	}
	public void setStockDate(String stockDate) {
		this.stockDate = stockDate;
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
	@Column(length=100)
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	@Column(length=50)
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getBeginNum() {
		return beginNum;
	}
	public void setBeginNum(BigDecimal beginNum) {
		this.beginNum = beginNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getBeginAmt() {
		return beginAmt;
	}
	public void setBeginAmt(BigDecimal beginAmt) {
		this.beginAmt = beginAmt;
	}
	@Column(precision=16, scale=3)
	public BigDecimal getEndNum() {
		return endNum;
	}
	public void setEndNum(BigDecimal endNum) {
		this.endNum = endNum;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getEndAmt() {
		return endAmt;
	}
	public void setEndAmt(BigDecimal endAmt) {
		this.endAmt = endAmt;
	}
	public Long getDatagramId() {
		return datagramId;
	}
	public void setDatagramId(Long datagramId) {
		this.datagramId = datagramId;
	}

}
