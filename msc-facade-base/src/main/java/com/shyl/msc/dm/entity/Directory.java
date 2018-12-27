package com.shyl.msc.dm.entity;

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

/** 遴选入围品种目录 */
@Entity
@Table(name = "t_dm_directory")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Directory extends BasicEntity {
	private static final long serialVersionUID = 8999057192563600418L;
	/**
	 * 通 用 名
	 */
	private String genericName;
	/**
	 * 推荐剂型名称
	 */
	private String rcDosageFormName;
	/**
	 * 剂型名称
	 */
	private String dosageFormName;
	/**
	 * 规格
	 */
	private String model;	
	/**
	 * 质量层次(AttributeItem)
	 */
	private String qualityLevel;
	/**
	 * 质量层次Field1(AttributeItem)
	 */
	private String qualityLevelCode;
	/**
	 * 最小使用单位
	 */
	private String minUnit;
	/**
	 * 生产厂家
	 */
	private String producerNames;
	
	/**
	 * 批次
	 */
	private String batch;
	/**
	 * 备注（生成日期）
	 */
	private String note;
	/**
	 * 限制价格
	 */
	private BigDecimal limitPrice;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_directory_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length = 100)
	public String getGenericName() {
		return genericName;
	}
	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}
	@Column(length = 50)
	public String getDosageFormName() {
		return dosageFormName;
	}
	public void setDosageFormName(String dosageFormName) {
		this.dosageFormName = dosageFormName;
	}
	@Column(length = 300)
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	@Column(length = 20)
	public String getQualityLevel() {
		return qualityLevel;
	}
	public void setQualityLevel(String qualityLevel) {
		this.qualityLevel = qualityLevel;
	}
	@Column(length = 20)
	public String getQualityLevelCode() {
		return qualityLevelCode;
	}
	public void setQualityLevelCode(String qualityLevelCode) {
		this.qualityLevelCode = qualityLevelCode;
	}
	@Column(length = 20)
	public String getMinUnit() {
		return minUnit;
	}
	public void setMinUnit(String minUnit) {
		this.minUnit = minUnit;
	}
	@Column(length = 3000)
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	@Column(length = 50)	
	public String getRcDosageFormName() {
		return rcDosageFormName;
	}
	public void setRcDosageFormName(String rcDosageFormName) {
		this.rcDosageFormName = rcDosageFormName;
	}
	@Column(length = 3000)
	public String getProducerNames() {
		return producerNames;
	}
	public void setProducerNames(String producerNames) {
		this.producerNames = producerNames;
	}

	@Column(length = 30)
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getLimitPrice() {
		return limitPrice;
	}
	public void setLimitPrice(BigDecimal limitPrice) {
		this.limitPrice = limitPrice;
	}

	
}
