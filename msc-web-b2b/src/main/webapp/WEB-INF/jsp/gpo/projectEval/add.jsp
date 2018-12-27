<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		 <tr>
				<th>项目编码：</th>
		   		<th>	<input type="text" name="code" id="code"  class="easyui-validatebox  easyui-textbox"   data-options="required:true"/>
		   		</th>
		   </tr>
		   <tr>
				<th>项目名称：</th>
		   		<th>
		   			<input type="text" name="name" id="name" class="easyui-validatebox  easyui-textbox" data-options="required:true" />
		   		</th>
		   </tr>
		   <tr>
				<th>类型：</th>
		   		<th>
		   			<select id="type" name="type" class="easyui-validatebox  easyui-combobox" data-options="required:true" >
		   				<option value="all">不区分</option>
		   				<option value="base">基本药物</option>
		   				<option value="send">非基本药物</option>
		   			</select>
		   		</th>
		   </tr>
		   <tr>
		   		<th>报名时间：</th>
		   		<th>
		   			<input type="text" name="startDate" id="startDate" class="easyui-datebox"/>至<input type="text" name="endDate" id="endDate" class="easyui-datebox"/>
		   		</th>
		   </tr>
		   <tr>
		   		<th>默认计划周期：</th>
		   		<th>
		   			<input type="text" name="startMonthDef" id="startMonthDef" class="easyui-datebox"/>至<input type="text" name="endMonthDef" id="endMonthDef" class="easyui-datebox"/>
		   		</th>
		   </tr>
		</table>
		<input type="hidden" name="id" value="">
	</form>


</div>



<script>

function myformatter(date){
    var y = date.getFullYear();
    var m = date.getMonth()+1;
    var d = date.getDate();
    return y+'-'+(m<10?('0'+m):m);
}

function myparser(s){
    if (!s) return new Date();
    var ss = (s.split('-'));
    var y = parseInt(ss[0],10);
    var m = parseInt(ss[1],10);
    return new Date(y,m-1);
} 
//初始化
$(function(){
	var options = {formatter:myformatter,parser:myparser,editable:false,required:true};
	$("#startDate").datebox({
		required: true,
		editable:false
	});
	$("#endDate").datebox({
		required: true,
		editable:false
	});
	$("#startMonthDef").datebox(options);
	$("#endMonthDef").datebox(options);
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/gpo/project/add.htmlx",
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