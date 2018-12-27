<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="height: 100%;">
	<table id="dgDetail"></table>
</div>



<script>
	//初始化
	$(function() {
		$('#dgDetail').datagrid({
			fitColumns:true,
			striped:true,
			singleSelect:true,
			rownumbers:true,
			border:true,
			height : '100%',
			pagination: true,
			url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/contracttrade/mxlist.htmlx",  
		    queryParams:{
		    	"query['c#hospitalCode_S_EQ']":'<c:out value='${hospitalCode}'/>',
		    	"query['p#code_S_EQ']":'<c:out value='${productCode}'/>',
		    	"startDate":'<c:out value='${startDate}'/>',
		    	"endDate":'<c:out value='${endDate}'/>',
		    },
			pageSize:10,
			pageNumber:1,
			showFooter: true,
			columns:[[
				{field:'CODE',title:'药品编码',width:10,align:'center'},
	        	{field:'NAME',title:'药品名称',width:20,align:'center'},
	        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},	
	        	{field:'MODEL',title:'规格',width:10,align:'center'},		
	        	{field:'CONTRACTNUM',title:'合同量',width:10,align:'center'},	
	        	{field:'PURCHASEPLANNUM',title:'计划量',width:10,align:'center'},	
	        	{field:'PURCHASENUM',title:'采购量',width:10,align:'center'},	
	        	{field:'DELIVERYNUM',title:'配送量',width:10,align:'center'},	
	        	{field:'RETURNSNUM',title:'退货量',width:10,align:'center'},	
	        	{field:'DETAILCODE',title:'合同明细编号',width:15,align:'center'},	
	        	{field:'HOSPITALNAME',title:'医院',width:20,align:'center'},	
	        	{field:'VENDORNAME',title:'供应商',width:20,align:'center'},	
	   		]],		
		});
	});
</script>