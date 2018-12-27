package com.shyl.msc.set.entity;

import com.shyl.common.entity.BasicEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

@Entity
@Table(name="t_set_monitorTask")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MonitorTask extends BasicEntity {
    private static final long serialVersionUID = 6075531684657460250L;
    /**
     * 主键id
     */
    private Long id;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 开始时间
     */
    private Date beginDate;
    /**
     * 执行时间(单位：秒)
     */
    private Long executeTime;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 改变的记录数
     */
    private Integer changeNum;
    /**
     * 异常
     */
    private String exceptions;
    /**
     * 是否执行成功
     */
    private Integer isSuccess;

    @Override
    @Id
    @SequenceGenerator(name = "generator",sequenceName="t_set_monitorTask_s")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Column(length=50)
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }


    public Long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Long executeTime) {
        this.executeTime = executeTime;
    }

    @Column(length=50)
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    public Integer getChangeNum() {
        return changeNum;
    }

    public void setChangeNum(Integer changeNum) {
        this.changeNum = changeNum;
    }

    @Column(length=500)
    public String getExceptions() {
        return exceptions;
    }

    public void setExceptions(String exceptions) {
        this.exceptions = exceptions;
    }

    public Integer getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }

}
