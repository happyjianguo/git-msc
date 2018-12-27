<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout" >
	<div class="search-bar">
		企业类型：
		<select id="companyType" class="easyui-combobox" style="width:100px;" data-options="editable: false">
		    <option value="">请选择</option>
		    <option value="isProducer=1">厂商</option>
		    <option value="isVendor=1">供应商</option>
		</select>
	 	企业名称: 
	 	<input id="companyName" type="text" class="easyui-textbox" style="width:150px">
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="query()">查询</a>
    </div>					
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>

</body>
</html>

<script>
//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/company/page.htmlx",
		pageSize:20,
		pageNumber:1,
		columns:[[
        	{field:'FULLNAME',title:'企业名称',width:10,align:'center'},
        	{field:'RNAME',title:'所属省份',width:10,align:'center'},
        	{field:'AUTHORIZOR',title:'联系人',width:10,align:'center'},
        	{field:'AUTHORIZORPHONE',title:'联系电话',width:10,align:'center'},
   		]],
	});
});


//搜索
function query(){
	$('#dg').datagrid('load',{
		"companyType": $("#companyType").combobox("getValue"),
		"name": $('#companyName').textbox('getValue')
	});
}

</script>