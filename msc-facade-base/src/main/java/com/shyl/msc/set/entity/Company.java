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

/** 企业 */
@Entity
@Table(name = "t_set_company")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company extends BasicEntity {
	private static final long serialVersionUID = -7385223160198861529L;
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
	 * 曾用名
	 */
	private String alias;
	/**
	 * 是否法人企业（1是，0否）
	 */
	private Integer isCorporateEnterprises;
	/**
	 * 法定代表人(负责人)
	 */
	private String manager;
	/**
	 * 五笔简称
	 */
	private String wbcode;
	/**
	 * 注册地址
	 */
	private String registryAddr;
	/**
	 * 注册日期
	 */
	private Date registryDate;
	/**
	 * 所在国家
	 */
	private Long country;
	/**
	 * 所在地区
	 */
	private Long regionCode;
	/**
	 * 是否为生产企业(1是，0否) 
	 */
	private Integer isProducer;
	/**
	 * 是否为供应商（1是，0否） 
	 */
	private Integer isVendor;
	/**
	 * 是否为配送商（1是，0否） 
	 */
	private Integer isSender;
	/**
	 * 是否为GPO（1是，0否） 
	 */
	private Integer isGPO;
	/**
	 * 年销售额
	 */
	private Integer salesValue;
	/**
	 * 销售年份
	 */
	private Integer salesYear;
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
	 * 人员数
	 */
	private Integer employeeNum;
	/**
	 * 机构简介
	 */
	private String introduce;
	/**
	 * 生产许可证号
	 */
	private String plincenseNo;
	/**
	 * 经营许可证号
	 */
	private String slincenseNo;
	/**
	 * 有效截止日期
	 */
	private Date poutDate;
	/**
	 * 营业执照
	 */
	private String licenseNO;
	/**
	 * 营业执照有效日期
	 */
	private Date outDate;
	/**
	 * 信用等级
	 */
	private String creditLevel;
	/**
	 * 信用证号
	 */
	private String creditNo;
	/**
	 * 信用证有效期
	 */
	private Date creditOutDate;
	/**
	 * 是否有GSP证书(1是，0否)
	 */
	private Integer hasGsp;
	/**
	 * GSP证书
	 */
	private String gsPNo;
	/**
	 * 经营范围
	 */
	private String saleScope;
	/**
	 * 是否有GMP证书
	 */
	private Integer gmPFlag;
	/**
	 * GMP证书
	 */
	private String gmPNo;
	/**
	 * 生产范围
	 */
	private String productScope;
	/**
	 * 授权人姓名
	 */
	private String authorizor;
	/**
	 * 授权人联系电话
	 */
	private String authorizorPhone;
	/**
	 * 授权人证件号
	 */
	private String authorizorNo;
	/**
	 * 备 注
	 */
	private String notes;
	/**
	 * 企业登记注册类型(Attribute)
	 * */
	private Integer companyType;
	/**
	 * SFDA基础数据库对应的链接
	 */
	private String sfdaURL;
	/**
	 * 是否禁用（1是，0否）
	 */
	private Integer isDisabled;
	/** 主键*/
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_company_s")
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
	@Column(length=20)
	public String getIocode() {
		return iocode;
	}

	public void setIocode(String iocode) {
		this.iocode = iocode;
	}
	@Column(length=200, nullable = false)
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	@Column(length=100)
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
	@Column(length=100)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public Integer getIsCorporateEnterprises() {
		return isCorporateEnterprises;
	}

	public void setIsCorporateEnterprises(Integer isCorporateEnterprises) {
		this.isCorporateEnterprises = isCorporateEnterprises;
	}
	@Column(length=20)
	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}
	@Column(length=50)
	public String getWbcode() {
		return wbcode;
	}

	public void setWbcode(String wbcode) {
		this.wbcode = wbcode;
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

	public Long getCountry() {
		return country;
	}

	public void setCountry(Long country) {
		this.country = country;
	}

	public Long getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(Long regionCode) {
		this.regionCode = regionCode;
	}

	public Integer getIsProducer() {
		return isProducer;
	}

	public void setIsProducer(Integer isProducer) {
		this.isProducer = isProducer;
	}

	public Integer getIsVendor() {
		return isVendor;
	}

	public void setIsVendor(Integer isVendor) {
		this.isVendor = isVendor;
	}

	public Integer getIsSender() {
		return isSender;
	}
	public void setIsSender(Integer isSender) {
		this.isSender = isSender;
	}
	
	public Integer getIsGPO() {
		return isGPO;
	}
	
	public void setIsGPO(Integer isGPO) {
		this.isGPO = isGPO;
	}
	
	public Integer getSalesValue() {
		return salesValue;
	}

	public void setSalesValue(Integer salesValue) {
		this.salesValue = salesValue;
	}

	public Integer getSalesYear() {
		return salesYear;
	}

	public void setSalesYear(Integer salesYear) {
		this.salesYear = salesYear;
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
	@Column(length=50)
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
	@Column(length=100)
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

	public Integer getEmployeeNum() {
		return employeeNum;
	}

	public void setEmployeeNum(Integer employeeNum) {
		this.employeeNum = employeeNum;
	}
	@Column(length=100)
	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	@Column(length=20)
	public String getPlincenseNo() {
		return plincenseNo;
	}

	public void setPlincenseNo(String plincenseNo) {
		this.plincenseNo = plincenseNo;
	}
	@Column(length=20)
	public String getSlincenseNo() {
		return slincenseNo;
	}

	public void setSlincenseNo(String slincenseNo) {
		this.slincenseNo = slincenseNo;
	}

	public Date getPoutDate() {
		return poutDate;
	}

	public void setPoutDate(Date poutDate) {
		this.poutDate = poutDate;
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
	@Column(length=20)
	public String getCreditLevel() {
		return creditLevel;
	}

	public void setCreditLevel(String creditLevel) {
		this.creditLevel = creditLevel;
	}
	@Column(length=20)
	public String getCreditNo() {
		return creditNo;
	}

	public void setCreditNo(String creditNo) {
		this.creditNo = creditNo;
	}

	public Date getCreditOutDate() {
		return creditOutDate;
	}

	public void setCreditOutDate(Date creditOutDate) {
		this.creditOutDate = creditOutDate;
	}

	public Integer getHasGsp() {
		return hasGsp;
	}

	public void setHasGsp(Integer hasGsp) {
		this.hasGsp = hasGsp;
	}
	@Column(length=20)
	public String getGsPNo() {
		return gsPNo;
	}

	public void setGsPNo(String gsPNo) {
		this.gsPNo = gsPNo;
	}
	@Column(length=100)
	public String getSaleScope() {
		return saleScope;
	}

	public void setSaleScope(String saleScope) {
		this.saleScope = saleScope;
	}

	public Integer getGmPFlag() {
		return gmPFlag;
	}

	public void setGmPFlag(Integer gmPFlag) {
		this.gmPFlag = gmPFlag;
	}
	@Column(length=20)
	public String getGmPNo() {
		return gmPNo;
	}

	public void setGmPNo(String gmPNo) {
		this.gmPNo = gmPNo;
	}
	@Column(length=100)
	public String getProductScope() {
		return productScope;
	}

	public void setProductScope(String productScope) {
		this.productScope = productScope;
	}
	@Column(length=20)
	public String getAuthorizor() {
		return authorizor;
	}

	public void setAuthorizor(String authorizor) {
		this.authorizor = authorizor;
	}
	@Column(length=20)
	public String getAuthorizorPhone() {
		return authorizorPhone;
	}

	public void setAuthorizorPhone(String authorizorPhone) {
		this.authorizorPhone = authorizorPhone;
	}
	@Column(length=20)
	public String getAuthorizorNo() {
		return authorizorNo;
	}

	public void setAuthorizorNo(String authorizorNo) {
		this.authorizorNo = authorizorNo;
	}
	@Column(length=100)
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getCompanyType() {
		return companyType;
	}

	public void setCompanyType(Integer companyType) {
		this.companyType = companyType;
	}
	@Column(length=50)
	public String getSfdaURL() {
		return sfdaURL;
	}

	public void setSfdaURL(String sfdaURL) {
		this.sfdaURL = sfdaURL;
	}

	public Integer getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}



	

}