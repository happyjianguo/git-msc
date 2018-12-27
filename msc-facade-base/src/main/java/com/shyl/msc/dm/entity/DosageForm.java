package com.shyl.msc.dm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;


/** 剂型 */
@Entity
@Table(name = "t_dm_dosageform")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DosageForm extends BasicEntity {
	private static final long serialVersionUID = 222898051944417353L;
	/**
	 * 编码
	 */
	private String code;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 英文名称
	 */
	private String englishName;
	/**
	 * 备注
	 */
	private String notes;
	/**
	 * 树路径
	 */
	private String treePath;
	/**
	 * 上级分类
	 */
	private Long parentId;

	/**
	 * 是否禁用(1是，0否)  
	 */
	private Integer isDisabled;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_dosageform_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=20,unique = true,nullable = false,updatable = false)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	@Column(length=50,nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(length=50)
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	@Column(length=50)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	@Column(length=20)
	public String getTreePath() {
		return treePath;
	}
	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public Integer getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}





}
