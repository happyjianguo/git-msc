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

/** 专家 */
@Entity
@Table(name = "t_set_expert")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Expert extends BasicEntity {
	private static final long serialVersionUID = 8999057192563600418L;
	
	/**
	 * 所属学科代码
	 * attributeItem
	 */
	private String courseCode;
	/**
	 * 所属学科名称
	 */
	private String courseName;
	/**
	 * 编号
	 */
	private String code;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 性别
	 */
	private String sex;
	/**
	 * 固定电话
	 */
	private String tel;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 备注
	 */
	private String notes;
	/**
	 * 所属机构类别
	 */
	private String orgType;
	/**
	 * 所属机构编码
	 */
	private String orgCode;
	/**
	 * 所属机构名称
	 */
	private String orgName;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_expert_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=20)
	public String getCourseCode() {
		return courseCode;
	}
	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}
	@Column(length=50)
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	@Column(length=20)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Column(length=10)
	public String getOrgType() {
		return orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	@Column(length=20)
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	@Column(length=200)
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	
	
}
