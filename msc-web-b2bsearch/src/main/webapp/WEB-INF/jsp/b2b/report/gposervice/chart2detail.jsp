<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="height: 98%;" class="single-dg">
	<table id="dgDetail" ></table>
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
			url: " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/gposervice/chartBDetail.htmlx",
		    queryParams:{
		    	"hospitalCode":'<c:out value='${hospitalCode}'/>',
		    	"year":'<c:out value='${year}'/>',
		    },
			pageSize:10,
			pageNumber:1,
			showFooter: true,
			columns:[[
				{field:'HOSPITALNAME',title:'医院',width:10,align:'center'},
	        	{field:'CODE',title:'订单编号',width:16,align:'center'},
	        	{field:'CODEDETAIL',title:'订单明细编号',width:20,align:'center'},
	        	{field:'ORDERDATE',title:'订单日期',width:10,align:'center'},	
	        	{field:'NUM',title:'订单数量',width:10,align:'center'},
	        	{field:'PRODUCTCODE',title:'药品编码',width:10,align:'center'},	
	        	{field:'PRODUCTNAME',title:'药品名称',width:10,align:'center'},
	        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},	
	        	{field:'MODEL',title:'规格',width:10,align:'center'},
	        	{field:'PACKDESC',title:'包装规格',width:10,align:'center'},
	        	{field:'PRODUCERNAME',title:'生产厂家',width:10,align:'center'},	
	        	{field:'GOODSNUM',title:'采购量',width:10,align:'center'},
	        	{field:'NOTES',title:'断供原因',width:20,align:'center'} 
	   		]],		
		});
	});
</script>