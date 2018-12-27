<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px">
	<form id="form1">
		<table class="table-bordered">
			<tr>
				<th>付款日期：</th>
				<th><input id="payDate" /></th>
			</tr>
		</table>
	</form>
</div>




<script>
//日期
$('#payDate').datebox({
	editable:false,
	required:true
});
$('#payDate').datebox().datebox('calendar').calendar({
	validator: function(date){
		var now = new Date();
		return date>=now;
	}
});

var date = new Date();
date.setDate(date.getDate()+30)
$('#payDate').datebox("setValue",$.format.date(date,"yyyy-MM-dd"));

</script>