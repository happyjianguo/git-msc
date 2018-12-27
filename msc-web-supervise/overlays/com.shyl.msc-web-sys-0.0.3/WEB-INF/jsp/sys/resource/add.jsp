<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>资源名称:</th>
		   		<th>
					<input type="text" name="name" id="name" class="easyui-validatebox  easyui-textbox" data-options="required:true"  >
				</th>
		   </tr>
		   <tr>
				<th>资源路径：</th>
		   		<th>
		   			<input type="text" name="url" class="easyui-validatebox  easyui-textbox"  >
		   		</th>
		   </tr>
		   <tr>
		   		<th>资源图标：</th>
		   		<th>
					<a href="#" class="easyui-linkbutton" id="iconBtn"  data-options="iconCls:'${resource.icon }'" onclick="chooseIcon()">选择图标</a>
					<input type="text" name="icon" id="icon" value="${resource.icon }" class="easyui-validatebox  easyui-textbox"  >
					
				</th>
		   </tr>
		   <!-- <tr>
		   		<th>描述：</th>
		   		<th>
					<input type="text" name="description" class="easyui-validatebox  easyui-textbox"  >
				</th>
		   </tr> -->
		   
		</table>
		<div id="iconlist" style="padding:10px;">
 			
		</div>
		<input type="hidden" name="type" value="F"  >
		<input type="hidden" name="state" value=""  >
	</form>


</div>



<script>
//初始化
$(function(){
	//$("input").textbox({validType:'length[0,100]'});
	$("#form1").form({
		url :"${pageContext.request.contextPath}/sys/resource/add.htmlx",
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

function chooseIcon(){
	$.ajax({
		url:"${pageContext.request.contextPath }/sys/resource/iconlist.htmlx",
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			$("#iconlist").empty();
			$.each(data,function(i,v){
				var d = $("<a style='margin:2px;'></a>");
				d.linkbutton({
					iconCls:v,
					onClick:function(){
						$("#icon").textbox("setValue",v);
						$("#iconBtn").linkbutton({
						    iconCls: v
						});
					}}); 
				$("#iconlist").append(d);
			})
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
	
}
</script>