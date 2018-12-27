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
		<shiro:hasPermission name="supervise:diseaseAnalysis:export">
   			<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true" onclick="exportexcel()">导出</a>
		</shiro:hasPermission>
   		<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-chart_bar',plain:true" onclick="showChart()">图表</a>
	<span id='time'></span></div>
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/diseaseAnalysis/page.htmlx",
		queryParams:window.queryParams,
		pageSize:20,
		pageNumber:1,
		columns:[getColumns(window.queryParams["queryType"])],
		toolbar: "#toolbar",
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
                        width:'150',          
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
		href : " <c:out value='${pageContext.request.contextPath }'/>/supervise/diseaseAnalysis/search.htmlx",
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
				var hospitalCode = f.find("#hospitalCode").combobox("getValue");
				try {
					var provinceCode = f.find("#combox1").combobox("getValue");
					var cityCode = f.find("#combox2").combobox("getValue")
					var countyCode = f.find("#combox3").combobox("getValue");
					if (hospitalCode.length > 0) {
						window.nextQueryType = 2;
					}
					if(<c:out value="${orgType==1}"/>){
						window.nextQueryType +=1;
					} 
				} catch(e){}
				window.queryParams={
					"query['c#hospitalCode_S_EQ']" : f.find("#hospitalCode").combobox("getValue"),
					"query['c#orgLevel_S_EQ']" : f.find("#orgLevel").combobox("getValue"),
					"query['a#diseaseCode_S_EQ']" : f.find("#diseaseCode").combobox("getValue"),
					"query['a#type_L_EQ']" : f.find("#sumType").combobox("getValue"),
					"query['a#month_S_GE']":monthBegin,
					"query['a#month_S_LE']":monthEnd,
					"query['c#provinceCode_S_EQ']" : provinceCode,
					"query['c#cityCode_S_EQ']" : cityCode,
					"query['c#countyCode_S_EQ']" : countyCode,
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
	var url = "/supervise/diseaseAnalysis/index.htmlx?queryType="+window.nextQueryType+"&jsonStr=";
	if (window.nextQueryType == 1) {
		query["query['a#DISEASECODE_S_EQ']"] = row.DISEASECODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "单病种分析（医院）";
	} else {
		query = {};
		query["query['c#hospitalCode_S_EQ']"] = row.HOSPITALCODE;
		query["query['a#diseaseCode_S_EQ']"] = row.DISEASECODE;
		query["query['a#isOperation_I_EQ']"] = row.ISOPERATION;
		query["query['a#type_L_EQ']"] = window.queryParams["query['a#type_L_EQ']"];
		query["query['a#month_S_GE']"] = (window.queryParams["query['a#month_S_GE']"]?(window.queryParams["query['a#month_S_GE']"]):"");
		query["query['a#month_S_LE']"] = (window.queryParams["query['a#month_S_LE']"]?(window.queryParams["query['a#month_S_LE']"]):"");
		url = "/supervise/diseaseAnalysis/product.htmlx?queryType="+window.nextQueryType+"&jsonStr="+encodeURIComponent(JSON.stringify(query));
		title = "单病种用药明细";
	}
    top.addTab(title, url,null,true);
}
function getColumns(queryType) {
	if (queryType == 1) {
		return [
				{field:'HOSPITALNAME',title:'医院',width:100,align:'center'},
	        	{field:'DISEASECODE',title:'疾病编码',width:75,align:'center'},
	        	{field:'DISEASENAME',title:'疾病名称',width:150,align:'center'},
	        	{field:'ISOPERATION',title:'是否手术',width:100,align:'center',formatter: function(value,row,index){
					return value==1?"是":"否";
				}},
	        	{field:'SUM',title:'药品费用',width:100,align:'center'},
	        	{field:'DRUGNUM',title:'品规数',width:100,align:'center'},
	        	{field:'TREATMENTTOTAL',title:'诊疗人次',width:100,align:'center'},
	        	{field:'TREATMENTRATE',title:'<span title="" class="tip">诊疗率</span>',width:100,align:'center'}
		   ];
	} else {

		return [
				{field:'ZONENAME',title:'区域',width:75,align:'center',formatter: function(value,row,index){
					return window.zoneName;
				}},
	        	{field:'DISEASECODE',title:'疾病编码',width:75,align:'center'},
	        	{field:'DISEASENAME',title:'疾病名称',width:150,align:'center'},
	        	{field:'ISOPERATION',title:'是否手术',width:100,align:'center',formatter: function(value,row,index){
					return value==1?"是":"否";
				}},
	        	{field:'SUM',title:'药品费用',width:100,align:'center'},
	        	{field:'DRUGNUM',title:'品规数',width:100,align:'center'},
	        	{field:'TREATMENTTOTAL',title:'诊疗人次',width:100,align:'center'},
	        	{field:'TREATMENTRATE',title:'<span title="该疾病诊疗人数/总就诊人数" class="tip">诊疗率</span>',width:100,align:'center'}
		   ];
	}
}
function showChart() {
	var nextQueryType = (window.jsonStr.length <= 2?0:window.queryParams["queryType"]);
	top.$.modalDialog.data= [$('#dg').datagrid("getRows"), nextQueryType, window.queryParams["orderType"]];
	top.$.modalDialog({
		title : "查询",
		width : 800,
		height : 460,
		href : " <c:out value='${pageContext.request.contextPath }'/>/supervise/diseaseAnalysis/chart.htmlx"
	});
	
	
}
function exportexcel(){
	var nextQueryType = (window.jsonStr.length <= 2?0:window.queryParams["queryType"]);
	var orderType = window.queryParams["orderType"]||0;
	var url = " <c:out value='${pageContext.request.contextPath }'/>/supervise/diseaseAnalysis/export.htmlx?";
	for (var k in window.queryParams) {
		url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(window.queryParams[k]);
	}
	window.open(url);	
}

</script>