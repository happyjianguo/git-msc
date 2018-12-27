<%@page import="com.shyl.sys.entity.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
					<input name="name" class="easyui-textbox" data-options="disabled:true" style="width:300px;" />
				</th>
				
			</tr>
		   <tr>
				<th>描述：</th>
				<th>
					<input id="describe" name = "describe" class="easyui-textbox" data-options="multiline:true,disabled:true" value="" style="width:300px;height:100px">
				</th>
			</tr>
			<tr>
		   		<th>审核:</th>
		   		<th>
					<input name="statusYN" id="statusYN"  />
				</th>
			</tr>
			<tr>
		   		<th>扣分:</th>
		   		<th>
					<input name="deduct" id="deduct"  />
				</th>
			</tr>
		</table>
		<input type="hidden" name="id" />
	</form>


</div>



<script>

//初始化
$(function(){
	
	$('#statusYN').combobox({
		 valueField:'label',    
		    textField:'value',
		    width:80,
		    value:"Y",
		    data: [{
				label: 'Y',
				value: '同意'
			},{
				label: 'N',
				value: '不同意'
			}],
			editable:false,
			required:true
	});
	
	$('#deduct').combobox({
		 valueField:'label',    
		    textField:'value',
		    width:80,
		    value:"1",
		    data: [{
				label: '1',
				value: '1分'
			},{
				label: '2',
				value: '2分'
			},{
				label: '3',
				value: '3分'
			},{
				label: '4',
				value: '4分'
			},{
				label: '5',
				value: '5分'
			}],
			editable:false
	});
	
	$("#form1").form({
		url :"<c:out value='${pageContext.request.contextPath }'/>/yunwei/serviceJudge/judge.htmlx",
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