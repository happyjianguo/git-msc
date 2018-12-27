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
		rownumbers:false,
		border:true,
		height : $(this).height()-30,
		pagination:true,
		url:"<c:out value='${pageContext.request.contextPath }'/>/menu/supplement/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'productName',title:'药品名称',width:150,align:'center'},
		        	{field:'model',title:'规格',width:150,align:'center'},
		        	{field:'packDesc',title:'包装规格',width:150,align:'center'},
		        	{field:'producerName',title:'厂家',width:150,align:'center'},
		        	{field:'authorizeNo',title:'原批准文号',width:100,align:'center'}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"启动药品规格同步",
			handler: function(){
				$.ajax({
					url:"<c:out value='${pageContext.request.contextPath }'/>/menu/productData/snyc.htmlx?tableid=63",
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
					url:"<c:out value='${pageContext.request.contextPath }'/>/menu/productData/stop.htmlx",
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