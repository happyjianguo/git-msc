<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<tag:head title="" />

<html>

<body class="easyui-layout"  >
            <div  data-options="region:'north',title:'',collapsible:false"  class="my-north" style="height:300px;" >
				<div class="search-bar" > 
					<form id="form1">
					医疗机构名称
					<input id="name" name="name" class="easyui-validatebox  easyui-textbox" /> 
					查询时间
					<input id="year" name="year" />  
					<input id="month" name="month" />  
 					<a id="btn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="dosearch()" >查询  </a>
					<shiro:hasPermission name="b2b:report:hospitaltrade:export">
						<a href="#"  class="easyui-linkbutton"  data-options="iconCls:'icon-xls'"  onclick="doExport()">导出</a>
					</shiro:hasPermission>
    </form>
				</div>
				<table  id="dg" ></table>
            </div>
            
            <div data-options="region:'center',title:''" class="my-center">
				<table id="dg2" ></table>
            </div>
</body>

</html>
<script>
function dosearch(){
	var isValid = $("#form1").form('validate');
	if(!isValid)
		return;
	var para1 = $("#name").textbox("getValue");
	var para2 = $("#year").combo('getValue');
	var para3 = $("#month").combo('getValue');
	
	$('#dg').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitaltrade/page.htmlx",
		queryParams:{
			"name":para1,
			"year":para2,
			"month":para3
	    }
	}); 
}

function doExport() {
	var isValid = $("#form1").form('validate');
	if(!isValid)
		return;
	var para1 = $("#name").textbox("getValue");
	var para2 = $("#year").combo('getValue');
	var para3 = $("#month").combo('getValue');
	window.open(" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitaltrade/export.htmlx?"
			+"name="+encodeURIComponent(para1)+"&year="+encodeURIComponent(para2)+"&month="+encodeURIComponent(para3));
}

$(function(){

	initYearCombobox("year");
	initMonthCombobox("month");
	
	

	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		showFooter: true,
		singleSelect:true,
		rownumbers:true,
		height :$(".my-north").height()-$(".search-bar").height() -12,
		pagination:true,
		pageSize:50,
		pageNumber:1,
		columns:[[
		        	{field:'HOSPITALNAME',title:'医疗机构',width:15,align:'center'},
		        	{field:'SL',title:'采购品种数',width:15,align:'center'},
					{field:'JE',title:'采购金额（元）',width:15,align:'right',
						formatter: function(value,row,index){
							if (row.JE){
								return common.fmoney(row.JE);
							}
						}},
					{field:'PER',title:'占总采购额比例',width:15,align:'center'}
		   		]],
		onClickRow: function(index,row){
			todg2();  
		},
		onLoadSuccess: function(data){
			
		}
	});

	//组成员
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		rownumbers:true,
		height :'100%',
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
					{field:'NAME',title:'通用名（商品名）',width:15,align:'center'},
					{field:'DOSAGEFORMNAME',title:'剂型',width:15,align:'center'},
					{field:'MODEL',title:'规格',width:15,align:'center'},
					{field:'PRODUCERNAME',title:'生产厂家',width:15,align:'center'},
					{field:'VENDORNAME',title:'供应商',width:15,align:'center'},
					{field:'SL',title:'采购数量',width:15,align:'center'},
					{field:'JE',title:'采购金额（元）',width:15,align:'right',
						formatter: function(value,row,index){
							if (row.JE){
								return common.fmoney(row.JE);
							}
						}}
		   		]]
	});
	
})
function todg2(){
	$('#dg2').datagrid('clearSelections');
	var selrow = $('#dg').datagrid('getSelected');

	var para2 = $("#year").combo('getValue');
	var para3 = $("#month").combo('getValue');
	
	$('#dg2').datagrid({  
	    url:' <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitaltrade/mxpage.htmlx',  
	    queryParams:{
	        "hospitalcode":selrow.HOSPITALCODE,
	        "year":para2,
			"month":para3
	    }
	}); 
	
}

//=============ajax===============

</script>