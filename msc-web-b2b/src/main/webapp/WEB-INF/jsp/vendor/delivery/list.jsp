<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="订单列表" />
<html>
<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false"  class="my-north" style="height:300px;">
	 <div id="search-bar" style="padding:6px">
			订单日期: <input class="easyui-datebox" style="width:110px" id="startDate">
	        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="dosearch()">查询</a>
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" onclick="addPara()">配送</a>
	   </div>
	<div>
		<input type="hidden" id="internalCode" /> 
		<input type="hidden" id="senderCode" />
		<table  id="dg"></table>
	</div>
</div>

<div data-options="region:'center',title:''"  class="my-center">
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
		"query['t#createDate_D_GE']": startDate,
		"query['t#createDate_D_LE']": toDate
	});
}
//查询
function searchDefectsList(row){
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/delivery/mxlist.htmlx", 
	    queryParams:{  
	    	"query['t#purchaseOrder.id_L_EQ']":row.id,
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
	$("#status").combobox({    
	    valueField:'label',    
	    textField:'value',  
	    panelHeight:160,
	    editable:false,
	    data:[{
	    	label: '',
			value: '全部'
		},{
			label: 'effect',
			value: '已生效'
		},{
			label: 'sending',
			value: '配送中'
		},{
			label: 'sent',
			value: '配送完成'
		},{
			label: 'forceClosed',
			value: '强行结案'
		}]
	});
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(".my-north").height()-$("#search-bar").height() -12,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/delivery/page.htmlx",
		pageSize:10,
		pageNumber:1,
		columns:[[
        	{field:'code',title:'订单号',width:20,align:'center'},
        	{field:'createDate',title:'订单日期',width:20,align:'center',
				formatter: function(value,row,index){
					if (row.createDate){
						return $.format.date(row.createDate,"yyyy-MM-dd HH:mm:ss");
					}
				}
			},       	
			{field:'requireDate',title:'要求配货时间',width:20,align:'center',
				formatter: function(value,row,index){
					if (row.requireDate){
						return $.format.date(row.requireDate,"yyyy-MM-dd HH:mm:ss");
					}
				}
			},
        	{field:'hospitalName',title:'医疗机构',width:20,align:'center'},
        	{field:'vendorName',title:'供应商',width:20,align:'center'},  			    
        	{field:'warehouseName',title:'收货地点',width:10,align:'center'},
        	/* 
			{field:'inOutBoundNum',title:'入库数量',width:10,align:'center'},
			{field:'returnsNum',title:'退货数量',width:10,align:'center'}, */
			{field:'deliveryNum',title:'配送数量',width:10,align:'center'},
			{field:'num',title:'采购数量',width:10,align:'center'},
			{field:'sum',title:'采购金额（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.sum){
						return common.fmoney(row.sum);
					}
				}},
			{field:'status',title:'状态',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.status == 'effect'){
						return "已生效";
					}else if(row.status == 'sending'){
						return "配送中";
					}else if(row.status == 'sent'){
						return "配送完成";
					}else if(row.status == 'forceClosed'){
						return "强行结案";
					}
				}
			}/* ,
			{field:'purchaseOrderPlanCode',title:'订单计划号',width:15,align:'center'} */
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
			{field:'goodsNum',title:'采购数量',width:80,align:'center'},
			{field:'goodsSum',title:'采购金额 （元）',width:100,align:'right',
				formatter: function(value,row,index){
					if (row.goodsSum){
						return common.fmoney(row.goodsSum);
					}
				}},
			{field:'deliveryGoodsNum',title:'已配送数量',width:80,align:'center'},
        	{field:'batchCode',title:'批号',width:100,align:'center', 
				editor: { 
					type: 'textbox', 
					options: {required:true } 
				}
			},
			{field:'batchDate',title:'生产日期',width:100,align:'center', 
				editor: { 
					type: 'datebox', 
					options: {editable:false,required:true } 
				}
			},
			{field:'expiryDate',title:'有效日期',width:100,align:'center', 
				editor: { 
					type: 'datebox', 
					options: {editable:false,required:true  } 
				}
			},
			{field:'returnsGoodsNum',title:'数量',width:80,align:'center', 
				editor: { 
					type: 'numberbox', 
					options: { min:0,precision:0 } 
				}
			},
			{field:'qualityRecord',title:'质量记录',width:100,align:'center', 
				editor: { 
					type: 'textbox', 
					options: {required:true } 
				}
			},
			{field:'inspectionReportUrl',title:'检验报告链接',width:150,align:'center', 
				editor: { 
					type: 'textbox', 
					options: {required:true } 
				}
			}
			
		]],
		onClickRow: function (index, row) {
			//$('#dgDetail').datagrid('beginEdit', index);
		},
		onLoadSuccess:function(data){
			 $.each(data.rows,function(index,row){
				$('#dgDetail').datagrid('beginEdit', index);
			});  
			$('#dgDetail').datagrid('doCellTip',{delay:500}); 
		}
	
	});
});
//下单
function vendorFunc(){
	var selrow = $('#dg').datagrid('getSelected');
	var selobjs = $('#dgDetail').datagrid('getRows');
	
	var orderId = selrow.id;
	var detailIds = new Array();
	var nums = new Array();
	var batchCodes = new Array();
	var batchDates = new Array();
	var expiryDates = new Array();
	var qualityRecords = new Array();
	var inspectionReportUrls = new Array();
	$.each(selobjs,function(index,row){
		var number = row.returnsGoodsNum;
		var ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'returnsGoodsNum' });
		if(ed != null){
			number = $(ed.target).numberbox('getValue');
	    }
	     if(number>0){
	    	 detailIds[detailIds.length] = row.id;
		     nums[nums.length] = number;
		     ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'batchCode' });
		     if(ed != null){
		    	 batchCodes.push($(ed.target).textbox('getValue'));
			 }else{
				 batchCodes.push("");
			 }
		    
		     ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'batchDate' });
		     if(ed != null){
		    	 batchDates.push($(ed.target).textbox('getValue'));
			 }else{
				 batchDates.push("");
			 }
		    
		     ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'expiryDate' });
		     if(ed != null){
		    	 expiryDates.push($(ed.target).textbox('getValue'));
			 }else{
				 expiryDates.push("");
			 }
		     
		     ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'qualityRecord' });
		     if(ed != null){
		    	 qualityRecords.push($(ed.target).textbox('getValue'));
			 }else{
				 qualityRecords.push("");
			 }
		     
		     ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'inspectionReportUrl' });
		     if(ed != null){
		    	 inspectionReportUrls.push($(ed.target).textbox('getValue'));
			 }else{
				 inspectionReportUrls.push("");
			 }
	     }
	}); 
	
	$.messager.confirm('确认信息', '请核对配送数量，确认配送吗?', function(r){
		if (r){
			vendorAjax(orderId,detailIds,nums,batchCodes,batchDates,expiryDates,qualityRecords,inspectionReportUrls);
		}
	});
	
	
}

