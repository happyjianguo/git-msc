<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<tag:head title="药品交易汇总统计" />

<html>

<body class="easyui-layout"  >
	<div class="search-bar" id="tb">
		类型: 
	 	<input style="width:100px" id="orderType"/>
		日期区间: <input id="startDate">
        ~ <input id="endDate">
        <div style="display: none;">
      	 医院: 
	 	<input style="width:100px;" id="hospital" /></div>
       	药品编码: 
	 	<input id="productCode" class="easyui-textbox" style="width:150px"/>
	 	药品名称: 
	 	<input id="productName" class="easyui-textbox" style="width:150px"/>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch()">查询</a>
        <span class="datagrid-btn-separator split-line" ></span>
		<shiro:hasPermission name="b2b:report:productReport:exportExcel">
				<a href="#" class="easyui-linkbutton" iconCls="icon-xls" onclick="exportExcel()">报表下载</a>
		</shiro:hasPermission>
	</div>
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>
</body>

</html>
<script>
var startDate="";
var endDate="";
var hospitalCode="";
var productCode = "";
var productName = "";
var orderType = "";
function dosearch(){
  	startDate = $('#startDate').datebox('getValue');		
	endDate = $('#endDate').datebox('getValue');
	hospitalCode = $('#hospital').combogrid('getValue');
	productCode = $('#productCode').textbox('getValue');
	productName = $('#productName').textbox('getValue');
	orderType = $("#orderType").combobox('getValue');
	var data = {
		"startDate": startDate,
		"endDate": endDate,
		"orderType": orderType,
		"hospitalCode":hospitalCode,
		"productCode":productCode,
		"productName":productName
	};
	var columns=[
				{field:'PRODUCTCODE',title:'药品编码',width:10,align:'center'},
				{field:'PRODUCTNAME',title:'药品名称',width:15,align:'center'},
				//{field:'GENERICNAME',title:'通用名',width:15,align:'center'},
				{field:'DOSAGEFORMNAME',title:'剂型',width:15,align:'center'},
				{field:'MODEL',title:'规格',width:15,align:'center'},
				{field:'UNITNAME',title:'单位',width:10,align:'center'},
				{field:'PACKDESC',title:'包装规格',width:15,align:'center'},
        		{field:'AUTHORIZENO',title:'国药准字',width:15,align:'center'},
				{field:'PRODUCERNAME',title:'生产企业名称',width:15,align:'center'},
				{field:'NUM',title:'数量',width:15,align:'right'},
        		{field:'PRICE',title:'单价',width:15,align:'center'},
				{field:'AMT',title:'金额',width:15,align:'right',
					formatter: function(value,row,index){
						if (row.AMT){
								return common.fmoney(row.AMT);
						}
					}},
		        {field:'ISGPOPURCHASE',title:'是否GPO药品',width:15,align:'center',
            		formatter: function(value,row,index){
                		if (row.ISGPOPURCHASE ==1){
                   			 return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
               			 }
            		}}
	   		]
	if ("4" == orderType) {
		columns.push({field:'GPOGOODSSUM',title:'GPO金额',width:15,align:'right',
			formatter: function(value,row,index){
				if (row.GPOGOODSSUM){
					return common.fmoney(row.GPOGOODSSUM);
				}
			}});
	}
	$('#dg').datagrid({queryParams:data,columns:[columns]});
}

function exportExcel(){
	startDate = $('#startDate').datebox('getValue');		
	endDate = $('#endDate').datebox('getValue');
	hospitalCode = $('#hospital').combogrid('getValue');
	productCode = $('#productCode').textbox('getValue');
	productName = $('#productName').textbox('getValue');
	orderType = $("#orderType").combobox('getValue');
	$.messager.confirm('确认信息', '确认下载?', function(r){
		if (r){
			window.open(" <c:out value='${pageContext.request.contextPath }'/>"+
					"/b2b/report/productReport/exportExcel.htmlx"+
					"?orderType="+orderType+"&startDate="+startDate+"&endDate="+endDate+
					"&productCode="+productCode+"&productName="+productName);
		}
	});
}

