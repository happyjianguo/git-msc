<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1" method="post">
		<table class="table-bordered">
			<tr>
				<th>合同期限：</th>
				<th><input id="deadline"/></th>
			</tr>
		</table>
	</form>


</div>



<script>
	//初始化
	$(function() {
		<%
			String type = (String)request.getAttribute("type");
			if ("2".equals(type)) {
		%>
		var dataArr = "<c:out value='${dateStr}'/>".split(";");
		var data = [];
		for(var i=0;i<=dataArr.length;i++) {
			data.push({name:dataArr[i],value:dataArr[i]});
		}
		<%	
		} else {
		%>
		var data = [{
			    	value: '3',
					name: '3个月'
				} ,{
			    	value: '6',
					name: '6个月'
				},{
			    	value: '9',
					name: '9个月'
				},{
			    	value: '12',
					name: '一年'
				}]
		<% } %>
		$('#deadline').combobox({
		    valueField:'value',    
		    textField:'name',
		    width:100,
		    data: data,
			value:data[0].value,
			editable:false,
			required : true,
			onSelect: function(record){
				$("#deadline").val(record.value);
			}
		});
		
	});
</script>