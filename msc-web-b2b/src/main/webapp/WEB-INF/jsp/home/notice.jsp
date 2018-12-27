<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<style>
<!--
ul {margin:0px;line-height: 30px;list-style-type: square;padding:0px 10px 0px 25px; }
ul li {border-bottom:1px dotted #999;cursor:pointer;}
ul li:hover{ background:#eee;color:#00438a } 
-->
</style>
<ul >
<c:forEach items="${noticeList}" var="p">
	<li onclick='editOpen("<c:out value='${p.id}'/>")'>
	<div style="overflow:hidden;width:160px;float:left;text-overflow:ellipsis;white-space:nowrap;" title="<c:out value='${p.title }'/>">
	<c:out value='${p.title }'/>
	</div>
	<c:if test="${p.fileManagement != null}" >  
    	<img style="vertical-align: middle;" src=' <c:out value='${pageContext.request.contextPath }'/>/resources/images/fileDown.png' width=16 height=16  />
    </c:if> 
	<span style='color:#999;float:right;margin-right:15px;'><c:out value='${p.publishDate }'/> </span>
	</li>

</c:forEach>

</ul>

<script>
//初始化
$(function(){
	
	
});
//弹窗详情
function editOpen(id) {
	//var selrow = $('#dg_notice').datagrid('getSelected');
	top.$.modalDialog({
		title : "详情",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/home/notice/sub.htmlx",
		queryParams:{
			id:id
		},
		onLoad:function(){
			//top.setData_notice(a,b,c,d,e);
		}
	});
}
</script>