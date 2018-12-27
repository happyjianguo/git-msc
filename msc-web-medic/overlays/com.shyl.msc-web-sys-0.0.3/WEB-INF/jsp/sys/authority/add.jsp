<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div>
	<form id="form1" name="form1"  method="post">
						<table class="table-bordered">
							<tr>
						   		<th>资源名称：</th>
						   		<th>
						   			<span id="resourceName"  />
								</th>
						   </tr>
						   <tr>
						   		<th>权限名称:</th>
						   		<th>
									<input type="text" name="name" class="easyui-validatebox easyui-textbox" data-options="required:true" style="width:200px;">
								</th>
						   </tr>
						   <tr>
								<th>权限路径：</th>
						   		<th>
						   			<input type="text" name="url" class="easyui-validatebox easyui-textbox" data-options="required:true" style="width:200px;" >
						   		</th>
						   </tr>
						   <tr>
								<th>权限编码：</th>
						   		<th>
						   			<input type="text" name="permCode" class="easyui-validatebox easyui-textbox" data-options="required:true" style="width:200px;" >
						   		</th>
						   </tr>
						   
						   <!-- <tr>
						   		<th>描述：</th>
						   		<th>
									<input type="text" name="description" class="easyui-validatebox easyui-textbox"  style="width:200px;" >
								</th>
						   </tr> -->
						   
						</table>
						<input type="hidden" name="type" value="O"  >
						<input type="hidden" name="state" value=""  >
						<input type="hidden" id="parentId" name="parentId" value=""  >
					</form>


</div>



<script>
//初始化
$(function(){
	//$("input").textbox({validType:'length[0,100]'});
	$("#form1").form({
		url :"${pageContext.request.contextPath}/sys/authority/add.htmlx",
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
				showMsg("新增成功！");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
});
</script>