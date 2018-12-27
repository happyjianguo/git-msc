<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="订单列表" />
<style type="text/css">
.datagrid-cell-c1-num{
	color:#0081c2;
}
.datagrid-cell-c1-reason{
	color:#0081c2;
}
</style>
<html>
<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false"  class="my-north" style="height:100%" >
	<div id="tb" class="search-bar" >
		<!-- 配送日期: <input class="easyui-datebox" style="width:110px" id="startDate">
        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
        <span class="datagrid-btn-separator split-line" ></span> -->
	   	<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true" onclick="gporeturn()">退货申请</a>
    </div>
	<div>
		<table  id="dg"></table>
	</div>
</div>
</body>
</html>
<script>
function filterFunc(){
	$('#dg').datagrid('enableFilter', 
		[{
	        field:'expiryDate',
	        type:'datebox',
	        options:{
	            onChange:function(value){
	                if (value == ''){
	                	$('#dg').datagrid('removeFilterRule', 'expiryDate');
	                } else {
	                	$('#dg').datagrid('addFilterRule', {
	                        field: 'expiryDate',
	                        op: 'LK',
	                        fieldType:'S',
	                        value: value
	                    });
	                }
	                $('#dg').datagrid('doFilter');
	            }
	        }
	    },{
	        field:'batchDate',
	        type:'datebox',
	        options:{
	            onChange:function(value){
	                if (value == ''){
	                	$('#dg').datagrid('removeFilterRule', 'batchDate');
	                } else {
	                	$('#dg').datagrid('addFilterRule', {
	                        field: 'batchDate',
	                        op: 'LK',
	                        fieldType:'S',
	                        value: value
	                    });
	                }
	                $('#dg').datagrid('doFilter');
	            }
	        }
	    },{
	        field:'num',
	        type:'text',
	    	isDisabled:1
	        
	    },{
	        field:'reason',
	        type:'text',
	    	isDisabled:1
	        
	    },{
	        field:'goodsNum',
	        type:'text',
	    	isDisabled:1
	        
	    },{
	        field:'returnsGoodsNum',
	        type:'text',
	       	isDisabled:1
	        
	    },{
	        field:'price',
	        type:'text',
	       	isDisabled:1
	        
	    },{
	        field:'unit',
	        type:'text',
	       	isDisabled:1
	        
	    },{
	        field:'vendorName',
	        type:'text',
	       	isDisabled:1
	        
	    }]);
}
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
	var vendorName = $("#vendorName").textbox("getValue");
	$('#dg').datagrid('load',{
		"query['t#vendorName_S_LK']":vendorName,
		"query['t#createDate_D_GE']": startDate,
		"query['t#createDate_D_LE']": toDate
	});
}

//初始化
$(function(){
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height()-10,
		pagination:true,
		remoteFilter: true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/returnRequest/page.htmlx",
		pageSize:20,
		pageNumber:1,
		toolbar:"#tb",
		columns:[[
			{field:'code',title:'配送单明细编号',width:180,align:'center'},
			{field:'productCode',title:'药品编码',width:60,align:'center'},
			{field:'productName',title:'药品名称',width:120,align:'center'},
			{field:'dosageFormName',title:'剂型',width:80,align:'center'},		
			{field:'model',title:'规格',width:80,align:'center'},
			{field:'producerName',title:'生产企业',width:120,align:'center'},
			{field:'unit',title:'单位',width:50,align:'center'},
			{field:'price',title:'成交价（元）',width:80,align:'right',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}},	
			{field:'goodsNum',title:'配送数量',width:60,align:'center'},
			{field:'returnsGoodsNum',title:'退货数量',width:60,align:'center'},
        	{field:'num',title:'数量',width:60,align:'center', editor: { type: 'numberbox',options: { required: false,min:0,precision:3 } }},
			{field:'reason',title:'退货原因',width:130,align:'center', editor: { type: 'textbox',options: {} }},
        	{field:'vendorName',title:'供应商',width:120,align:'center',
	       		formatter: function(value,row,index){
					if (row.deliveryOrder){
						return row.deliveryOrder.vendorName;
					}
				}},
			{field:'batchCode',title:'生产批号',width:80,align:'center'},
			{field:'batchDate',title:'生产日期',width:100,align:'center'},
			{field:'expiryDate',title:'有效日期',width:100,align:'center'}
		]],
		onClickRow: function(index,row){			
			$('#dg').datagrid('beginEdit', index);
		},
		onLoadSuccess:function(data){
			$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});
	filterFunc();
});
//下单
function gporeturn(){
	var selobjs = $('#dg').datagrid('getRows');
	var datas = new Array();
	var eachflag = 1;
	$.each(selobjs,function(index,row){
		 var ed = $('#dg').datagrid('getEditor', { index: $('#dg').datagrid("getRowIndex",row) , field: 'num' });
	     if(ed){
	    	 var number = $(ed.target).numberbox('getValue');
		     if(number > 0){
			     ed = $('#dg').datagrid('getEditor', { index: $('#dg').datagrid("getRowIndex",row) , field: 'reason' });
			     var data = new Object();
		    	 data.id = row.id;
		    	 data.num = number;
			     data.reason = $(ed.target).textbox('getValue');
			     data.vendorCode = row.deliveryOrder.vendorCode;
			     data.gpoCode = row.deliveryOrder.gpoCode;
			     datas[datas.length] = data;
		     }
		     if(number>row.goodsNum - row.returnsGoodsNum){
		    	 showErr(row.code+"的退货数量"+number+"超上限"+(row.goodsNum - row.returnsGoodsNum));
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
	$.messager.confirm('确认信息', '请核对退货数量，确认申请退货吗?', function(r){
		if (r){
			returnAjax(datas);
		}
	});
	
	
}

//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "退货申请成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/returnRequest/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}

//=============ajax===============
function returnAjax(datas){
	var data = JSON.stringify(datas);
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/returnRequest/mkreturn.htmlx",
		data:{
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
</script>