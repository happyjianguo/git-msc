<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
				<th>企业：</th>
		   		<th>
		   			<c:out value='${fullName }'/>
		   			
		   		</th>
		   </tr>
		   <tr>
		   		<th>加入原因：</th>
		   		<th>
		   			<c:out value='${joinReason }'/>
				</th>
		   </tr>
		   <tr>
		   		<th>解除原因：</th>
		   		<th>
		   			<input  id="disabledReason" name="disabledReason" class="easyui-textbox-67" data-options="multiline:true" style="width:200px;height:100px;">
					
				</th>
		   </tr>
		</table>
		<input type="hidden" name="id">
	</form>


</div>



<script>
//初始化
$(function(){
    $("input.easyui-textbox-67").textbox({validType: 'maxLength[100]'});//暂时控制
	
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/blacklist/edit.htmlx",
		onSubmit : function() {
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
				showMsg("修改成功！");
			}else{
				showErr(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
});

</script>