<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="实施基本药物制度进展" />

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
			var url = " <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugProgress/export.htmlx?";
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugProgress/page.htmlx",
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
		href : " <c:out value='${pageContext.request.contextPath }'/>/supervise/baseDrugProgress/search.htmlx",
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
				var healthStationType = f.find("#healthStationType").combobox("getValue"); 
				console.log(healthStationType);
				window.queryParams={
					"query['b#healthStationType_S_EQ']" : healthStationType,
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
	var url = "/supervise/baseDrugProgress.htmlx?queryType="+window.nextQueryType+"&jsonStr=";
	if (window.nextQueryType == 1) {
		query["query['b#countyCode_S_EQ']"] = row.COUNTYCODE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "实施基本药物制度进展(镇区)";
	} else if (window.nextQueryType == 2) {
		query["query['b#healthStationType_S_EQ']"] = row.HEALTHSTATIONTYPE;
		url += encodeURIComponent(JSON.stringify(query));
		title = "实施基本药物制度进展(机构类型)";
	}else{
		title = "实施基本药物制度进展(机构类型)";
	}
    top.addTab(title, url,null,true);
} 

function getColumns(queryType) {
	
	var columns = [
		{field:'HEALTHSTATIONNUM',title:'村卫生站机构数',width:100,align:'center'},
		{field:'ISHIGHSIXTY',title:'基本药物集中采购且采购品规数、金额占比均不低于60%机构数',width:100,align:'center'},
		{field:'ISIMPLEMENTEDSTATION',title:'已实行药品零差率销售的机构数',width:100,align:'center'},
		{field:'ISGENERALSTATION',title:'已实施一般诊疗费收费的机构数',width:105,align:'center'},
		{field:'ISTHIRDHEALTHSTATION',title:'承担30%以上基本公共卫生服务机构数',width:105,align:'center'},
		{field:'ISINHEALTHINSURANCE',title:'已纳入城乡居民医保门诊统筹实施范围的机构数',width:105,align:'center'}
	   ];

	var first = {field:'CITYNAME',title:'市',width:50,align:'center'};
	var second =null;
	var third =null;
	if(queryType>0){
		if (queryType == 1) {
			first = {field:'COUNTYNAME',title:'镇区',width:50,align:'center'};
		} else if (queryType == 2) {
			first = {field:'COUNTYNAME',title:'镇区',width:50,align:'center'};
			second = {field:'MONTH',title:'月份',width:60,align:'center'};
		} 
	}
	columns.splice(0,0,first);
	if(queryType==2){ 
		columns.splice(1,0,second);
	}	
	
	return columns;
}
</script>