<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
	<div id='tb1' class="search-bar">
		年月：
		<input  style="width:100px" id="month1"/>
		<span class="datagrid-btn-separator split-line" ></span>
	 	供应商：
	 	<input style="width:100px" id="vendor1"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch(1)">查询</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-chart_bar" onclick="openChart(1,'配送效率分析')">图表</a>
	</div>
	<div id='tb2' class="search-bar">
		年月：
		<input  style="width:100px" id="month2"/>
		<span class="datagrid-btn-separator split-line" ></span>
	 	供应商：
	 	<input style="width:100px" id="vendor2"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch(2)">查询</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-chart_bar" onclick="openChart(2,'供货质量分析')">图表</a>
	</div>
	<div id='tb3' class="search-bar">
		年月：
		<input  style="width:100px" id="month3"/>
		<span class="datagrid-btn-separator split-line" ></span>
	 	供应商：
	 	<input style="width:100px" id="vendor3"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch(3)">查询</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-chart_bar" onclick="openChart(3,'市场占用率分析')">图表</a>
	</div>
	
	<div id="tt">
		<div title="配送效率分析" class="my-tabs">
			<table id="dg1" class="single-dg"></table>
		</div>
		<div title="供货质量分析" class="my-tabs">
			<table id="dg2" class="single-dg"></table>
		</div>
		<div title="市场占用率分析" class="my-tabs">
			<table id="dg3" class="single-dg"></table>
		</div>
	</div>
</body>
</html>

<script>

//初始化
$(function(){
	$('#tt').tabs({
		plain:true,
		justified:true
	});
	
	
	initSearchBar(1);
	initSearchBar(2);
	initSearchBar(3);
	
	//datagrid
	$('#dg1').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -$("#tb1").height() -4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/vendorC1/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		toolbar:'#tb1',
		columns:[[														
		        	{field:'month',title:'年月',width:10,align:'center'},
		        	{field:'vendorName',title:'供应商',width:10,align:'center'},
		        	{field:'contractCount',title:'订单响应率',width:10,align:'center',
						formatter: function(value,row,index){
							if(row.purchaseTimes>0)
								return row.reviewTimes*100/row.purchaseTimes+"%";
							else return "0%";
					}},
		        	{field:'reviewHour',title:'平均订单审核时间（小时）',width:10,align:'center'},
		        	{field:'deliveryHour',title:'平均配送时间（小时）',width:10,align:'center'},
		        	{field:'sumHour',title:'平均订单响应时间（小时）',width:10,align:'center',
						formatter: function(value,row,index){
							return row.reviewHour+row.deliveryHour;
					}},
		        	{field:'contractSum',title:'配送及时率',width:10,align:'center',
						formatter: function(value,row,index){
							if(row.deliveryTimes>0)
								return row.deliveryTimelyTimes*100/row.deliveryTimes+"%";
							else return "0%";
					}},
		        	{field:'shortSupplyTimes',title:'断货次数',width:10,align:'center'},
		        	{field:'shortSupplySum',title:'断货采购金额',width:10,align:'center'}
		   		]]
		 
	});
	
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -$("#tb2").height() -4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/vendorC2/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		toolbar:'#tb2',								
		columns:[[											
		        	{field:'month',title:'年月',width:10,align:'center'},
		        	{field:'vendorName',title:'供应商',width:10,align:'center'},
		        	{field:'segmentStr',title:'平均有效期',width:10,align:'center'},
		        	{field:'returnsNum',title:'退货数量',width:10,align:'center'},
		        	{field:'deliveryNum',title:'配送数量',width:10,align:'center'},
		        	{field:'returnsSum',title:'退货金额',width:10,align:'center',
						formatter: function(value,row,index){
							return common.fmoney(row.returnsSum);
					}},
		        	{field:'returnsPer',title:'退货率',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.deliveryNum > 0){
								return row.returnsNum*100/row.deliveryNum+"%";
							}else{
								return "0%";
							}
					}}
		   		]]
		 
	});
	
	$('#dg3').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -$("#tb3").height() -4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/vendorC3/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		toolbar:'#tb3',				
		columns:[[										
		        	{field:'month',title:'年月',width:10,align:'center'},
		        	{field:'vendorName',title:'供应商',width:10,align:'center'},
		        	{field:'segmentStr',title:'医院数',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.segmentStr){
								return row.segmentStr;
							}else{
								return 0;
							}
					}},
		        	{field:'contractPurchaseSpecCount',title:'品种数',width:10,align:'center'},
		        	{field:'purchaseNum',title:'交易数量',width:10,align:'center'},
		        	{field:'purchaseSum',title:'交易金额',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.purchaseSum){
								return common.fmoney(row.purchaseSum);
							}
					}}
		   		]]
		 
	});
	
});
//搜索
function dosearch(index){
	var month = $("#month"+index).datebox('getValue');
	var vendorName = $('#vendor'+index).combogrid('getText');
	var data = {
		"query['t#month_S_EQ']":month,
		"query['t#vendorName_S_LK']":vendorName,
	};
	$('#dg'+index).datagrid('load',data);
}

function initSearchBar(index){
	easyuiMonth($("#month"+index));
	
	$('#vendor'+index).combogrid({
		idField:'code',    
		textField:'fullName', 
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/company/page.htmlx",
	    queryParams:{
	    	"query['t#isDisabled_I_EQ']":0,
	    	"query['t#isVendor_I_EQ']":1
		},
		pagination : true,
		pageSize : 10,
		pageNumber : 1,
		width:200,
		panelWidth:300,
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
				$('#vendor'+index).combogrid('grid').datagrid("reload",{
									"query['t#fullName_S_LK']" : q
								});
				$('#vendor'+index).combogrid("setValue", q);
			}

		},
		onSelect: function(rowIndex, rowData){
			//$("#hospitalName").val(rowData.fullName);
		}
	}).combobox("initClear"); 
	$('#vendor'+index).combogrid('grid').datagrid('getPager').pagination({
		showPageList : false,
		showRefresh : false,
		displayMsg : "共{total}记录"
	});
}

//图表窗口
function openChart(index,title) {
	var month = $("#month"+index).datebox('getValue');
	var vendorCode = $('#vendor'+index).combogrid('getValue');
	if(month == "" && vendorCode == ""){
		showMsg("请选择【年月】或者【供应商】");
		return;
	}
	var chartName = "chart1";
	if(month != ""){
		chartName = "chart2";
	}
	top.$.modalDialog({
		title : title,
		width : 1024,
		height : 600,
		href : " <c:out value='${pageContext.request.contextPath }'/>/count/vendorC"+index+"/"+chartName+".htmlx",
		queryParams:{
			"month": month,
			"vendorCode": vendorCode
		},
		buttons : [ {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				top.$.modalDialog.handler.dialog('destroy');
				top.$.modalDialog.handler = undefined;
			}
		}]
	});
}

//=============ajax===============
	

</script>