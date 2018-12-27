<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="single-dg">
<table  id="dg" ></table>

</div>



<script>
//初始化
$(function(){
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		border:true,
		height:356,
		url:"<c:out value='${pageContext.request.contextPath }'/>/dm/directoryPrice/page.htmlx",
		queryParams:{
			"query[t#directory.id_L_EQ]":'<c:out value='${directoryId }'/>'
		},
		columns:[[
		        	{field:'directory.genericName',title:'通用名',width:10,align:'center',
						formatter: function(value,row,index){return row.directory.genericName;}},
		        	{field:'directory.dosageFormName',title:'剂型',width:10,align:'center',
						formatter: function(value,row,index){return row.directory.dosageFormName;}},
		        	{field:'directory.model',title:'规格',width:10,align:'center',
						formatter: function(value,row,index){return row.directory.model;}},
					{field:'minUnit',title:'最小使用单位',width:10,align:'center',
						formatter: function(value,row,index){return row.directory.minUnit;}},
					{field:'areaName',title:'地区名称',width:10,align:'center'},
					{field:'price',title:'价格',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.price){
								return common.fmoney(row.price);
							}
					}}
		 ]],
		onClickRow: function(index,row){
        	//searchDefectsList(row);
		  	return;
		}
	});
});

function getScore(){
	
	var selrows = $('#dg').datagrid("getSelections");
	return selrows.length
	
}
</script>