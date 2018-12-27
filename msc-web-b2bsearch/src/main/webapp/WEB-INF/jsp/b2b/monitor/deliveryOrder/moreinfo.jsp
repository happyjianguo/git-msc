<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px">
	<form id="form1">
		<table class="table-bordered">
			
			
			<tr>
				<th>质量记录：</th>
				<th>
					<input  id="QUALITYRECORD" name="QUALITYRECORD" readonly="true" class="easyui-textbox" data-options="multiline:true" style="width:200px;height:100px;">
				</th>
			</tr>
			<tr>
				<th>检验报告链接：</th>
				<th>
					<a href="<c:out value='${reporturl }'/>" target="_BLANK"><c:out value='${reporturl }'/></a>
				</th>
			</tr>
		</table>
	</form>
</div>


