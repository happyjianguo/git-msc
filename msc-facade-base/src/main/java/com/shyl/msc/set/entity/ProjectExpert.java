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
/**
 * 项目专家
 * 
 *
 */
@Entity
@Table(name = "t_set_projectexpertrule")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProjectExpert extends BasicEntity{
	private static final long serialVersionUID = -3434239894330200398L;

	/**
	 * 项目
	 */
	private Project project;
	/**
	 * 专家
	 */
	private Expert expert;
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_projectexpertrule_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="projectId",nullable=false)
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="expertId",nullable=false)
	public Expert getExpert() {
		return expert;
	}
	public void setExpert(Expert expert) {
		this.expert = expert;
	}
	
}
