<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="订单列表" />
<html>
<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false"  class="my-north" style="height:300px;" >
	 <div id="search-bar" style="padding:6px">
			退货日期
			<input class="easyui-datebox" style="width:110px" id="startDate"/> -
	 		<input class="easyui-datebox" style="width:110px" id="toDate"/>
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="dosearch(<c:out value="${orgId}"/>)">查询</a>
	   	 	<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" onclick="addPara()">上传发票</a>
	   </div>
	<div>
		<table  id="dg"></table>
	</div>
</div>

<div data-options="region:'center',title:''"  class="my-center" >
<input type="hidden" id="invoiceCode" />
<input type="hidden" id="invoiceDate" />
<input type="hidden" id="taxRate" />

    <table id="dgDetail"></table>
</div>
</body>
</html>
<script>

//搜索
function dosearch(orgId){
	var startDate="";
	var toDate="";
	if($('#startDate').datebox('getValue')!=""){
	  	startDate = $('#startDate').datebox('getValue');		
	}
	if($('#toDate').datebox('getValue')!=""){
		toDate = $('#toDate').datebox('getValue');
	}	

	$('#dg').datagrid('load',{
		"query['t#orderDate_D_GE']": startDate,
		"query['t#orderDate_D_LE']": toDate
	});
}
//查询
function searchDefectsList(row){
	$('#dgDetail').datagrid({  
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/invoicered/mxlist.htmlx",  
	    queryParams:{
	        "query['t#returnsOrder.id_L_EQ']":row.id
	    }
	});
}
//初始化
$(function(){
	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height :$(".my-north").height()-$("#search-bar").height() -12,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/invoicered/page.htmlx",
		pageSize:10,
		pageNumber:1,
		columns:[[
		        	{field:'code',title:'退货单号',width:20,align:'center'},
		        	{field:'orderDate',title:'退货时间',width:20,align:'center',
						formatter: function(value,row,index){
							if (row.orderDate){
								return $.format.date(row.orderDate,"yyyy-MM-dd HH:mm:ss");
							}
						}
					}, 
					{field:'vendorName',title:'供应商',width:20,align:'center'},
					{field:'hospitalName',title:'医疗机构',width:20,align:'center'},
					{field:'num',title:'退货数量',width:10,align:'center'},
					{field:'sum',title:'退货金额(元)',width:10,align:'right',
						formatter: function(value,row,index){
							if (row.sum){
								return common.fmoney(row.sum);
							}
						}}/* ,
					{field:'purchaseOrderCode',title:'订单号',width:15,align:'center'} */
		   		]],
		onClickRow: function(index,row){
			searchDefectsList(row);  
		},
		onLoadSuccess:function(data){
			var rows=data.rows;
			//大于1行默认选中
			if(rows.length > 0) {
				$(this).datagrid('selectRow', 0);
	        	searchDefectsList(rows[0]);
			}
			$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});

	//组成员
	$('#dgDetail').datagrid({
		fitColumns:true,
		striped:true,
		rownumbers:true,
		height :'100%',
		columns:[[
					{field:'productCode',title:'药品编码',width:10,align:'center'},
					{field:'productName',title:'药品名称',width:20,align:'center'},
					{field:'dosageFormName',title:'剂型',width:10,align:'center'},		
					{field:'model',title:'规格',width:10,align:'center'},
		        	{field:'producerName',title:'生产企业',width:20,align:'center'},
					{field:'unit',title:'单位',width:10,align:'center'},
					{field:'price',title:'退货价格(元)',width:10,align:'right',
						formatter: function(value,row,index){
							if (row.price){
								return common.fmoney(row.price);
							}
						}},	
					{field:'goodsNum',title:'退货数量',width:10,align:'center'},/* 
					{field:'reason',title:'退货原因',width:20,align:'center'}, */
					{field:'goodsSum',title:'退货金额(元)',width:10,align:'right',
						formatter: function(value,row,index){
							if (row.goodsSum){
								return common.fmoney(row.goodsSum);
							}
						}},
					{field:'batchCode',title:'批号',width:10,align:'center'},
		   		]]
	});
	
	
	
});

//弹窗信息
function addPara() {
	var selrow = $('#dg').datagrid('getSelected');
	if(selrow==null){
		showMsg("请至少选择一笔");
		return;
	}
	top.$.modalDialog({
		title : "信息",
		width : 400,
		height : 200,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/invoicered/para.htmlx",
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
				
				var invoiceCode = f.find("#invoiceCode").textbox("getText");
				var invoiceDate = f.find("#invoiceDate").datebox("getValue");
				var taxRate = f.find("#taxRate").combobox("getValue");
				
				top.$.modalDialog.handler.dialog('close');
				$("#invoiceCode").val(invoiceCode);
				$("#invoiceDate").val(invoiceDate);
				$("#taxRate").val(taxRate);
				mkinvoice();
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
//下单
function mkinvoice(){
	var selrow = $('#dg').datagrid('getSelected');
	
	mkinvoiceAjax(selrow.id);
}

//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "上传成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/invoicered/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}

//=============ajax===============
function mkinvoiceAjax(returnId){
	var invoiceCode = $("#invoiceCode").val();	
	var invoiceDate = $("#invoiceDate").val();
	var taxRate = $("#taxRate").val();
	
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/invoicered/mkinvoice.htmlx",
		data:{
			"returnId":returnId,
			"invoiceCode":invoiceCode,
			"invoiceDate":invoiceDate,
			"taxRate":taxRate
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("成功！"+data.msg);
				successOpen(data.data);
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>