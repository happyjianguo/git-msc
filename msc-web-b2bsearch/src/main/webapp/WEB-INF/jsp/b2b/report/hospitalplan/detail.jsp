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
			url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitalplan/mxlist.htmlx",  
		    queryParams:{
		    	"query['hp#hospitalCode_S_EQ']":'<c:out value='${hospitalCode}'/>',
		    	"query['pd#id_L_EQ']":'<c:out value='${pdId}'/>',
		    	"startDate":'<c:out value='${startDate}'/>',
		    	"endDate":'<c:out value='${endDate}'/>',
		    },
			pageSize:10,
			pageNumber:1,
			showFooter: true,
			columns:[[
				{field:'CODE',title:'项目编码',width:10,align:'center'},
	        	{field:'NAME',title:'项目名称',width:20,align:'center'},
	        	{field:'GENERICNAME',title:'通 用 名',width:20,align:'center'},
	        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},	
	        	{field:'MODEL',title:'规格',width:10,align:'center'},
	        	{field:'QUALITYLEVEL',title:'质量层次',width:10,align:'center'},	
	        	{field:'NUM',title:'报量',width:10,align:'center'},
	        	{field:'HOSPITALNAME',title:'医院',width:20,align:'center'},	
	   		]],		
		});
	});
</script>