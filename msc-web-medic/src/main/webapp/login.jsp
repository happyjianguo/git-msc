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
    <link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/login.css" />
    <script  type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.min.js"></script>
    <script type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.md5.js"></script> 
    <script  type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.easyui.min.js"></script>
    
   
   	
<html>
<body>
<div id="head" class="head" style="margin-top:0px">
<img src="<c:out value='${pageContext.request.contextPath }'/>/resources/images/logo2015.png" style="margin-left:30px;margin-top:5px;width:60px;float:left"></img>
<span style="margin-left:30px;font-size:32px; line-height: 70px;">上海药联药品管理系统
<span style="font-size:18px;margin-left:40px;color:#999">系统运维</span>
</span>
</div>

<div id="content" class="content" >
	<div id='loginwin' class="loginwin">
	  <form name="form1" id="form1" action="" method="post">
			<input id="input_emid" value="" name="userName" tabindex="1" />
			<p>
			<input id="input_pswd" value=""  name="password" tabindex="2" />
			<p>
			<input type="hidden" value="59"  name="loginType" />
			<input name="verifyCode" id="verifyCode" />  
				<img id="verifyCodeImage" style="margin-left:15px;vertical-align: middle;height:25px;" title="点我换一张" onclick="reloadVerifyCode()" /><br/>
			<p style="color:#d43f3a;margin-top:5px;font-size:12px;">&nbsp;<c:out value='${FAILMSG}'/></p>
			<br>
			<a  id="btn" href="#" class="ui-button" onclick="javascript:doSubmit()" >登 录</a>
			<select id="UserList" name="userName" style="display:none"></select>
			<input type="hidden" ID="UserSignedData" name="UserSignedData">
			<input type="hidden" ID="UserCert" name="UserCert"> 
		</form>
	  </div>	
  </div>

<div id="backgrounddiv" class="backgrounddiv">
	<img id="bgimg" width="100%"  height="100%" src="<c:out value='${pageContext.request.contextPath }'/>/resources/images/loginbgY.jpg" ></img>
<div id="footer" class="footer" style="color:#999;text-align:center;font-size:10px;line-height: 30px;">COPYRIGHT ©2016-2017 版权所有</div></div>

  <!-- <div style="position:absolute;bottom:20px ;text-align: center;width:100%;color:#fff;">COPYRIGHT ©2016 上海药联信息服务有限公司</div>
 -->
  	
  	<script>
  	
  	function clearAll(){
  	  	$("#input_emid").textbox("setValue","");
  	  	$("#input_pswd").textbox("setValue","");
  	}
  	function doSubmit(){
  	  	if($("#input_emid").textbox("getValue") ==""||$("#input_pswd").textbox("getValue") ==""||$("#verifyCode").textbox("getValue") ==""){
			return;
  	  	}	
  		$("#input_pswd").textbox("setValue",$.md5($("#input_pswd").textbox("getValue")));
  		
  		form1.submit();
  	}

  	
  	function reloadVerifyCode(){  
  	    $('#verifyCodeImage').attr('src', '<c:out value='${pageContext.request.contextPath}'/>/sys/login/vcimage.htmlx?f='+new Date());  
  	}
  	$(function(){
  		reloadVerifyCode();
  		var bgH = document.body.clientHeight;
  		if(bgH<620)
  			bgH = 620;
  		$("#backgrounddiv").css("height",bgH);
  		$("#bgimg").css("height",bgH-30);
  		
  		$("#loginwin").show();
  		$('#input_emid').textbox({
  		    iconCls:'icon-user',
  		    iconAlign:'left',
  		  prompt:'用户名',
  		width:200,
  		height:36
  		  
  		})

  		$("#input_pswd").textbox({
  			 iconCls:'icon-Auth_Mgmt',
   		    iconAlign:'left',
   		 type:'password',
   		  prompt:'',
   		width:200,
   		height:36
  		});
  		$("#verifyCode").textbox({
   		    
   		  prompt:'验证码',
   		width:100,
   		height:30
  		});
  
  		$('#input_emid').textbox('textbox').focus(); 

  	//当浏览器窗口大小改变时，设置显示内容的高度  
        $(document).keydown(function(event){  
        	  if(event.keyCode == 13){  
        		  doSubmit();   
        	  }  
        }); 
  	 })
  	</script>
  	</body></html>