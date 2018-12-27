package com.shyl.msc.b2b.stl.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
 * 结算单
 */
@Entity
@Table(name = "t_stl_settlement")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Settlement extends Order {
	private static final long serialVersionUID = 1L;
	/**
	 * 状态
	 */
	public enum Status {
		unpay("未付款"),
		paying("付款中"),
		paid("已付款");
		private String name;
		private Status(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 账期起始日期
	 */
	private Date accBeginDate;
	/**
	 * 账期结束日期
	 */
	private Date accEndDate;
	/**
	 * 已付款金额
	 */
	private BigDecimal paidAmt;
	
	/**
	 * 状态
	 */
	private Status status;
	/**
	 * 结算明细
	 */
	@JsonIgnore
	private Set<SettlementDetail> settlementDetails = new HashSet<SettlementDetail>();
	/** 主键*/
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_stl_settlement_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getAccBeginDate() {
		return accBeginDate;
	}
	public void setAccBeginDate(Date accBeginDate) {
		this.accBeginDate = accBeginDate;
	}
	public Date getAccEndDate() {
		return accEndDate;
	}
	public void setAccEndDate(Date accEndDate) {
		this.accEndDate = accEndDate;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	@Transient
	public Set<SettlementDetail> getSettlementDetails() {
		return settlementDetails;
	}
	public void setSettlementDetails(Set<SettlementDetail> settlementDetails) {
		this.settlementDetails = settlementDetails;
	}
	public BigDecimal getPaidAmt() {
		return paidAmt;
	}
	public void setPaidAmt(BigDecimal paidAmt) {
		this.paidAmt = paidAmt;
	}

}