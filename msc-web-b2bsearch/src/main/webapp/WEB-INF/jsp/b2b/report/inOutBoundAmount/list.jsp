<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="入库金额统计" />
<html>
<body class="easyui-layout" >
	<div class="search-bar" >
		医院 :<input id="hospitalCode" name="hospitalCode" style="width:80px;"/>
		<span class="datagrid-btn-separator split-line" ></span>
                       时间 :
		<input class="easyui-datebox" style="width:100px" id="startDate"/> -
		<input class="easyui-datebox" style="width:100px" id="toDate"/>
  			<span class="datagrid-btn-separator split-line" ></span>
     	<a href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="query_btn">查询</a>
     	<span class="datagrid-btn-separator split-line" ></span>
		<shiro:hasPermission name="b2b:report:inOutBoundAmount:export">
				<a href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-xls'" id="query_xls">导出Excel</a>
		</shiro:hasPermission>
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/inOutBoundAmount/page.htmlx",
		pageSize:50,
		pageNumber:1,
		showFooter: true,
		columns:[[
        	{field:'HOSPITALNAME',title:'医疗机构名称',width:10,align:'center'},
        	{field:'GOODSSUM',title:'总入库金额',width:10,align:'center'},
        	{field:'GPOGOODSSUM',title:'GPO入库金额',width:10,align:'center'},
        	{field:'OTHERGOODSSUM',title:'非GPO入库金额',width:10,align:'center'},
   		]]
	});
	//搜索功能
	$("#query_btn").click(function() {
		var hospitalCode = $("#hospitalCode").combobox("getValue");
		var startDate = $("#startDate").combo('getValue');
		var endDate = $("#toDate").combo('getValue');
		if(startDate > endDate){
			showErr("起始日期不能大于结束日期");
			return ;
		}
		var data = {
			"query['b#hospitalCode_S_EQ']":hospitalCode,
			"query['d#orderDate_D_GE']":startDate,
			"query['d#orderDate_D_LE']":endDate
		};
		$('#dg').datagrid('load',data);
	});
	//导出
	$("#query_xls").click(function() {
		var hospitalCode = $("#hospitalCode").combobox("getValue");
		var startDate = $("#startDate").combo('getValue');
		var endDate = $("#toDate").combo('getValue');
		if(startDate > endDate){
			showErr("起始日期不能大于结束日期");
			return ;
		}
		var data = {
			"query['b#hospitalCode_S_EQ']":hospitalCode,
			"query['d#orderDate_D_GE']":startDate,
			"query['d#orderDate_D_LE']":endDate
		};
		var url = " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/inOutBoundAmount/export.htmlx?";
		for (var k in data) {
			url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(data[k]);
		}
		window.open(url);
	});
	$('#hospitalCode').combogrid({
		idField:'code',    
		textField:'fullName',
		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/hospital/listByCounty.htmlx',
		pagination:false,
		queryParams:{
			"query['t#isDisabled_I_EQ']" : 0
		},
	    columns: [[
	        {field:'code',title:'机构编码',width:150},
	        {field:'fullName',title:'机构名称',width:200}
	    ]],
	    width:160,
	    panelWidth:360,
		delay:800,
		prompt:"名称模糊搜索",
		keyHandler: { 
	        enter: function(e) {
                $('#hospitalCode').combogrid('grid').datagrid("reload",{
        			"query['t#isDisabled_I_EQ']" : 0,
        			"query['t#fullName_S_LK']":$('#hospitalCode').combogrid("getText")});
                $('#hospitalCode').combogrid("setValue", $('#hospitalCode').combogrid("getText"));
	        }
        }
	}).combobox("initClear");
	$('#hospitalCode').combogrid('grid').datagrid("reload",{
		"query['t#isDisabled_I_EQ']" : 0,
		"query['t#fullName_S_LK']":$('#hospitalCode').combogrid("getText")});
});
</script>