<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >  

	<div class="search-bar">
		<form id="form1">
		医疗机构名称
		<input id="name" name="name" class="easyui-validatebox  easyui-textbox" /> 
		日期: <input class="easyui-datebox" style="width:110px" id="dateS" data-options="required:true" value="<c:out value='${dateS }'/>">
	    				~ <input class="easyui-datebox" style="width:110px" id="dateE" data-options="required:true" value="<c:out value='${dateE }'/>">
			<a id="btn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="dosearch()" >查询  </a> 
		    <a href="#"  class="easyui-linkbutton"  data-options="iconCls:'icon-chart_bar'"  onclick="openChart2()">图表</a>
        <shiro:hasPermission name="b2b:report:hospitaltradejbgoods:export">
            <a href="#"  class="easyui-linkbutton"  data-options="iconCls:'icon-xls'"  onclick="doExport()">导出</a>
        </shiro:hasPermission>
		</form>
	</div>
	<div class="single-dg">
	<table  id="dg" ></table>
	</div>

</body>
</html>
<script>
var rows = new Array();
function dosearch(){
	var isValid = $("#form1").form('validate');
	if(!isValid)
		return;
	var para1 = $("#name").textbox("getValue");
	var para2 = $("#dateS").combo('getValue');
	var para3 = $("#dateE").combo('getValue');
	
	$('#dg').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitaltradejbgoods/page.htmlx",
		queryParams:{
			"name":para1,
			"query[o#createDate_D_GE]":para2,
			"query[o#createDate_D_LE]":para3
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
		pagination:true,
        pageList : [10,50,100],
        pageSize:100,
		pageNumber:1,
		columns:[[
		        	{field:'HOSPITALNAME',title:'医疗机构',width:20,align:'center'},   	
		        	{field:'SUM',title:'采购总金额（元）',width:30,align:'right',
						formatter: function(value,row,index){
							if (row.SUM){
								return common.fmoney(row.SUM);
							}
						}}, 	
		        	{field:'SBSUM',title:'社保药物采购金额（元）',width:30,align:'right',
						formatter: function(value,row,index){
							if (row.SBSUM){
								return common.fmoney(row.SBSUM);
							}
						}},
		        	{field:'JBSUM',title:'基本药物采购金额（元）',width:30,align:'right',
						formatter: function(value,row,index){
							if (row.JBSUM){
								return common.fmoney(row.JBSUM);
							}
						}},
		        	{field:'FJBSUM',title:'非基本药物采购金额（元）',width:30,align:'right',
						formatter: function(value,row,index){
							if (row.FJBSUM){
								return common.fmoney(row.FJBSUM);
							}
						}}
		   		]],
		onDblClickRow: function(index,field,value){
			openChart();
		},
		onLoadSuccess:function(data){
			if(data.rows){
				rows = data.rows;
			}
		}
	});
	dosearch();
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
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitaltradejbgoods/chart2.htmlx",
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
//图表窗口
function openChart() {
	var selrow = $('#dg').datagrid('getSelected');
	if(selrow == null){
		alert("请选择一家医疗机构");
		return;
	}
	top.$.modalDialog({
		title : "医疗机构采购汇总 － "+selrow.HOSPITALNAME,
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitaltradejbgoods/chart.htmlx",
		queryParams:{
			name: selrow.HOSPITALCODE,
			dateS:$("#dateS").combo('getValue'),
			dateE:$("#dateE").combo('getValue')
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
function doExport() {
	var isValid = $("#form1").form('validate');
	if(!isValid)
		return;
	var para1 = $("#name").textbox("getValue");
	var para2 = $("#dateS").combo('getValue');
	var para3 = $("#dateE").combo('getValue');
	<%--$.ajax({--%>
        <%--type:'POST',--%>
        <%--data:--%>
            <%--[{'name':para1},--%>
            <%--{'query[o#createDate_D_GE]':para2},--%>
            <%--{'query[o#createDate_D_LE]':para3}],--%>
        <%--url:'<c:out value="${pageContext.request.contextPath }"/>/b2b/report/hospitaltradejbgoods/export.htmlx'--%>
    <%--});--%>
	window.open(" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitaltradejbgoods/export.htmlx?name="+encodeURIComponent(para1)
			+"&"+encodeURIComponent("query[o#createDate_D_GE]")+"="+para2
			+"&"+encodeURIComponent("query[o#createDate_D_LE]")+"="+para3);
	
	
}
//=============ajax===============

</script>