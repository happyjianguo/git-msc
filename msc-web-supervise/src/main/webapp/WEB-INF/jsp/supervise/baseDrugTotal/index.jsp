<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>
<body class="easyui-layout" >

<div data-options="region:'center',title:''" >
	<div id="toolbar" class="search-bar" >
		<a href="#" id="search" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="openSearch()">筛选</a>
		<shiro:hasPermission name="supervise:baseDrugTotal:export">
			<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true" onclick="exportexcel()">导出</a>
		</shiro:hasPermission>
	</div>
	<table id="dg"></table>
</div>

</body>
</html>

<script>

//初始化
$(function(){
	
	window.zoneName = "<c:out value='${zoneName}'/>";
	window.queryType = "<c:out value='${queryType}'/>";
	window.jsonStr = decodeURIComponent("<c:out value='${jsonStr}'/>");
	if (jsonStr.length == 0) {
		jsonStr = "{}";
        $("#search").show();
	}
	if(<c:out value="${orgType==1}"/>&&window.queryType==2){
        $("#search").show();
	}
	window.queryParams = eval("("+jsonStr+")");
	if (jsonStr.length > 2) {
		//查询条件不为空，说明不是首次进入
		window.queryParams["queryType"] = "<c:out value='${queryType}'/>";
	}
	window.nextQueryType = "<c:out value='${queryType+1}'/>";
	//datagrid
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-2,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugTotal/page.htmlx",
		queryParams:window.queryParams,
		pageSize:20,
		pageNumber:1,
		columns:[getColumns(window.queryParams["queryType"])],
		toolbar: "#toolbar",
		onDblClickRow: function(index,row){
        	openChild(row);
		},
		onLoadSuccess:function(){
			$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});
	
});
//弹窗增加
function openSearch() {
	top.$.modalDialog({
		title : "查询",
		width : 600,
		height : 360,
		href : " <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugTotal/search.htmlx",
		onLoad:function(){
			
		},
		buttons : [ {
			text : '查询',
			iconCls : 'icon-ok',
			handler : function() {
				window.nextQueryType = (window.jsonStr.length <= 2?1:window.queryParams["queryType"]);
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				try {
					var provinceCode = f.find("#combox1").combobox("getValue");
					
					var cityCode = f.find("#combox2").combobox("getValue")
					var countyCode = f.find("#combox3").combobox("getValue");
					if (countyCode.length != 0) {
						window.nextQueryType = 2;
					}	
					var hospitalCode = f.find("#hospitalCode").combobox("getValue");
					if (hospitalCode.length != 0) {
						window.nextQueryType = 3;
					}
				} catch(e){}
				window.queryParams={
					"query['c#hospitalCode_S_EQ']" : hospitalCode,
					"query['c#orgLevel_S_EQ']" : f.find("#orgLevel").combobox("getValue"),
					"query['c#provinceCode_S_EQ']" : provinceCode,
					"query['c#cityCode_S_EQ']" : cityCode,
					"query['c#countyCode_S_EQ']" : countyCode,
					"query['baseDrugType_L_EQ']": f.find("#baseDrugType").combobox("getValue"),
					queryType:window.nextQueryType-1
				};
				$('#dg').datagrid({
					columns:[getColumns(window.nextQueryType-1)],
					queryParams:window.queryParams
				});
				
				top.$.modalDialog.handler.dialog('destroy');
				top.$.modalDialog.handler = undefined;
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
function openChild(row) {
	var query = {};
	for (var k in window.queryParams) {
		query[k]=window.queryParams[k];
	}
	var title = "";
	query["queryType"] = window.nextQueryType;
	if(window.nextQueryType == 1) {
		query["query['c#countyCode_S_EQ']"] = row.COUNTYCODE;
		var url = "/supervise/baseDrugTotal/index.htmlx?queryType="+window.nextQueryType+"&jsonStr=" + encodeURIComponent(JSON.stringify(query));
		title = "基药品规数(区域)";
	}else if(window.nextQueryType == 2) {
		query["query['c#countyCode_S_EQ']"] = row.COUNTYCODE;
		var url = "/supervise/baseDrugTotal/index.htmlx?queryType="+window.nextQueryType+"&jsonStr=" + encodeURIComponent(JSON.stringify(query));
		title = "基药品规数(医院)";
	}else {
		query["query['c#countyCode_S_EQ']"] = row.COUNTYCODE;
		query["query['t#hospitalCode_S_EQ']"]=row.HOSPITALCODE;
		var url = "/supervise/medicineHospital.htmlx?queryType="+window.nextQueryType+"&jsonStr=" + encodeURIComponent(JSON.stringify(query));
		title = "基药明细(医院)";
	}
    top.addTab(title, url,null,true);
}
function getColumns(queryType) {
	var columns = [
        	{field:'NUM',title:'药品总数',width:200,align:'center'},
        	{field:'BASEDRUGNUM',title:'基药品规数',width:200,align:'center'},
        	{field:'JYZB',title:'基药品规占比(%)',width:200,align:'center'}
	   ];

	var first = {field:'NAME',title:'区域',width:200,align:'center',formatter: function(value,row,index){
		return window.zoneName;
	}};
	if (queryType == 1) {
		first = {field:'COUNTYNAME',title:'区域',width:200,align:'center'};
	}else if(queryType == 2){
		first = {field:'HOSPITALNAME',title:'医院',width:200,align:'center'};
	}
	columns.splice(0,0,first);
	return columns;
}

//导出
function exportexcel(){
    var nextQueryType = (window.jsonStr.length <= 2?0:window.queryParams["queryType"]);
    var url = " <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugTotal/export.htmlx?";
    for (var k in window.queryParams) {
        url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(window.queryParams[k]);
    }
    window.open(url);
}
</script>