//弹窗信息
function addPara() {
	var selrow = $('#dg').datagrid('getSelected');
	var selobjs = $('#dgDetail').datagrid('getRows');

	var eachflag = 1;
	var numcount = 0;
	$.each(selobjs,function(index,row){
		if(eachflag == 1){
			var number = row.returnsGoodsNum;
			var ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'returnsGoodsNum' });
			if(ed != null){
				number = $(ed.target).numberbox('getValue');
		    }
		     if(number>row.goodsNum - row.deliveryGoodsNum){
		    	 showErr(row.productName+"的配送数量"+number+"超上限"+(row.goodsNum - row.deliveryGoodsNum));
		    	 eachflag = 0;
		     }
		     if(number>0){
		    	numcount ++;
		    	 
		    	var batchCode = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'batchCode' });
				if(batchCode != null){
					var isValid = $(batchCode.target).textbox('isValid');
					if(!isValid){
						eachflag = 0;
					}						
			    }
				
		    	var batchDate = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'batchDate' });
				if(batchDate != null){
					var isValid = $(batchDate.target).datebox('isValid');
					if(!isValid){
						eachflag = 0;
					}						
			    }
				
				var expiryDate = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'expiryDate' });
				if(expiryDate != null){
					var isValid = $(expiryDate.target).datebox('isValid');
					if(!isValid){
						eachflag = 0;
					}						
			    }
				
				var qualityRecord = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'qualityRecord' });
				if(qualityRecord != null){
					var isValid = $(qualityRecord.target).textbox('isValid');
					if(!isValid){
						eachflag = 0;
					}						
			    }
				
				var inspectionReportUrl = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'inspectionReportUrl' });
				if(inspectionReportUrl != null){
					var isValid = $(inspectionReportUrl.target).textbox('isValid');
					if(!isValid){
						eachflag = 0;
					}						
			    }
		    }
		 	
		}
	}); 
	if(eachflag == 0)
		return;
	if(numcount == 0){
		showErr("请输入配送数量");
		return;
	}
	
	top.$.modalDialog({
		title : "信息",
		width : 400,
		height : 200,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/delivery/para.htmlx",
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
				
				var internalCode = f.find("#internalCode").textbox("getValue");
				var senderCode = f.find("#senderCode").combobox("getValue");
				
				top.$.modalDialog.handler.dialog('close');
				
				$("#internalCode").val(internalCode);
				$("#senderCode").val(senderCode);
				
				vendorFunc();
				
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
		title : "配送成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/delivery/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}


//=============ajax===============
function vendorAjax(orderId,detailIds,nums,batchCodes,batchDates,expiryDates,qualityRecords,inspectionReportUrls){
	var internalCode = $("#internalCode").val();
	var senderCode = $("#senderCode").val();
	
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/delivery/mkdelivery.htmlx",
		data:{
			"orderId":orderId,
			"internalCode":internalCode,
			"senderCode":senderCode,
			"detailIds":detailIds,
			"nums":nums,
			"batchCodes":batchCodes,
			"batchDates":batchDates,
			"expiryDates":expiryDates,
			"qualityRecords":qualityRecords,
			"inspectionReportUrls":inspectionReportUrls
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				$('#dgDetail').datagrid('reload');  
				showMsg("配送成功！");
				successOpen(data.data);
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