<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout" >
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
		singleSelect:false,
		rownumbers:true,
		border:true,
		height :  $(this).height() -4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsStock/stockPage.htmlx",
		queryParams:{
		},
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'productCode',title:'药品编码',width:15,align:'center'},
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
		        	{field:'product.packDesc',title:'包装规格',width:10,align:'center',
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
		        	{field:'stockNum',title:'库存数量',width:10,align:'center',editor:{type:'numberbox',options:{min:0,precision:0,validType:'integer'}}},
		        	{field:'stockUpLimit',title:'库存上限',width:10,align:'center',editor:{type:'numberbox',options:{min:0,precision:0,validType:'integer'}}},
		        	{field:'stockDownLimit',title:'库存下限',width:10,align:'center',editor:{type:'numberbox',options:{min:0,precision:0,validType:'integer'}}}
			    ]],
		toolbar: [{
			iconCls: 'icon-ok',
			text:"设定库存",
			handler: function(){
				setStock();	
			}
		}],
		onClickRow: function (index, row) {
			$('#dg').datagrid('beginEdit', index);
		},
		onLoadSuccess:function(){
			
			$('#dg').datagrid('doCellTip',{delay:500});
			
		}
	});

	$('#dg').datagrid('enableFilter', [{
		 field:'stockNum',
		 type:'text',
		 isDisabled:1
	},{
		 field:'stockUpLimit',
		 type:'text',
		 isDisabled:1
	},{
		 field:'stockDownLimit',
		 type:'text',
		 isDisabled:1
	}]);
	
});

function setStock(){
	var selobjs = $('#dg').datagrid('getSelections');
	if(selobjs.length==0){
		showMsg("没有选定行");
		return;
	}
	var stockLimit = new Array();
	$.each(selobjs,function(index,row){
		 var edstockNum = $('#dg').datagrid('getEditor', { index: $('#dg').datagrid("getRowIndex",row) , field: 'stockNum' });
	     var stockNum = $(edstockNum.target).numberbox('getValue');
	     var edUp = $('#dg').datagrid('getEditor', { index: $('#dg').datagrid("getRowIndex",row) , field: 'stockUpLimit' });
	     var stockUpLimit = $(edUp.target).numberbox('getValue');
	     var edDown = $('#dg').datagrid('getEditor', { index: $('#dg').datagrid("getRowIndex",row) , field: 'stockDownLimit' });
	     var stockDownLimit = $(edDown.target).numberbox('getValue');
	     var numObj = new Object();
	     numObj.id = row.id;
	     numObj.stockNum = stockNum;
	     numObj.stockUpLimit = stockUpLimit;
	     numObj.stockDownLimit = stockDownLimit;
	     stockLimit[index] = numObj;
	}); 
	setStockAjax(stockLimit);
}

function setStockAjax(stockLimit){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsStock/setStock.htmlx",
		data:{
			"stockLimit":JSON.stringify(stockLimit)
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("设定成功！");
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
});	
}

//搜索
function dosearch(){
	var isValid = $("#form1").form('validate');
	if(!isValid)
		return;
	var para1 = $("#code").textbox("getValue");
	var para2 = $("#name").textbox("getValue");

	$('#dg').datagrid('load',{
		"query['t#productCode_S_LK']":para1,
		"query['t#product.name_S_LK']":para2
	}); 
}

</script>