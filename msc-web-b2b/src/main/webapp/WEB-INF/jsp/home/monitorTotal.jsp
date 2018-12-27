<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
a{
	color: #333;
	text-decoration:none;
}
a:hover{
	color: #2589F3;
	text-decoration:underline;
}
</style>
<div style="line-height: 30px;font-size:12px;margin-left:15px;margin-top:5px;"><a href="#" onclick="toProduct()">药品总数：<c:out value='${productCount}'/></a></div>
<div style="line-height: 30px;font-size:12px;margin-left:15px;"><a href="#" onclick="toHospital()">医疗机构总数：<c:out value='${hospitalCount}'/></a></div>
<div style="line-height: 30px;font-size:12px;margin-left:15px;"><a href="#" onclick="toProducer()">生产厂商总数：<c:out value='${producerCount}'/></a></div>
<div style="line-height: 30px;font-size:12px;margin-left:15px;"><a href="#" onclick="toVendor()">供应商总数：<c:out value='${vendorCount}'/></a></div>
<script>
	function toProduct(){
		top.addTab("药品查询 ", "/dm/product.htmlx",null, true);
	}
	function toHospital(){
		top.addTab("医疗机构查询 ", "/set/hospital.htmlx",null, true);
	}
	function toProducer(){
		top.addTab("生产厂商查询", "/set/company.htmlx?companyType=0",null, true);
	}
	function toVendor(){
		top.addTab("供应商查询", "/set/company.htmlx?companyType=1",null, true);
	}
</script>