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
 * 发票
 */
@Entity
@Table(name = "t_stl_invoice")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Invoice extends Order {
	private static final long serialVersionUID = 1L;
	/**
	 * 状态
	 */
	public enum Status {
		unsettle("未结算"),
		settle("已结算"),
		cancel("作废");
		private String name;

		Status(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 不含税金额
	 */
	private BigDecimal noTaxSum;
	/**
	 * 是否冲红(1是，0否)
	 */
	private Integer isRed;
	/**
	 * 是否手工发票(1是，0否)
	 */
	private Integer isManual;
	/**
	 * 备注
	 */
	private String remarks;
	/**
	 * 状态
	 */
	private Status status;
	/**
	 * 发票文件
	 */
	private String filePath;




	/**
	 * 发票明细
	 */
	@JsonIgnore
	private Set<InvoiceDetail> invoiceDetails = new HashSet<InvoiceDetail>();
	/** 主键*/
	
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_stl_invoice_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Integer getIsManual() {
		return isManual;
	}
	public void setIsManual(Integer isManual) {
		this.isManual = isManual;
	}
	@Column(length=100)
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	@Transient
	public Set<InvoiceDetail> getInvoiceDetails() {
		return invoiceDetails;
	}
	public void setInvoiceDetails(Set<InvoiceDetail> invoiceDetails) {
		this.invoiceDetails = invoiceDetails;
	}
	@Column(length = 100)
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	

}