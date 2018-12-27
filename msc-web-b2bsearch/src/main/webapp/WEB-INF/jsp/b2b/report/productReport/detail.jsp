<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="height: 100%;">
	<table id="dgDetail"></table>
</div>



<script>
	//初始化
	$(function() {
		var orderType = '<c:out value='${orderType}'/>';
		var column ;
		if(orderType == 1 || orderType == 2){
			column = [[
				{field:'PRODUCTCODE',title:'药品编码',width:135,align:'center'},
	        	{field:'PRODUCTNAME',title:'药品名称',width:135,align:'center'},
	        	{field:'DOSAGEFORMNAME',title:'剂型',width:135,align:'center'},	
	        	{field:'MODEL',title:'规格',width:135,align:'center'},		
	        	{field:'NUM',title:'数量',width:100,align:'center'},	
	        	{field:'AMT',title:'金额',width:100,align:'right',
					formatter: function(value,row,index){
						if (row.AMT){
							return common.fmoney(row.AMT);
						}
					}},	
	        	{field:'CODE',title:'明细编号',width:200,align:'center'},	
	        	{field:'HOSPITALNAME',title:'医院',width:135,align:'center'},	
	        	{field:'VENDORNAME',title:'供应商',width:160,align:'center'},
	        	{field:'handle',title:'操作',width:135,align:'center',formatter:function(value,row,index){
	        		var url = "";
	        		var title = "";
	        		if(orderType == 1) {
	        			url = "/hospital/contract.htmlx?code="+row.CONTRACTCODE;
		        		var title = "合同明细执行情况";
	        		} else if(orderType == 2) {
	        			url = "/hospital/purchaseRequest/other.htmlx?code="+row.PURCHASEORDERCODE;
		        		var title = "订单查询";
	        		}
					return "<img onclick=\"opensrc('"+title+"', '"+url+"');\" src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/undo.png\" style='cursor: pointer;'/>";
	    			
	        	}}
	   		]];
		} else if (orderType == 3) {

			column = [[
				{field:'PRODUCTCODE',title:'药品编码',width:135,align:'center'},
	        	{field:'PRODUCTNAME',title:'药品名称',width:135,align:'center'},
	        	{field:'DOSAGEFORMNAME',title:'剂型',width:135,align:'center'},	
	        	{field:'MODEL',title:'规格',width:135,align:'center'},			
	        	{field:'BARCODE',title:'条码',width:135,align:'center'},		
	        	{field:'BATCHCODE',title:'批号',width:135,align:'center'},	
	        	{field:'BATCHDATE',title:'生产日期',width:135,align:'center'},	
	        	{field:'EXPIRYDATE',title:'有效日期',width:135,align:'center'},	
	        	{field:'NUM',title:'数量',width:100,align:'center'},	
	        	{field:'AMT',title:'金额',width:135,align:'right',
					formatter: function(value,row,index){
						if (row.AMT){
							return common.fmoney(row.AMT);
						}
					}},	
	        	{field:'CODE',title:'明细编号',width:200,align:'center'},	
	        	{field:'HOSPITALNAME',title:'医院',width:135,align:'center'},	
	        	{field:'VENDORNAME',title:'供应商',width:135,align:'center'}	
	   		]];
		} else if (orderType == 6) {

			column = [[
				{field:'PRODUCTCODE',title:'药品编码',width:135,align:'center'},
	        	{field:'PRODUCTNAME',title:'药品名称',width:135,align:'center'},
	        	{field:'DOSAGEFORMNAME',title:'剂型',width:135,align:'center'},	
	        	{field:'MODEL',title:'规格',width:135,align:'center'},			
	        	{field:'NUM',title:'数量',width:100,align:'center'},	
	        	{field:'AMT',title:'金额',width:135,align:'right',
					formatter: function(value,row,index){
						if (row.AMT){
							return common.fmoney(row.AMT);
						}
					}},	
	        	{field:'STATUS',title:'状态',width:135,align:'center',
						 formatter: function(value,row,index){
							if (row.STATUS==0){
								return "正常";
							}else if(row.STATUS==1){
								return "供应商取消";
							}else{
								return "医院取消";
							}
							} },	
	        	{field:'CODE',title:'明细编号',width:200,align:'center'},	
	        	{field:'DELIVERYGOODSNUM',title:'实际配送数量',width:135,align:'center'},	
	        	{field:'DELIVERYGOODSSUM',title:'实际配送金额',width:135,align:'center'},	
	        	{field:'INOUTBOUNDGOODSNUM',title:'实际入库数量',width:135,align:'center'},	
	        	{field:'INOUTBOUNDGOODSSUM',title:'实际入库金额',width:135,align:'center'},	
	        	{field:'INOUTBOUNDGOODSNUM',title:'实际入库数量',width:135,align:'center'},
	        	{field:'ISUSED',title:'是否拆单',width:135,align:'center',formatter: function(value,row,index){
					if (row.ISUSED==1){
						return '是';
					}else{
						return '否';
					}
					}},
	        	{field:'PRODUCERNAME',title:'生产企业',width:100,align:'center'}
	        	/* {field:'HOSPITALNAME',title:'医院',width:20,align:'center'},	
	        		 */
	   		]];
		} else{
			column = [[
				{field:'PRODUCTCODE',title:'药品编码',width:135,align:'center'},
	        	{field:'PRODUCTNAME',title:'药品名称',width:135,align:'center'},
	        	{field:'DOSAGEFORMNAME',title:'剂型',width:135,align:'center'},	
	        	{field:'MODEL',title:'规格',width:135,align:'center'},			
	        	{field:'BATCHCODE',title:'批号',width:135,align:'center'},	
	        	{field:'BATCHDATE',title:'生产日期',width:135,align:'center'},	
	        	{field:'EXPIRYDATE',title:'有效日期',width:135,align:'center'},	
	        	{field:'NUM',title:'数量',width:100,align:'center'},	
	        	{field:'AMT',title:'金额',width:135,align:'right',
					formatter: function(value,row,index){
						if (row.AMT){
							return common.fmoney(row.AMT);
						}
					}},	
	        	{field:'CODE',title:'明细编号',width:200,align:'center'},	
	        	{field:'HOSPITALNAME',title:'医院',width:135,align:'center'},	
	        	{field:'VENDORNAME',title:'供应商',width:135,align:'center'}
	   		]];
		}
		$('#dgDetail').datagrid({
			fitColumns:false,
			striped:true,
			singleSelect:true,
			rownumbers:true,
			border:true,
			height : '100%',
			pagination: true,
			url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productReport/mxPage.htmlx",  
		    queryParams:{
		    	"orderType":orderType,
		    	"hospitalCode":'<c:out value='${hospitalCode}'/>',
		    	"productCode":'<c:out value='${productCode}'/>',
		    	"startDate":'<c:out value='${startDate}'/>',
		    	"endDate":'<c:out value='${endDate}'/>'
		    },
			pageSize:10,
			pageNumber:1,
			showFooter: true,
			remoteFilter: true,
			columns:column
		});
		$('#dgDetail').datagrid('removeFilterRule');
		if(orderType == "1"){
			$('#dgDetail').datagrid('enableFilter', [{
		        field:'PRODUCTCODE',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'PRODUCTNAME',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'DOSAGEFORMNAME',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'MODEL',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'NUM',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'AMT',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'CODE',
		        type:'text',
		        fieldType:'cd#S'
		    },{
		        field:'HOSPITALNAME',
		        type:'text',
		        fieldType:'c#S'
		    },{
		        field:'VENDORNAME',
		        type:'text',
		        fieldType:'c#S'
		    }]);
		}else{
			$('#dgDetail').datagrid('enableFilter', [{
		        field:'PRODUCTCODE',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'PRODUCTNAME',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'DOSAGEFORMNAME',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'MODEL',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'NUM',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'BATCHCODE',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'BATCHDATE',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'EXPIRYDATE',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'AMT',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'CODE',
		        type:'text',
		        fieldType:'p#S'
		    },{
		        field:'HOSPITALNAME',
		        type:'text',
		        fieldType:'po#S'
		    },{
		        field:'VENDORNAME',
		        type:'text',
		        fieldType:'po#S'
		    }]);
		}
	});
	function opensrc(title,url) {
		top.addTab(title, url);
		top.$.modalDialog.handler.dialog('destroy');
		top.$.modalDialog.handler = undefined;
	}
</script>