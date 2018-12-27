package com.shyl.msc.set.entity;

import java.util.Date;

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
import com.shyl.msc.enmu.ProjectStus;
/**
 * 报量项目
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_set_project")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Project extends BasicEntity{
	private static final long serialVersionUID = -3434239894330200398L;
	public enum ProjectType {	
		/** 全部 */
		all,
		/** 基本药物*/
		base,
		/** 非基本药物 */
		notBase
	}
	/**
	 * 编号
	 */
	private String code;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 类型
	 */
	private ProjectType type;
	/**
	 * 报名开始时间
	 */
	private Date startDate;	
	/**
	 * 报名结束时间
	 */
	private Date endDate;	
	/**
	 * 默认医院计划开始时间
	 */
	private String startMonthDef;
	/**
	 * 默认医院计划结束时间
	 */
	private String endMonthDef;
	/**
	 * GPO代号
	 */
	private String gpoCode;
	/**
	 * GPO名称
	 */
	private String gpoName;
	/**
	 * 项目状态
	 */
	private ProjectStus projectStus = ProjectStus.create;
	/**
	 * 项目状态名
	 */
	private String projectStusName = projectStus.getName();
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_project_s")
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
	@Column(length=100,nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ProjectType getType() {
		return type;
	}
	public void setType(ProjectType type) {
		this.type = type;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getStartMonthDef() {
		return startMonthDef;
	}
	public void setStartMonthDef(String startMonthDef) {
		this.startMonthDef = startMonthDef;
	}
	public String getEndMonthDef() {
		return endMonthDef;
	}
	public void setEndMonthDef(String endMonthDef) {
		this.endMonthDef = endMonthDef;
	}
	@Column(length = 20)
	public String getGpoCode() {
		return gpoCode;
	}
	public void setGpoCode(String gpoCode) {
		this.gpoCode = gpoCode;
	}
	@Column(length = 100)
	public String getGpoName() {
		return gpoName;
	}
	public void setGpoName(String gpoName) {
		this.gpoName = gpoName;
	}
	
	@Column(length = 10)
	public ProjectStus getProjectStus() {
		return projectStus;
	}
	public void setProjectStus(ProjectStus projectStus) {
		this.projectStus = projectStus;
	}
	@Transient
	public String getProjectStusName() {
		return projectStus.getName();
	}
	public void setProjectStusName(String projectStusName) {
		this.projectStusName = projectStusName;
	}
}
