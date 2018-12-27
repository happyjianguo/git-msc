package com.shyl.msc.b2b.plan.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shyl.common.entity.BasicEntity;
/**
 * 医院报量明细
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_plan_hospitalplan_detail")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HospitalPlanDetail extends BasicEntity{
	private static final long serialVersionUID = -3434239894330200398L;
	
	/**
	 * 医院报量
	 */
	private HospitalPlan hospitalPlan;
	/**
	 * 年月
	 */
	private String month;
	/**
	 * 数量
	 */
	private BigDecimal Num;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_plan_hospitalplan_detail_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="hospitalPlanId")
	public HospitalPlan getHospitalPlan() {
		return hospitalPlan;
	}
	public void setHospitalPlan(HospitalPlan hospitalPlan) {
		this.hospitalPlan = hospitalPlan;
	}
	@Column(length=10)
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	@Column(precision=16, scale=0)
	public BigDecimal getNum() {
		return Num;
	}
	public void setNum(BigDecimal num) {
		Num = num;
	}
	
}
