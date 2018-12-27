<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="收货" />
<html>
<style>
.datagrid-cell-c1-shsl{
	color:#0081c2;
}
</style>
<body class="easyui-layout" >
	<form id="form1">
		<div id="search-bar" style="padding:5px">
			配送日期: 
			<input class="easyui-datebox" style="width:110px" id="orderDate">
					<div id="mm">
						<div data-options="name:'barcode' ">条形码</div>
						<div data-options="name:'code' ">配送单编码</div>
						<div data-options="name:'productCode' ">药品编码</div>
						<div data-options="name:'productName' ">药品名称</div>
					</div>
		  		  	<input id="ss"/>
	        
<!-- 	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="query()">查询</a> -->
	   </div>
   	
		<div style="padding:0px 5px"> 
			<table  id="dg"></table>
		</div>
	</form>
</body>
</html>
<script>
//搜索
function dosearch(){
	var data = {};
	var orderDate = $("#orderDate").datebox('getValue');
	if(orderDate){
		var dataS = orderDate ;
		var dataE = orderDate ;
		data = {
			"query['d#orderDate_D_GE']": dataS,
			"query['d#orderDate_D_LE']": dataE
		};
	}
	
	var ssName = $('#ss').searchbox("getName");
	var ssValue = $('#ss').searchbox("getValue");

	if(ssName == "barcode" || ssName == "code"){
		data["query['d#"+ssName+"_S_LK']"] = ssValue;
	}else if(ssName == "productCode" || ssName == "productName"){
		data["query['t#"+ssName+"_S_LK']"] = ssValue;
	}

	$('#dg').datagrid('load',data);
}

//初始化
$(function(){
	$('#ss').searchbox({
		searcher:function(value, name) {
			dosearch();
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 
	
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(this).height()-40,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/delivery/page.htmlx",
		pageSize:20,
		pageNumber:1,
		columns:[[
			{field:'deliveryOrder.orderDate',title:'配送时间',width:120,align:'center',
        		formatter: function(value,row,index){
					if (row.deliveryOrder.orderDate){
						return $.format.date(row.deliveryOrder.orderDate,"yyyy-MM-dd HH:mm");
					}
				}},
			
        	{field:'vendorName',title:'供应商',width:120,align:'center',
	       		formatter: function(value,row,index){
					if (row.deliveryOrder){
						return row.deliveryOrder.vendorName;
					}
				}},
			{field:'productCode',title:'药品编码',width:80,align:'center'},
			{field:'productName',title:'药品名称',width:120,align:'center'},
			{field:'dosageFormName',title:'剂型',width:80,align:'center'},
			{field:'model',title:'规格',width:80,align:'center'},
			{field:'producerName',title:'生产企业名称',width:120,align:'center'},
			{field:'unit',title:'单位',width:40,align:'center'},
        	{field:'price',title:'价格',width:70,align:'right',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}},
			{field:'shsl',title:'收货数量',width:80,align:'center',editor:{type:'numberbox',options:{validType:'pinteger'}}},
			{field:'goodsNum',title:'配送数量',width:60,align:'center'},
			{field:'inOutBoundGoodsNum',title:'入库数量',width:60,align:'center'},    
			{field:'batchCode',title:'批号',width:80,align:'center'},
			{field:'batchDate',title:'生产日期',width:80,align:'center'},
			{field:'expiryDate',title:'有效日期',width:80,align:'center'},
	        {field:'code',title:'配送单编号',width:180,align:'center'},
	        {field:'barcode',title:'条形码',width:180,align:'center',
	       		formatter: function(value,row,index){
					if (row.deliveryOrder){
						return row.deliveryOrder.barcode;
					}
				}}
   		]],
		toolbar: [{
			iconCls: 'icon-ok',
			text:"确认收货",
			handler: function(){
				takeDelivery();	
			}
		}],
		onClickRow: function(index,row){			
			$('#dg').datagrid('beginEdit', index);
		  	return;
		},
		onLoadSuccess:function(data){
			/* var s = $('#dg').datagrid('getRows');
			for(var i = 0;i<s.length;i++){
				$('#dg').datagrid('beginEdit', i);
			} */
			$('#dg').datagrid('doCellTip',{delay:500});   
			/* $.each($("input.textbox-text",$("span.numberbox")),function(index){
				$(this).click(function(){
					$('#dg').datagrid("selectRow",index);
			    });
			}) */
		},
		queryParams:{
			"query['d#code_S_EQ']":"<c:out value='${code}'/>"
		}
	});
});

//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "收货成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/delivery/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}

function takeDelivery(){
	var selobjs = $('#dg').datagrid('getRows');
	if(selobjs.length == 0){
		showErr("请至少选择一行");
		return;
	}
	var isValid = $("#form1").form('validate');
	if(!isValid)
		return;
	
	var datas = new Array();
	var flag = true;
	var flagindex = 0;
	$.each(selobjs,function(index,row){
		 var selIndex = $('#dg').datagrid("getRowIndex",row);
		 var ed = $('#dg').datagrid('getEditor', { index: selIndex, field: 'shsl' });
		 if(ed != null){
			 var number = $(ed.target).numberbox('getValue');
		     var yrk = row.inOutBoundGoodsNum == null?0:row.inOutBoundGoodsNum;
		     var deliveryNum = row.goodsNum;
		
		     if(number&&number!=null){
			   	if(yrk -(-1)*number > deliveryNum){
			    	 flag = false;
			    	 showErr("收货数量("+yrk+"+"+number+")不能大于配送数量("+deliveryNum+")");
			     }
			   	 
		   		 var data = new Object();
			     data.goodsNum = number;
			     data.id = row.id;
			     datas[flagindex++] = data;
			   	 
		     }
		 }
	     
	   	 
	});
	if(flag && datas.length > 0){
		$.messager.confirm('确认信息', '您确认要收货吗?', function(r){
			if (r){
				takeDeliveryAjax(datas);
			}
		});
	}
	
}

function takeDeliveryAjax(datas){
	var data = JSON.stringify(datas);
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/delivery/takeDelivery.htmlx",
		data:{
			"fastjson": data
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');
				showMsg(data.msg);
				successOpen(data.data);
			} else{
				showErr("出错，请刷新重新操作");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function onClickRow(index){
	$('#dg').datagrid('selectRow', index).datagrid('beginEdit', index);
}

</script>