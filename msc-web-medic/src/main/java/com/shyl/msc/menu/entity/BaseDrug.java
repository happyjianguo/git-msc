package com.shyl.msc.menu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BaseEntity;
import com.shyl.common.entity.BasicEntity;

/**
 * 基本药物数据
 * 
 * @author Administrator tableId==74
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_menu_basedrug")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class BaseDrug extends BaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 国家药品信息网ID
	 */
	private Integer extId;
	/**
	 * table id 26/27
	 */
	public Integer type;
	/**
	 * 药品分类
	 */
	private String drugType;
	/**
	 * 一级类别
	 */
	private String oneLevelType;
	/**
	 * 二级类别
	 */
	private String twoLevelType;
	/**
	 * 三级类别
	 */
	private String threeLevelType;
	/**
	 * 品种名称（药品名称
	 */
	private String name;
	/**
	 * 英文名称
	 */
	private String englishName;
	/**
	 * 剂型/规格
	 */
	private String model;
	/**
	 * 剂型说明
	 */
	private String modelNotes;
	/**
	 * 使用范围
	 */
	private String userRange;

	/**
	 * 备注
	 */
	private String notes;

	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
		this.drugType = drugType;
	}

	public String getOneLevelType() {
		return oneLevelType;
	}

	public void setOneLevelType(String oneLevelType) {
		this.oneLevelType = oneLevelType;
	}

	public String getTwoLevelType() {
		return twoLevelType;
	}

	public void setTwoLevelType(String twoLevelType) {
		this.twoLevelType = twoLevelType;
	}

	public String getThreeLevelType() {
		return threeLevelType;
	}

	public void setThreeLevelType(String threeLevelType) {
		this.threeLevelType = threeLevelType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	@Column(length = 500)
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModelNotes() {
		return modelNotes;
	}

	public void setModelNotes(String modelNotes) {
		this.modelNotes = modelNotes;
	}

	public String getUserRange() {
		return userRange;
	}

	public void setUserRange(String userRange) {
		this.userRange = userRange;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

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

}
