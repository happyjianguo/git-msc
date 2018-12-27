<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
<div class="search-bar">
		采购日期：
		<input class="easyui-datebox" style="width:100px" id="startDate"/> -
		<input class="easyui-datebox" style="width:100px" id="toDate"/>
		<span class="datagrid-btn-separator split-line" ></span>
		区：
	 	<input style="width:100px" id="regionCode"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
	 	医院：
	 	<input style="width:100px" id="hospital"/>
	 	<span class="datagrid-btn-separator split-line" ></span>
	 	药品名称：
	 	<input id="genaricname" class="easyui-textbox" />
        <a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="dosearch()">查询</a>
	</div>
	<div id="tt">
		<div title="药品采购量" class="my-tabs"> 
			<table id="dg" class="single-dg"></table>
		</div>
		<div title="订单明细" class="my-tabs">
			<table id="dg1" class="single-dg"></table>
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
		"query['p#hospitalCode_S_EQ']":hospitalCode,
		"query['pd#productName_S_LK']":genaricname
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
		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/hospital/page.htmlx',
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
	    url: ' <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/lvltwo.htmlx',
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
		rownumbers:true,
		border:true,
		height : $(this).height() - $(".search-bar").height() -39,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productOrder/page.htmlx",
		pageSize:20,
		pageNumber:1,
		columns:[[	
		        	{field:'PRODUCTCODE',title:'药品编码',width:10,align:'center'},
		        	{field:'PRODUCTNAME',title:'药品名称',width:10,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
		        	{field:'MODEL',title:'规格',width:10,align:'center'},
		        	{field:'PACKDESC',title:'包装规格',width:10,align:'center'},
		        	{field:'PRODUCERNAME',title:'生产厂家',width:10,align:'center'},
		        	{field:'NUM',title:'采购量',width:10,align:'center'}
		   		]],
		
		onDblClickRow:function(index,row) {
			$("#tt").tabs("select",1);
        	searchDefectsList(row);
		}
	});
	
	//datagrid
	$('#dg1').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() - $(".search-bar").height() -39,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productOrder/mxpage.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'HOSPITALNAME',title:'医院',width:10,align:'center'},
        	{field:'CODE',title:'订单编号',width:10,align:'center'},
        	{field:'CODEDETAIL',title:'订单明细编号',width:10,align:'center'},
        	{field:'ORDERDATE',title:'订单日期',width:10,align:'center'},
        	{field:'GOODSNUM',title:'订单数量',width:10,align:'center'}
   		]]
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
	regionCode = $('#regionCode').combobox('getValue');
	hospitalCode = $('#hospital').combogrid('getValue');
	var genaricname = $('#genaricname').textbox('getValue');
	var data = {
		"startDate": startDate,
		"endDate": endDate,
		"query['pd#productCode_L_EQ']":row.PRODUCTCODE,
		"query['h#regionCode_L_EQ']":regionCode,
		"query['p#hospitalCode_S_EQ']":hospitalCode,
		"query['pd#productName_S_LK']":genaricname
	};
	$('#dg1').datagrid('load',data);
}

//=============ajax===============



</script>