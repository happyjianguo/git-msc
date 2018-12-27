<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="订单列表" />
<html>
<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false"  class="my-north" style="height:300px;" >
	 <div id="search-bar" style="padding:6px">
	 		医疗机构：<input class="easyui-textbox" style="width:100px" id="hospitalName" />
			申请日期: <input class="easyui-datebox" style="width:110px" id="startDate">
	        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
	        
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="dosearch()">查询</a> 
	   	 	<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" onclick="addPara(0)">通过</a> 
	   	 	<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-no'" onclick="addPara(1)">不通过</a>
	   </div>
	<div>
	<input type="hidden" id="explain" /> 
		<table  id="dg"></table>
	</div>
</div>

<div data-options="region:'center',title:''"   class="my-center" >
    <table id="dgDetail"></table>
</div>
</body>
</html>
<script>

//搜索
function dosearch(){
	var startDate="";
	var toDate="";
	if($('#startDate').datebox('getValue')!=""){
	  	startDate = $('#startDate').datebox('getValue');		
	}
	if($('#toDate').datebox('getValue')!=""){
		toDate = $('#toDate').datebox('getValue');
	}	
	var hospitalName = $("#hospitalName").textbox("getValue");
	$('#dg').datagrid('load',{
		"query['t#hospitalName_S_LK']":hospitalName,
		"query['t#createDate_D_GE']": startDate,
		"query['t#createDate_D_LE']": toDate
	});
}
//查询
function searchDefectsList(row){
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/returnrequest/mxlist.htmlx", 
	    queryParams:{  
	    	"query['t#returnsRequest.id_L_EQ']":row.id
	    }
	});
}
//初始化
$(function(){
	
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(".my-north").height()-$("#search-bar").height() -12,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/returnrequest/page.htmlx",
		pageSize:10,
		pageNumber:1,
		columns:[[
		          {field:'code',title:'退货申请单号',width:20,align:'center'},
		{field:'returnsBeginDate',title:'申请时间',width:20,align:'center',
			formatter: function(value,row,index){
				if (row.returnsBeginDate){
					return $.format.date(row.returnsBeginDate,"yyyy-MM-dd HH:mm");
				}
			}
		}, 
		{field:'vendorName',title:'供应商',width:20,align:'center'},
		{field:'hospitalName',title:'医疗机构',width:20,align:'center'},
		{field:'num',title:'退货申请数量',width:10,align:'center'},
		{field:'sum',title:'配送金额（元）',width:10,align:'right',
			formatter: function(value,row,index){
				if (row.sum){
					return common.fmoney(row.sum);
				}
			}}
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
	$('#dgDetail').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : '100%',
		pagination:false,
		pageSize:10,
		pageNumber:1,
		columns:[[
			{field:'productCode',title:'药品编码',width:80,align:'center'},
			{field:'productName',title:'药品名称',width:150,align:'center'},
			{field:'dosageFormName',title:'剂型',width:80,align:'center'},		
			{field:'model',title:'规格',width:80,align:'center'},
			{field:'producerName',title:'生产企业',width:150,align:'center'},
			{field:'unit',title:'单位',width:60,align:'center'},
			{field:'price',title:'成交价（元）',width:80,align:'right',
					formatter: function(value,row,index){
						if (row.price){
							return common.fmoney(row.price);
						}
					}},	
			{field:'goodsNum',title:'退货申请数量',width:60,align:'center'},
			{field:'goodsSum',title:'金额 （元）',width:80,align:'right',
				formatter: function(value,row,index){
					if (row.goodsSum){
						return common.fmoney(row.goodsSum);
					}
				}},
			{field:'reason',title:'退货原因',width:150,align:'center'},
			{field:'replyNum',title:'退货数量',width:80,align:'center', editor: { type: 'numberbox',options: { required: false,min:0,precision:3 } }},
			{field:'reply',title:'答复',width:120,align:'center', editor: { type: 'textbox',options: {} }},
			{field:'batchCode',title:'生产批号',width:80,align:'center'},
			{field:'batchDate',title:'生产日期',width:80,align:'center'},
			{field:'expiryDate',title:'有效日期',width:80,align:'center'},
		]],
		onLoadSuccess:function(data){
			$.each(data.rows,function(index,row){
				$('#dgDetail').datagrid('beginEdit', index);
			}); 
			$('#dgDetail').datagrid('doCellTip',{delay:500}); 
		}
	
	});
	
	
});
//下单
function gporeturn(datas){
	var selrow = $('#dg').datagrid('getSelected');
	var returnrequestId = selrow.id;
	
	
	$.messager.confirm('确认信息', '同意确认退货吗?', function(r){
		if (r){
			returnAjax(returnrequestId, datas);
		}
	});
}
//取消下单
function cancelreturn(){
	var selrow = $('#dg').datagrid('getSelected');
	var returnrequestId = selrow.id;
	
	$.messager.confirm('确认信息', '确认取消退货吗?', function(r){
		if (r){
			cancelAjax(returnrequestId);
		}
	});
}
//弹窗信息
function addPara(type) {
	var selrow = $('#dg').datagrid('getSelected');
	if(selrow== null){
		showErr("请先选择一笔订单");
		return;
	}
	if(type == 0){
		var selobjs = $('#dgDetail').datagrid('getRows');
		var datas = new Array();
		var eachflag = 1;
		$.each(selobjs,function(index,row){
			 var ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'replyNum' });
		     if(ed){
		    	 var number = $(ed.target).numberbox('getValue');
			     if(number > 0){
				     ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'reply' });
				     var data = new Object();
			    	 data.id = row.id;
			    	 data.replyNum = number;
				     data.reply = $(ed.target).textbox('getValue');
				     datas[datas.length] = data;
			     }
			     if(number>row.goodsNum){
			    	 showErr(row.code+"的退货数量不能大于申请退货数量");
			    	 eachflag = 0;
			     }
		     }
		});
		if(eachflag == 0)
			return;
		if(datas.length<=0){
			showErr("请输入退货数量");
			return;
		}
	}
	top.$.modalDialog({
		title : "信息",
		width : 400,
		height : 200,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/returnrequest/para.htmlx",
		onLoad:function(){
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				var f = top.$.modalDialog.handler.find("#form1");
				var isValid = f.form('validate');
				if(!isValid)
					return;
				
				var explain = f.find("#explain").textbox("getValue");
				
				top.$.modalDialog.handler.dialog('close');
				
				$("#explain").val(explain);
				
				if(type == 0){
					gporeturn(datas);
				}else if(type == 1){
					cancelreturn();
				}
				
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
		title : "退货成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/returnrequest/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}

//=============ajax===============
function returnAjax(returnrequestId, datas){
	var explain = $("#explain").val();
	var data = JSON.stringify(datas);
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/returnrequest/mkreturn.htmlx",
		data:{
			"returnrequestId":returnrequestId,
			"explain":explain,
			"data":data
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				//$('#dgDetail').datagrid('reload');  
				showMsg("退货成功！");
				successOpen(data.data);
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
function cancelAjax(returnrequestId){
	var explain = $("#explain").val();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/returnrequest/cancelreturn.htmlx",
		data:{
			"returnrequestId":returnrequestId,
			"explain":explain
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				//$('#dgDetail').datagrid('reload');  
				showMsg("取消申请！");
				//successOpen(data.data);
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>