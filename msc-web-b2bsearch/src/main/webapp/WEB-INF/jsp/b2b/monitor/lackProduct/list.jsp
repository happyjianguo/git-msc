<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js//My97DatePicker/WdatePicker.js"></script>

<html>
<body class="easyui-layout" >
	<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:100%;">
		 <div id="tb" class="search-bar" >
			上报年月: 
			<input type="text" id="startMonth" name="startMonth" class="Wdate" onFocus="WdatePicker({isShowClear:false,lang:'zh-cn',qsEnabled:false,autoShowQS:false,dateFmt:'yyyy-MM'})"/>
	        ~ 
	        <input type="text" id="toMonth" name="toMonth" class="Wdate" onFocus="WdatePicker({isShowClear:false,qsEnabled:false,autoShowQS:false,lang:'zh-cn',dateFmt:'yyyy-MM'})"/>	
	       	医院：
	       	<input id="hospitalCode" name="hospitalCode" />
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
	         <span class="datagrid-btn-separator split-line" ></span>
<shiro:hasPermission name="b2b:monitor:lackProduct:exportExcel">
	  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true"  onclick="doexport()">导出Excel</a>
</shiro:hasPermission>
	        <span class="datagrid-btn-separator split-line" ></span>
	  		<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true"  onclick="filterFunc()">数据过滤</a>
	    </div>
		<div id="tt">
			<div title="短缺药品" class="my-tabs">
				<table id="dg" class="single-dg"></table>
			</div>
			<div title="短缺药品明细" class="my-tabs">
				<table id="dg1" class="single-dg"></table>
			</div>
		</div>
	</div>
</body>
</html>

<script>
function filterFunc(){
	$('#dg').datagrid({remoteFilter: true});
	$('#dg').datagrid('enableFilter', [{
        field:'MONTH',
        type:'text',
        op:'LK',
		fieldType:'a#S'
    },{
        field:'CODE',
        type:'text',
        op:'LK',
		fieldType:'b#S'
    },{
        field:'NAME',
        type:'text',
        op:'LK',
		fieldType:'b#S'
    },{
        field:'DOSAGEFORMNAME',
        type:'text',
        op:'LK',
		fieldType:'b#S'
    },{
        field:'MODEL',
        type:'text',
        op:'LK',
		fieldType:'b#S'
    },{
        field:'PACKDESC',
        type:'text',
        op:'LK',
		fieldType:'b#S'
    },{
        field:'PRODUCERNAME',
        type:'text',
    	isDisabled:1
    },{
        field:'NUM',
        type:'text',
    	isDisabled:1
    }]);
	$('#dg').datagrid('reload');
}
//搜索
function dosearch(){
	var startMonth = "";
	var toMonth = "";
	if(document.getElementById('startMonth').value != ""){
	  	startMonth = document.getElementById('startMonth').value;		
	}
	if(document.getElementById('toMonth').value != ""){
		toMonth = document.getElementById('toMonth').value;
	}
	var hospitalCode = $('#hospitalCode').textbox('getValue');

	var data = {
		"query['a#month_S_GE']": startMonth,
		"query['a#month_S_LE']": toMonth,
		"query['a#hospitalCode_S_EQ']": hospitalCode
	};

	$('#dg').datagrid('load',data);
}
//导出excel
function doexport(){
	var startMonth = "";
	var toMonth = "";
	if(document.getElementById('startMonth').value != ""){
	  	startMonth = document.getElementById('startMonth').value;		
	}
	if(document.getElementById('toMonth').value != ""){
		toMonth = document.getElementById('toMonth').value;
	}
	var hospitalCode = $('#hospitalCode').textbox('getValue');
	
	var data = {
			"query['a#month_S_GE']": startMonth,
			"query['a#month_S_LE']": toMonth,
			"query['a#hospitalCode_S_EQ']": hospitalCode
		};
	var url = "<c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/lackProduct/exportExcel.htmlx?";
	for (var k in data) {
		url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(data[k]);
	}
	$.messager.confirm('确认信息', '确认下载?', function(r){
		if (r){
			window.open(url);
		}
	});
}
//初始化
$(function(){	
	$('#tt').tabs({
		plain:true,
		justified:true
	});
	$('#hospitalCode').combogrid({
		idField:'code',    
		textField:'fullName',
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/hospital/page.htmlx",
		queryParams:{
	    	"query['t#isDisabled_I_EQ']": 0
		},
		pagination:true,
		pageSize:10,
		pageNumber:1,
		delay:800,
	    columns: [[
	        {field:'code',title:'医院编码',width:100},
	        {field:'fullName',title:'医院名称',width:400}
	    ]],
	    panelWidth:510,
	    panelHeight:310,
	    onClickRow: function(index,row){  
	    	$('#hospitalName').textbox("setValue", row.fullName);
		},
		keyHandler: {
            query: function(q) {
                //动态搜索
                $('#hospitalCode').combogrid('grid').datagrid("reload",{"query['t#fullName_S_LK']":q});
                $('#hospitalCode').combogrid("setValue", q);
            }

        }
	});	
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() -75,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/lackProduct/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'CODE',title:'药品编码',width:10,align:'center'},
		        	{field:'NAME',title:'药品名称',width:10,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
		        	{field:'MODEL',title:'规格',width:10,align:'center'},
		        	{field:'PACKDESC',title:'包装规格',width:10,align:'center'},
		        	{field:'PRODUCERNAME',title:'生产厂家',width:13,align:'center'},
		        	{field:'ISGPOPURCHASE',title:'是否GPO药品',width:10,align:'center',
		        		formatter: function(value,row,index){
		        			if (row.ISGPOPURCHASE ==1){
		    					return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
		    				}
		    		}},	
		        	{field:'NUM',title:'上报次数',width:10,align:'center'}
		   		]],
		toolbar: [],
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/lackProduct/mxpage.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
			{field:'MONTH',title:'年月',width:10,align:'center'},
			{field:'HOSPITALNAME',title:'医院名称',width:10,align:'center'},
        	{field:'REASON',title:'短缺原因',width:30,align:'center'}
        	
   		]]
	});
	
});

function searchDefectsList(row){
	detailRow = row;
	var data = {
		"query['b#CODE_S_LK']":row.CODE
	};
	$('#dg1').datagrid('load',data);
}

</script>