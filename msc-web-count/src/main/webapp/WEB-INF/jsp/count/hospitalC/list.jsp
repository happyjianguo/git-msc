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
	 	医院：
	 	<input style="width:100px" id="hospital1"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch(1)">查询</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-chart_bar" onclick="openChart(1,'合同执行率分析')">图表</a>
	</div>
	<div id='tb2' class="search-bar">
		年月：
		<input  style="width:100px" id="month2"/>
		<span class="datagrid-btn-separator split-line" ></span>
	 	医院：
	 	<input style="width:100px" id="hospital2"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch(2)">查询</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-chart_bar" onclick="openChart(2,'采购品种占比分析')">图表</a>
	</div>
	<div id='tb3' class="search-bar">
		年月：
		<input  style="width:100px" id="month3"/>
		<span class="datagrid-btn-separator split-line" ></span>
	 	医院：
	 	<input style="width:100px" id="hospital3"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch(3)">查询</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-chart_bar" onclick="openChart(3,'采购频率分析')">图表</a>
	</div>
	<div id='tb4' class="search-bar">
		年月：
		<input  style="width:100px" id="month4"/>
		<span class="datagrid-btn-separator split-line" ></span>
	 	医院：
	 	<input style="width:100px" id="hospital4"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch(4)">查询</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-chart_bar" onclick="openChart(4,'采购金额分析')">图表</a>
	</div>
	<div id='tb5' class="search-bar">
		年月：
		<input  style="width:100px" id="month5"/>
		<span class="datagrid-btn-separator split-line" ></span>
	 	医院：
	 	<input style="width:100px" id="hospital5"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch(5)">查询</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-chart_bar" onclick="openChart(5,'验收完成率分析')">图表</a>
	</div>
	<div id='tb6' class="search-bar">
		年月：
		<input  style="width:100px" id="month6"/>
		<span class="datagrid-btn-separator split-line" ></span>
	 	医院：
	 	<input style="width:100px" id="hospital6"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch(6)">查询</a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-chart_bar" onclick="openChart(6,'库存周转率分析')">图表</a>
	</div>
	<div id="tt">
		<div title="合同执行率分析" class="my-tabs">
			<table id="dg1" class="single-dg"></table>
		</div>
		<div title="采购品种占比分析" class="my-tabs">
			<table id="dg2" class="single-dg"></table>
		</div>
		<div title="采购频率分析" class="my-tabs">
			<table id="dg3" class="single-dg"></table>
		</div>
		<div title="采购金额分析" class="my-tabs">
			<table id="dg4" class="single-dg"></table>
		</div>
		<div title="验收完成率分析" class="my-tabs">
			<table id="dg5" class="single-dg"></table>
		</div>
		<div title="库存周转率分析" class="my-tabs">
			<table id="dg6" class="single-dg"></table>
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
	initSearchBar(4);
	initSearchBar(5);
	initSearchBar(6);
	
	//datagrid
	$('#dg1').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -$("#tb1").height() -4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/hospitalC1/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		toolbar:'#tb1',
		columns:[[											
		        	{field:'month',title:'年月',width:10,align:'center'},
		        	{field:'hospitalName',title:'医院',width:10,align:'center'},
		        	{field:'contractCount',title:'合同个数',width:10,align:'center'},
		        	{field:'contractSpecCount',title:'合同采购品种',width:10,align:'center'},
		        	{field:'contractPurchaseSpecCount',title:'实际采购品种',width:10,align:'center'},
		        	{field:'contractSum',title:'合同采购金额',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.contractSum){
								return common.fmoney(row.contractSum);
							}
					}},
		        	{field:'contractPurchaseSum',title:'实际采购金额',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.contractPurchaseSum){
								return common.fmoney(row.contractPurchaseSum);
							}
					}}
		   		]]
		 
	});
	
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -$("#tb2").height() -4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/hospitalC2/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		toolbar:'#tb2',				
		columns:[[											
		        	{field:'month',title:'年月',width:10,align:'center'},
		        	{field:'hospitalName',title:'医院',width:10,align:'center'},
		        	{field:'purchaseSpecCount',title:'采购品规数',width:10,align:'center'},
		        	{field:'segmentBD',title:'总品规数',width:10,align:'center'},
		        	{field:'segmentStr',title:'品种占比',width:10,align:'center',
						formatter:function (value,row,index) {
		        	    if(row.segmentStr){
                            var num =parseFloat(row.segmentStr);
                            num = num.toFixed(2) + "%";
                            return num;
							}
                        }
					},
		        	{field:'segementMap.b01',title:'批次一',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.segementMap){
								return row.segementMap.b01;
							}
					}},
		        	{field:'segementMap.b02',title:'批次二',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.segementMap.b02;
							}
					}},
					{field:'segementMap.b03',title:'批次三',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.segementMap.b03;
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/hospitalC3/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		toolbar:'#tb3',				
		columns:[[												
		        	{field:'month',title:'年月',width:10,align:'center'},
		        	{field:'hospitalName',title:'医院',width:10,align:'center'},
		        	{field:'purchaseTimes',title:'采购次数',width:10,align:'center'},
		        	{field:'segmentStr',title:'平均采购周期',width:10,align:'center',
						formatter:function (value,row,index) {
							if(row.segmentStr){
								var num = parseFloat(row.segmentStr).toFixed(2);
								return num;
							}
                        }
					},
		        	{field:'purchaseSum',title:'采购总金额	',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.segmentBD){
								return common.fmoney(row.purchaseSum);
							}
					}},
		        	{field:'segmentBD',title:'单次平均采购金额',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.segmentBD){
								return common.fmoney(row.segmentBD);
							}
					}}
		   		]]
		 
	});
	$('#dg4').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -$("#tb4").height() -4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/hospitalC2/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		toolbar:'#tb4',				
		columns:[[														
		        	{field:'month',title:'年月',width:10,align:'center'},
		        	{field:'hospitalName',title:'医院',width:10,align:'center'},
		        	{field:'purchaseSum',title:'采购总金额',width:10,align:'center'},
		        	{field:'insuranceDrugSum',title:'社保药采购金额',width:10,align:'center'},
		        	{field:'baseDrugSum',title:'基药采购金额',width:10,align:'center'},
		        	{field:'prescriptDrugSum',title:'处方药采购金额',width:10,align:'center'},
		        	{field:'segementMap.b01',title:'批次一',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.segementMap){
								return row.segementMap.b01;
							}
					}},
		        	{field:'segementMap.b02',title:'批次二',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.segementMap.b02;
							}
					}},
					{field:'segementMap.b03',title:'批次三',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.segementMap.b03;
							}
					}}
		   		]]
	});
	
	$('#dg5').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -$("#tb5").height() -4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/hospitalC5/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		toolbar:'#tb5',				
		columns:[[														
		        	{field:'month',title:'年月',width:10,align:'center'},
		        	{field:'hospitalName',title:'医院',width:10,align:'center'},
		        	{field:'deliveryNum',title:'配送数量',width:10,align:'center'},
		        	{field:'inOutBoundNum',title:'入库数量',width:10,align:'center'},
		        	{field:'segmentStr',title:'验收完成率',width:10,align:'center'}
		   		]]
		 
	});
	
	$('#dg6').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -$("#tb6").height() -4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/count/hospitalC6/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		toolbar:'#tb6',				
		columns:[[																
		        	{field:'month',title:'年月',width:10,align:'center'},
		        	{field:'hospitalName',title:'医院',width:10,align:'center'},
		        	{field:'purchaseSum',title:'采购总金额	',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.purchaseSum){
								return common.fmoney(row.purchaseSum);
							}
					}},
		        	{field:'beginStockSum',title:'期初库存金额',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.beginStockSum){
								return common.fmoney(row.beginStockSum);
							}else {
							    return "0";
							}
					}},
		        	{field:'endStockSum',title:'期末库存金额',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.endStockSum){
								return common.fmoney(row.endStockSum);
							}else {
                                return "0";
                            }
					}},
		        	{field:'num1',title:'平均库存金额',width:10,align:'center',
						formatter: function(value,row,index){
							return common.fmoney((row.beginStockSum+row.endStockSum)/2);
					}},
		        	{field:'num2',title:'库存周转率',width:10,align:'center',
						formatter: function(value,row,index){
							if((row.beginStockSum+row.endStockSum)/2 != 0)
								return (row.purchaseSum*2/(row.beginStockSum+row.endStockSum)).toFixed(2);
							return "0";
					}}
		   		]]
		 
	});
});
//搜索
function dosearch(index){
	var month = $("#month"+index).datebox('getValue');
	var hospitalName = $('#hospital'+index).combogrid('getText');
	var data = {
		"query['t#month_S_EQ']":month,
		"query['t#hospitalName_S_LK']":hospitalName,
	};
	$('#dg'+index).datagrid('load',data);
}

function initSearchBar(index){
	easyuiMonth($("#month"+index));
	
	$('#hospital'+index).combogrid({
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
				$('#hospital'+index).combogrid('grid').datagrid("reload",{
									"query['t#fullName_S_LK']" : q
								});
				$('#hospital'+index).combogrid("setValue", q);
			}

		},
		onSelect: function(rowIndex, rowData){
			$("#hospitalName").val(rowData.fullName);
		}
	}).combobox("initClear"); 
	$('#hospital'+index).combogrid('grid').datagrid('getPager').pagination({
		showPageList : false,
		showRefresh : false,
		displayMsg : "共{total}记录"
	});
}

//图表窗口
function openChart(index,title) {
	var month = $("#month"+index).datebox('getValue');
	var hospitalCode = $('#hospital'+index).combogrid('getValue');
	if(month == "" && hospitalCode == ""){
		showMsg("请选择【年月】或者【医院】");
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
		href : " <c:out value='${pageContext.request.contextPath }'/>/count/hospitalC"+index+"/"+chartName+".htmlx",
		queryParams:{
			"month": month,
			"hospitalCode": hospitalCode
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