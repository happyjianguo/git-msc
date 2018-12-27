<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<style type="text/css">
#toolbar{
width:100%;
}
#time{
margin-right:30px;float:right;
}
</style>
<html>
<body class="easyui-layout" >

<div data-options="region:'center',title:''" >
	<div id="toolbar" class="search-bar" >
        		<!-- <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true" onclick="exportexcel()">导出</a> -->
		        <span id='time'></span>
	    	</div>
    <table id="dg"></table>
</div>

</body>
</html>

<script>
//初始化
$(function() {
	window.jsonStr = decodeURIComponent("<c:out value='${jsonStr}'/>");
	if (jsonStr.length == 0) {
		jsonStr = "{}";
	}
	window.queryParams = eval("(" + jsonStr + ")");
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-2,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/clinicRecipe/query.htmlx",
		queryParams:window.queryParams,
		pageSize:20,
		pageNumber:1,
		toolbar:"#toolbar",
		columns:[[
		        	{field:'PATIENNAME',title:'病人姓名',width:75,align:'center'},
		        	{field:'SEX',title:'性别',width:75,align:'center'},
		        	{field:'AGE',title:'病人年龄',width:75,align:'center'},
		        	{field:'TYPE',title:'类型',width:150,align:'center'},
		        	{field:'CDATE',title:'门诊/出院日期',width:150,align:'center'},
		        	{field:'SUM',title:'总金额',width:100,align:'center'},
		        	{field:'DRUGNUM',title:'品规数',width:100,align:'center'},
		        	{field:'DRUGSUM',title:'药品费用',width:100,align:'center'},
		        	{field:'OTHERSUM',title:'医疗费用',width:100,align:'center'}
		   ]],
		   onLoadSuccess:function(data){
			var array = data.rows;
			if(array.length>0){
				var begindata = array[0]['begindata'];
				var enddata = array[0]['enddata'];
				var timetile="";
				if(begindata==enddata||enddata==""){
					timetile=begindata;
				}else{
					timetile=begindata+" -- "+enddata;
				}
				$("#time").html("查询时间  : "+timetile);
			}
			
			$('#dg').datagrid('doCellTip',{delay:500}); 
		},
		onDblClickRow:function(index, row){
			openDetail(row);
		}
	});
	
});
function openDetail(row) {
	if (row.TYPE == "门诊") {
		top.$.modalDialog({
			title : "门诊详情",
			width : 800,
			height : 460,
			href : " <c:out value='${pageContext.request.contextPath }'/>/supervise/clinicRecipe/detail.htmlx?hospitalCode="+row.HOSPITALCODE+"&outSno="+row.SNO
		});
	} else {
		top.$.modalDialog({
			title : "住院详情",
			width : 800,
			height : 460,
			href : " <c:out value='${pageContext.request.contextPath }'/>/supervise/hisRecipe/detail.htmlx?hospitalCode="+row.HOSPITALCODE+"&inSno="+row.SNO
		});
	}
	function  exportexcel(){
		
	}
}
</script>