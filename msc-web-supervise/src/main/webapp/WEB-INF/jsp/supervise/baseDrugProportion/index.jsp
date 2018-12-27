<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<style>
#toolbar{
width:100%;
}
#time{
margin-right:30px;float:right;
}
#search{
display:none;
}
</style>
<html>
<body class="easyui-layout" >

<div data-options="region:'center',title:''" >
<div id="toolbar" class="search-bar" >
				<a href="#" id="search" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="openSearch()">筛选</a>
				<shiro:hasPermission name="supervise:baseDrugProportion:export">
        			<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true" onclick="exportexcel()">导出</a>
				</shiro:hasPermission>
        		<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-chart_bar',plain:true" onclick="showChart()">图表</a>
		        <span id='time'></span>
	    	</div>
	    <table id="dg"></table>
</div>

</body>
</html>

<script>

function filterFunc(){
	$('#dg').datagrid({remoteFilter: true});
	$('#dg').datagrid('enableFilter', [{
		 field:'isDisabled',
		 type:'text',
		 isDisabled:1
	}]
	);
	
}
//初始化
$(function(){
	
	window.zoneName = "<c:out value='${zoneName}'/>";
	window.queryType = "<c:out value='${queryType}'/>";
	window.jsonStr = decodeURIComponent("<c:out value='${jsonStr}'/>");
	if (jsonStr.length == 0) {
		jsonStr = "{}";
		$("#search").show();
	}
	if(<c:out value="${orgType==1}"/>&&window.queryType==4){
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugProportion/page.htmlx",
		queryParams:window.queryParams,
		pageSize:20,
		pageNumber:1,
		columns:[getColumns(window.queryParams["queryType"])],
		toolbar:"#toolbar",
		onDblClickRow: function(index,row){
        	openChild(row);
		},
		onLoadSuccess:function(data){
			var array = data.rows;
			var begindata = array[0]['begindata'];
			var enddata = array[0]['enddata'];
			var timetile="";
			if(begindata==enddata||enddata==""){
				timetile=begindata;
			}else{
				timetile=begindata+" -- "+enddata;
			}
			$("#time").html("查询时间  : "+timetile);
			$('#dg').datagrid('doCellTip',{delay:500}); 
			$(".tip").tooltip({  
                onShow: function(){  
                    $(this).tooltip('tip').css({   
                        width:'170',          
                        boxShadow: '2px 2px 4px #292929'                          
                    });  
                }  
            });
		}
	});
	
});
//弹窗增加
function openSearch() {
	top.$.modalDialog({
		title : "查询",
		width : 600,
		height : 360,
		href : " <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugProportion/search.htmlx",
		onLoad:function(){
			
		},
		buttons : [ {
			text : '查询',
			iconCls : 'icon-ok',
			handler : function() {
				window.nextQueryType = (window.jsonStr.length <= 2?1:window.queryParams["queryType"]);
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				var monthBegin = f.find("#beginDate_yy").combobox("getValue")+"-"+f.find("#beginDate_mm").combobox("getValue");
				var monthEnd = f.find("#endDate_yy").combobox("getValue")+"-"+f.find("#endDate_mm").combobox("getValue");
				try {
					var provinceCode = f.find("#combox1").combobox("getValue");
					if (provinceCode.length != 0) {
						window.nextQueryType = 2;
					}
					var cityCode = f.find("#combox2").combobox("getValue")
					if (cityCode.length != 0) {
						window.nextQueryType = 3;
					}
					var countyCode = f.find("#combox3").combobox("getValue");
					if (countyCode.length != 0) {
						window.nextQueryType = 4;
					}
				} catch(e){}
				if (f.find("#hospitalCode").combobox("getValue").length > 0) {
					window.nextQueryType = 5;
				} 
				if(<c:out value="${orgType==1}"/>){
					window.nextQueryType +=1;
				}
				window.queryParams={
					"query['c#hospitalCode_S_EQ']" : f.find("#hospitalCode").combobox("getValue"),
					"query['c#orgLevel_S_EQ']" : f.find("#orgLevel").combobox("getValue"),
					"query['type_L_EQ']" : f.find("#sumType").combobox("getValue"),
					"query['month_S_GE']":monthBegin,
					"query['month_S_LE']":monthEnd,
					"query['c#provinceCode_S_EQ']" : provinceCode,
					"query['c#cityCode_S_EQ']" : cityCode,
					"query['c#countyCode_S_EQ']" : countyCode,
					queryType:window.nextQueryType-1
				};
				var baseDrugType = f.find("#baseDrugType").combotree("getValue");
				
				var baseSearchKey = "query['baseDrugType_L_EQ']";
				if (baseDrugType+"" == "-1") {
					baseSearchKey = "query['baseDrugType_NULL_IS']";
				} else if (baseDrugType+"" == "2") {
					baseSearchKey = "query['baseDrugType_NULL_NOT']";
				}
				window.queryParams[baseSearchKey] = baseDrugType;
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
	var url = "/supervise/baseDrugProportion/index.htmlx?queryType="+window.nextQueryType+"&jsonStr=";
	if (window.nextQueryType == 1) {
		url += encodeURIComponent(JSON.stringify(query));
		title = "基药金额比例分析(省)";
	} else if (window.nextQueryType == 2) {
		query["query['c#provinceCode_S_EQ']"] = row.CODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "基药金额比例分析(市)";
	} else if (window.nextQueryType == 3) {
		query["query['c#cityCode_S_EQ']"] = row.CODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "基药金额比例分析(区/县)";
	} else if (window.nextQueryType == 4) {
		query["query['c#countyCode_S_EQ']"] = row.CODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "基药金额比例分析(医院)";
	} else if (window.nextQueryType == 5) {
		query["query['c#provinceCode_S_EQ']"] = undefined;
		query["query['c#cityCode_S_EQ']"] = undefined;
		query["query['c#countyCode_S_EQ']"] = undefined;
		query["query['c#hospitalCode_S_EQ']"] = row.CODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "基药金额比例分析(科室)";
	} else if (window.nextQueryType == 6) {
		query["query['c#provinceCode_S_EQ']"] = undefined;
		query["query['c#cityCode_S_EQ']"] = undefined;
		query["query['c#countyCode_S_EQ']"] = undefined;
		query["query['c#hospitalCode_S_EQ']"] = row.HOSPITALCODE;
		query["query['departCode_S_EQ']"] = row.CODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "基药金额比例分析(医生)";
	} else {
		query = {};
		query["query['a#hospitalCode_S_EQ']"] = window.queryParams["query['c#hospitalCode_S_EQ']"];
		query["query['departCode_S_EQ']"] = window.queryParams["query['departCode_S_EQ']"];
		query["query['doctorCode_S_EQ']"] = row.CODE;
		query["sumType"] = window.queryParams["query['type_L_EQ']"];
		query["query['cdate_D_GE']"] = (window.queryParams["query['month_S_GE']"]?(window.queryParams["query['month_S_GE']"]+"-01"):"");
		query["query['cdate_D_LE']"] = (window.queryParams["query['month_S_LE']"]?(window.queryParams["query['month_S_LE']"]+"-31"):"");
		url = "/supervise/clinicRecipe/index.htmlx?queryType="+window.nextQueryType+"&jsonStr="+encodeURIComponent(JSON.stringify(query));
		title = "基药金额比例分析(住院/门诊处方)";
	}
    top.addTab(title, url,null,true);
}
//导出
function exportexcel(){
	var nextQueryType = (window.jsonStr.length <= 2?0:window.queryParams["queryType"]);
	var orderType = window.queryParams["orderType"]||0;
	var url = " <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugProportion/export.htmlx?";
	for (var k in window.queryParams) {
		url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(window.queryParams[k]);
	}
	window.open(url);
}
function getColumns(queryType) {
	var columns = [
        	{field:'BASEDRUGSUM',title:'基药总费用',width:250,align:'center'},
        	{field:'SUM',title:'用药总费用',width:250,align:'center'},
        	{field:'JYZB',title:'<span title=" 基药总费用  / 用药总费用 " class="tip">基药占比(%)</span>',width:250,align:'center'}
	   ];

	var first = {field:'NAME',title:'区域',width:250,align:'center',formatter: function(value,row,index){
		return window.zoneName;
	}};
	var second =null;
	var third = null;
	if (queryType == 1) {
		first = {field:'NAME',title:'省',width:250,align:'center'};
	} else if (queryType == 2) {
		first = {field:'NAME',title:'市',width:250,align:'center'};
	} else if (queryType == 3) {
		first = {field:'NAME',title:'区/县',width:250,align:'center'};
	} else if (queryType == 4) {
		first = {field:'NAME',title:'医院',width:250,align:'center'};
	} else if (queryType == 5) {
		first = {field:'NAME',title:'科室',width:250,align:'center'};
		second = {field:'HOSPITALNAME',title:'医院',width:250,align:'center'};
	} else if (queryType == 6) {
		first = {field:'NAME',title:'医生',width:250,align:'center'};
		second = {field:'HOSPITALNAME',title:'医院',width:250,align:'center'};
		third = {field:'DEPARTNAME',title:'科室',width:250,align:'center'};
	}
	columns.splice(0,0,first);
	if(queryType==5){
		columns.splice(0,0,second);
	}else if(queryType==6){
		columns.splice(0,0,second);
		columns.splice(1,0,third);
	}	
	return columns;
}
function showChart() {
	var nextQueryType = (window.jsonStr.length <= 2?0:window.queryParams["queryType"]);
	top.$.modalDialog.data= [$('#dg').datagrid("getRows"), nextQueryType, window.queryParams["orderType"]];
	top.$.modalDialog({
		title : "查询",
		width : 800,
		height : 460,
		href : " <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugProportion/chart.htmlx"
	});
	
	function exportexcel(){
		
	}
}
</script>