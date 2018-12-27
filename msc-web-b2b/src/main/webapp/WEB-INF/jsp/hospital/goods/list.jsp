<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>
<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false"  class="my-north" style="height:300px;">
	<div>
		<table  id="dg"></table>
	</div>
</div>

<div data-options="region:'center',title:''"  class="my-center">
    <table id="dgDetail"></table>
</div>

</body>
</html>

<script>

function filterFunc(){
	$('#dg').datagrid({remoteFilter: true});
	$('#dg').datagrid('enableFilter', [{
		 field:'isDisabled',
		 type:'text',
		 isDisabled:1
	}]
	);
	
}
//初始化
$(function(){

	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(".my-north").height(),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goods/page.htmlx",
		queryParams:{
		},
		pageSize:10,
		pageNumber:1,
		columns:[[
		        	{field:'productCode',title:'药品编码',width:20,align:'center'},
		        	{field:'product.name',title:'药品名称',width:20,align:'center',
						formatter: function(value,row,index){
							return row.product.name;
						}},
		        	{field:'product.pinyin',title:'拼音',width:10,align:'center',
						formatter: function(value,row,index){
							return row.product.pinyin;
						}},
		        	{field:'product.dosageFormName',title:'剂型',width:10,align:'center',
						formatter: function(value,row,index){
							return row.product.dosageFormName;
						}},
					
		        	{field:'product.model',title:'规格',width:10,align:'center',
						formatter: function(value,row,index){
							return row.product.model;
						}},
		        	{field:'product.packDesc',title:'包装规格',width:10,align:'center',
						formatter: function(value,row,index){
							return row.product.packDesc;
						}},
		        	{field:'product.producerName',title:'生产厂家',width:20,align:'center',
						formatter: function(value,row,index){
							return row.product.producerName;
						}},
		        	{field:'isDisabled',title:'已选供应商',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.isDisabled == 1){
								return "<img src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
						}
		        	} 
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加目录",
			handler: function(){
				addOpen();	
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delFunc();	
			}
		},'-',{
			iconCls: 'icon-export',
			text:"目录下载",
			handler: function(){
				$.messager.confirm('确认信息', '确认下载?', function(r){
					if (r){
						window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/goods/exportExcel.htmlx");
					}
				});
			}
		}/* ,'-',{
			iconCls: 'icon-filter',
			text:"数据过滤",
			align:"right",
			handler: function(){
				filterFunc();	
			}
		} */],
		onClickRow: function(index,row){			
        	searchDefectsList(row);
		},
		onLoadSuccess:function(){
			$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});

	filterFunc();	
	
	$('#dgDetail').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : '100%',
		pagination:false,
		pageSize:10,
		pageNumber:1,
		columns:[[
			{field:'vendorCode',title:'供应商编码',width:50,align:'center'},
			{field:'vendorName',title:'供应商',width:50,align:'center'},
			{field:'finalPrice',title:'价格',width:50,align:'right',
				formatter: function(value,row,index){
					if (row.finalPrice){
						return common.fmoney(row.finalPrice);
					}
				}}
			
		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"选择供应商",
			handler: function(){
				subaddOpen();	
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delVendorFunc();
			}
		}],
		onLoadSuccess:function(data){
			$.each(data.rows,function(index,row){
				$('#dgDetail').datagrid('beginEdit', index);
			}); 
			$('#dgDetail').datagrid('doCellTip',{delay:500}); 
		}
	
	});
	
});

//查询
function searchDefectsList(row){
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsPrice/list.htmlx", 
	    queryParams:{  
	    	"query['t#goodsId_L_EQ']":row.id,
	    	"query['t#isDisabledByH_I_EQ']":0,
	    	"query['t#isDisabled_I_EQ']":0
	    }  
	});
}

//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/goods/add.htmlx",
		onLoad:function(){
			
		},
		onDestroy:function(){
			$('#dg').datagrid("reload");
		}
	});
}

//弹窗选择供应商
function subaddOpen(){
	var selobj = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsPrice/add.htmlx",
		queryParams:{
			"goodsId":selobj.id,
			"productCode":selobj.product.code
		},
		onLoad:function(){
			
		},
		onDestroy:function(){
			$('#dgDetail').datagrid("reload");
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
		"query['t#productName_S_LK']":para2
	}); 
}
function delVendorFunc(){
	var selobj = $('#dgDetail').datagrid('getSelected');
	if(selobj == null){
		showErr("请选择一笔数据");
		return;
	}
	var id= $('#dgDetail').datagrid('getSelected').id;
	
	$.messager.confirm('确认信息', '确认要删除此目录?', function(r){
		if (r){
			disableAjax(id);
		}
	});
}
function disableAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsPrice/disable.htmlx",
		data:{
			"id":id,
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				showMsg("删除成功！");
				$('#dgDetail').datagrid('reload');
			}else{
				showErr(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
}
//删除
function delFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		showErr("请选择一笔数据");
		return;
	}
	var id= $('#dg').datagrid('getSelected').product.id;
	
	$.messager.confirm('确认信息', '确认要删除此目录?', function(r){
		if (r){
			delAjax(id);
		}
	});
}


//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goods/del.htmlx",
		data:{
			"productId":id,
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				showMsg("删除成功！");
				$('#dg').datagrid('reload');
			}else{
				showErr(data.msg);
			}
		},
		error:function(){
			//top.$.modalDialog.openner.datagrid('reload');
			//top.$.modalDialog.handler.dialog('close');
			showErr("出错，请刷新重新操作");
		}
	});
}

</script>