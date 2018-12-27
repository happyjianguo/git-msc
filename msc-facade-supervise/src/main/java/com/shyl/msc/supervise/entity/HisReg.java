package com.shyl.msc.supervise.entity;

import java.math.BigDecimal;
import java.util.Date;

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
 * 出院记录
 *
 * @author Administrator
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "sup_his_reg")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HisReg extends BasicEntity {

    /**
     * 诊断流水 hospitalCode||inSno
     */
    public String code;
    /**
     * 诊断流水号
     */
    public String inSno;
    /**
     * 医院编码
     */
    public String hospitalCode;
    /**
     * 医院编码
     */
    public String hospitalName;
    /**
     * 科室编码
     **/
    public String departCode;
    /**
     * 科室名称
     **/
    public String departName;
    /**
     * 医生编码
     **/
    public String doctorCode;
    /**
     * 医生名称
     **/
    public String doctorName;
    /**
     * 病人编号
     **/
    public String patientId;
    /**
     * 病人名称
     */
    public String patienName;
    /**
     * 性别
     */
    public String sex;
    /**
     * 出院日期
     **/
    public Date cdate;
    /**
     * 是否进行手术
     **/
    public Integer isOperation;
    /**
     * 医院收入汇总
     **/
    public BigDecimal sum;
    /**
     * 药品收入汇总
     **/
    public BigDecimal drugSum;
    /**
     * 非药品收入汇总
     **/
    public BigDecimal otherSum;
    /**
     * 基本药物收入汇总
     */
    public BigDecimal baseDrugSum;
    /**
     * 抗菌药物收入汇总
     */
    public BigDecimal absDrugSum;
    /**
     * 住院天数
     */
    public Integer daySum;
    /**
     * ddd值累计
     **/
    public BigDecimal dddSum;
    /**
     * ddd强度（抗菌药物sum/住院天数*100）
     **/
    public BigDecimal dddIntensity;
    /**
     * 总品规数
     **/
    public Integer drugNum;
    /**
     * 抗菌药物收入汇总（非限制使用级)
     */
    public BigDecimal absType1Sum;
    /**
     * 抗菌药物收入汇总（限制使用级)
     */
    public BigDecimal absType2Sum;
    /**
     * 抗菌药物收入汇总（特殊使用级)
     */
    public BigDecimal absType3Sum;
    /**
     * 年龄
     **/
    private Integer age;
    /**
     * 基本药物类型
     **/
    private String baseDrugType;
    /**
     * 抗菌药物类型
     **/
    private String absDrugType;
    /**
     * 特殊药品类型
     **/
    private String specialDrugType;
    /**
     * 医保药品类型
     **/
    private String insuranceDrugType;
    /**
     * 辅助药品类型
     **/
    private String auxiliaryType;
    /**
     * 药品新增
     **/
    private String ypxz;

    /**
     * 主键
     */
    @Id
    @SequenceGenerator(name = "generator", sequenceName = "sup_his_reg_s")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "generator")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(length = 50, unique = true, nullable = false, updatable = false)
    public String getCode() {
        return code;
    }

    public String getInSno() {
        return inSno;
    }

    public String getHospitalCode() {
        return hospitalCode;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getDepartCode() {
        return departCode;
    }

    public String getDepartName() {
        return departName;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatienName() {
        return patienName;
    }

    public Date getCdate() {
        return cdate;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getDrugSum() {
        return drugSum;
    }

    public BigDecimal getOtherSum() {
        return otherSum;
    }

    public BigDecimal getBaseDrugSum() {
        return baseDrugSum;
    }

    public BigDecimal getAbsDrugSum() {
        return absDrugSum;
    }

    public Integer getDaySum() {
        return daySum;
    }

    public BigDecimal getDddSum() {
        return dddSum;
    }

    public BigDecimal getDddIntensity() {
        return dddIntensity;
    }

    public Integer getDrugNum() {
        return drugNum;
    }

    public BigDecimal getAbsType1Sum() {
        return absType1Sum;
    }

    public BigDecimal getAbsType2Sum() {
        return absType2Sum;
    }

    public BigDecimal getAbsType3Sum() {
        return absType3Sum;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setInSno(String inSno) {
        this.inSno = inSno;
    }

    public void setHospitalCode(String hospitalCode) {
        this.hospitalCode = hospitalCode;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setDepartCode(String departCode) {
        this.departCode = departCode;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setPatienName(String patienName) {
        this.patienName = patienName;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public void setDrugSum(BigDecimal drugSum) {
        this.drugSum = drugSum;
    }

    public void setOtherSum(BigDecimal otherSum) {
        this.otherSum = otherSum;
    }

    public void setBaseDrugSum(BigDecimal baseDrugSum) {
        this.baseDrugSum = baseDrugSum;
    }

    public void setAbsDrugSum(BigDecimal absDrugSum) {
        this.absDrugSum = absDrugSum;
    }

    public void setDaySum(Integer daySum) {
        this.daySum = daySum;
    }

    public void setDddSum(BigDecimal dddSum) {
        this.dddSum = dddSum;
    }

    public void setDddIntensity(BigDecimal dddIntensity) {
        this.dddIntensity = dddIntensity;
    }

    public void setDrugNum(Integer drugNum) {
        this.drugNum = drugNum;
    }

    public void setAbsType1Sum(BigDecimal absType1Sum) {
        this.absType1Sum = absType1Sum;
    }

    public void setAbsType2Sum(BigDecimal absType2Sum) {
        this.absType2Sum = absType2Sum;
    }

    public void setAbsType3Sum(BigDecimal absType3Sum) {
        this.absType3Sum = absType3Sum;
    }

    public Integer getIsOperation() {
        return isOperation;
    }

    public void setIsOperation(Integer isOperation) {
        this.isOperation = isOperation;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBaseDrugType() {
        return baseDrugType;
    }

    public void setBaseDrugType(String baseDrugType) {
        this.baseDrugType = baseDrugType;
    }

    public String getAbsDrugType() {
        return absDrugType;
    }

    public void setAbsDrugType(String absDrugType) {
        this.absDrugType = absDrugType;
    }

    public String getSpecialDrugType() {
        return specialDrugType;
    }

    public void setSpecialDrugType(String specialDrugType) {
        this.specialDrugType = specialDrugType;
    }

    public String getInsuranceDrugType() {
        return insuranceDrugType;
    }

    public void setInsuranceDrugType(String insuranceDrugType) {
        this.insuranceDrugType = insuranceDrugType;
    }

    public String getAuxiliaryType() {
        return auxiliaryType;
    }

    public void setAuxiliaryType(String auxiliaryType) {
        this.auxiliaryType = auxiliaryType;
    }

    public String getYpxz() {
        return ypxz;
    }

    public void setYpxz(String ypxz) {
        this.ypxz = ypxz;
    }

    @Column(length = 10)
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

}