$(function(){
	$("#orderType").combobox({
	    valueField:'label',    
	    textField:'value',  
	    panelHeight:160,
	    editable:false,
	    data:[{
	    	label: '1',
			value: '合同'
		},{
			label: '2',
			value: '订单'
		},{
			label: '3',
			value: '配送单'
		},{
			label: '4',
			value: '入库单'
		},{
			label: '5',
			value: '退货单'
		},{
			label: '6',
			value: '订单计划'
		}],
		value:1,
		onChange:function(value){
			dosearch();
		}
	});
	$('#startDate').datebox({
		width:130
	}).combobox("initClear");
	$('#endDate').datebox({
		width:130
	}).combobox("initClear");
	$('#hospital').combogrid({
		idField:'code',    
		textField:'fullName', 
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/hospital/page.htmlx",
	    queryParams:{
	    	"query['t#isDisabled_I_EQ']":0
		},
		pagination : true,
		pageSize : 10,
		pageNumber : 1,
		width:200,
		panelWidth:300,
		delay:800,
		columns : [ [ {
			field : 'code',
			title : '编码',
			width : 100
		}, {
			field : 'fullName',
			title : '医院名称',
			width : 180
		}]],//panelHeight:'auto',
		keyHandler : {
			query : function(q) {
				//动态搜索
				$('#hospital')
						.combogrid('grid')
						.datagrid(
								"reload",
								{
									"query['t#fullName_S_LK']" : q
								});
				$('#hospital').combogrid("setValue", q);
			}

		},
		onSelect: function(rowIndex, rowData){
			$("#hospitalName").val(rowData.fullName);
		}
	}).combobox("initClear");
	$('#hospital').combogrid('grid').datagrid('getPager').pagination({
		showPageList : false,
		showRefresh : false,
		displayMsg : "共{total}记录"
	});
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height:$(this).height()-4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productReport/page.htmlx",
		queryParams:{
	    	"orderType":$("#orderType").combobox('getValue')
	    },
		pageSize:20,
		pageNumber:1,
		pagination:true,
		columns:[[
			{field:'PRODUCTCODE',title:'药品编码',width:10,align:'center'},
			{field:'PRODUCTNAME',title:'药品名称',width:15,align:'center'},
			//{field:'GENERICNAME',title:'通用名',width:15,align:'center'},
			{field:'DOSAGEFORMNAME',title:'剂型',width:15,align:'center'},
			{field:'MODEL',title:'规格',width:15,align:'center'},
			{field:'UNITNAME',title:'单位',width:10,align:'center'},
			{field:'PACKDESC',title:'包装规格',width:15,align:'center'},
            {field:'AUTHORIZENO',title:'国药准字',width:15,align:'center'},
			{field:'PRODUCERNAME',title:'生产企业名称',width:15,align:'center'},
			{field:'NUM',title:'数量',width:15,align:'right'},
            {field:'PRICE',title:'单价',width:15,align:'center'},
			{field:'AMT',title:'金额',width:15,align:'right',
				formatter: function(value,row,index){
					if (row.AMT){
							return common.fmoney(row.AMT);
					}
				}},
			{field:'ISGPOPURCHASE',title:'是否GPO药品',width:15,align:'center',
        		formatter: function(value,row,index){
        			if (row.ISGPOPURCHASE ==1){
    					return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
    				}
    		}}
   		]],
   		toolbar:"#tb",
   		onDblClickRow:function(index,row){		
        	openDetail(row);
		}
	});
})
function openDetail(row) {
	startDate = $('#startDate').datebox('getValue');		
	endDate = $('#endDate').datebox('getValue');
	hospitalCode = $('#hospital').combogrid('getValue');
	orderType = $("#orderType").combobox('getValue');
	parent.$.modalDialog({
		title : "明细情况",
		width : 800,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productReport/detail.htmlx",
		queryParams:{
			"productCode": row.PRODUCTCODE,
			"startDate": startDate,
			"endDate": endDate,
			"orderType": orderType,
			"hospitalCode":hospitalCode
		},
		buttons : [{
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				top.$.modalDialog.handler.dialog('destroy');
				top.$.modalDialog.handler = undefined;
			}
		}]
	});
}
</script>