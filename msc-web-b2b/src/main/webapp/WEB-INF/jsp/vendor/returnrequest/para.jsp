<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px">
	<form id="form1">
		<table class="table-bordered">
			<tr>
				<th>说明：</th>
				<th>
					<input  id="explain" />
				</th>
			</tr>
			
		</table>
	</form>
</div>




<script>
$(function(){
	$('#explain').textbox({
		multiline:true,
		width:200,
		height:100,
		prompt:"最多100个字",
		required:false
	});
});

		

</script>