<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <%--  <tr >	
		   		<th>医院：</th>
		   		<th>
		   			${tType}
		   		</th>
		   </tr> --%>
		   <tr>
				<th>中标价：</th>
		   		<th>
		   			<input type="text" name="biddingPrice" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:2,required:true"  />
		   		</th>
		   	</tr>
		   	<tr>
				<th>成交价：</th>
		   		<th>
		   			<input type="text" name="finalPrice" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:2,required:true"  />
		   		</th>
		   	</tr>
		   <!--  <tr>	
		   		<th>有效期：</th>
		   		<th>
		   			<input id="beginDate" name="beginDate" /> -
		   			<input id="outDate" name="outDate" validType="dateSE['#beginDate']" />
		   		</th>
		   </tr> -->
		    <tr>	
		   		<th>禁用：</th>
		   		<th>
		   			<label><input type="radio" name="isDisabled" value="1">是 </label>
					<label><input type="radio" name="isDisabled" value="0" checked>否 </label>
		   		</th>
		   </tr>
		</table>
		<input type="hidden" name="id" value=""  >
	</form>


</div>



<script>
//初始化
$(function(){
	
	//日期
	$('#beginDate').datebox({
		editable:false,
		disabled:true
	});
	$('#outDate').datebox({
		editable:false,
		disabled:true
	});

	
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/productPrice/edit.htmlx",
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
			top.$.modalDialog.openner.datagrid('reload');
			top.$.modalDialog.handler.dialog('close');
			showErr("出错，请刷新重新操作");
		}
	});
});
</script>