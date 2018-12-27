<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />


<body class="easyui-layout"  >
	<div  data-options="region:'west',title:'订单',collapsible:false" style="width:450px;background: rgb(238, 238, 238);padding:2px">
		<table  id="dg" ></table>
	</div>
	       
	<div data-options="region:'center',title:'配送单明细'" style="background: rgb(238, 238, 238);">
		<div style="padding:2px;">
			<input type="hidden" id="payDate" />
			<table id="dgDetail" ></table>
			<input type="hidden" id="invoiceCode" />
			<input type="hidden" id="invoiceDate" />
			<input type="hidden" id="taxRate" />
		</div>
    </div>
</body>

</body>
</html>

<script>
//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-40,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/invoiceblue/orderPage.htmlx",
		pageSize:10,
		pageNumber:1,
		columns:[[
					{field:'PURCHASEORDERCODE',title:'订单号',width:25,align:'center'},
		        	{field:'HOSPITALNAME',title:'医疗机构',width:15,align:'center'},
		        	{field:'PSUM',title:'采购金额',width:15,align:'center',
						formatter: function(value,row,index){
							if (row.PSUM){
								return common.fmoney(row.PSUM);
							}
						}},
		        	{field:'PORDERDATE',title:'订单日期',width:15,align:'center'}
		   		]],
		onLoadSuccess:function(data){
			var rows=data.rows;
			//大于1行默认选中
			if(rows.length > 0) {
				$(this).datagrid('selectRow', 0);
	        	searchDefectsList(rows[0]);
			}
			$('#dg').datagrid('doCellTip',{delay:500}); 
		},
		onClickRow: function(index,row){
			searchDefectsList(row);  
		}
	});
	
	$('#dg').datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false,
		displayMsg:"共{total}记录"
	});	
	
	//datagrid
	$('#dgDetail').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		border:true,
		height :  $(this).height()-40,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		columns:[[
				{field:'code',title:'配送单明细',width:25,align:'center'},
				{field:'productCode',title:'药品编码',width:10,align:'center'},
				{field:'productName',title:'药品名称',width:15,align:'center'},
			/* {field:'producerName',title:'生产企业',width:20,align:'center'},
 			{field:'price',title:'成交价（元）',width:10,align:'right',
					formatter: function(value,row,index){
						if (row.price){
							return common.fmoney(row.price);
						}
					}},	
			{field:'unitName',title:'单位',width:10,align:'center',
        		formatter: function(value,row,index){
					if (row.product){
						return row.product.unitName;
					}
				}}, */
/* 			{field:'goodsNum',title:'采购数量',width:10,align:'center'}, */
			{field:'goodsSum',title:'采购金额',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.goodsSum){
						return common.fmoney(row.goodsSum);
					}
				}},
			{field:'invoiceGoodsSum',title:'已开票',width:10,align:'right',
					formatter: function(value,row,index){
						if (row.invoiceGoodsSum){
							return common.fmoney(row.invoiceGoodsSum);
						}
					}},
			/* {field:'inOutBoundGoodsNum',title:'入库数量',width:10,align:'center'},
			{field:'returnsGoodsNum',title:'退货数量',width:10,align:'center'}, */
			{field:'returnsGoodsSum',title:'开票',width:10,align:'center', 
				editor: { 
					type: 'numberbox', 
					options: { min:0,precision:2 } 
				}
			}
		   		]],
		toolbar: [{
			iconCls: 'icon-ok',
			text:"上传发票",
			handler: function(){
				addPara();	
			}
		}],
		onDblClickRow: function(index,field,value){
			editOpen();
		},
		onLoadSuccess:function(data){
			$.each(data.rows,function(index,row){
				$('#dgDetail').datagrid('beginEdit', index);
			}); 
			$('#dgDetail').datagrid('doCellTip',{delay:500}); 
		}
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
			href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/invoiceblue/para.htmlx",
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
	
	
});

//下单
function mkinvoice(){
	var selrow = $('#dg').datagrid('getSelected');
	var selobjs = $('#dgDetail').datagrid('getRows');
	
	var orderCode = selrow.PURCHASEORDERCODE;
	var detailIds = new Array();
	var sums = new Array();
	$.each(selobjs,function(index,row){
		var number = row.returnsGoodsSum;
		var ed = $('#dgDetail').datagrid('getEditor', { index: $('#dgDetail').datagrid("getRowIndex",row) , field: 'returnsGoodsSum' });
		if(ed != null){
			number = $(ed.target).numberbox('getValue');
	    }
	     if(number>0){
	    	 detailIds[detailIds.length] = row.id;
	    	 sums[sums.length] = number;
	     }
	}); 
	
	$.messager.confirm('确认信息', '请核对发票金额，确认上传吗?', function(r){
		if (r){
			mkinvoiceAjax(orderCode,detailIds,sums);
		}
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
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/settle/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}

//搜索
function searchDefectsList(row){
	$('#dgDetail').datagrid({  
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/invoiceblue/mxlist.htmlx",
	    queryParams:{
	    	"query[t#purchaseOrderDetailCode_S_LK]":row.PURCHASEORDERCODE,
	    	"query[t#isInvoice_I_EQ]":0
	    }
	}); 
}
//下单
function mksettle(){
	var selobjs = $('#dgDetail').datagrid('getSelections');
	if(selobjs.length == 0){
		showMsg("请至少选择一笔");
		return;
	}
	var invoiceids = new Array();
	$.each(selobjs,function(index,row){
	     invoiceids[index] = row.ID;
	}); 
	$.messager.confirm('确认信息', '确定要结算吗?', function(r){
		if (r){
			mksettleAjax(invoiceids);
		}
	});
	
}
//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "开票成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/invoiceblue/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}


//=============ajax===============
function mkinvoiceAjax(orderCode,detailIds,sums){
	var invoiceCode = $("#invoiceCode").val();	
	var invoiceDate = $("#invoiceDate").val();
	var taxRate = $("#taxRate").val();
	
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/invoiceblue/mkinvoice.htmlx",
		data:{
			"orderCode":orderCode,
			"detailIds":detailIds,
			"sums":sums,
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