<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout"  >
<div  data-options="region:'north',title:'',collapsible:false"  class="my-north" style="height:300px;" >
	<div class="search-bar">
		<form id="form1">
		药品编码
		<input id="productCode" name="productCode" class="easyui-validatebox  easyui-textbox" />
		药品名称
		<input id="productName" name="productName" class="easyui-validatebox  easyui-textbox" />
		生产厂家
		<input id="producerName" name="producerName" class="easyui-validatebox  easyui-textbox" />
		时间段
		<input id="yearS" name="yearS" /> <input id="monthS" name="monthS" /> -
		<input id="yearE" name="yearE" /> <input id="monthE" name="monthE" /> 
		<a id="btn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="dosearch()" >查询  </a> 
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
	var productCode = $("#productCode").textbox("getValue");
	var productName = $("#productName").textbox("getValue");
	var producerName = $("#producerName").textbox('getValue');
	var yearS = $("#yearS").combo('getValue');
	var monthS = $("#monthS").combo('getValue');
	var yearE = $("#yearE").combo('getValue');
	var monthE = $("#monthE").combo('getValue');
	$('#dg').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/goodstrade/page.htmlx",
		queryParams:{
			"query['g#code_S_LK']":productCode,
			"query['g#name_S_LK']":productName,
			"query['g#producerName_S_LK']":producerName,
	        "dateS":yearS+"-"+monthS,
	        "dateE":yearE+"-"+monthE,
	    }
	}); 
}

$(function(){
	initYearCombobox("yearS");
	initMonthCombobox("monthS");
	initYearCombobox("yearE");
	initMonthCombobox("monthE");
	
	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height :$(".my-north").height()-$(".search-bar").height() -12,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/goodstrade/page.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
					{field:'CODE',title:'药品编码',width:15,align:'center'},
		        	{field:'NAME',title:'药品名称',width:15,align:'center'},
					{field:'DOSAGEFORMNAME',title:'剂型',width:15,align:'center'},
					{field:'MODEL',title:'规格',width:15,align:'center'},
					{field:'PRODUCERNAME',title:'生产厂家',width:15,align:'center'},
					{field:'ORDERNUM',title:'采购数量',width:15,align:'center'},
					{field:'ORDERSUM',title:'采购金额（元）',width:15,align:'right',
						formatter: function(value,row,index){
							if (row.ORDERSUM){
								return common.fmoney(row.ORDERSUM);
							}
						}}
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
		columns:[[
					{field:'HOSPITALNAME',title:'医疗机构',width:15,align:'center'},
					{field:'VENDORNAME',title:'供应商',width:15,align:'center'},
					{field:'SL',title:'采购数量',width:15,align:'center'},
					{field:'JE',title:'采购金额（元）',width:15,align:'right',
						formatter: function(value,row,index){
							if (row.JE){
								return common.fmoney(row.JE);
							}
						}},
					{field:'CGSJ',title:'采购时间',width:15,align:'center'}
		   		]]
	});
	
})
function todg2(){
	$('#dg2').datagrid('clearSelections');
	var selrow = $('#dg').datagrid('getSelected');
	var yearS = $("#yearS").combo('getValue');
	var monthS = $("#monthS").combo('getValue');
	var yearE = $("#yearE").combo('getValue');
	var monthE = $("#monthE").combo('getValue');
	$('#dg2').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/count/goodstrade/mxpage.htmlx",  
	    queryParams:{
	        "id":selrow.ID,
	        "dateS":yearS+monthS,
	        "dateE":yearE+monthE
	    }
	}); 
	
}


//=============ajax===============

</script>