/***********************************************************************
 * Module:  Sickness.java
 * Author:  Administrator
 * Purpose: Defines the Class Sickness
 ***********************************************************************/

package com.shyl.msc.dm.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;


/**
 * 疾病
 */
@Entity
@Table(name = "t_dm_sickness")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Sickness extends BasicEntity {
	private static final long serialVersionUID = 1L;	
	/**
	 * 疾病代码
	 */
	private String code;
	/**
	 * 疾病名称
	 */
	private String name;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_dm_sickness_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}