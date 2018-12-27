<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>



<div class="open-dg" >
	<table id="dg"></table>
</div>



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
		height :  460,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goods/newProductPage.htmlx",
		pageSize:10,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'CODE',title:'药品编码',width:30,align:'center'},
		        	{field:'NAME',title:'药品名称',width:15,align:'center'},
		        	{field:'PINYIN',title:'拼音',width:10,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
		        	{field:'MODEL',title:'规格',width:10,align:'center'},
		        	{field:'PACKDESC',title:'包装规格',width:10,align:'center'},
		        	{field:'PRODUCERNAME',title:'厂家',width:15,align:'center'}
		   		]],
   		onClickRow:function(index,row){
   			var selobjs = $('#dg').datagrid('getSelections');
   			var isSelected = 0;
   			$.each(selobjs,function(index){
   				if(this.ID == row.ID){
   					isSelected = 1;
   					return false;
   				}
   			});

   			if(isSelected){
   				addAjax(row.ID);
   			}else{
   				delAjax(row.ID);
   			}
   		},
   		onLoadSuccess:function(data){
			$.each(data.rows,function(index,row){
				if(row.selected){
					$('#dg').datagrid('selectRow', index);
				}
			}); 
		}
		
	});

	$('#dg').datagrid('removeFilterRule');
	$('#dg').datagrid('enableFilter', [{
		 field:'CODE',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'NAME',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'PINYIN',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'DOSAGEFORMNAME',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'PRODUCERNAME',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'MODEL',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'PACKDESC',
		 type:'text',
		 fieldType:'p#S'
	}]
	
	);
	
});

function save(){
	var selobjs = $('#dg').datagrid('getSelections');
	if(selobjs.length == 0){
		return;
	}
	var productids = new Array();
	
	$.each(selobjs,function(index,row){
		productids[index] = row.ID;
	}); 
	saveAjax(productids);
}



//搜索
function dosearch(){
	var isValid = $("#form1").form('validate');
	if(!isValid)
		return;
	var para1 = $("#code").textbox("getValue");
	var para2 = $("#name").textbox("getValue");
	$('#dg').datagrid('load',{
		"code":para1,
		"name":para2
	}); 
}


//=============ajax===============
function saveAjax(productids){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goods/save.htmlx",
		data:{
			"productids":productids,
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				top.$.modalDialog.openner.datagrid('reload');
				top.$.modalDialog.handler.dialog('close');
				showMsg("保存成功！");
			}else{
				showErr(data.msg);
			}
		},
		error:function(){
			top.$.modalDialog.openner.datagrid('reload');
			top.$.modalDialog.handler.dialog('close');
			showErr("出错，请刷新重新操作");
		}
	});	
}

function addAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goods/add.htmlx",
		data:{
			"productId":id,
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				showMsg("新增成功！");
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