<%@page import="com.shyl.msc.common.CommonProperties"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<shiro:authenticated>
<%
response.sendRedirect(request.getContextPath() + "/index.jsp");
%>
</shiro:authenticated>

	<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/default/easyui.css" />
    <link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/base.css" />
    <link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icon.css" />
    <link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/home.css" />
    <script  type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.min.js"></script>
    <script type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.md5.js"></script> 
    <script  type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.easyui.min.js"></script>
<html>


<div id="head" class="head" style=""><span style="margin-left:30px;font-size:36px; line-height: 70px;color:#eee;"></span></div>

<div id="content" class="content" >
<ul >
               <li onclick="javascript:window.location.href='<c:out value='${pageContext.request.contextPath}'/>/login.jsp'">
                    <div class="theme"   onmouseenter="m_over(this)" onmouseleave="m_out(this)">
                        
                        <img width=100% height=280px src="<c:out value='${pageContext.request.contextPath }'/>/resources/images/homesys.jpg" >
                        <h2 style="text-align:center;color:#333;">
                            系统管理
                        </h2>
                    </div>
                </li>
                 <li onclick="javascript:window.location.href='<c:out value='${pageContext.request.contextPath}'/>/login.jsp'">
                    <div class="theme"  onmouseenter="m_over(this);" onmouseleave="m_out(this);">
                        <img width=100% height=280px src="<c:out value='${pageContext.request.contextPath }'/>/resources/images/homeyw.jpg" >
                        <h2 style="text-align:center;color:#333">
                            系统运维
                        </h2>
                    </div>
                </li>
               
              </ul>
</div>

<div id="backgrounddiv" class="backgrounddiv">
	<div id="bgimg" ></div>
<div id="footer" class="footer" style=""></div>
</div>

	<script  type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-1.7.2.js"></script>
	<script type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery.color.js"></script>
<script>

function m_over(obj){
	 $(obj).animate({
		 backgroundColor:'#1471CA',
		 marginTop:'20px'});
	 $(obj).css("borderColor",'#1471CA');
	 $(obj).css("fontColor",'#eee');
	 $(obj).find("h2").css("color",'#eee');
}
function m_out(obj){
	 $(obj).animate({
		 backgroundColor:'#fff',
		 marginTop:'30px'});
	 $(obj).css("borderColor",'#eee');
	 $(obj).css("color",'#333');
	 $(obj).find("h2").css("color",'#333');
}

$(function(){
		var bgH = document.body.clientHeight;
		if(bgH<620)
			bgH = 620;
		$("#backgrounddiv").css("height",bgH);
		$("#bgimg").css("height",bgH-30);
});

</script>
