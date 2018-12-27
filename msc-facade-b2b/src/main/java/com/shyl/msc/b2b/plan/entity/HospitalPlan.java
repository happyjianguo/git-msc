package com.shyl.msc.b2b.plan.entity;

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
 * 医院报量
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_plan_hospitalplan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HospitalPlan extends BasicEntity{
	private static final long serialVersionUID = -3434239894330200398L;
	/**
	 * 项目细项id
	 */
	private Long projectDetailId;
	/**
	 * 医院编码
	 */
	private String hospitalCode;
	/**
	 * 医院名称
	 */
	private String hospitalName;
	/**
	 * 数量
	 */
	private BigDecimal Num;
	/**
	 * 采购周期
	 */
	private Integer cycle;
	/**
	 * 开始年月
	 */
	private String startMonth;
	/**
	 * 结束年月
	 */
	private String endMonth;

	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_plan_hospitalplan_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getProjectDetailId() {
		return projectDetailId;
	}
	public void setProjectDetailId(Long projectDetailId) {
		this.projectDetailId = projectDetailId;
	}
	@Column(length=50)
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	@Column(length=100)
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	@Column(precision=16, scale=0)
	public BigDecimal getNum() {
		return Num;
	}
	public void setNum(BigDecimal num) {
		Num = num;
	}
	public Integer getCycle() {
		return cycle;
	}
	public void setCycle(Integer cycle) {
		this.cycle = cycle;
	}
	@Column(length=10)
	public String getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}
	@Column(length=10)
	public String getEndMonth() {
		return endMonth;
	}
	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}
	
}
