<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>
<body class="easyui-layout" >

<div data-options="region:'center',title:''" >
    <table id="dg"></table>
</div>

</body>
</html>

<script>
//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-2,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/diseaseAnalysis/product.htmlx",
		queryParams:eval("("+decodeURIComponent("<c:out value='${jsonStr}'/>")+")"),
		pageSize:20,
		pageNumber:1,
		columns:[ [
					{field:'productCode',title:'药品编码',width:100,align:'center'},
		        	{field:'productName',title:'药品名称',width:75,align:'center'},
		        	{field:'model',title:'规格',width:100,align:'center'},
		        	{field:'packDesc',title:'包装',width:100,align:'center'},
		        	{field:'dosageFormName',title:'剂型',width:100,align:'center'},
		        	{field:'producerName',title:'生产厂家',width:100,align:'center'},
		        	{field:'sum',title:'药品费用',width:100,align:'center'},
		        	{field:'num',title:'药品用量',width:100,align:'center'},
		        	{field:'ddd',title:'DDD',width:100,align:'center'},
		        	{field:'dddc',title:'DDDC',width:100,align:'center'},
		        	{field:'ddds',title:'DDDS',width:100,align:'center'}
			   ]],
		onLoadSuccess:function(){
			$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});
	

	
});
</script>