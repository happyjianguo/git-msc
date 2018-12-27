<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>
<body class="easyui-layout" >

<div data-options="region:'center',title:''" >
	<div id="toolbar" class="search-bar" >
		<a href="#" id="search" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="openSearch()">筛选</a>
		<shiro:hasPermission name="supervise:drugCatalogDrugNum:export">
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
	window.queryParams = eval("("+jsonStr+")");
	if (jsonStr.length > 2) {
		//查询条件不为空，说明不是首次进入
		window.queryParams["queryType"] = "<c:out value='${queryType}'/>";
	}
	window.nextQueryType = "<c:out value='${queryType+1}'/>";
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-2,
		pagination:true,
		url:"<c:out value='${pageContext.request.contextPath }'/>/supervise/drugCatalogDrugNum/page.htmlx",
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
		href : "<c:out value='${pageContext.request.contextPath }'/>/supervise/drugCatalogDrugNum/search.htmlx",
		onLoad:function(){
			
		},
		buttons : [ {
			text : '查询',
			iconCls : 'icon-ok',
			handler : function() {
				//console.log('${zoneName}');
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
					var countyName = f.find("#combox3").combobox("getText");
					var hospitalName=f.find("#hospitalCode").combobox("getText");	
					if (hospitalName.length != 0) {
						window.nextQueryType = 3;
					}
				} catch(e){}
				window.queryParams={
					/* "query['c#hospitalCode_S_EQ']" : f.find("#hospitalCode").combobox("getValue"), */
					"query['c#hospitalCode_S_EQ']" : f.find("#hospitalCode").combobox("getValue"),
					"query['c#orgLevel_S_EQ']" : f.find("#orgLevel").combobox("getValue"),
					"query['provinceCode_S_EQ']" : provinceCode,
					"query['cityCode_S_EQ']" : cityCode,
					"query['c#countyCode_S_EQ']" : countyCode,
					queryType:window.nextQueryType-1,
					countyName:countyName,
					hospitalName:hospitalName
					
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
		var url = "/supervise/drugCatalogDrugNum/index.htmlx?queryType="+window.nextQueryType+"&jsonStr=" + encodeURIComponent(JSON.stringify(query));
		title = "药品目录品规数(区域)";
	}else if(window.nextQueryType == 2) {
		query["query['c#countyCode_S_EQ']"] = row.COUNTYCODE;
		var url = "/supervise/drugCatalogDrugNum/index.htmlx?queryType="+window.nextQueryType+"&jsonStr=" + encodeURIComponent(JSON.stringify(query));
		title = "药品目录品规数(医院)";
	} else {
		query["query['c#countyCode_S_EQ']"] = row.COUNTYCODE;
		query["query['hospitalCode_S_EQ']"]=row.HOSPITALCODE;
		 var url = "/supervise/medicineHospital.htmlx?queryType="+window.nextQueryType+"&jsonStr=" + encodeURIComponent(JSON.stringify(query)); 
		title = "药品明细(医院)";
	}
    top.addTab(title, url,null,true);
}
function getColumns(queryType) {
	console.log(queryType);
	
	var columns = [
        	{field:'NUM',title:'药品目录品规数',width:100,align:'center'},
        	{field:'CHINADRUGNUM',title:'中成药品规数',width:150,align:'center'},
        	{field:'WESTDRUGNUM',title:'西药品规数',width:100,align:'center'}
	   ];

	var first = {field:'NAME',title:'区域',width:180,align:'center',formatter: function(value,row,index){
	 	
	 	
		 if(""!=row.name&&row.name!=undefined){ 
			 window.zoneName=row.name;
	 	 } 
		return window.zoneName; 
	 	  
		
	}};
	if (queryType == 1) {
		first = {field:'COUNTYNAME',title:'区域',width:180,align:'center'};
	}else if(queryType == 2){
		first = {field:'HOSPITALNAME',title:'医院',width:180,align:'center'};
	}
	columns.splice(0,0,first);
	return columns;
}
//导出
function exportexcel(){
    var nextQueryType = (window.jsonStr.length <= 2?0:window.queryParams["queryType"]);
    var url = "<c:out value='${pageContext.request.contextPath }'/>/supervise/drugCatalogDrugNum/export.htmlx?";
    for (var k in window.queryParams) {
        url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(window.queryParams[k]);
    }
    window.open(url);
}
</script>