<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="医院在用药品目录" />

<html>

<body class="easyui-layout"  >
	<div class="single-dg">
		<shiro:hasPermission name="supervise:quota:add">
			<div id="toolbar" class="search-bar" >
		        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="editOpen(false)">添加</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="delAjax()">删除</a>
		    </div>
	    </shiro:hasPermission>
		<table  id="dg" ></table>
	</div>
</body>

</html>
<script>
$(function(){
	var jsonStr = "<c:out value='${jsonStr}'/>";
	if (jsonStr == "") {
		jsonStr = "{}";
	}
	//用户组
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height:$(this).height()-4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/quota/page.htmlx",
		pageSize:20,
		pageNumber:1,
		pagination:true,
		remoteFilter: true,
		queryParams:eval("("+decodeURIComponent(jsonStr)+")"),
		columns:[[
					{field:'code',title:'指标编码',width:75,align:'center'},
					{field:'name',title:'指标名称',width:150,align:'center'},
					{field:'remark',title:'指标描述',width:250,align:'center'},
					{field:'calculation',title:'计算公式',width:250,align:'center'},
					{field:'min',title:'最小值',width:100,align:'center'},
					{field:'max',title:'最大值',width:100,align:'center'},
					{field:'isDisable',title:'是否禁用',width:100,align:'center',
		        		formatter: function(value,row,index){
		        			console.log(value);
							if (row.isDisable==1){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
					}},
					{field:'type',title:'类别',width:100,align:'center',formatter: function(value,row,index){
						if (value == 'ordinary') {
							return "门诊";
						} else if (value == 'urgent') {
							return "急诊";
						} else if (value == 'his') {
							return "住院";
						} else {
							return "全院";
						}
					}}
		   		]],
		toolbar:"#toolbar",
		onDblClickRow: function(index,field,value){
			editOpen(true);
		}
	});
	
})

//弹窗修改
function editOpen(isedit) {
	var selrow = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : (isedit?"修改":"新增"),
		width : 600,
		height : 380,
		href : " <c:out value='${pageContext.request.contextPath }'/>/supervise/quota/"+(isedit?"edit":"add")+".htmlx",
		onLoad:function(){
			if(selrow){
				var f = top.$.modalDialog.handler.find("#form1");
				f.form("load", selrow);
			}
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				doSave(isedit);	
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

function delAjax(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null) return;
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
	$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/quota/delete.htmlx",
				data:{"id":selobj.id},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dg').datagrid('reload');  
						showMsg("删除成功！");
					} 
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
		}
	});
}
function doSave(isedit) {
	top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
	var f = top.$.modalDialog.handler.find("#form1");
	console.log(f);
	f.form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/supervise/quota/"+(isedit?"update":"save")+".htmlx",
		onSubmit : function() {
			top.$.messager.progress({
				title : '提示',
				text : '数据处理中，请稍后....'
			});
			var isValid = f.form('validate');
			console.log(isValid);
			if (!isValid) {
				top.$.messager.progress('close');
			}
			return isValid;
		},
		success : function(result) {
			top.$.messager.progress('close');
			result = $.parseJSON(result); 
			if(result.success){
				$('#dg').datagrid('reload');
				showMsg((isedit?"修改":"新增")+"成功！");	
				top.$.modalDialog.handler.dialog('destroy');
				top.$.modalDialog.handler = undefined;
			}else{
				showErr(result.msg);
			}	
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	f.submit();
}
</script>