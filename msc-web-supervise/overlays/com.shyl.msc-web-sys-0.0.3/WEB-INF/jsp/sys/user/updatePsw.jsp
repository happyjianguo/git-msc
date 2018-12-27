<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>工号:</th>
		   		<th>
					<input type="text" name="empId" id="empId" class="easyui-validatebox easyui-textbox" data-options="required:true" style="width:200px" >
					<input type="hidden" name="id" id='id'>
				</th>
		   </tr>
		   <tr>
		   	<th>旧密码:</th>
		   		<th>
		   			<input  type="password" name="password" id="password" class="easyui-validatebox easyui-textbox"  style="width:200px" >
		   		</th>
		   </tr>
		    <tr>
		   		<th>新密码:</th>
		   		<th>
				<c:choose>
				    <c:when test="${checkpwd == 1}">
				        <input  type="password" id="NewPsd" name="NewPsd" class="easyui-validatebox easyui-textbox" validType="pwd"  style="width:200px">
				    </c:when>
				    <c:otherwise>
				        <input  type="password" id="NewPsd" name="NewPsd" class="easyui-validatebox easyui-textbox" data-options="required:true"   style="width:200px">
				    </c:otherwise>
				</c:choose>
		   	
		   		
				</th>
		   </tr>
		    <tr>
		   		<th>新密码确认:</th>
		   		<th>
					<input  type="password" name="NewPsd1" class="easyui-validatebox easyui-textbox" data-options="required:true" validType="same['#NewPsd']" style="width:200px" />
				</th>
		   </tr>
		</table>
	</form>
 

</div>



<script>
function updatePswFunc(){
	var password = $("#password").textbox("getValue");
	var NewPsd = $("#NewPsd").textbox("getValue");
	if(password ==NewPsd){
		showErr("不能与原密码相同");
		return false;
	}
	 
	$.ajax({
		url :"${pageContext.request.contextPath}/sys/login/updatePsw.htmlx",
		data:{
			"empId":$("#empId").val(),
			"password":$.md5($("#password").val()),
			"newPsd":$.md5($("#NewPsd").val()),
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				top.$.modalDialog.handler.dialog('close');
				showMsg(data.msg);
                alert("密码修改成功！");
				location.href = "${pageContext.request.contextPath }/sys/logout.htmlx";
			}else{
				top.$.modalDialog.handler.dialog('close');
				showMsg(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
function showMsg(text){
	top.$.messager.show({
		title :  '消息',
		msg : text,
		timeout : 1000 * 2
	});
}

function showErr(text){
	top.$.messager.show({
		title :  '错误',
		msg : text,
		timeout : 1000 * 2
	});

}

//初始化
$(function(){
	$("#empId").textbox({validType:'length[0,100]'});
});

</script>