<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>
<style>
.datagrid-cell-c1-NUM{
	color:#0081c2;
}
</style>
<body class="easyui-layout">

	<div style="padding: 5px; display: none">
		<form id="form1" style="float: left;">
			库房名称: <input id="warehouseCode" />要求供货日期:<input id="requireDate" />紧急程度：<input id="uLevel" />
			
		</form>
	</div>
	
	<div id="tb" class="search-bar" >
       <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true" onclick="addPara()">确认下单</a>
		<span class="datagrid-btn-separator split-line" ></span>
		<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true" onclick="delFunc()">删除</a>
  		<span class="datagrid-btn-separator split-line" ></span>
  		<span style="float:right;margin-top:5px;margin-right:15px;" id="totalspan"><c:out value='${totalstr}'/></span>
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
		 fieldType:'c#S'
	},{
		 field:'NAME',
		 type:'text',
		 fieldType:'c#S'
	},{
		 field:'PINYIN',
		 type:'text',
		 fieldType:'c#S'
	},{
		 field:'GENERICNAME',
		 type:'text',
		 fieldType:'c#S'
	},{
		 field:'DOSAGEFORMNAME',
		 type:'text',
		 fieldType:'c#S'
	},{
		 field:'PRODUCERNAME',
		 type:'text',
		 fieldType:'c#S'
	},{
		 field:'MODEL',
		 type:'text',
		 fieldType:'c#S'
	},{
		 field:'PACKDESC',
		 type:'text',
		 fieldType:'c#S'
	},{
		 field:'UNITNAME',
		 type:'text',
		 fieldType:'c#S'
	},{
		 field:'PRICE',
		 type:'text',
		 isDisabled:1
	},{
		 field:'lastNum',
		 type:'text',
		 isDisabled:1
	},{
		 field:'ENDVALIDDATE',
		 type:'text',
		 isDisabled:1
	},{
		 field:'VENDORNAME',
		 type:'text'
	},{
		 field:'NUM',
		 type:'text',
		 isDisabled:1
	},{
		 field:'do',
		 type:'text',
		 isDisabled:1
	}]);
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
		height :  $(this).height() -4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/cartContract/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'CODE',title:'药品编码',width:10,align:'center'},
		        	{field:'NAME',title:'药品名称',width:15,align:'center'},
		        	{field:'PINYIN',title:'拼音',width:10,align:'center'},
		        	{field:'PRODUCERNAME',title:'生产企业',width:15,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
		        	{field:'MODEL',title:'规格',width:10,align:'center'},
		        	{field:'PACKDESC',title:'包装',width:10,align:'center'},
		        	{field:'UNITNAME',title:'单位',width:10,align:'center'},
		        	{field:'ENDVALIDDATE',title:'截止日期',width:10,align:'right'},
					{field:'lastNum',title:'可购余量',width:10,align:'right',
						formatter: function(value,row,index){
							return common.fmoney(row.CONTRACTNUM-row.PURCHASEPLANNUM);
						}},
		        	{field:'PRICE',title:'价格',width:15,align:'right',
						formatter: function(value,row,index){
							if (row.PRICE){
								return common.fmoney(row.PRICE);
							}
						}},
		        	{field:'NUM',title:'数量',width:10,align:'center', editor: { type: 'numberbox', options: { validType:'pinteger' } }},
		        	{field:'do',title:'操作',width:10,align:'center',
		        		formatter: function(value,row,index){
		        			return "<a class='dgbtn' href='#' onclick='editCart("+index+")' class='easyui-linkbutton'>修改</a>";
						}}
		   		]],
		toolbar:"#tb",
		onClickRow: function(index,row){
			$('#dg').datagrid('beginEdit', index);
		},
		onLoadSuccess:function(data){
			$('.dgbtn').linkbutton({iconCls:'icon-edit',plain:true,height:20}); 
			$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});

	addFilter();	
});

//弹窗信息
function addPara() {
	var data = $('#dg').datagrid('getData');
	if(data.total <= 0){
		return;
	}
	
	top.$.modalDialog({
		title : "信息",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/cartContract/para.htmlx",
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

				var warehouseCode = f.find("#warehouseCode").combobox("getValue");
				var requireDate = f.find("#requireDate").datetimespinner('getValue');
				var urgencyLevel = f.find("#urgencyLevel").datetimespinner('getValue');
				
				$("#warehouseCode").val(warehouseCode);
				$("#requireDate").val(requireDate);
				$("#uLevel").val(urgencyLevel);
				top.$.modalDialog.handler.dialog('destroy');
				top.$.modalDialog.handler = undefined;
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
			     cartids[index] = row.CARTID;
			}); 
			delAjax(cartids);
		}
	});
}

//下单
function mkorder(){
	mkorderAjax();
}

function editCart(index){
	var row = $("#dg").datagrid("getRows")[index];
	
	var cartId = row.CARTID;
	var lastNum = row.CONTRACTNUM-row.PURCHASEPLANNUM;
	
	var ed = $('#dg').datagrid('getEditor', {index:index,field:'NUM'});
	if(ed == null)
		return;
	var isValid = $(ed.target).textbox('isValid');
	if(!isValid){
		return;
	}	
	var goodsNum = ed.target.val();
	if(goodsNum == ""){
		showErr("数量不能为空");
		return false;
	}
	if(goodsNum > lastNum){
		showErr("数量不能超过余量［"+lastNum+"］");
		return false;
	}
	
	editCartAjax(cartId,goodsNum);
	
}



//=============ajax===============
function mkorderAjax(cartids,nums){
	var warehouseCode = $("#warehouseCode").val();
	var requireDate = $("#requireDate").val();
	var uLevel = $("#uLevel").val();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/cartContract/mkorder.htmlx",
		data:{
			"warehouseCode":warehouseCode,
			"requireDate":requireDate,
			"uLevel":uLevel
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
				url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/cartContract/del.htmlx",
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
						$("#totalspan").html(data.data);
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

function editCartAjax(cartId,goodsNum){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/cartContract/edit.htmlx",
		data:{
			"cartId": cartId,
			"goodsNum":goodsNum
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				//$('#dg').datagrid('reload');
				showMsg("修改成功");
				$("#totalspan").html(data.data);
			} else{
				showErr(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>