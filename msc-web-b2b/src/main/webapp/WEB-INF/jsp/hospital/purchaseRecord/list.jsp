<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<style type="text/css">
.file {
	position: absolute;
	top: 0px;
	left: 0px;
	height: 30px;
	width: 85px;
	filter: alpha(opacity : 0);
	opacity: 0;
	cursor: pointer;
}
</style>
<html>

<body class="easyui-layout">
	<form id="fileId" method="POST" enctype="multipart/form-data">
		<input type="file" name="myfile" class="file" id="myfile" />
	</form>
	<div class="single-dg">
		<table id="dg"></table>
	</div>

</body>
</html>

<script>

//初始化
$(function(){	
	$("#myfile").change(function(){
		$.messager.confirm('确认信息', '确认导入资料吗?', function(r){
			if (r){
				$("#fileId").submit();
			}else{
				location.reload(true);
			}
		});
	});
	
	$("#fileId").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseRecord/upload.htmlx",
		onSubmit : function() {
			top.$.messager.progress({
				title : '提示',
				text : '数据导入中，请稍后....'
			});
			return true;
		},
		success : function(result) {
			$('#myfile').val("");
			top.$.messager.progress('close');
			result = $.parseJSON(result);
			if(result.success){
				$('#dg').datagrid('reload');
				showMsg(result.msg);
			}else{
				showErr(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});

	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() -4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseRecord/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'month',title:'年月',width:10,align:'center'},
		        	{field:'product.code',title:'药品编码',width:10,align:'center',
						formatter: function(value,row,index){
							if(row.product)
							return row.product.code;
						}
		        	},
		        	{field:'product.name',title:'药品名称',width:10,align:'center',
						formatter: function(value,row,index){
							if(row.product)
							return row.product.name;
						}
		        	},
		        	{field:'product.dosageFormName',title:'剂型',width:10,align:'center',
						formatter: function(value,row,index){
							return row.product.dosageFormName;
						}
		        	},
					
		        	{field:'product.model',title:'规格',width:10,align:'center',
						formatter: function(value,row,index){
							return row.product.model;
						}
		        	},
		        	{field:'product.packDesc',title:'包装规格',width:10,align:'center',
						formatter: function(value,row,index){
							return row.product.packDesc;
						}
		        	},
		        	{field:'product.producerName',title:'生产厂家',width:10,align:'center',
						formatter: function(value,row,index){
							return row.product.producerName;
						}
		        	},
		        	{field:'platform',title:'交易平台',width:10,align:'center'},
		        	{field:'productTranId',title:'省药交产品ID',width:10,align:'center'},
		        	{field:'num',title:'采购数量',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.num){
								return common.fmoney(row.num);
							}
						}
					},
		        	{field:'amt',title:'采购金额',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.amt){
								return common.fmoney(row.amt);
							}
						}
					}		
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
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
			iconCls: 'icon-import',
			text:"导入",
			handler: function(){
				$("#myfile").click();
			}
		},'-',{
			iconCls: 'icon-xls',
			text:"模板下载",
			handler: function(){
				window.open(" <c:out value='${pageContext.request.contextPath }'/>/resources/template/purchaseRecordTemplate.xls");			
			}
		}]
	});
	
	$('#dg').datagrid('enableFilter', [{
        
	}]);
	
});
//弹窗新增
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseRecord/add.htmlx",
		onLoad:function(){
			
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				f.submit();
			}
		}, {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				top.$.modalDialog.handler.dialog('destroy');
				top.$.modalDialog.handler = undefined;
			}
		}]
	});
}

//删除
function delFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(selobj.id);
		}
	});
}

//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseRecord/delete.htmlx",
		data:{
			"id":id
		},
		dataType:"json",
		type:"POST",
		cache:false,
		//traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("删除成功！");
			} else{
				showMsg("该数据已被使用，无法删除");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>