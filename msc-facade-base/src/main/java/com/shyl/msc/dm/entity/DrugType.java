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

/** 药品分类 */
@Entity
@Table(name = "t_dm_drug_type")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DrugType extends BasicEntity {
	private static final long serialVersionUID = 6310818940439421614L;
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
	 * 备注
	 */
	private String treePath;
	/**
	 * 上级分类
	 */
	private Long parentId;
	/**
	 * 是否禁用
	 */
	private Integer isDisabled;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_drug_type_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=50,unique = true, nullable = false,updatable = false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(length=100,nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(length=100)
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	@Column(length=100)
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Column(length=30)
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