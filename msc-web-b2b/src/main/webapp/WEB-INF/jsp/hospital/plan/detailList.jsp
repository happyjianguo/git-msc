<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="single-dg">
	<table  id="dg" ></table>
</div>
<script>
//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		columns:[[
		        	{field:'month',title:'月份',width:10,align:'center', editor: { type: 'textbox', options: {readonly:true } }},
		        	{field:'num',title:'数量',width:10,align:'center', editor: { type: 'numberbox', options: {min:0,precision:0 } }}
		   		]],
		onLoadSuccess:function(data){
			$(data.rows).each(function(i,row){
				$('#dg').datagrid('beginEdit', i);
			})
		}
		   		
	});
});
function getDetailSetup() {
	var arr = [];
	var f = true;
	var selobjs = $("#dg").datagrid("getRows");
	var total = 0;
	$.each(selobjs,function(i,row) {
	 	var num = $('#dg').datagrid('getEditor', { index: i, field: 'num' });
		var month = $('#dg').datagrid('getEditor', { index: i, field: 'month' });
 		num = $(num.target).numberbox('getValue');
 		month = $(month.target).textbox('getValue');
 		if(!month) {
 			showMsg("月份不能为空");
 			return f = false;
 		}
 		if(!num) {
 			showMsg("采购量不能为空");
 			return f = false;
 		}
 		total+=parseInt(num);
 		arr.push({num:num,month:month});
	});
	if(!f) return;
	return {details:arr,total:total};
}
</script>