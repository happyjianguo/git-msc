package com.shyl.msc.supervise.entity;

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
 * 系统指标设置
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "sup_Quota")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Quota extends BasicEntity {
	private enum Type {
		/** 全院 **/
		all,
		/** 普通门诊 **/
		ordinary,
		/** 紧急门诊 **/
		urgent,
		/**住院 **/
		his
		
	}
	/** 指标编码 **/
	private String code;
	/** 指标名称 **/
	private String name;
	/** 指标描述 **/
	private String remark;
	/** 计算方式 **/
	private String calculation;
	/** 下限 **/
	private BigDecimal min;
	/** 上限 **/
	private BigDecimal max;
	/** 使用标志 **/
	private Integer isDisable;
	/** 类型 **/
	private Type type;

	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "sup_quota_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=30)
	public String getCode() {
		return code;
	}
	@Column(length=100)
	public String getName() {
		return name;
	}
	@Column(length=200)
	public String getRemark() {
		return remark;
	}
	@Column(length=100)
	public String getCalculation() {
		return calculation;
	}
	public BigDecimal getMin() {
		return min;
	}
	public BigDecimal getMax() {
		return max;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public void setCalculation(String calculation) {
		this.calculation = calculation;
	}
	public void setMin(BigDecimal min) {
		this.min = min;
	}
	public void setMax(BigDecimal max) {
		this.max = max;
	}

	public Integer getIsDisable() {
		return isDisable;
	}

	public void setIsDisable(Integer isDisable) {
		this.isDisable = isDisable;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
