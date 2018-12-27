<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout"  >
<div  data-options="region:'north',title:'',collapsible:false"  class="my-north" style="height:300px;" >
	<div id='tb' class="search-bar">
		年月：
		<input  style="width:100px" id="month"/>
		<span class="datagrid-btn-separator split-line" ></span>
	 	药品名称：
	 	<input id="productName" class="easyui-textbox" />
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch()">查询</a>
	</div>
	<table  id="dg" ></table>
</div>

<div data-options="region:'center',title:''" class="my-center">
	<table id="dg2" ></table>
</div>
</body>

</html>
<script>
//搜索
function dosearch(){
	var month = $("#month").datebox('getValue');
	var productName = $('#productName').textbox('getValue');
	if(month == ""){
		showMsg("请选择【年月】");
		return;
	}
	var data = {
		"query['t#month_S_EQ']":month,
		"query['t#product.name_S_LK']":productName,
	};
	$('#dg').datagrid('load',data);
}

$(function(){
	easyuiMonth($("#month"));
	
	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height :$(".my-north").height()-$(".search-bar").height() -12,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/productC2/page.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
					{field:'product.code',title:'药品编码',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.code;
							}
					}},
					{field:'product.name',title:'药品名称',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.name;
							}
					}},
		        	{field:'product.dosageFormName',title:'剂型',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.dosageFormName;
							}
					}},
		        	{field:'product.model',title:'规格',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.model;
							}
					}},
					{field:'product.packDesc',title:'包装',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.packDesc;
							}
					}},
		        	{field:'product.producerName',title:'厂家',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.producerName;
							}
					}},
					{field:'purchaseNum',title:'交易数量',width:10,align:'center',sortable:"true"},
		        	{field:'purchaseSum',title:'交易金额',width:10,align:'center',sortable:"true"}
		   		]],
		onClickRow: function(index,row){
			todg2(row);  
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
					{field:'month',title:'年月',width:10,align:'center'},
					{field:'hospitalName',title:'医疗机构',width:15,align:'center'},
					{field:'vendorName',title:'供应商',width:15,align:'center'},
					{field:'purchaseNum',title:'采购数量',width:15,align:'center'},
					{field:'purchaseSum',title:'采购金额（元）',width:15,align:'right',
						formatter: function(value,row,index){
							if (row.purchaseSum){
								return common.fmoney(row.purchaseSum);
							}
						}}
		   		]]
	});
	
})
function todg2(row){
	
	var month = $("#month").datebox('getValue');
	var data = {
		"query['t#month_S_EQ']":month
	};
	$('#dg2').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/count/productC2/mxpage.htmlx",
	    queryParams:{
	        "id":row.product.id,
	        "query['t#month_S_EQ']":month,
	    }
	}); 
	
}


//=============ajax===============

</script>