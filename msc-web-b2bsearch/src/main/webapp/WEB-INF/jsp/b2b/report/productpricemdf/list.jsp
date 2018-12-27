<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout" >
	<div class="search-bar" >
	 	药品名称: 
	 	<input id="productName" type="text" class="easyui-textbox" style="width:150px">
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="query()">查询</a>
    	<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-chart_bar'"  onclick="openChart()">图表</a>
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productPriceMdf/page.htmlx",
		pageSize:20,
		pageNumber:1,
		columns:[[
        	{field:'productCode',title:'药品编码',width:10,align:'center'},
        	{field:'productName',title:'药品名称',width:10,align:'center'},
        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center',
				formatter: function(value,row,index){
					return row.segmentMap.product.dosageFormName;
				}},
        	{field:'MODEL',title:'规格',width:10,align:'center',
				formatter: function(value,row,index){
					return row.segmentMap.product.model;
				}},
        	{field:'PACKDESC',title:'包装规格',width:10,align:'center',
				formatter: function(value,row,index){
					return row.segmentMap.product.packDesc;
				}},
        	{field:'PRODUCERNAME',title:'生产厂家',width:10,align:'center',
				formatter: function(value,row,index){
					return row.segmentMap.product.producerName;
				}},
        	{field:'STANDARDCODE',title:'药品本位码',width:10,align:'center',
				formatter: function(value,row,index){
					return row.segmentMap.product.standardCode;
				}},/* 
        	{field:'NATIONALCODE',title:'国药准字号',width:10,align:'center'}, */
        	{field:'gpoName',title:'gpo名称',width:10,align:'center'},
        	{field:'finalPrice',title:'当前价格',width:10,align:'center',
				formatter: function(value,row,index){
					return common.fmoney(row.finalPrice);
				}},
        	{field:'lastPrice',title:'最近一次价格',width:10,align:'center',
				formatter: function(value,row,index){
					return common.fmoney(row.lastPrice);
				}},
			{field:'priceCount',title:'异动次数',width:10,align:'center',sortable:true}
   		]],
   		onDblClickRow:function(index,row){			
        	openMX(row);
		  	return;
		}
	});
});

function openMX(row) {
	
	top.$.modalDialog({
		title : "历史价格",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productPriceMdf/detail.htmlx",
		queryParams:{
			"productCode":row.productCode,
			"gpoCode":row.gpoCode
		}
	});
}
function openChart() {
	var obj = $('#dg').datagrid('getSelected');
	if(obj == null){
		showErr("请选择一家供应商");
		return;
	}
	top.$.modalDialog({
		title : "价格变动情况",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productPriceMdf/chart.htmlx",
		queryParams:{
			"productCode":obj.productCode
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
		"query[t#productName_S_LK]":$('#productName').textbox('getValue')
	});
}

</script>