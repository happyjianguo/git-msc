<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="医院基本药物配备使用比例" />

<html>
	
<body class="easyui-layout">
	<div data-options="region:'center',title:''" >
    <table id="dg"></table>
</div>
</body>
</html>

<script>
$(function(){
	window.queryType = "<c:out value='${queryType}'/>";
	window.jsonStr = decodeURIComponent("<c:out value='${jsonStr}'/>");
	var toolbar = [{
		iconCls: 'icon-xls',
		text:"导出",
		handler: function(){
			var nextQueryType = (window.jsonStr.length <= 2?0:window.queryParams["queryType"]);
			var url = " <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugProvide/export.htmlx?";
			for (var k in window.queryParams) {
				url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(window.queryParams[k]);
			} 
			window.open(url);
		}
	}]
	if (jsonStr.length == 0) {
		jsonStr = "{}";
		toolbar.splice(0,0,{
			iconCls: 'icon-search',
			text:"筛选",
			handler: function() {
				openSearch();
			}
		});
	}
	window.queryParams = eval("("+jsonStr+")");
	if (jsonStr.length > 2) {
		//查询条件不为空，说明不是首次进入
		window.queryParams["queryType"] = "<c:out value='${queryType}'/>";
	}
	window.nextQueryType = "<c:out value='${queryType+1}'/>"; 
	//datagrid
	$("#dg").datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-2,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugProvide/page.htmlx",
		queryParams:window.queryParams,
		pageSize:20,
		pageNumber:1,
		columns:[getColumns(window.queryParams["queryType"])],
		toolbar: toolbar,
		onDblClickRow: function(index,row){
			addOpen(row);
		},
		onLoadSuccess:function(data){
			$('#dg').datagrid('doCellTip',{delay:500}); 
			$(".tip").tooltip({  
                onShow: function(){  
                    $(this).tooltip('tip').css({   
                        width:'220',          
                        boxShadow: '2px 2px 4px #292929'                          
                    });  
                }  
            });
		}
	});
});

//刷选
function openSearch() {
	top.$.modalDialog({
		title : "查询",
		width : 600,
		height : 360,
		href : " <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugProvide/search.htmlx",
		onLoad:function(){
			
		},
		buttons : [ {
			text : '查询',
			iconCls : 'icon-ok',
			handler : function() {
				window.nextQueryType = (window.jsonStr.length <= 2?1:window.queryParams["queryType"]);
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				var monthBegin = f.find("#beginDate_yy").combobox("getValue")+"-"+f.find("#beginDate_mm").combobox("getValue")+"-01";
				var monthEnd = f.find("#endDate_yy").combobox("getValue")+"-"+f.find("#endDate_mm").combobox("getValue")+"-30";
				try {
					var countyCode = f.find("#combox3").combobox("getValue");
					if (countyCode.length != 0) {
					}
				} catch(e){}
				window.queryParams={
					"query['b#hospitalCode_S_EQ']" : f.find("#hospitalCode").combobox("getValue"),
					"query['b#orgLevel_S_EQ']" : f.find("#orgLevel").combobox("getValue"),
					"query['b#month_S_GE']":monthBegin,
					"query['b#month_S_LE']":monthEnd,
					"query['b#countyCode_S_EQ']" : countyCode,
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

//钻取
function addOpen(row) {
	var query = {};
	for (var k in window.queryParams) {
		query[k]=window.queryParams[k];
	}
	var title = "";
	query["queryType"] = window.nextQueryType;
	var url = "/supervise/baseDrugProvide.htmlx?queryType="+window.nextQueryType+"&jsonStr=";
	if (window.nextQueryType == 1) {
		query["query['b#countyCode_S_EQ']"] = row.COUNTYCODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "基本药物配备使用比例(医院)";
	} else if (window.nextQueryType == 2) {
		query["query['b#hospitalCode_S_EQ']"] = row.HOSPITALCODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "基本药物配备使用比例(月份)";
	}else{
		title = "基本药物配备使用比例(月份)";
	}
    top.addTab(title, url,null,true);
} 

function getColumns(queryType) {
	
	var columns = [
		{field:'BASEDRUGTOTAL',title:'基本药物品规数',width:100,align:'center'},
		{field:'DRUGTOTAL',title:'全部药物品规数',width:100,align:'center'},
		{field:'DRUGRATIO',title:'<span title=" 基本药物品规数  / 全部药物品规数 " class="tip">基本药物品规数占比</span>',width:120,align:'center',
			formatter: function(value,row,index){
				if (row.drugTotal != 0){
					return (parseFloat(row.BASEDRUGTOTAL/row.DRUGTOTAL)*100).toFixed(1)+'%';
				}else{
					return 0;
				}
		}},
		{field:'BASEDRUGTRADE',title:'基本药物销售金额',width:105,align:'center'},
		{field:'DRUGTRADE',title:'全部药物销售金额',width:105,align:'center'},
		{field:'DRUGTRADERATIO',title:'<span title=" 基本药物销售金额  / 全部药物销售金额 " class="tip">基本药物销售金额占比</span>',width:125,align:'center',
			formatter: function(value,row,index){
				if (row.drugTrade != 0){
					return (parseFloat(row.BASEDRUGTRADE/row.DRUGTRADE)*100).toFixed(1)+'%';
				}else{
					return 0;
				}
		}}
	   ];

	var first = {field:'COUNTYNAME',title:'区',width:82,align:'center'};
	var second =null;
	var third =null;
	if(queryType>0){
		if (queryType == 1) {
			first = {field:'HOSPITALNAME',title:'医院',width:140,align:'center'};
			
		} else if (queryType == 2) {
			second = {field:'HOSPITALNAME',title:'医疗机构名称',width:140,align:'center'};
			first = {field:'MONTH',title:'月份',width:100,align:'center'};
		} 
		third={field:'ISREFORMHOSPITAL',title:'是否县级公立医院改革试点医院',width:170,align:'center',
				formatter: function(value,row,index){
					if (row.ISREFORMHOSPITAL==1){
						return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
					}
			}}; 
		fourth={field:'ORGLEVEL',title:'医疗机构级别',width:90,align:'center'};
		fivth={field:'ORGTYPE',title:'医疗机构类型',width:90,align:'center'}; 
	}
	columns.splice(0,0,first);
	if(queryType==1){
		columns.splice(2,0,fourth);
		columns.splice(3,0,fivth); 
		columns.splice(10,0,third);
	}	
	else if(queryType==2){
		columns.splice(1,0,fourth);
		columns.splice(2,0,fivth); 
		columns.splice(0,0,second);
		columns.splice(11,0,third); 
	}	
	
	return columns;
}
</script>