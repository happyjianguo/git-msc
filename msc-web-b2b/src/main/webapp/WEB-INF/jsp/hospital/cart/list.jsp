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

.file_form {
	width: 85px;
	height: 20px;
	float: left;
	margin: 0px 5px 0px 0px;
}
</style>
<html>

<body class="easyui-layout">

	<div style="padding: 5px; display: none">

		<form id="form1" style="float: left;">
			库房名称: <input id="warehouseCode" /> 紧急程度: <input id="urgencyLevel" />
			要求供货日期:<input id="requireDate" />

		</form>

		<form id="fileId" method="POST" enctype="multipart/form-data">
			<input type="file" name="myfile" class="file" id="myfile" />
		</form>


	</div>



	<div class="single-dg">
		<table id="dg"></table>
	</div>

</body>
</html>

<script>
function addFilter(){
	$('#dg').datagrid('enableFilter', [{
		 field:'CODE',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'NAME',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'PINYIN',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'GENERICNAME',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'DOSAGEFORMNAME',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'PRODUCERNAME',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'MODEL',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'PACKDESC',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'UNITNAME',
		 type:'text',
		 fieldType:'g#S'
	},{
		 field:'FINALPRICE',
		 type:'text',
		 isDisabled:1
	},{
		 field:'VENDORNAME',
		 type:'text'
	},{
		 field:'NUM',
		 type:'text',
		 isDisabled:1
	}]
	
	);
}

//初始化
$(function(){
	$("#myfile").change(function(){
		$.messager.confirm('确认信息', '确认导入药品资料么?', function(r){
			if (r){
				$("#fileId").submit();
			}else{
				location.reload(true);
			}
		});
	});
	
	$("#fileId").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/cart/upload.htmlx",
		onSubmit : function() {
			return true;
		},
		success : function(result) {
			$('#myfile').val("");
			result = $.parseJSON(result);
			if(result.success){
				$('#dg').datagrid('reload');
				showMsg(result.msg);
			}else{
				importErrOpen(result.msg);
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
		singleSelect:false,
		rownumbers:true,
		border:true,
		height :  $(this).height() -4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/cart/list.htmlx",
		filterDelay:100,
		columns:[[
		        	{field:'CODE',title:'药品编码',width:20,align:'center'},
		        	{field:'NAME',title:'药品名称',width:15,align:'center'},
		        	{field:'PINYIN',title:'拼音',width:10,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
		        	{field:'MODEL',title:'规格',width:10,align:'center'},
		        	{field:'PACKDESC',title:'包装',width:10,align:'center'},
		        	{field:'UNITNAME',title:'单位',width:10,align:'center'},
		        	{field:'PRODUCERNAME',title:'生产企业',width:15,align:'center'},
		        	{field:'VENDORNAME',title:'供应商',width:15,align:'center'},
		        	{field:'FINALPRICE',title:'价格',width:15,align:'right',
						formatter: function(value,row,index){
							if (row.FINALPRICE){
								return common.fmoney(row.FINALPRICE);
							}
						}},
		        	{field:'NUM',title:'数量',width:10,align:'center', editor: { type: 'numberbox', options: { required: true,min:0,precision:0 } }}
		   		]],
		toolbar: [{
			iconCls: 'icon-ok',
			text:"确认下单",
			handler: function(){
				addPara();	
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delFunc();
			}
		}/* ,'-',{
			iconCls: 'icon-import',
			text:"导入",
			handler: function(){
				$("#myfile").click();
			}
		},'-',{
			iconCls: 'icon-xls',
			text:"模板下载",
			handler: function(){
				location.href = " <c:out value='${pageContext.request.contextPath }'/>/resources/template/orderTp.xlsx";
			}
			
		} */],
		onLoadSuccess:function(data){
			$('#dg').datagrid('selectAll');
			$.each(data.rows,function(index,row){
				$('#dg').datagrid('beginEdit', index);
			}); 
			$('#dg').datagrid('doCellTip',{delay:500}); 

			$.each($("input.textbox-text",$("span.numberbox")),function(index){
				$(this).click(function(){
					$('#dg').datagrid("selectRow",index);
			    });
			})
		}
	});
	
	addFilter();

	//弹窗信息
	function addPara() {
		var selobjs = $('#dg').datagrid('getSelections');
		if(selobjs.length == 0){
			showMsg("请至少选择一笔");
			return;
		}
		top.$.modalDialog({
			title : "信息",
			width : 600,
			height : 400,
			href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/cart/para.htmlx",
			onLoad:function(){
				
			},
			buttons : [ {
				text : '保存',
				iconCls : 'icon-ok',
				handler : function() {
					//top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
					
					
					var f = top.$.modalDialog.handler.find("#form1");
					var isValid = f.form('validate');
					if(!isValid)
						return;
					
					//var urgencyLevel = f.find("#urgencyLevel").combo("getValue");
					var warehouseCode = f.find("#warehouseCode").combo("getValue");
					var requireDate = f.find("#requireDate").datetimespinner('getValue');
					top.$.modalDialog.handler.dialog('close');
					
					$("#warehouseCode").val(warehouseCode);
					//$("#urgencyLevel").val(urgencyLevel);
					$("#requireDate").val(requireDate);
					mkorder();
					
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
	
	
	
});
//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "下单成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/cart/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}
//弹窗导入失败
function importErrOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "导入失败",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/cart/importErr.htmlx",
		onLoad:function(){
			top.setImportErr(data);
		}
	});
}
//删除
function delFunc(){
	
	var selobjs = $('#dg').datagrid('getSelections');
	if(selobjs.length == 0){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			var cartids = new Array();
			$.each(selobjs,function(index,row){
			     cartids[index] = row.ID;
			}); 
			delAjax(cartids);
		}
	});
}

//下单
function mkorder(){
	var selobjs = $('#dg').datagrid('getSelections');
	var goodspriceids = new Array();
	var nums = new Array();
	var cartids = new Array();
	$.each(selobjs,function(index,row){
		 var ed = $('#dg').datagrid('getEditor', { index: $('#dg').datagrid("getRowIndex",row) , field: 'NUM' });
	     var number = $(ed.target).numberbox('getValue');
	     cartids[index] = row.ID;
	     goodspriceids[index] = row.GOODSPRICEID;
	     nums[index] = number;
	}); 
	mkorderAjax(cartids,goodspriceids,nums);
}



//=============ajax===============
function mkorderAjax(cartids,goodspriceids,nums){
	//var urgencyLevel = $("#urgencyLevel").val();	
	var urgencyLevel = 1;
	var warehouseCode = $("#warehouseCode").val();
	var requireDate = $("#requireDate").val();

	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/cart/mkorder.htmlx",
		data:{
			"cartids":cartids,
			"goodspriceids":goodspriceids,
			"nums":nums,
			uLevel:urgencyLevel,
			warehouseCode:warehouseCode,
			requireDate:requireDate
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("下单成功！");
				successOpen(data.data);
			}else{
				showErr(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function delAjax(cartids){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/cart/del.htmlx",
		data:{
			"cartids":cartids
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
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