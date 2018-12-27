package com.shyl.msc.supervise.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;
/**
 * 
 * 医院药品月采购情况查询
 *
 */
@Entity
@Table(name = "sup_his_monthlyPurchase")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MonthlyPurchase extends BasicEntity{
	private static final long serialVersionUID = -1426640934482187322L;
	/**
	 * 年月
	 */
	private String month;
	/**
	 * 医院编码
	 */
	private String hospitalCode;
	/**
	 * 医院名称
	 */
	private String hospitalName;
	/**
	 * 药品编码
	 */
	private String code;
	/**
	 * 药品名称
	 */
	private String name;
	/**
	 * 剂型
	 */
	private String dosageFormname;
	/**
	 * 规格
	 */
	private String model;
	/**
	 * 包装规格
	 */
	private String packDesc;
	/**
	 * 生产厂家
	 */
	private String producerName;
	/**
	 * gpo采购数量
	 */
	private Integer gpoNum;
	/**
	 * gpo采购金额
	 */
	private BigDecimal gpoAmt;
	/**
	 * 非gpo采购数量
	 */
	private Integer notGpoNum;
	/**
	 * 非gpo采购金额
	 */
	private BigDecimal notGpoAmt;
	/**
	 * 总采购数量
	 */
	private Integer num;
	/**
	 * 总采购金额
	 */
	private BigDecimal amt;
	
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "sup_his_monthlyPurchase_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}
	@Column(length = 50)
	public String getHospitalCode() {
		return hospitalCode;
	}

	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	@Column(length = 100)
	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	@Column(precision = 16, scale = 3)
	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	@Column(precision = 16, scale = 3)
	public BigDecimal getAmt() {
		return amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	@Column
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	@Column
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column
	public String getDosageFormname() {
		return dosageFormname;
	}

	public void setDosageFormname(String dosageFormname) {
		this.dosageFormname = dosageFormname;
	}
	@Column
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	@Column
	public String getPackDesc() {
		return packDesc;
	}

	public void setPackDesc(String packDesc) {
		this.packDesc = packDesc;
	}
	@Column
	public String getProducerName() {
		return producerName;
	}

	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
	@Column(precision = 16, scale = 3)
	public Integer getGpoNum() {
		return gpoNum;
	}

	public void setGpoNum(Integer gpoNum) {
		this.gpoNum = gpoNum;
	}
	@Column(precision = 16, scale = 3)
	public BigDecimal getGpoAmt() {
		return gpoAmt;
	}

	public void setGpoAmt(BigDecimal gpoAmt) {
		this.gpoAmt = gpoAmt;
	}
	@Column(precision = 16, scale = 3)
	public Integer getNotGpoNum() {
		return notGpoNum;
	}

	public void setNotGpoNum(Integer notGpoNum) {
		this.notGpoNum = notGpoNum;
	}
	@Column(precision = 16, scale = 3)
	public BigDecimal getNotGpoAmt() {
		return notGpoAmt;
	}

	public void setNotGpoAmt(BigDecimal notGpoAmt) {
		this.notGpoAmt = notGpoAmt;
	}

}
