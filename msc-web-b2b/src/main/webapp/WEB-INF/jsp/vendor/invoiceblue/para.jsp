<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px">
	<form id="form1">
		<table class="table-bordered">
			<tr>
				<th>发票号码：</th>
				<th><input type="text" id="invoiceCode" class="easyui-validatebox easyui-textbox" data-options="required:true" style="width:200px" ></th>
			</tr>
			<tr>
				<th>发票日期：</th>
				<th><input id="invoiceDate" /></th>
			</tr>
			<tr>
				<th>税率：</th>
				<th><input id="taxRate" /></th>
			</tr>
		</table>
	</form>
</div>




<script>
//日期
$('#invoiceDate').datebox({
	editable:false,
	required:true
});

$('#invoiceDate').datebox().datebox('calendar').calendar({
	validator: function(date){
		var now = new Date();
		return date<=now;
	}
});

$('#taxRate').combobox({
    valueField:'label',    
    textField:'value',
    width:80,
    panelHeight:25,
    value:17,
    data: [{
		label: '17',
		value: '17%'
	}],
	editable:false
	
});


</script>