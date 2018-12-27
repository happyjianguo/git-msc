package com.shyl.msc.set.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;

/** 地区代码 */
@Entity
@Table(name = "t_set_regioncode")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RegionCode extends BasicEntity {
	private static final long serialVersionUID = 1989125923833991275L;
	/**
	 * 地区编码
	 */
	private String code;
	/**
	 * 地区名称
	 */
	private String name;	
	/** 
	 * 全称
	 */
	private String fullName;
	/** 
	 * 树路径
	 */
	private String treePath;
	/**
	 * 上级地区
	 */
	private Long parentId;
	/**
	 * 是否禁用(1是，0否)
	 */
	private Integer isDisabled;
	
	/** 排序 */
	private Integer sort = 0;
	
	/** 主键*/
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_regioncode_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=20,unique = true, nullable = false,updatable = false)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(length=50, nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(length=100)
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
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
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	@Transient
	public Long getProvinceId(){
		Long id = null;
		if(treePath != null){
			if(treePath.equals(",")){
				return this.getId();
			}
			String[] s = treePath.split(",");
			if(s.length == 1){
				id = this.getId();
			}else{
				id = new Long(s[1]);
			}	
		}
		return id;
	}
	


	

}