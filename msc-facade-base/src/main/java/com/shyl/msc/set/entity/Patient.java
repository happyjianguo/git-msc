package com.shyl.msc.set.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;

/** 病人 */
@Entity
@Table(name = "t_set_patient")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Patient extends BasicEntity {
	private static final long serialVersionUID = 7885443688544783180L;
	/**
	 * 身份证号
	 */
	private String idCode;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 性别
	 */
	private String sex;
	/**
	 * 出生日期
	 */
	private String birthday;
	/**
	 * 电话号码
	 */
	private String tel;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 居住地址
	 */
	private String livingAddress;
	/**
	 * 默认送货地址id
	 */
	private Long addressId;
	/**
	 * 默认送货地址
	 */
	private String addressName;
	/**
	 * 健康卡号
	 */
	private String jkCode;
	/**
	 * 社保卡号
	 */
	private String sbCode;
	/**
	 * 医保卡号
	 */
	private String ybCode;
	/**
	 * 是否禁用(1是，0否)
	 */
	private Integer isDisabled;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="T_SET_PATIENT_S")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=50)
	public String getIdCode() {
		return idCode;
	}
	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}
	@Column(length=50)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(length=10)
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	@Column(length=10)
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	@Column(length=20)
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	@Column(length=20)
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Column(length=100)	
	public String getLivingAddress() {
		return livingAddress;
	}
	public void setLivingAddress(String livingAddress) {
		this.livingAddress = livingAddress;
	}
	public Long getAddressId() {
		return addressId;
	}
	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
	@Column(length=100)
	public String getAddressName() {
		return addressName;
	}
	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}
	@Column(length=50)
	public String getJkCode() {
		return jkCode;
	}
	public void setJkCode(String jkCode) {
		this.jkCode = jkCode;
	}
	@Column(length=50)
	public String getSbCode() {
		return sbCode;
	}
	public void setSbCode(String sbCode) {
		this.sbCode = sbCode;
	}
	@Column(length=50)
	public String getYbCode() {
		return ybCode;
	}
	public void setYbCode(String ybCode) {
		this.ybCode = ybCode;
	}
	public Integer getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}
	
}