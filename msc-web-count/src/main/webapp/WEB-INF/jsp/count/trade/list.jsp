<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout" >
	<div class="search-bar" >
	 	年份: 
	 	<input id="year"/>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="query()">查询</a>
        <a href="#" class="easyui-linkbutton"  iconCls="icon-chart_bar" onclick="openChart()">图表</a>
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
		showFooter: true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		//pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/trade/list.htmlx",
		queryParams:{
			"year":$('#year').combobox('getValue'),
		},
		//pageSize:20,
		//pageNumber:1,
		columns:[[
        	{field:'MONTH',title:'月份',width:10,align:'center'},
        	{field:'ORDERSUM',title:'总交易金额（元）',width:20,align:'right',
				formatter: function(value,row,index){
					if (row.ORDERSUM){
						return common.fmoney(row.ORDERSUM);
					}
				}}
   		]],
	});
});


//搜索
function query(){
	$('#dg').datagrid('load',{
		"year": $('#year').combobox('getValue')
	});
}


//图表窗口
function openChart() {
	top.$.modalDialog({
		title : "交易汇总统计",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/count/trade/chart.htmlx",
		queryParams:{
			year: $('#year').combobox('getValue')
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



</script>