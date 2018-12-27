package com.shyl.msc.b2b.stl.entity;

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
 * 结算明细
 */
@Entity
@Table(name = "t_stl_settlement_detail")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class SettlementDetail extends BasicEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * 结算单
	 */
	private Settlement settlement;
	/**
	 * 编号
	 */
	private String code;	
	/**
	 * 结算金额
	 */
	private BigDecimal sum;
	/**
	 * 发票编号
	 */
	private String invoiceCode;
	
	/** 主键*/	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_stl_settlement_d_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="settlementId")
	public Settlement getSettlement() {
		return settlement;
	}
	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}
	@Column(length=30)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getSum() {
		return sum;
	}
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	@Column(length=30)
	public String getInvoiceCode() {
		return invoiceCode;
	}
	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}
	
}