<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >
	<div class="search-bar">
		时间段
		<input class="easyui-datebox" style="width:110px" id="startDate"/> -
		<input class="easyui-datebox" style="width:110px" id="toDate"/>
		医院: 
	 	<input style="width:100px" id="hospital"/>
	 	通 用 名: 
	 	<input id="genaricname"/>
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
	var startDate = $("#startDate").combo('getValue');
	var endDate = $("#toDate").combo('getValue');
	if(startDate > endDate){
		showErr("起始日期不能大于结束日期");
		return ;
	}
	hospitalCode = $('#hospital').combogrid('getValue');
	var genaricname = $('#genaricname').textbox('getValue');
	var data = {
		"startDate": startDate,
		"endDate": endDate,
		"query['hp#hospitalCode_S_EQ']":hospitalCode,
		"query['pd#GENERICNAME_S_LK']":genaricname
	};
	$('#dg').datagrid('load',data);
}
//初始化
$(function(){
	/* initYearCombobox("yearS");
	initMonthCombobox("monthS");
	initYearCombobox("yearE");
	initMonthCombobox("monthE"); */
	//日期
	$('#startDate').datebox({
		editable:false
	});
	$('#toDate').datebox({
		editable:false
	});
	$('#genaricname').textbox({
		width:150
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitalplan/page.htmlx",
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
			{field:'CODE',title:'项目编码',width:10,align:'center'},
        	{field:'NAME',title:'项目名称',width:20,align:'center'},
        	{field:'GENERICNAME',title:'通 用 名',width:20,align:'center'},
        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},	
        	{field:'MODEL',title:'规格',width:10,align:'center'},
        	{field:'QUALITYLEVEL',title:'质量层次',width:10,align:'center'},
        	{field:'NUM',title:'报量',width:10,align:'center'},
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
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitalplan/detail.htmlx",
		queryParams:{
			pdId: row.PDID,
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