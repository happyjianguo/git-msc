/***********************************************************************
 * Module:  GPOSender.java
 * Author:  Administrator
 * Purpose: Defines the Class GPOSender
 ***********************************************************************/

package com.shyl.msc.set.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;

/**
 * 配送商
 * 
 */
@Entity
@Table(name = "t_set_vendor_sender")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class VendorSender extends BasicEntity {
	private static final long serialVersionUID = 4401916733979249605L;
	/**
	 * 配送商编码
	 */
	private String senderCode;
	/**
	 * 配送商名称
	 */
	private String senderName;
	/**
	 * vendor企业编码
	 */
	private String vendorCode;
	/**
	 * vendor企业名称
	 */
	private String vendorName;
	/**
	 * 是否禁用(1是，0否)
	 */
	private Integer isDisabled = 0;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_vendor_sender_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSenderCode() {
		return senderCode;
	}
	public void setSenderCode(String senderCode) {
		this.senderCode = senderCode;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public Integer getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}
	
}