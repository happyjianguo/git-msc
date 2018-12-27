<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
<div class="search-bar">
		报量日期：
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
		<div title="报量医院" class="my-tabs">
			<table id="dg" class="single-dg"></table>
		</div>
		<div title="报量明细" class="my-tabs">
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
		"query['hp#hospitalCode_S_EQ']":hospitalCode,
		"query['pd#GENERICNAME_S_LK']":genaricname
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
		showFooter: true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() - $(".search-bar").height() -39,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productplan/page.htmlx",
		pageSize:50,
		pageNumber:1,
		columns:[[			
		        	{field:'REGIONCODE',title:'区',width:10,align:'center'},
		        	{field:'HOSPITALNAME',title:'医院',width:10,align:'center'},
		        	{field:'COUNT',title:'报量品种数',width:10,align:'center'}
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/productplan/mxpage.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'HOSPITALNAME',title:'医院',width:10,align:'center'},
        	{field:'CODE',title:'项目编码',width:10,align:'center'},
        	{field:'NAME',title:'项目名称',width:10,align:'center'},
        	{field:'STARTDATE',title:'计划开始时间',width:10,align:'center'},
        	{field:'ENDDATE',title:'计划结束时间',width:10,align:'center'},
        	{field:'GENERICNAME',title:'通用名',width:10,align:'center'},
        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
        	{field:'MODEL',title:'规格',width:10,align:'center'},
        	{field:'QUALITYLEVEL',title:'质量层次',width:10,align:'center'},
        	{field:'PRODUCERNAMES',title:'厂家',width:10,align:'center'},
        	{field:'NUM',title:'报量数量',width:10,align:'center'}
        	
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
	var genaricname = $('#genaricname').textbox('getValue');
	var data = {
		"startDate": startDate,
		"endDate": endDate,
		"query['h#regionCode_L_EQ']":regionCode,
		"query['hp#hospitalName_S_EQ']":row.HOSPITALNAME,
		"query['pd#GENERICNAME_S_LK']":genaricname
	};
	$('#dg1').datagrid('load',data);
}


//=============ajax===============



</script>