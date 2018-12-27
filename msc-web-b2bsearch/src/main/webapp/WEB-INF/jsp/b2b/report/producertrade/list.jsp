<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout" >
	<div class="search-bar" >
	 	生产厂家名称: 
	 	<input id="proName" type="text" class="easyui-textbox" style="width:150px">
                     年份: 
	 	<input id="year"/>
        <a href="#" class="easyui-linkbutton small-btn" iconCls="icon-search" onclick="query()">查询</a>
    	<a href="#"  class="easyui-linkbutton small-btn" data-options="iconCls:'icon-chart_bar'"  onclick="openChart()">图表</a>
	</div>					
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>

</body>
</html>

<script>
//初始化
$(function(){
	initYearCombobox("year");
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/producertrade/page.htmlx",
		queryParams:{
			"year":$('#year').combobox('getValue'),
		},
		pageSize:20,
		pageNumber:1,
		columns:[[
        	{field:'NAME',title:'生产厂家名称',width:10,align:'center'},
        	{field:'GOODSNUM',title:'交易品种数',width:10,align:'center'},
        	{field:'ORDERSUM',title:'交易金额',width:10,align:'center'},
        	{field:'PROPORTION',title:'占总交易金额比例',width:10,align:'center'},
   		]],
   		onDblClickRow:function(index,row){			
        	openChart();
		  	return;
		}
	});
});

function openChart() {
	var obj = $('#dg').datagrid('getSelected');
	if(obj == null){
		showErr("请选择一家生产企业");
		return;
	}
	parent.$.modalDialog({
		title : "生产企业交易额情况",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/producertrade/chart.htmlx",
		queryParams:{
			year: $('#year').combobox('getValue'),
			producerId: obj.PRODUCERID,
			producerName: obj.NAME
		},
		buttons : [{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				top.$.modalDialog.handler.dialog('destroy');
				top.$.modalDialog.handler = undefined;
			}
		}]
	});
}
//搜索
function query(){
	$('#dg').datagrid('load',{
		"name": $('#proName').textbox('getValue'),
		"year": $('#year').combobox('getValue')
	});
}

</script>