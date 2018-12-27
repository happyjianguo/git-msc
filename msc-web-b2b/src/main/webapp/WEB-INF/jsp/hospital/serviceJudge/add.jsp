<%@page import="com.shyl.sys.entity.User"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	User currentUser = session.getAttribute("currentUser")==null?new User():(User)session.getAttribute("currentUser");
	int orgType = currentUser.getOrganization().getOrgType();
%>
<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>服务投诉:</th>
		   		<th>
					<input name="code" id="code">
					<input type="hidden" name="name" id="name" >
				</th>
				
			</tr>
		   <tr>
				<th>描述：</th>
				<th>
					<input id="describe" name = "describe" class="easyui-textbox" data-options="multiline:true,required:true" value="" style="width:300px;height:100px">
				</th>
			</tr>
		</table>
	</form>


</div>



<script>
//初始化
$(function(){
	
	$('#code').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/sys/attributeItem/getItemSelect.htmlx",
	    valueField:'id',    
	    textField:'field2', 
	    width:200,
	    queryParams:{
	    	"attributeNo": "complain"
		},
		required: true,
		editable:false,
	    onSelect:function(a){
	    	$('#name').val(a.field2);
	    }
	});
	
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/serviceJudge/add.htmlx",
		onSubmit : function() {
			//$("#pwd").textbox('setValue',$.md5($("#pwd").textbox('getValue')));
			top.$.messager.progress({
				title : '提示',
				text : '数据处理中，请稍后....'
			});
			var isValid = $(this).form('validate');
			if (!isValid) {
				top.$.messager.progress('close');
			}
			return isValid;
		},
		success : function(result) {
			top.$.messager.progress('close');
			result = $.parseJSON(result);
			if(result.success){
				top.$.modalDialog.openner.datagrid('reload');
				top.$.modalDialog.handler.dialog('close');
				showMsg("添加成功");
			}else{
				showMsg(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});

});
</script>