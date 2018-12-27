<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<pre style="padding:10px;" id="datagram_form"></pre>

<script type="text/javascript">
$(function() {
		$.post(
		"<c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/datagram/data.htmlx",
		{id:<c:out value='${dataId}'/>},
		function (data) {
			$("#datagram_form").text(data);
		}, "text");
})
</script>