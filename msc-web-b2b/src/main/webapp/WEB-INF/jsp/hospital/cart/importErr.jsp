<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px; text-align: center">
	<img
		src=" <c:out value='${pageContext.request.contextPath }'/>/resources/images/exportErr.png"
		width=48px height=48px /> <span style="font-size: 30px;margin-left:10px;">导入失败!</span>
</div>
<div id="main"
	style="padding: 5px; margin-left: 30px; color: #999; text-align: left">
</div>

<script>
	function setImportErr(data) {
		$("#main").html(data);
	}
</script>




