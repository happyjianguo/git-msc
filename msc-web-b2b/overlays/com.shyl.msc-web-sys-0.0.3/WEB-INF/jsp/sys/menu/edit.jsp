<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>资源名称:</th>
		   		<th>
					${resourceName}
				</th>
		   </tr>
		   <tr>
				<th>菜单名称：</th>
		   		<th>
		   			<input type="text" name="name" class="easyui-validatebox  easyui-textbox"  value="${name }"  >
		   		</th>
		   </tr>
		   <tr>
		   		<th>上级菜单：</th>
		   		<th>
		   			<input id="parentId_win1" name="parentId" style="width:150px;" value="${parentId }">
				</th>
		   </tr>
		   
		   
		</table>
		<input type="hidden" name="id" value="${id}"  >
		<input type="hidden" name="orgType" value="${orgType}"  >
	</form>


</div>



<script>
var orgType  = ${orgType};
var sysId  = "${sysId}";
//初始化
$(function(){
 	//$("input").textbox({validType:'length[0,100]'});
	$('#parentId_win1').combotree({
		idField:'id',    
		textFiled:'name',
		parentField:'parentId',
		url: '${pageContext.request.contextPath}/sys/menu/list.htmlx',
		queryParams:{  
			 "sysId":sysId,
			 "orgType":orgType
		},
		required: false
	}).combo("initClear");
	
		
	$("#form1").form({
		url :"${pageContext.request.contextPath}/sys/menu/edit.htmlx",
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
				top.$.modalDialog.openner.treegrid('reload');
				top.$.modalDialog.handler.dialog('close');
				showMsg("修改成功！");
			}else{
				showErr("出错，请刷新重新操作");
			}
		}
	});
});
</script>