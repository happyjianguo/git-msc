<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:300px;" >
	<div>
		<table  id="dg"></table>
	</div>
	
</div>

<div data-options="region:'center',title:''"   class="my-center" >
    <table id="dgDetail"></table>
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
		height : $(".my-north").height(),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/directoryPrice/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
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
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addOpen();	
			}
		}],
		onClickRow: function(index,row){
        	searchDefectsList(row);
		  	return;
		}
	});
	$('#dg').datagrid('enableFilter',[{
		 field:'directory.genericName',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'directory.dosageFormName',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'directory.model',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'price',
		 type:'text',
	     isDisabled:1
	}]);
	
	$('#dgDetail').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(".my-center").height(),
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
			{field:'month',title:'月份',width:10,align:'center'},
        	{field:'genericName',title:'通用名',width:10,align:'center',
				formatter: function(value,row,index){return row.directory.genericName;}},
        	{field:'dosageFormName',title:'剂型',width:10,align:'center',
				formatter: function(value,row,index){return row.directory.dosageFormName;}},
        	{field:'model',title:'规格',width:10,align:'center',
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
		toolbar: [{
			iconCls: 'icon-remove',
			text:"删除",
			handler: function(){
				delFunc();	
			}
		}],
		onDblClickRow: function(index,row){
			editOpen(row);
		}
	});
});

//查询
function searchDefectsList(row){
	$('#dgDetail').datagrid({  
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/directoryPrice/mxpage.htmlx",
	    queryParams:{  
	    	"query['t#directory.id_L_EQ']":row.directory.id,
	    }  
	});
}
//删除
function delFunc(){
	var selobj = $('#dgDetail').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(selobj.id);
		}
	});
}

//搜索
function query(){
	var data = {};
	data["query['a#code_S_LK']"] = $("#projectCode").textbox("getValue");
	$('#dg').datagrid('load',data);
}
//=============ajax===============
function delAjax(id){
	$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/dm/directoryPrice/del.htmlx",
				data:{id:id},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dgDetail').datagrid('reload');  
						showMsg("删除成功！");
					} else {
						showMsg(data.msg);
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}

//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/directoryPrice/add.htmlx",
		buttons : [{
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				f.submit();
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

//弹窗修改
function editOpen(row) {
	top.$.modalDialog({
		title : "编辑",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/directoryPrice/edit.htmlx",
		queryParams:{
			"id":row.id
		},
		onLoad:function(){
			var f = top.$.modalDialog.handler.find("#form1");
			f.form("load", row);
		},
		buttons : [{
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				f.submit();
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

</script>