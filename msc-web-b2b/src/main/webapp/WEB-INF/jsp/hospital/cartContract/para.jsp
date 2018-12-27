<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px">
	<form id="form1">
		<table class="table-bordered">
			<tr>
				<th>配送点：</th>
				<th><input id="warehouseCode" /></th>
			</tr>
			<tr>
				<th>紧急程度：</th>
				<th><input id="urgencyLevel" /></th>
			</tr>
			<tr>
				<th>要求供货日期：</th>
				<th><input id="requireDate" /></th>
			</tr>
		</table>
	</form>
</div>




<script>
//日期
/* $('#requireDate').datetimespinner({
	width:150
});
var date = new Date();
date.setDate(date.getDate()+1)
$('#requireDate').datetimespinner("setValue",$.format.date(date,"yyyy-MM-dd HH:mm:ss"));
 */

//日期
$('#requireDate').datetimebox({
	editable:false,
	required:true,
	showSeconds: false
}); 
 
 var date = new Date();
 date.setDate(date.getDate()+1)

 $('#requireDate').datebox().datebox('calendar').calendar({
     validator: function(date){
         var now = new Date();
         var d1 = new Date(now.getFullYear(), now.getMonth(), now.getDate());
         return d1<=date
     }
 });

 $('#requireDate').datetimebox("setValue",$.format.date(date,"yyyy-MM-dd HH:mm"));


 var hospitalId = '<c:out value='${currentUser.organization.orgId}'/>';
if(hospitalId){
	$('#warehouseCode').combobox({    
	    url:" <c:out value='${pageContext.request.contextPath }'/>/set/warehouse/listByHospital.htmlx",
	    queryParams:{  
	    	hospitalId: hospitalId
	    },
	    valueField:'CODE',    
	    textField:'NAME',
	    editable:false,
	    required:true
	}).combobox('selectedIndex', 0);
}else{
	$('#warehouseId').combobox({
		required:true,
		editable:false
	});
}


$('#urgencyLevel').combobox({
    valueField:'label',    
    textField:'value',
    width:80,
    data: [{
		label: '1',
		value: '不紧急'
	},{
		label: '0',
		value: '紧急'
	}],
	editable:false,
	value:1
});
</script>