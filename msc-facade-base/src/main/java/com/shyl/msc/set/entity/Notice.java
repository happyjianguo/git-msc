package com.shyl.msc.set.entity;

import java.util.HashSet;
import java.util.Set;

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
import com.shyl.sys.entity.FileManagement;

/** 公告 */
@Entity
@Table(name = "t_set_notice")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notice extends BasicEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 内容
	 */
	private String content;
	/**
	 * 发布日期
	 */
	private String publishDate;
	
	/**
	 * 发布状态 1发布 0未发布
	 */
	Integer status = 0;
	/**
	 * 附件
	 */
	private Set<FileManagement> fileManagement = new HashSet<FileManagement>();
	
	@Id
	@SequenceGenerator(name = "generator",sequenceName="t_set_notice_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return super.id;
	}
	public void setId(Long id) {
		super.id = id;
	}
	@Column(length=100,nullable=false)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Column(length=1000)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Transient
	public Set<FileManagement> getFileManagement() {
		return fileManagement;
	}
	public void setFileManagement(Set<FileManagement> fileManagement) {
		this.fileManagement = fileManagement;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	
}