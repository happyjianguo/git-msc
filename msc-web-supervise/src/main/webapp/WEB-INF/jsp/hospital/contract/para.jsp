<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px">
	<form id="form1">
		<table class="table-bordered">
			<tr>
				<th>申请人：</th>
				<th><input class="easyui-textbox" data-options="readonly:true" value="<c:out value='${user.name }'/>" /></th>
			</tr>
			<tr>
				<th>申请原因：</th>
				<th>
					<input id="reason" class="easyui-textbox" data-options="multiline:true,required:true" value="" style="width:300px;height:100px">
				</th>
			</tr>
		</table>
	</form>
</div>
