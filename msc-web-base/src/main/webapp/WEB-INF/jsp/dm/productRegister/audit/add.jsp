<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px">
	<form id="form1">
		<table class="table-bordered">
			<tr>
				<th>审核人：</th>
				<th><input class="easyui-textbox" data-options="readonly:true" value="<c:out value='${user.name }'/>" /></th>
			</tr>
			<tr>
				<th>审核意见：</th>
				<th><input id="status" /></th>
			</tr>
			<tr>
				<th>审核内容：</th>
				<th>
					<input id="suggestion" class="easyui-textbox" data-options="multiline:true,required:true" value="" style="width:300px;height:100px">
				</th>
			</tr>
		</table>
	</form>
</div>




<script>

$('#status').combobox({
    valueField:'label',    
    textField:'value',
    width:80,
    data: [{
		label: '1',
		value: '同意'
	},{
		label: '2',
		value: '不同意'
	}],
	editable:false,
	value:1
});
</script>