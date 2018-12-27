package com.shyl.msc.set.entity;

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
import com.shyl.msc.dm.entity.Directory;
/**
 * 报量项目细项
 * 
 * @author a_Q
 *
 */
@Entity
@Table(name = "t_set_project_detail")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProjectDetail extends BasicEntity{
	private static final long serialVersionUID = -3434239894330200398L;
	/**
	 * 项目
	 */
	private Project project;
	/**
	 * 遴选药品目录
	 */
	private Directory directory;
	/**
	 * 申报数量
	 */
	private Integer num = 0;
	/**
	 * 申报医院数
	 */
	private Integer hospitalNum = 0;
	/**
	 * 投标医院数
	 */
	private Integer vendorNum = 0;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_project_detail_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="projectId")
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="directoryId")
	public Directory getDirectory() {
		return directory;
	}
	public void setDirectory(Directory directory) {
		this.directory = directory;
	}
	
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Integer getHospitalNum() {
		return hospitalNum;
	}
	public void setHospitalNum(Integer hospitalNum) {
		this.hospitalNum = hospitalNum;
	}
	public Integer getVendorNum() {
		return vendorNum;
	}
	public void setVendorNum(Integer vendorNum) {
		this.vendorNum = vendorNum;
	}
	
}
