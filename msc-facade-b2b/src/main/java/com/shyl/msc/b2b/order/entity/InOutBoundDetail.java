package com.shyl.msc.b2b.order.entity;

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

/**
 * 出入库明细
 * */
@Entity
@Table(name = "t_order_inoutbound_detail")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InOutBoundDetail extends OrderDetail {
	private static final long serialVersionUID = 8720617780177728113L;
	/**
	 * 出入库
	 */
	private InOutBound inOutBound;
	/**
	 * 生产批号
	 */
	private String batchCode;
	/**
	 * 生产日期
	 */
	private String batchDate;
	/**
	 * 有效日期
	 */
	private String expiryDate;
	/**
	 * 条码
	 */
	private String barcode;
	/**
	 * 取药验证码
	 */
	private String checkCode;
	/**
	 * 是否取药（0为未取药，1为取药）
	 */
	private Integer isFetch;
	/**
	 * 配送单明细编号
	 */
	private String deliveryOrderDetailCode;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_inoutbound_d_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="inOutBoundId")
	public InOutBound getInOutBound() {
		return inOutBound;
	}
	public void setInOutBound(InOutBound inOutBound) {
		this.inOutBound = inOutBound;
	}
	@Column(length=100)
	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	@Column(length=10)
	public String getBatchDate() {
		return batchDate;
	}
	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}
	@Column(length=10)
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	@Column(length=50)
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	@Column(length=20)
	public String getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
	public Integer getIsFetch() {
		return isFetch;
	}
	public void setIsFetch(Integer isFetch) {
		this.isFetch = isFetch;
	}
	@Column(length=50)
	public String getDeliveryOrderDetailCode() {
		return deliveryOrderDetailCode;
	}
	public void setDeliveryOrderDetailCode(String deliveryOrderDetailCode) {
		this.deliveryOrderDetailCode = deliveryOrderDetailCode;
	}


}