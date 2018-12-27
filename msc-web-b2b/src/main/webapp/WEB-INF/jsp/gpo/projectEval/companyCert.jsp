<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class='single-dg'>
		<table  id="dg" ></table>
	</div>


<script>
//初始化
$(function(){
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height:350,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEval/companyCertList.htmlx",
		queryParams:{  
			"query['t#company.id_L_EQ']": '${company.id }'
		},
		pageSize:10,
		pageNumber:1,
		columns:[[
        	{field:'typeName',title:'类型',width:10,align:'center'},
        	{field:'code',title:'代码',width:10,align:'center'},
        	{field:'name',title:'名称',width:10,align:'center'},
        	{field:'status',title:'证照状态',width:10,align:'center',formatter: function(value,row,index){
        		return row.status;
        	}},
        	{field:'directory.minUnit',title:'有效期',width:10,align:'center',formatter: function(value,row,index){
        		return row.issueDate+"-"+row.validDate;
        	}}
   		]]
	});
});
</script>