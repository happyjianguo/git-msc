<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout"  >
	<div  data-options="region:'west',title:'公共设置',collapsible:false" class="my-west" >
		<table  id="dg" ></table>
	</div>
       
	<div data-options="region:'center',title:'明细设置'" class="my-center" >	
			<table id="dg2" ></table>

	</div>
</body>

</html>
<script>
//主档删除
function del(){

	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		$.messager.alert('错误','没有选中行!','info');
		return;
	}
	var id= selobj.id;
	
	$.messager.confirm('确认信息', '确认要删除此属性?', function(r){
		if (r){
			delAjax(id);
		}
	});
}

//明细删除
function subdel(){

	var selobj = $('#dg2').datagrid('getSelected');
	if(selobj == null){
		$.messager.alert('错误','没有选中行!','info');
		return;
	}
	var id= selobj.id;
	
	$.messager.confirm('确认信息', '确认要删除此明细?', function(r){
		if (r){
			subdelAjax(id);
		}
	});
}

//搜索
function search(searchkey){
	var selrow = $('#dg').datagrid('getSelected');
	$('#dg2').datagrid({  
	    url:"<c:out value='${pageContext.request.contextPath }'/>/sys/attribute/queryByAttributeItem.htmlx",  
	    queryParams:{
	        id:selrow.id,
	        searchkey:""
	    }
	}); 
}



//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 400,
		height : 237,
		href : " <c:out value='${pageContext.request.contextPath }'/>/sys/attribute/add.htmlx",
		onLoad:function(){
		},
		buttons : [ {
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

//弹窗增加
function subaddOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	
	if(selrow == null){
		$.messager.alert('提示','请先选择设置主档!','info');
		return;
	}
	
	top.$.modalDialog({
		title : "添加",
		width : 400,
		height : 237,
		href : " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/add.htmlx",
		onLoad:function(){
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg2');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				var pid = top.$.modalDialog.handler.find("#pid");
				pid.val(selrow.id);
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
function editOpen() {
	top.$.modalDialog({
		title : "编辑",
		width : 400,
		height : 237,
		href : " <c:out value='${pageContext.request.contextPath }'/>/sys/attribute/edit.htmlx",
		onLoad:function(){
			var selrow = $('#dg').datagrid('getSelected');
			if(selrow){
				var f = parent.$.modalDialog.handler.find("#form1");
				parent.$.modalDialog.handler.find("#showattributeNo").html(selrow.attributeNo);
				f.form("load", selrow);
			}
		},
		buttons : [ {
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
function subeditOpen() {
	top.$.modalDialog({
		title : "编辑",
		width : 400,
		height : 237,
		href : " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/edit.htmlx",
		onLoad:function(){
			var selrow = $('#dg2').datagrid('getSelected');
			if(selrow){
				var f = parent.$.modalDialog.handler.find("#form1");
				f.form("load", selrow);
			}
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg2');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
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

$(function(){

	//搜索框
	$("#ss").searchbox({
		searcher:function(value,name){
			search(value);
		},
		prompt:'搜索在此输入代号',
		width:150
	});

	//公共设置
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height :  $(this).height()-34,
		url:" <c:out value='${pageContext.request.contextPath }'/>/sys/attribute/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		columns:[[
		        	{field:'attributeNo',title:'属性代码',width:40,align:'center'},
		        	{field:'name',title:'名称',width:30,align:'center'},
		        	{field:'description',title:'描述',width:30,align:'center'}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addOpen();  			
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				del();
			}
		}],
		onClickRow: function(index,row){
			search("");  
		},
		onDblClickRow: function(index,field,value){
			editOpen();
		}

	});

	$("#dg").datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false
	})
	
	//明细设置
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		editable:true,
		height :  $(this).height()-34,
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
					{field:'field1',title:'代码',width:40,align:'center'},
					{field:'field2',title:'名称',width:60,align:'center'}
		   		]],
   		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				subaddOpen();  
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				subdel();
			}
		}],
		onDblClickRow: function(index,field,value){
			subeditOpen();
		}
	});

})


//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/sys/attribute/delete.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){				
				$('#dg').datagrid('reload');
				$('#dg2').datagrid('reload');
				showMsg(data.msg);
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
function subdelAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/delete.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg2').datagrid('reload');
				showMsg(data.msg);
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>