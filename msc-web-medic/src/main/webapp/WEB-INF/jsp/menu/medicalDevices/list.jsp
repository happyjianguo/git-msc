<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >
	<div data-options="region:'center',title:''">
		<table  id="dg" >
		</table>
	</div>
</body>
</html>

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
		height : $(this).height()-30,
		pagination:true,
		url:"<c:out value='${pageContext.request.contextPath }'/>/menu/medicalDevices/page.htmlx",
		pageSize:50,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'productName',title:'药品名称',width:150,align:'center'},
		        	{field:'englishName',title:'英文名',width:120,align:'center'},
		        	{field:'registName',title:'注册人名称',width:75,align:'center'},
		        	{field:'producerAddress',title:'生产地址',width:100,align:'center'},
		        	{field:'model',title:'规格/型号',width:100,align:'center'},
		        	{field:'range',title:'适用范围',width:150,align:'center'},
		        	{field:'areaName',title:'生产国家',width:150,align:'center'},
		        	{field:'authorizeDate',title:'批准日期',width:150,align:'center'},
		        	{field:'validExpiry',title:'有效期',width:100,align:'center'},
		        	{field:'productStandard',title:'产品标准',width:100,align:'center'}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"启动国产器械同步",
			handler: function(){
				$.ajax({
					url:"<c:out value='${pageContext.request.contextPath }'/>/menu/medicalDevices/snyc.htmlx?tableid=26",
					dataType:"json",
					type:"POST",
					cache:false,
					success:function(data) {
						showMsg(data.msg);
					},
					error:function(){
						showErr("出错，请刷新重新操作");
					}
				});	
			}
		},{
			iconCls: 'icon-add',
			text:"启动进口器械同步",
			handler: function(){
				$.ajax({
					url:"<c:out value='${pageContext.request.contextPath }'/>/menu/medicalDevices/snyc.htmlx?tableid=27",
					dataType:"json",
					type:"POST",
					cache:false,
					success:function(data) {
						showMsg(data.msg);
					},
					error:function(){
						showErr("出错，请刷新重新操作");
					}
				});	
			}
		},{
			iconCls: 'icon-add',
			text:"停止同步线程",
			handler: function(){
				$.ajax({
					url:"<c:out value='${pageContext.request.contextPath }'/>/menu/medicalDevices/stop.htmlx",
					dataType:"json",
					type:"POST",
					cache:false,
					success:function(data) {
						showMsg(data.msg);
					},
					error:function(){
						showErr("出错，请刷新重新操作");
					}
				});	
			}
		}],
		onLoadSuccess:function(data) {
		}
	});
	$('#dg').datagrid('enableFilter');
});

</script>