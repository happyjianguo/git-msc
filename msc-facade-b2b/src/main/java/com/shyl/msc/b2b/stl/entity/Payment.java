package com.shyl.msc.b2b.stl.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.msc.b2b.order.entity.Order;


/**
 * 付款单
 */
@Entity
@Table(name = "t_stl_payment")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Payment extends Order {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 付款方
	 */
	private String payer;
	/**
	 * 收款方
	 */
	private String reciever;
	/**
	 * 银行名称
	 */
	private String bankName;
	/**
	 * 支行
	 */
	private String bankBranch;
	/**
	 * 账号
	 */
	private String accNo;
	/**
	 * 经办人
	 */
	private String responsor;
	/**
	 * 付款时间
	 */
	private Date payDate;
	/**
	 * 付款方式
	 */
	private Integer payMethod;
	/**
	 * 结算单编号
	 */
	private String settlementCode;
	
	/** 主键*/
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_stl_payment_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=50)
	public String getPayer() {
		return payer;
	}
	public void setPayer(String payer) {
		this.payer = payer;
	}
	@Column(length=50)
	public String getReciever() {
		return reciever;
	}
	public void setReciever(String reciever) {
		this.reciever = reciever;
	}
	@Column(length=100)
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	@Column(length=100)
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	@Column(length=50)
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	@Column(length=50)
	public String getResponsor() {
		return responsor;
	}
	public void setResponsor(String responsor) {
		this.responsor = responsor;
	}
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}
	@Column(length=30)
	public String getSettlementCode() {
		return settlementCode;
	}
	public void setSettlementCode(String settlementCode) {
		this.settlementCode = settlementCode;
	}

}