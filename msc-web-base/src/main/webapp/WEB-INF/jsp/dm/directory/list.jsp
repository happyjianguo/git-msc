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
		if ($("#myfile").val() == "") return;
		$.messager.confirm('确认信息', '确认导入遴选目录么?', function(r){
			if (r){
				$("#fileId").submit();
			}else{
				location.reload(true);
			}
		});
	});
	
	$("#fileId").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/directory/importExcel.htmlx",
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/directory/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
			{field:'id',title:'ID',width:10,align:'center'},
        	{field:'genericName',title:'通用名',width:10,align:'center'},
        	{field:'rcDosageFormName',title:'推荐剂型',width:10,align:'center'},
        	{field:'dosageFormName',title:'剂型',width:10,align:'center'},
        	{field:'model',title:'规格',width:10,align:'center'},
        	{field:'qualityLevel',title:'质量层次',width:10,align:'center'},
        	{field:'minUnit',title:'最小制剂单位',width:10,align:'center'},
        	{field:'producerNames',title:'生产厂家',width:10,align:'center'},
        	{field:'note',title:'备注',width:10,align:'center'},
        	{field:'batch',title:'批次',width:10,align:'center'}
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
				window.open(" <c:out value='${pageContext.request.contextPath }'/>/dm/directory/export.htmlx");
			}
		}]
	});
	$('#dg').datagrid('enableFilter',[{
		 field:'genericName',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'dosageFormName',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'model',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'qualityLevel',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'minUnit',
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
				url:" <c:out value='${pageContext.request.contextPath }'/>/dm/directory/delete.htmlx",
				data:{id:id},
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
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/directory/add.htmlx",
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