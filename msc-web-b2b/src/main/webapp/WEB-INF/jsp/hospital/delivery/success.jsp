<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding: 5px; text-align: center">
	<img
		src=" <c:out value='${pageContext.request.contextPath }'/>/resources/images/success.png"
		width=48px height=48px /> <span style="font-size: 30px">收货成功!</span>
</div>
<div
	style="padding: 5px; margin-left: 30px; color: #999; text-align: left">
	入库编号:
	<ul id="main"></ul>
</div>

<div style="padding: 5px; text-align: right">
	<a href="javascript:goOrder()" class='easyui-linkbutton' data-options="iconCls:'icon-redo',plain:true" style="margin-right: 30px;">前往查看</a>
</div>
<script>
	function goOrder() {
		top.$.modalDialog.handler.dialog('close');
		parent.addTab("入库单查询", "/b2b/monitor/inOutBound.htmlx", null);

	}
	function setData(data) {
		
		//data = $.parseJSON(data);
		for (var i = 0; i < data.length; i++) {
			var li = $("<li>" + data[i].code + "</li>");
			$("#main").append(li);
		}
	}
</script>




