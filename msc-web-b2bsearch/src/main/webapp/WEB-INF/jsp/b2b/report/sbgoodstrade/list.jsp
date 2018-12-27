<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >  
	<div class="search-bar">
		<form id="form1">
		年份
		<input id="year" name="year" /> 
			<a id="btn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="dosearch()" >查询  </a> 
		<a href="#"  class="easyui-linkbutton"  data-options="iconCls:'icon-chart_bar'"  onclick="openChart()">图表</a>
		
		</form>
	</div>
	<div class="single-dg">
	<table  id="dg" ></table>
	</div>
	



</body>
</html>
<script>
function dosearch(){
	var isValid = $("#form1").form('validate');
	if(!isValid)
		return;
	var para1 = $("#year").combo("getValue");
	$('#dg').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/sbgoodstrade/page.htmlx",
		queryParams:{
			"year":para1
	    }
	}); 
}


//初始化
$(function(){
	
	initYearCombobox("year");
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		showFooter: true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		//pagination:true,
		pageSize:20,
		pageNumber:1,
		columns:[[
		        	{field:'YM',title:'年-月',width:10,align:'center'},
		        	{field:'SBSUM',title:'社保药品交易金额（元）',width:30,align:'right',
						formatter: function(value,row,index){
							if (row.SBSUM){
								return common.fmoney(row.SBSUM);
							}
						}},
					{field:'FSBSUM',title:'非社保药品交易金额（元）',width:30,align:'right',
						formatter: function(value,row,index){
							if (row.FSBSUM){
								return common.fmoney(row.FSBSUM);
							}
						}}
		   		]]
	});
	dosearch();
});

//图表窗口
function openChart() {
	top.$.modalDialog({
		title : "社保药品汇总统计",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/sbgoodstrade/chart.htmlx",
		queryParams:{
			year: $("#year").combo("getValue")
		},
		buttons : [ {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				top.$.modalDialog.handler.dialog('destroy');
				top.$.modalDialog.handler = undefined;
			}
		}]
	});
}
//=============ajax===============

</script>