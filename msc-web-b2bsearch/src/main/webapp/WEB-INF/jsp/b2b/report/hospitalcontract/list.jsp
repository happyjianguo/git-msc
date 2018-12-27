<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
<div class="search-bar">
		合同生效日期：
		<input class="easyui-datebox" style="width:100px" id="startDate"/> -
		<input class="easyui-datebox" style="width:100px" id="toDate"/>
		<span class="datagrid-btn-separator split-line" ></span>
		区：
	 	<input style="width:100px" id="regionCode"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
	 	医院：
	 	<input style="width:100px" id="hospital"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
	 	通用名：
	 	<input id="genaricname" class="easyui-textbox" />
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch()">查询</a>
	</div>
	<div id="tt">
		<div title="合同签订情况" class="my-tabs">
			<table id="dg" class="single-dg"></table>
		</div>
		<div title="合同详情" class="my-tabs">
			<table id="dg1" class="single-dg"></table>
		</div>
		<div title="合同执行情况" class="my-tabs">
			<table id="dg2" class="single-dg"></table>
		</div>
	</div>
</body>
</html>

<script>
var detailRow = '';
//搜索
var startDate="";
var endDate="";
var hospitalCode="";
var regionCode = "";
function dosearch(){
	var startDate = $("#startDate").combo('getValue');
	var endDate = $("#toDate").combo('getValue');
	if(startDate > endDate){
		showErr("起始日期不能大于结束日期");
		return ;
	}
	regionCode = $('#regionCode').combobox('getValue');
	hospitalCode = $('#hospital').combogrid('getValue');
	var genaricname = $('#genaricname').textbox('getValue');
	var data = {
		"startDate": startDate,
		"endDate": endDate,
		"query['h#regionCode_L_EQ']":regionCode,
		"query['t#hospitalCode_S_EQ']":hospitalCode,
		"query['p#GENERICNAME_S_LK']":genaricname
	};
	$('#dg').datagrid('load',data);
}

//初始化
$(function(){
	$('#tt').tabs({
		plain:true,
		justified:true
	});
	
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
	
	$('#regionCode').combobox({
	    valueField:'id',    
	    textField:'name', 
	    url: " <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx",
	    editable:false,
		queryParams:{
			parentId:"<c:out value='${regionCodePId}'/>"
		}
	}).combobox("initClear"); 
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		showFooter: true,
		rownumbers:true,
		border:true,
		height : $(this).height() - $(".search-bar").height() -39,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitalContract/page.htmlx",
		pageSize:50,
		pageNumber:1,
		columns:[[			
		        	{field:'REGIONCODE',title:'区',width:10,align:'center'},
		        	{field:'HOSPITALNAME',title:'医院',width:10,align:'center'},
		        	{field:'CONTRACTNUM',title:'签订合同数',width:10,align:'center'},
		        	{field:'PRODUCTNUM',title:'药品品种数',width:10,align:'center'},
		        	{field:'PRODUCTORDERNUM',title:'已采购药品品种数',width:10,align:'center'},
		        	{field:'CONTRACTAMT',title:'合同总金额',width:10,align:'center'},
		        	{field:'PURCHASEAMT',title:'已采购总金额',width:10,align:'center'}
		   		]],
		
		onDblClickRow:function(index,row) {
			$("#tt").tabs("select",1);
        	searchDefectsList(row);
		}
	});
	
	//datagrid
	$('#dg1').datagrid({
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() - $(".search-bar").height() -39,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitalContract/mxpage.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'contractCode',title:'合同编号',width:150,align:'center',
				formatter: function(value,row,index){
					if (row.contract){
						return row.contract.code;
					}
				}},
			{field:'effectiveDate',title:'生效日期',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.contract){
						return $.format.date(row.contract.effectiveDate,"yyyy-MM-dd");
					}
				}},
        	{field:'productCode',title:'药品编码',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.code;
					}
				}},
        	{field:'genericName',title:'药品名称',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.genericName;
					}
				}},
        	{field:'dosageFormName',title:'剂型',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.dosageFormName;
					}
				}},
        	{field:'model',title:'规格',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.model;
					}
				}},
        	{field:'packDesc',title:'包装规格',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.packDesc;
					}
				}},
			{field:'producerName',title:'生产厂家',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.producerName;
					}
				}},
        	{field:'contractNum',title:'合同数量',width:100,align:'center'},
        	{field:'lefNum',title:'合同剩余数量',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.contractNum - row.purchasePlanNum;
					}
				}},
        	{field:'purchaseNum',title:'采购数量',width:100,align:'center'},
        	{field:'deliveryNum',title:'配送数量',width:100,align:'center'},
/*         	{field:'DOSAGEFORMNAME',title:'入库数量',width:10,align:'center'}, */
        	{field:'returnsNum',title:'退货数量',width:100,align:'center'},
        	{field:'vendorName',title:'供应商',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.contract){
						return row.contract.vendorName;
					}
				}},
        	{field:'gpoName',title:'GPO',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.contract){
						return row.contract.gpoName;
					}
				}},
/*         	{field:'NUM',title:'配送商',width:10,align:'center'}, */
        	{field:'endValidDate',title:'合同限期',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.contract){
						return row.contract.endValidDate;
					}
				}},
        	{field:'price',title:'价格',width:100,align:'center',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}},
        	{field:'standardCode',title:'药品本位码',width:120,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.standardCode;
					}
				}},
        	{field:'code',title:'合同明细编号',width:200,align:'center'}
        	
   		]],
   		onDblClickRow:function(index,row) {
			$("#tt").tabs("select",2);
        	searchDefectsList1(row);
		}
	});
	
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() - $(".search-bar").height() -39,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitalContract/page2.htmlx",
		pageSize:20,
		pageNumber:1,
		columns:[[			
		        	{field:'contractDetailCode',title:'合同明细编号',width:20,align:'center'},
		        	{field:'productCode',title:'药品编码',width:10,align:'center'},
		        	{field:'productName',title:'药品名称',width:10,align:'center'},
		        	{field:'dosageFormName',title:'剂型',width:10,align:'center'},
		        	{field:'model',title:'规格',width:10,align:'center'},
		        	{field:'producerName',title:'生产企业',width:10,align:'center'},/* 
		        	{field:'code',title:'订单编号',width:10,align:'center'}, */
		        	{field:'code',title:'订单明细编号',width:20,align:'center'},
		        	{field:'goodsNum',title:'订单数量',width:10,align:'center'}
		   		]],
		
		onDblClickRow:function(index,row) {
			$("#tt").tabs("select",1);
        	searchDefectsList(row);
		}
	});
});


function searchDefectsList(row){
	detailRow = row;
	var startDate = $("#startDate").combo('getValue');
	var endDate = $("#toDate").combo('getValue');
	if(startDate > endDate){
		showErr("起始日期不能大于结束日期");
		return ;
	}	
	var genaricname = $('#genaricname').textbox('getValue');
	var data = {
		"query['t#contract.effectiveDate_D_GT']":startDate,
		"query['t#contract.effectiveDate_D_LT']":endDate,
		"query['t#contract.hospitalCode_S_EQ']":row.HOSPITALCODE,
		"query['t#product.genericName_S_LK']":genaricname
	};
	$('#dg1').datagrid('load',data);
}

function searchDefectsList1(row){
	$('#dg2').datagrid('load',{
		"query[t#contractDetailCode_S_EQ]":row.code,
	});
}

//=============ajax===============


</script>