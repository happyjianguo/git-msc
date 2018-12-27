<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="single-dg">
	<table  id="dg" ></table>
	</div>
<script>
//初始化379
$(function(){
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		showFooter: true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  230,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitaltradejbgoods/page.htmlx",
		queryParams:{
			"name":null,
	    },
		pageSize:20,
		pageNumber:1,
		columns:[[
		        	{field:'HOSPITALNAME',title:'医疗机构',width:20,align:'center'},   	
		        	{field:'SUM',title:'采购总金额（元）',width:30,align:'right',
						formatter: function(value,row,index){
							if (row.SUM){
								return common.fmoney(row.SUM);
							}
						}}, 	
		        	{field:'SBSUM',title:'社保药物采购金额（元）',width:30,align:'right',
						formatter: function(value,row,index){
							if (row.SBSUM){
								return common.fmoney(row.SBSUM);
							}
						}},
		        	{field:'JBSUM',title:'基本药物采购金额（元）',width:30,align:'right',
						formatter: function(value,row,index){
							if (row.JBSUM){
								return common.fmoney(row.JBSUM);
							}
						}},
		        	{field:'FJBSUM',title:'非基本药物采购金额（元）',width:30,align:'right',
						formatter: function(value,row,index){
							if (row.FJBSUM){
								return common.fmoney(row.FJBSUM);
							}
						}}
		   		]],
		onDblClickRow: function(index,field,value){
			openChart();
		},
		onLoadSuccess:function(data){

		}
	});
});
</script>