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
			订单日期: <input class="easyui-datebox" style="width:110px" id="startDate">
	        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
	        
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="dosearch()">查询</a>
	   	 	<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" onclick="gporeturn()">拒收</a>
	   </div>
	<div>
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
	    url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/return/mxlist.htmlx", 
	    queryParams:{  
	    	"query['t#deliveryOrder.id_L_EQ']":row.id
	    }
	});
}
//初始化
$(function(){
	$("#hospital").combobox({    
	    url:" <c:out value='${pageContext.request.contextPath }'/>/set/hospital/list.htmlx",  
	    valueField:'id',    
	    textField:'fullName',   
	    width:180,
	    //editable:false, 
	    onSelect:function(a,b){
	    	
	    }
	});

	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(".my-north").height()-$("#search-bar").height() -12,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/return/page.htmlx",
		pageSize:10,
		pageNumber:1,
		columns:[[
		          {field:'code',title:'配送单号',width:20,align:'center'},
		        	{field:'receiveDate',title:'通知时间',width:20,align:'center',
			formatter: function(value,row,index){
				if (row.receiveDate){
					return $.format.date(row.receiveDate,"yyyy-MM-dd HH:mm:ss");
				}
			}
		}, 
		{field:'orderDate',title:'配送时间',width:20,align:'center',
			formatter: function(value,row,index){
				if (row.orderDate){
					return $.format.date(row.orderDate,"yyyy-MM-dd HH:mm:ss");
				}
			}
		}, 
		{field:'vendorName',title:'供应商',width:20,align:'center'},
		{field:'hospitalName',title:'医疗机构',width:20,align:'center'},
		{field:'num',title:'配送数量',width:10,align:'center'},
		{field:'inOutBoundNum',title:'入库数量',width:10,align:'center'},
		{field:'rejectNum',title:'拒收数量',width:10,align:'center'},
		{field:'returnsNum',title:'退货数量',width:10,align:'center'},
		{field:'sum',title:'配送金额（元）',width:10,align:'right',
			formatter: function(value,row,index){
				if (row.sum){
					return common.fmoney(row.sum);
				}
			}},
		{field:'status',title:'状态',width:10,align:'center',
			formatter: function(value,row,index){
				if (row.status == 'unreceive'){
					return "未收货";
				}else if(row.status == 'receiving'){
					return "收货中";
				}else if(row.status == 'closed'){
					return "收货完成";
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
			{field:'goodsNum',title:'配送数量',width:60,align:'center'},
			{field:'goodsSum',title:'金额 （元）',width:80,align:'right',
				formatter: function(value,row,index){
					if (row.goodsSum){
						return common.fmoney(row.goodsSum);
					}
				}},		
        	{field:'NUM',title:'数量',width:60,align:'center', editor: { type: 'numberbox',options: { required:true,min:0,precision:3 } }},
			{field:'reason',title:'拒收原因',width:150,align:'center', editor: { type: 'textbox',options: {required:true} }},
			{field:'inOutBoundGoodsNum',title:'入库数量',width:60,align:'center'},
			{field:'rejectGoodsNum',title:'拒收数量',width:60,align:'center'},
			{field:'returnsGoodsNum',title:'退货数量',width:60,align:'center'},
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
//退货
function gporeturn(){
	var selrow = $('#dg').datagrid('getSelected');
	if(selrow== null){
		showErr("请先选择一笔订单");
		return;
	}
		
	var selobjs = $('#dgDetail').datagrid('getRows');
	var deliveryId = selrow.id;
	var detailIds = new Array();
	var reasons = new Array();
	var nums = new Array();
	var eachflag = 1;
	$.each(selobjs,function(index,row){
		 var ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'NUM' });
	     var number = $(ed.target).numberbox('getValue');
	     if(number > 0){
	    	 detailIds[detailIds.length] = row.id;
		     nums[nums.length] = number;
		     ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'reason' });
		     reasons[reasons.length] = $(ed.target).textbox('getValue');
	     }
	     if(number>row.goodsNum - row.inOutBoundGoodsNum - row.rejectGoodsNum){
	    	 showErr(row.productCode+"的拒收数量"+number+"超上限"+(row.goodsNum - row.returnsGoodsNum));
	    	 eachflag = 0;
	     }
	     if(number>0){		    	
	    	var reason = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'reason' });
			if(reason != null){
				var isValid = $(reason.target).textbox('isValid');
				if(!isValid){
					eachflag = 0;
				}						
		    }			
	    }
	}); 
	if(eachflag == 0)
		return;
	if(detailIds.length<=0){
		showErr("请输入退货数量");
		return;
	}
	$.messager.confirm('确认信息', '请核对退货数量，确认退货吗?', function(r){
		if (r){
			returnAjax(deliveryId,detailIds,nums,reasons);
		}
	});
	
	
}

//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "拒收成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/return/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}

//=============ajax===============
function returnAjax(deliveryId,detailIds,nums,reasons){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/return/mkreturn.htmlx",
		data:{
			"deliveryId":deliveryId,
			"detailIds":detailIds,
			"nums":nums,
			"reasons":reasons
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				//$('#dg').datagrid('reload');  
				//$('#dgDetail').datagrid('reload');  
				showMsg("拒收成功！");
				successOpen(data.data);
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>