<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="退货单" />

<html>
<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/monitor.css" />
<body class="easyui-layout"  >
	<div  data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:300px;">
		<div id="tb" class="search-bar" >
			退货日期: <input class="easyui-datebox" style="width:110px" id="startDate">
	        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
	        药品编码: 
	 	<input id="productCode" class="easyui-textbox" style="width:150px"/>
	 	药品名称: 
	 	<input id="productName" class="easyui-textbox" style="width:150px"/>
	 	拼音检索: 
	 	<input id="pinyin" class="easyui-textbox" style="width:150px"/>
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
		    <span class="datagrid-btn-separator split-line" ></span>
		   	<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true" onclick="filterFunc()">数据过滤</a>
	    </div>
		<table  id="dg" ></table>
	</div>
	
	<div data-options="region:'center',title:''"   class="my-center" >
		<table id="dg2" ></table>
	</div>
</body>

</html>
<script>
var isFilter=0;
function filterFunc(){
	if(isFilter == 1) return;
	isFilter=1;
	$('#dg').datagrid({remoteFilter: true});
	$('#dg').datagrid('enableFilter', 
			[{
		        field:'ORDERDATE',
		        type:'datebox',
		        options:{
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'ORDERDATE');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'ORDERDATE',
		                        op: 'LK',
		                        fieldType:'D',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
		        field:'NUM',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'SUM',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'STATUS',
		        type:'text',
		       	isDisabled:1
		    },{
		        field:'OPERATION',
		        type:'text',
		       	isDisabled:1
		        
		    }]);
	$('#dg').datagrid('reload');
}
$(function(){

	//日期
	$('#orderDate1').datebox({
		editable:false
	});
	$('#orderDate2').datebox({
		editable:false
	});
	
	$('#ss').searchbox({
		searcher:function(value, name) {
			dosearch();
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 
	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height :$(".my-north").height(),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/returnsOrder/page.htmlx",
		pageSize:10,
		pageNumber:1,
		toolbar:"#tb",
		columns:[[
		        	{field:'code',title:'退货单号',width:20,align:'center'},
		        	{field:'orderDate',title:'退货时间',width:20,align:'center',
						formatter: function(value,row,index){
							if (row.orderDate){
								return $.format.date(row.orderDate,"yyyy-MM-dd HH:mm");
							}
						}
					}, 
					{field:'type',title:'类型',width:20,align:'center',
						formatter: function(value,row,index){				
							if (row.type == 'returns'){
								return "退货";
							}
							if (row.type == 'reject'){
								return "拒收";
							}
						}
					}, 
					{field:'vendorName',title:'供应商',width:20,align:'center'},
					{field:'hospitalName',title:'医疗机构',width:20,align:'center'},
					{field:'num',title:'退货数量',width:10,align:'center'},
					{field:'sum',title:'退货金额（元）',width:10,align:'right',
						formatter: function(value,row,index){
							if (row.sum){
								return common.fmoney(row.sum);
							}
						}}
		   		]],
		onClickRow: function(index,row){
			todg2(row);  
		},
		onLoadSuccess: function(data){
			var rows=data.rows;
			//大于1行默认选中
			if($(this).datagrid("getRows").length > 0) {
				$(this).datagrid('selectRow', 0);
				todg2(rows[0]);
			}
		},
        queryParams:{
            "query['t#code_S_EQ']":"<c:out value='${code}'/>"
        }
	});

	//组成员
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		rownumbers:true,
		height :'100%',
		singleSelect:true,
		showFooter: false,
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
			{field:'productCode',title:'药品编码',width:10,align:'center'},
			{field:'productName',title:'药品名称',width:20,align:'center'},
			{field:'dosageFormName',title:'剂型',width:10,align:'center'},		
			{field:'model',title:'规格',width:10,align:'center'},	
			{field:'producerName',title:'生产企业',width:20,align:'center'},
			{field:'packDesc',title:'包装规格',width:10,align:'center'},
			{field:'price',title:'价格(元)',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}},
			{field:'goodsNum',title:'退货数量',width:10,align:'center'},
			{field:'reason',title:'退货原因',width:10,align:'center'},
			{field:'goodsSum',title:'退货金额(元)',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.goodsSum){
						return common.fmoney(row.goodsSum);
					}
				}},
			{field:'batchCode',title:'批号',width:10,align:'center'},
			{field:'batchDate',title:'生产日期',width:10,align:'center'},
			{field:'expiryDate',title:'有效期',width:10,align:'center'},
   		]]
	});
})

function openPreP(code){
	top.addTab("配送单查询 ", "/b2b/monitor/deliveryOrder.htmlx?code="+code);
}
function todg2(row){
	$('#dg2').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/returnsOrder/mxlist.htmlx",  
	    queryParams:{
	        "query['t#returnsOrder.id_L_EQ']":row.ID
	    }
	}); 
	
}
//搜索
function dosearch(){
	var startDate="";
	var toDate="";
	var productCode = "";
	var productName = "";
	var pinyin="";
	if($('#productCode').textbox('getValue')!=""){
		productCode=$('#productCode').textbox('getValue');
	}
	if($('#productName').textbox('getValue')!=""){
		productName=$('#productName').textbox('getValue');	
	}
	if($('#pinyin').textbox('getValue')!=""){
		pinyin=$('#pinyin').textbox('getValue').toUpperCase();	
	}
	if($('#startDate').datebox('getValue')!=""){
	  	startDate = $('#startDate').datebox('getValue');		
	}
	if($('#toDate').datebox('getValue')!=""){
		toDate = $('#toDate').datebox('getValue');
	}	
	
	var data = {
		"query['t#orderDate_D_GE']": startDate,
		"query['t#orderDate_D_LE']": toDate,
		"query['d#productCode_S_EQ']":productCode,
		"query['d#productName_S_LK']":productName,
		"query['p#pinyin_S_LK']":pinyin
	};
	
	$('#dg').datagrid('load',data);
}
//=============ajax===============

</script>