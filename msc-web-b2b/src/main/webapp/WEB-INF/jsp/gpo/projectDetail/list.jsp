<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
<form id="fileId" method="POST" enctype="multipart/form-data">
	<input type="file" name="myfile" class="file" id="myfile" style="display:none"/>
</form>
<div class="single-dg">
	<table  id="dg" ></table>
</div>

</body>
</html>

<script>
//初始化
$(function(){
	$("#myfile").change(function(){
		$.messager.confirm('确认信息', '确认导入药品招标项目么?', function(r){
			if (r){
				$("#fileId").submit();
			}else{
				location.reload(true);
			}
		});
	});
	
	$("#fileId").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/projectDetail/importExcel.htmlx?projectId=<c:out value='${projectId}'/>",
		onSubmit : function() {
			return true;
		},
		success : function(result) {
			$('#myfile').val("");
			result = $.parseJSON(result);
			if(result.success){
				$('#dg').datagrid('reload');
				showMsg(result.msg);
			}else{
				showMsg(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() - 4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/projectDetail/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'projectCode',title:'项目编码',width:10,align:'center',formatter: function(value,row,index){
        		return row.project.code;
        	}},
        	{field:'projectName',title:'项目名称',width:10,align:'center',formatter: function(value,row,index){
        		return row.project.name;
        	}},
        	{field:'directory.genericName',title:'通用名',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.genericName;
        	}},
        	{field:'directory.dosageFormName',title:'剂型',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.dosageFormName;
        	}},
        	{field:'directory.model',title:'规格',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.model;
        	}},
        	{field:'directory.qualityLevel',title:'质量层次',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.qualityLevel;
        	}},
        	{field:'directory.minUnit',title:'最小制剂单位',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.minUnit;
        	}},
        	{field:'directory.note',title:'厂家备注',width:10,align:'center',formatter: function(value,row,index){
        		return row.directory.note;
        	}}
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
				delFunc();	
			}
		},'-',{
			iconCls: 'icon-import',
			text:"导入",
			handler: function(){
				$("#myfile").click();
			}
		},'-',{
			iconCls: 'icon-import',
			text:"模板下载",
			handler: function(){
				window.open(" <c:out value='${pageContext.request.contextPath }'/>/set/projectDetail/export.htmlx");
			}
		}],
		queryParams:{
			"query['t#project.id_L_EQ']":"<c:out value='${projectId}'/>"
		}
	});
	$('#dg').datagrid('enableFilter',[{
		 field:'project.code',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'project.name',
		 type:'text',
		 fieldType:'t#S'
	},{
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
		 field:'directory.qualityLevel',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'directory.minUnit',
		 type:'text',
		 fieldType:'t#S'
	}]);
	

});


//删除
function delFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(selobj);
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
function delAjax(row){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/projectDetail/delete.htmlx",
		data:{
			"projectId":row.project.id,
			"directoryId":row.directory.id
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
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
		width : 1000,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/projectDetail/directory.htmlx",
		onLoad:function(){
			var f = top.$.modalDialog.handler.find("#projectId").val("<c:out value='${projectId}'/>");
			top.search();
		},
		onClose:function() {
			top.$.modalDialog.handler.dialog('destroy');
			top.$.modalDialog.handler = undefined;
			$('#dg').datagrid('reload');  
		}
	});
}

</script>