package com.shyl.msc.set.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;

/** 医疗机构 */
@Entity
@Table(name = "t_set_hospital")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Hospital extends BasicEntity {
	private static final long serialVersionUID = 7885443688544783180L;
	/**
	 * 编码
	 */
	private String code;
	/** 
	 * 数据接口编码
	 */
	private String iocode;
	/**
	 * 单位名称
	 */
	private String fullName;
	/**
	 * 单位简称
	 */
	private String shortName;
	/**
	 * 拼音简称
	 */
	private String pinyin;
	/**
	 * 五笔简称
	 */
	private String wbcode;
	/**
	 * 主办（管）单位
	 */
	private String superiors;
	/**
	 * 法定代表人(负责人)
	 */
	private String manager;
	/**
	 * 注册地址
	 */
	private String registryAddr;
	/**
	 * 注册日期
	 */
	private Date registryDate;
	/**
	 * 所在地区
	 */
	private Long regionCode;
	/**
	 * 机构类型
	 */
	private Integer orgType;
	/**
	 * 机构隶属关系
	 */
	private Integer reportType;
	/**
	 * 机构级别
	 */
	private Integer orgLevel;
	/**
	 * 归档文件序号
	 */
	private String archNo;
	/**
	 * 开户名称
	 */
	private String nameinBank;
	/**
	 * 纳税人登记号
	 */
	private String taxNO;
	/**
	 * 开户银行
	 */
	private String bankName;
	/**
	 * 开户账号
	 */
	private String bankAccount;
	/**
	 * 通讯地址
	 */
	private String postAddr;
	/**
	 * 邮政编码
	 */
	private String postCode;
	/**
	 * 电话号码
	 */
	private String telephone;
	/**
	 * 传真
	 */
	private String fax;
	/**
	 * 电子邮箱
	 */
	private String email;
	/**
	 * 网址
	 */
	private String website;
	/**
	 * 开业成立日期
	 */
	private Date openDate;
	/**
	 * 注册资金（万元）
	 */
	private BigDecimal capital;
	/**
	 * 总资产
	 */
	private BigDecimal totalAssets;
	/**
	 * 编制床位数
	 */
	private Integer bedNum;
	/**
	 * 实有床位数
	 */
	private Integer actBedNum;
	/**
	 * 人员数
	 */
	private Integer employeeNum;
	/**
	 * 卫技人员数
	 */
	private Integer profsNum;
	/**
	 * 诊疗科室数
	 */
	private Integer roomsNum;
	/**
	 * 年门诊人次数
	 */
	private Integer patientNum;
	/**
	 * 年出院人次数
	 */
	private Integer finishNum;
	/**
	 * 卫生许可证号
	 */
	private String hLicenseNo;
	/**
	 * 有限截止日期
	 */
	private Date hOutdate;
	/**
	 * 营业执照
	 */
	private String licenseNO;
	/**
	 * 营业执照有效日期
	 */
	private Date outDate;
	/**
	 * 机构简介
	 */
	private String introduce;
	/**
	 * 经营范围
	 */
	private String bizscope;
	/**
	 * 备 注
	 */
	private String notes;
	/**
	 * 医院是否管理到批次(1是，0否)
	 * 
	 */
	private Integer isBatchCode;
	/**
	 * 是否禁用(1是，0否)
	 */
	private Integer isDisabled;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_hospital_s")
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
	@Column(length=20)
	public String getIocode() {
		return iocode;
	}
	public void setIocode(String iocode) {
		this.iocode = iocode;
	}
	@Column(length=100,nullable = false)
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	@Column(length=50,nullable = false)
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	@Column(length=50)
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	@Column(length=50)
	public String getWbcode() {
		return wbcode;
	}
	public void setWbcode(String wbcode) {
		this.wbcode = wbcode;
	}
	@Column(length=50)
	public String getSuperiors() {
		return superiors;
	}
	public void setSuperiors(String superiors) {
		this.superiors = superiors;
	}
	@Column(length=20)
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	@Column(length=100)
	public String getRegistryAddr() {
		return registryAddr;
	}
	public void setRegistryAddr(String registryAddr) {
		this.registryAddr = registryAddr;
	}
	public Date getRegistryDate() {
		return registryDate;
	}
	public void setRegistryDate(Date registryDate) {
		this.registryDate = registryDate;
	}
	public Long getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(Long regionCode) {
		this.regionCode = regionCode;
	}
	public Integer getOrgType() {
		return orgType;
	}
	public void setOrgType(Integer orgType) {
		this.orgType = orgType;
	}
	public Integer getReportType() {
		return reportType;
	}
	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}
	public Integer getOrgLevel() {
		return orgLevel;
	}
	public void setOrgLevel(Integer orgLevel) {
		this.orgLevel = orgLevel;
	}
	@Column(length=20)
	public String getArchNo() {
		return archNo;
	}
	public void setArchNo(String archNo) {
		this.archNo = archNo;
	}
	@Column(length=50)
	public String getNameinBank() {
		return nameinBank;
	}
	public void setNameinBank(String nameinBank) {
		this.nameinBank = nameinBank;
	}
	@Column(length=50)
	public String getTaxNO() {
		return taxNO;
	}
	public void setTaxNO(String taxNO) {
		this.taxNO = taxNO;
	}
	@Column(length=50)
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	@Column(length=20)
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	@Column(length=100)
	public String getPostAddr() {
		return postAddr;
	}
	public void setPostAddr(String postAddr) {
		this.postAddr = postAddr;
	}
	@Column(length=20)
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	@Column(length=100)
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	@Column(length=20)
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	@Column(length=20)
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	@Column(length=50)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Column(length=50)
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public Date getOpenDate() {
		return openDate;
	}
	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getCapital() {
		return capital;
	}
	public void setCapital(BigDecimal capital) {
		this.capital = capital;
	}
	@Column(precision=16, scale=2)
	public BigDecimal getTotalAssets() {
		return totalAssets;
	}
	public void setTotalAssets(BigDecimal totalAssets) {
		this.totalAssets = totalAssets;
	}
	public Integer getBedNum() {
		return bedNum;
	}
	public void setBedNum(Integer bedNum) {
		this.bedNum = bedNum;
	}
	public Integer getActBedNum() {
		return actBedNum;
	}
	public void setActBedNum(Integer actBedNum) {
		this.actBedNum = actBedNum;
	}
	public Integer getEmployeeNum() {
		return employeeNum;
	}
	public void setEmployeeNum(Integer employeeNum) {
		this.employeeNum = employeeNum;
	}
	public Integer getProfsNum() {
		return profsNum;
	}
	public void setProfsNum(Integer profsNum) {
		this.profsNum = profsNum;
	}
	public Integer getRoomsNum() {
		return roomsNum;
	}
	public void setRoomsNum(Integer roomsNum) {
		this.roomsNum = roomsNum;
	}
	public Integer getPatientNum() {
		return patientNum;
	}
	public void setPatientNum(Integer patientNum) {
		this.patientNum = patientNum;
	}
	public Integer getFinishNum() {
		return finishNum;
	}
	public void setFinishNum(Integer finishNum) {
		this.finishNum = finishNum;
	}
	@Column(length=20)
	public String gethLicenseNo() {
		return hLicenseNo;
	}
	public void sethLicenseNo(String hLicenseNo) {
		this.hLicenseNo = hLicenseNo;
	}
	public Date gethOutdate() {
		return hOutdate;
	}
	public void sethOutdate(Date hOutdate) {
		this.hOutdate = hOutdate;
	}
	@Column(length=20)
	public String getLicenseNO() {
		return licenseNO;
	}
	public void setLicenseNO(String licenseNO) {
		this.licenseNO = licenseNO;
	}
	public Date getOutDate() {
		return outDate;
	}
	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}
	@Column(length=100)
	public String getBizscope() {
		return bizscope;
	}
	public void setBizscope(String bizscope) {
		this.bizscope = bizscope;
	}
	@Column(length=100)
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Integer getIsBatchCode() {
		return isBatchCode;
	}
	public void setIsBatchCode(Integer isBatchCode) {
		this.isBatchCode = isBatchCode;
	}
	public Integer getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}





}