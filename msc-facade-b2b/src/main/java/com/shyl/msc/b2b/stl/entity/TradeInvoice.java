package com.shyl.msc.b2b.stl.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.msc.b2b.order.entity.Order;


/**
 * 交易发票
 */
@Entity
@Table(name = "t_stl_tradeinvoice")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TradeInvoice extends Order {
	private static final long serialVersionUID = 1L;
	/**
	 * 发票类型
	 */
	public enum Type {
		producerToGPO("厂家到GPO"),
		GPOToVendor("GPO到供应商"),
		producerToVendor("厂家到供应商"),
		vendorToHospital("供应商到医院"),
		producerToHospital("厂家到医院");
		private String name;
		private Type(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 客户编码
	 */
	private String customerCode;
	/**
	 * 客户名称
	 */
	private String customerName;
	/**
	 * 不含税金额
	 */
	private BigDecimal noTaxSum;
	/**
	 * 是否冲红(1是，0否)
	 */
	private Integer isRed;
	/**
	 * 备注
	 */
	private String remarks;
	/**
	 * 类型
	 */
	private Type type;
	/**
	 * 上层发票号
	 */
	private String parentTradeInvoiceCode;
	/**
	 * 发票明细
	 */
	@JsonIgnore
	private Set<TradeInvoiceDetail> tradeInvoiceDetails = new HashSet<TradeInvoiceDetail>();
	/** 主键*/
	
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_stl_tradeInvoice_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=20)
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	@Column(length=200)
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getNoTaxSum() {
		return noTaxSum;
	}
	public void setNoTaxSum(BigDecimal noTaxSum) {
		this.noTaxSum = noTaxSum;
	}
	public Integer getIsRed() {
		return isRed;
	}
	public void setIsRed(Integer isRed) {
		this.isRed = isRed;
	}
	@Column(length=100)
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	@Transient
	public Set<TradeInvoiceDetail> getTradeInvoiceDetails() {
		return tradeInvoiceDetails;
	}
	public void setTradeInvoiceDetails(Set<TradeInvoiceDetail> tradeInvoiceDetails) {
		this.tradeInvoiceDetails = tradeInvoiceDetails;
	}

	@Column(length = 30)
	public String getParentTradeInvoiceCode() {
		return parentTradeInvoiceCode;
	}
	public void setParentTradeInvoiceCode(String parentTradeInvoiceCode) {
		this.parentTradeInvoiceCode = parentTradeInvoiceCode;
	}
	
}