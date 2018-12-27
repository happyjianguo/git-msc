<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
<table  id="dg" class="single-dg"></table>

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
		height:360 ,
		url:"<c:out value='${pageContext.request.contextPath }'/>/gpo/projectEval/expertList.htmlx",
		queryParams:{
			"query[t#project.id_L_EQ]":'<c:out value='${projectId }'/>'
		},
		columns:[[
        	{field:'expert.name',title:'专家',width:10,align:'center',formatter: function(value,row,index){
        		return row.expert.name;
        	}},
        	{field:'score',title:'打分',width:10,align:'center',formatter: function(value,row,index){
        		return "1分";
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