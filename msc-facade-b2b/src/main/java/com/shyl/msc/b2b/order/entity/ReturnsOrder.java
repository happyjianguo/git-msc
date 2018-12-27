package com.shyl.msc.b2b.order.entity;

import java.util.Date;
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

/** 退货单 */
@Entity
@Table(name = "t_order_returnsorder")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ReturnsOrder extends Order {
	private static final long serialVersionUID = 9156179523013703761L;
	/**
	 * 类型
	 */
	public enum Type {
		returns("退货"),
		reject("拒收");
		private String name;
		private Type(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	/**
	 * 退货发起人
	 */
	private String returnsMan;
	/**
	 * 退货发起时间
	 */
	private Date returnsBeginDate;
	/**
	 * 退货申请单编号
	 */
	private String returnsRequestCode;
	/**
	 * 是1否0已开票
	 */
	private Integer isInvoiced = 0;
	/**
	 * 类型
	 */
	private Type type;
	/**
	 * 退货单明细
	 */
	@JsonIgnore
	private Set<ReturnsOrderDetail> returnsOrderDetails = new HashSet<ReturnsOrderDetail>();
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_order_returnsorder_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=20)
	public String getReturnsMan() {
		return returnsMan;
	}
	public void setReturnsMan(String returnsMan) {
		this.returnsMan = returnsMan;
	}
	public Date getReturnsBeginDate() {
		return returnsBeginDate;
	}
	public void setReturnsBeginDate(Date returnsBeginDate) {
		this.returnsBeginDate = returnsBeginDate;
	}
	@Transient
	public Set<ReturnsOrderDetail> getReturnsOrderDetails() {
		return returnsOrderDetails;
	}
	public void setReturnsOrderDetails(Set<ReturnsOrderDetail> returnsOrderDetails) {
		this.returnsOrderDetails = returnsOrderDetails;
	}
	
	public Integer getIsInvoiced() {
		return isInvoiced;
	}
	public void setIsInvoiced(Integer isInvoiced) {
		this.isInvoiced = isInvoiced;
	}
	@Column(length=30)
	public String getReturnsRequestCode() {
		return returnsRequestCode;
	}
	public void setReturnsRequestCode(String returnsRequestCode) {
		this.returnsRequestCode = returnsRequestCode;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	

}