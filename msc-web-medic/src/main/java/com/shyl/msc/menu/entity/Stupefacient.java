package com.shyl.msc.menu.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BaseEntity;
import com.shyl.common.entity.BasicEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "t_menu_stupefacient")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Stupefacient extends BaseEntity {
	/**
	 * 国家药品信息网ID
	 */
	private Integer extId;
	/**
	 * table id 102
	 */
	public Integer type;
	/**
	 * 品种名称（药品名称
	 */
	private String name;
	/**
	 * 英文名
	 */
	private String englishName;
	/**
	 * 药品分类
	 */
	private String drugType;
	/**
	 * CAS号
	 */
	private String cas;
		
	public Integer getExtId() {
		return extId;
	}

	public void setExtId(Integer extId) {
		this.extId = extId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
		this.drugType = drugType;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getCas() {
		return cas;
	}

	public void setCas(String cas) {
		this.cas = cas;
	}
	
}

