package com.shyl.msc.dm.entity;

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

/** 招标目录价格 */
@Entity
@Table(name = "t_dm_directorypricerecord")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class DirectoryPriceRecord extends BasicEntity {
	private static final long serialVersionUID = 8999057192563600418L;
	/**
	 * 药品招标目录
	 */
	private Directory directory;
	/**
	 * 月份
	 */
	private String month;
	/**
	 * 地区代码
	 */
	private String areaCode;
	/**
	 * 地区名称
	 */
	private String areaName;
	/**
	 * 价格
	 */
	private BigDecimal price;
	
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_directorypricerecord_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "directoryId",nullable=false)
	public Directory getDirectory() {
		return directory;
	}
	public void setDirectory(Directory directory) {
		this.directory = directory;
	}
	@Column(length = 20)
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	@Column(length = 20)
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	@Column(length = 100)
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	

	
}
