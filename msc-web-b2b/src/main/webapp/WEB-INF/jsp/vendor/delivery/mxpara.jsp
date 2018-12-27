<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px">
	<form id="form1">
		<table class="table-bordered">
			
			<tr>
				<th>批号：</th>
				<th>
					<input  id="batchCode" name="batchCode" class="easyui-textbox"  style="width:200px">
				</th>
			</tr>
			<tr>
				<th>生产日期：</th>
				<th>
					<input  id="batchDate" name="batchDate"  >
				</th>
			</tr>
			<tr>
				<th>有效日期：</th>
				<th>
					<input  id="expiryDate" name="expiryDate"   >
				</th>
			</tr>
			<tr>
				<th>质量记录：</th>
				<th>
					<input  id="qualityRecord" name="qualityRecord" class="easyui-textbox" data-options="multiline:true" style="width:200px;height:100px;">
				</th>
			</tr>
			<tr>
				<th>检验报告链接：</th>
				<th>
					<input  id="inspectionReportUrl" name="inspectionReportUrl" class="easyui-textbox"  style="width:200px">
				</th>
			</tr>
		</table>
	</form>
</div>
<script>
//日期
$('#batchDate').datebox({
	editable:false
});

$('#expiryDate').datebox({
	editable:false
});
</script>

