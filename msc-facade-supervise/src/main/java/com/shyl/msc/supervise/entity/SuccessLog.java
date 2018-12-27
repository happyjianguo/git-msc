package com.shyl.msc.supervise.entity;

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
 * kettle日志类
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "success_log")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class SuccessLog extends BasicEntity {
	private String tableName;
	private Integer inRow;
	private Integer saveRow;
	private Integer updateRow;
	private Integer abandonRow;
	private String pushDate;
	private String orgCode;

	/** 主键 */
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "success_log_s")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "table_name", length = 50)
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Column(name = "in_row")
	public Integer getInRow() {
		return inRow;
	}

	public void setInRow(Integer inRow) {
		this.inRow = inRow;
	}

	@Column(name = "save_row")
	public Integer getSaveRow() {
		return saveRow;
	}

	public void setSaveRow(Integer saveRow) {
		this.saveRow = saveRow;
	}

	@Column(name = "update_row")
	public Integer getUpdateRow() {
		return updateRow;
	}

	public void setUpdateRow(Integer updateRow) {
		this.updateRow = updateRow;
	}

	@Column(name = "abandon_row")
	public Integer getAbandonRow() {
		return abandonRow;
	}

	public void setAbandonRow(Integer abandonRow) {
		this.abandonRow = abandonRow;
	}

	@Column(name = "push_date", length = 30)
	public String getPushDate() {
		return pushDate;
	}

	public void setPushDate(String pushDate) {
		this.pushDate = pushDate;
	}

	@Column(name = "org_code", length = 30)
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

}
