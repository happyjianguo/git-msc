<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >
	<div class="search-bar">
		有效期: <input id="startDate">
        ~ <input id="endDate">
		医院: 
	 	<input style="width:100px" id="hospital"/>
	 	药品名称: 
	 	<input id="productName" class="easyui-textbox" style="width:150px"/>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch()">查询</a>
	</div>
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>
</body>
</html>
<script>
//搜索
var startDate="";
var endDate="";
var hospitalCode="";
function dosearch(){
  	startDate = $('#startDate').datebox('getValue');		
	endDate = $('#endDate').datebox('getValue');
	hospitalCode = $('#hospital').combogrid('getValue');
	var productName = $('#productName').textbox('getValue');
	var data = {
		"startDate": startDate,
		"endDate": endDate,
		"query['c#hospitalCode_S_EQ']":hospitalCode,
		"query['p#name_S_LK']":productName
	};
	$('#dg').datagrid('load',data);
}
//初始化
$(function(){
	$('#startDate').datebox({
		width:130,
	});
	$('#endDate').datebox({
		width:130,
	});
	$('#hospital').combogrid({
		idField:'code',    
		textField:'fullName', 
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/hospital/page.htmlx",
	    queryParams:{
	    	"query['t#isDisabled_I_EQ']":0
		},
		pagination : true,
		pageSize : 10,
		pageNumber : 1,
		width:200,
		panelWidth:300,
		delay:800,
		columns : [ [ {
			field : 'code',
			title : '编码',
			width : 100
		}, {
			field : 'fullName',
			title : '医院名称',
			width : 180
		}]],//panelHeight:'auto',
		keyHandler : {
			query : function(q) {
				//动态搜索
				$('#hospital')
						.combogrid('grid')
						.datagrid(
								"reload",
								{
									"query['t#fullName_S_LK']" : q
								});
				$('#hospital').combogrid("setValue", q);
			}

		},
		onSelect: function(rowIndex, rowData){
			$("#hospitalName").val(rowData.fullName);
		}
	}).combobox("initClear");
	$('#hospital').combogrid('grid').datagrid('getPager').pagination({
		showPageList : false,
		showRefresh : false,
		displayMsg : "共{total}记录"
	});
	//datagrid
	$('#dg').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/contracttrade/page.htmlx",
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		columns:[[
			{field:'CODE',title:'药品编码',width:10,align:'center'},
        	{field:'NAME',title:'药品名称',width:20,align:'center'},
        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},	
        	{field:'MODEL',title:'规格',width:10,align:'center'},		
        	{field:'CONTRACTNUM',title:'合同量',width:10,align:'center'},	
        	{field:'PURCHASEPLANNUM',title:'计划量',width:10,align:'center'},	
        	{field:'PURCHASENUM',title:'采购量',width:10,align:'center'},	
        	{field:'DELIVERYNUM',title:'配送量',width:10,align:'center'},	
        	{field:'RETURNSNUM',title:'退货量',width:10,align:'center'},	
   		]],
   		onDblClickRow:function(index,row){		
        	openDetail(row);
		}
	});
});
function openDetail(row) {
	parent.$.modalDialog({
		title : "明细情况",
		width : 1000,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/contracttrade/detail.htmlx",
		queryParams:{
			productCode: row.CODE,
			startDate: startDate,
			endDate: endDate,
			hospitalCode: hospitalCode
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
//=============ajax===============

</script>