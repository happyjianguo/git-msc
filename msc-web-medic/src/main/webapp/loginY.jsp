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
    <script  type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.min.js"></script>
    <script type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.md5.js"></script> 
    <script  type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.easyui.min.js"></script>
<html>
<style>
body {
/* 加载背景图 */

background-image: url(<c:out value='${pageContext.request.contextPath}'/>resources/images/loginbg2.jpg);

/* 背景图垂直、水平均居中 */
background-position: center center;

/* 背景图不平铺 */
background-repeat: no-repeat;

/* 当内容高度大于图片高度时，背景图像的位置相对于viewport固定 */
background-attachment: fixed;

/* 让背景图基于容器大小伸缩 */
background-size: cover;

/* 设置背景颜色，背景图加载过程中会显示背景色 */
background-color: #f5f5f5
}
.ui-button {
    display: block;
    text-align: center;
    text-decoration: none;
    vertical-align: middle;
    cursor: pointer;
    width: 160px;
    padding: 0 20px;
    font-size: 18px;
    line-height: 36px;
    height: 40px;
    color: #fff;
    font-weight: 400;
    font-weight: bold\0;
    _font-weight: bold;
    border: 0;
    outline: 0;
    background: #0AE;
    -webkit-appearance: none;
    -webkit-border-radius: 4px;
    -moz-border-radius: 4px;
    border-radius: 4px;
}

.head{
position: relative;
    -webkit-box-shadow: 0 1px 3px rgba(100,100,100,0.35);
    box-shadow: 0 1px 3px rgba(100,100,100,0.35);
    -webkit-transition: all .3s ease-out 0s;
    transition: all .3s ease-out 0s;
    left: 0;
    top: 0;
    width: 100%;
    z-index: 999;
    height: 70px;
    background: #fff;
    color: #0AE;
}

.footer{
position: fixed;
    height: 30px;
    -webkit-box-shadow: 0 1px 3px rgba(100,100,100,0.35);
    box-shadow: 0 1px 3px rgba(100,100,100,0.35);
    -webkit-transition: all .3s ease-out 0s;
    transition: all .3s ease-out 0s;
    left: 0;
    bottom: 0;
    width: 100%;
    z-index: 999;
    background: #fff;
}


li{
width:33.33%;height:400px;float:left;    
list-style: none;

}
.theme{
border:1px #eee solid;
margin:30px;
background:#fff;
height:340px;
cursor: pointer;
}
h2{
font-size:25px; 
line-height:30px;
}
</style>
<body style="height:100%;min-height: 360px;min-width: 280px;overflow: auto;">
<div id="head" class="head" style=""><span style="margin-left:30px;font-size:36px; line-height: 70px;">深圳市药品交易监管系统<span style="font-size:25px;margin-left:40px;">系统运维</span></span></div>

<div id="content" class="content" >
	<div id='loginwin' style="width:280px;height:300px;position:absolute;left:50%;top:50%;margin-left:125px;margin-top:-160px;background: rgba(0,0,0,.2);">

	  <div style="margin:0 auto ;width:200px;margin-top:40px;">
	  <form name="form1" id="form1" action="" method="post">
			<input id="input_emid" value="" name="userName" tabindex="1" />
			<p>
			<input id="input_pswd" value=""  name="password" tabindex="2" />
<p>
			<input type="hidden" value="5"  name="loginType" />
			<input name="verifyCode" id="verifyCode" />  
				<img id="verifyCodeImage" style="margin-left:15px;vertical-align: middle;height:25px;" title="点我换一张" src="<c:out value='${pageContext.request.contextPath }'/>/vcimage.htmlx" onclick="reloadVerifyCode()" /><br/>
			<p style="color:gold;margin-top:5px;">&nbsp;<c:out value='${FAILMSG}'/></p>
			<br>
			<a  id="btn" href="#" class="ui-button" onclick="javascript:doSubmit()" >系统运维登录</a>
				
			
			
		</form>
	  </div>	
  </div>
</div>

<div id="footer" class="footer" style=""></div>

	
  	
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
  	    $('#verifyCodeImage').attr('src', '<c:out value='${pageContext.request.contextPath}'/>/vcimage.htmlx?f='+new Date());  
  	}
  	$(function(){
  		$("#loginwin").show();
  		$('#input_emid').textbox({
  		    iconCls:'icon-man',
  		    iconAlign:'left',
  		  prompt:'用户名',
  		width:200,
  		height:36
  		  
  		})

  		$("#input_pswd").textbox({
  			 iconCls:'icon-lock',
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
  		
/*   		 $("#loginwin").window({
			title:"用户登录",
			iconCls:'icon-key',
			width:600,
			height:400,
			minimizable:false,
			maximizable:false,
			collapsible:false,
			closable:false,
			draggable:false,
			resizable:false,
			opacity:0.3
		});  */
  		
  	//当浏览器窗口大小改变时，设置显示内容的高度  
        window.onresize=function(){  
			var w = $("#loginwin").width();
			if($(this).width()<w*3){
				$("#loginwin").css("marginLeft",-125);
			}else{
				$("#loginwin").css("marginLeft",125);
			}
			var h = $("#loginwin").height();
			if($(this).height()<h){
				$("#loginwin").css("marginTop",$(this).height()/2*(-1));
			}else{
				$("#loginwin").css("marginTop",-160);
			}
        }  
  	
        $(document).keydown(function(event){  
        	  if(event.keyCode == 13){  
        		  doSubmit();   
        	  }  
        }); 
  	 })
  	 
  	</script>
  	</body>
