<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >
	<div class="search-bar">
		<input id="ss1"/>
		<div id="mm">
			<div data-options="name:'p.name'">药品名称</div>
			<div data-options="name:'p.model'">规格</div>
			<div data-options="name:'p.producerName'">生产企业</div>			
		</div> 
	</div>
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>
</body>
</html>
<script>

//初始化
$(function(){
	//搜索框
	$("#ss1").searchbox({
		searcher:function(value,name){
			var query={};
			query["query[g.isDisabled_I_EQ]"] = 0;
			query["query["+name+"_S_LK]"] = value;
			$('#dg').datagrid('load',query); 
		},
		prompt:'搜索在此输入',
		menu:'#mm',
		width:200
	});

	//datagrid
	$('#dg').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/goodscount/page.htmlx",
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		columns:[[
					{field:'HOSPITALNAME',title:'医院名称',width:20,align:'center'},
					{field:'PRODUCTCODE',title:'药品编码',width:20,align:'center'},
		        	{field:'PRODUCTNAME',title:'药品名称',width:20,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},	
		        	{field:'MODEL',title:'规格',width:11,align:'center'},		
		        	{field:'PRODUCERNAME',title:'生产企业',width:20,align:'center'},		
		        	{field:'INSURANCEDRUGTYPE',title:'是否社保',width:10,align:'center',
						formatter: function(value,row,index){
							if(row.INSURANCEDRUGTYPE!=null)
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
						}
		        	},
		        	{field:'BASEDRUGTYPE',title:'是否基本药物',width:10,align:'center',
						formatter: function(value,row,index){
							if(row.BASEDRUGTYPE!=null)
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
						}
		        	}
		   		]]
	});
});

//=============ajax===============

</script>