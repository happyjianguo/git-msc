package com.shyl.msc.b2b.order.entity;

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

/**
 * HIS出入库单 
 * 
 * 出入库功能暂时未实现
 */
@Entity
@Table(name = "t_order_inoutbound")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InOutBound extends Order {
	private static final long serialVersionUID = 7534128467266302407L;
	/**
	 * 进出库类型
	 */
	public enum IOType {

		/** 进库 */
		in,
		
		/** 出库 */
		out
	}
	/**
	 * 出入库类型
	 */
	private IOType ioType;
	/**
	 * 操作人
	 */
	private String operator;
	/**
	 * 配送单编号
	 */
	private String deliveryOrderCode;
	/**
	 * 配送单明细
	 */
	@JsonIgnore
	private Set<InOutBoundDetail> inOutBoundDetails = new HashSet<InOutBoundDetail>();
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_inoutbound_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public IOType getIoType() {
		return ioType;
	}
	public void setIoType(IOType ioType) {
		this.ioType = ioType;
	}
	@Column(length=60)
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	@Column(length=30)
	public String getDeliveryOrderCode() {
		return deliveryOrderCode;
	}
	public void setDeliveryOrderCode(String deliveryOrderCode) {
		this.deliveryOrderCode = deliveryOrderCode;
	}
	@Transient
	public Set<InOutBoundDetail> getInOutBoundDetails() {
		return inOutBoundDetails;
	}
	public void setInOutBoundDetails(Set<InOutBoundDetail> inOutBoundDetails) {
		this.inOutBoundDetails = inOutBoundDetails;
	}

	

}