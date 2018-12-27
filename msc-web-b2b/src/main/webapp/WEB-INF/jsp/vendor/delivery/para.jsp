<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px">
	<form id="form1">
		<table class="table-bordered">
			<tr>
				<th>配送商：</th>
				<th>
					<input  id="senderCode" class="easyui-textbox" data-options="required:true" style="width:200px">
				</th>
			</tr>
			<tr>
				<th>配送单条形码：</th>
				<th>
					<input  id="internalCode" class="easyui-textbox" data-options="required:true" style="width:200px">
				</th>
			</tr>
		</table>
	</form>
</div>




<script>
$(function(){
	$('#senderCode').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/vendorSender/list.htmlx",
	    valueField:'senderCode',    
	    textField:'senderName',
	    editable:false
	}).combobox('selectedIndex', 0);;	
});

		

</script>