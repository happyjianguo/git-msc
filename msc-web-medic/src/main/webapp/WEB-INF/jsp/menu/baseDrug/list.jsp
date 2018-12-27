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
		url:"<c:out value='${pageContext.request.contextPath }'/>/menu/baseDrug/page.htmlx",
		pageSize:50,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'drugType',title:'药品分类',width:150,align:'center'},
		        	{field:'oneLevelType',title:'一级类别',width:120,align:'center'},
		        	{field:'twoLevelType',title:'二级类别',width:75,align:'center'},
		        	{field:'threeLevelType',title:'三级类别',width:100,align:'center'},
		        	{field:'name',title:'品种名称（药品名称）',width:100,align:'center'},
		        	{field:'englishName',title:'英文名称',width:150,align:'center'},
		        	{field:'model',title:'剂型/规格',width:150,align:'center'},
		        	{field:'modelNotes',title:'剂型说明',width:150,align:'center'},
		        	{field:'range',title:'使用范围',width:100,align:'center'},
		        	{field:'notes',title:'备注',width:100,align:'center'}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"启动基本药物同步",
			handler: function(){
				$.ajax({
					url:"<c:out value='${pageContext.request.contextPath }'/>/menu/baseDrug/snyc.htmlx?tableid=74",
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
					url:"<c:out value='${pageContext.request.contextPath }'/>/menu/baseDrug/stop.htmlx",
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