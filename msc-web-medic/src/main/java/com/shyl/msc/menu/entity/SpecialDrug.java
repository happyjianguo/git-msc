package com.shyl.msc.menu.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BaseEntity;
import com.shyl.common.entity.BasicEntity;

/**
 * 特殊药品信息
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_menu_specialdrug")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class SpecialDrug extends BaseEntity {

	public String name;
	
	public String type;


	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}
}
