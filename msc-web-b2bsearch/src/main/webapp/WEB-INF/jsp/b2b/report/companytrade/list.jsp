<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout" >
	<div class="search-bar">
		
	 	供应商: 
	 	<input id="companyName" type="text" class="easyui-textbox" style="width:150px">
                     年份: 
	 	<input id="year"/>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="query()">查询</a>
    	<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-chart_bar'"  onclick="openChart2()">图表</a>
	</div>					
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>

</body>
</html>

<script>
var rows = new Array();
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
		height : $(this).height() - $(".search-bar").height() -16,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/companytrade/page.htmlx",
		queryParams:{
			"year":$('#year').combobox('getValue')
		},
		pageSize:50,
		pageNumber:1,
		columns:[[
        	{field:'NAME',title:'企业名称',width:10,align:'center'},
        	{field:'GOODSNUM',title:'交易品种数',width:10,align:'center'},
        	{field:'ORDERSUM',title:'交易金额（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.ORDERSUM){
						return common.fmoney(row.ORDERSUM);
					}
				}},
        	{field:'PROPORTION',title:'占总交易金额比例',width:10,align:'center'},
   		]],
   		onDblClickRow:function(index,row){			
        	//openChart();
		  	return;
		},
		onLoadSuccess:function(data){
			if(data.rows){
				rows = data.rows;
			}
		}
	});
});

function openChart2(){
	if(rows.length == 0){
		showErr("没有数据");
		return;
	}
	top.$.modalDialog.data = [ $('#dg').datagrid("getRows") ];
	top.$.modalDialog({
		title : "企业交易额对比 ",
		width : 1000,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/companytrade/chart2.htmlx",
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

function openChart() {
	var obj = $('#dg').datagrid('getSelected');
	if(obj == null){
		showErr("请选择一家企业");
		return;
	}
	top.$.modalDialog({
		title : "企业交易额情况 - "+obj.NAME,
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/companytrade/chart.htmlx",
		queryParams:{
			year: $('#year').combobox('getValue'),
			id: obj.ID
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
		"name": $('#companyName').textbox('getValue'),
		"year": $('#year').combobox('getValue')
	});
}

</script>