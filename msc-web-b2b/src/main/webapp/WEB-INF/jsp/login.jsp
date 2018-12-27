<%@page import="com.shyl.common.web.datasource.ApplicationContextHolder"%>
<%@page import="com.shyl.msc.common.CommonProperties"%>
<%@page import="com.shyl.common.web.util.VerifyCodeUtil"%>
<%@ page import="com.shyl.common.cache.dao.VerifyCodeDAO" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<shiro:authenticated>
<script>
location.href = "${pageContext.request.contextPath }/home/index.htmlx";
</script>
</shiro:authenticated>

	<link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/default/easyui.css" />
    <link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/base.css" />
    <link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icon.css" />
    <link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/login.css" />
	<!--[if IE]><link rel="stylesheet" type="text/css" href="<c:out value='${pageContext.request.contextPath }'/>/resources/css/loginIE.css" /> <![endif]-->
    <script  type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.min.js"></script>
    <script type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.md5.js"></script> 
    <script  type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery.cookie.js"></script> 
    <script type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/common.js"></script> 
    
   
   	
<html>
<%
    /* String isCA = CommonProperties.ISCAUSED; */
    String strRandom = VerifyCodeUtil.generateTextCode(0, 4, null);
    
    VerifyCodeDAO verifyCodeDAO = (VerifyCodeDAO)ApplicationContextHolder.getBean("verifyCodeDAO");
    verifyCodeDAO.save("pdfSignRandom/"+request.getSession().getId(), strRandom);
%>
<script>
   var webName = "<c:out value='${pageContext.request.contextPath }'/>";
   var CAName = "<%=CommonProperties.CANAME_JS %>";
</script>
<script  type="text/javascript" src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/ca/ComCA.js"></script>

<body>
<div id="head" class="head" style="margin-top:0px">
<img  id="logoicon" style="margin-left:30px;margin-top:5px;width:60px;float:left"></img>
<span style="margin-left:30px;font-size:32px; line-height: 70px;"><%=CommonProperties.SYSNAME %>
<span style="font-size:18px;margin-left:40px;color:#999">欢迎登录</span>
</span>
</div>

<div id="content" class="content" >
	<div id='loginwin' class="loginwin">
	  <form name="form1" id="form1" action="${pageContext.request.contextPath }/sys/login.htmlx" method="post">
			<input id="input_emid" value="" name="userName" tabindex="1" />
			<p>
			<input id="input_pswd" value=""  name="password" tabindex="2" />
			<p>
			
			<input name="verifyCode" id="verifyCode" />  
				<img id="verifyCodeImage" style="margin-left:15px;vertical-align: middle;height:25px;" title="点我换一张" src="<c:out value='${pageContext.request.contextPath }'/>/sys/login/vcimage.htmlx" onclick="reloadVerifyCode()" /><br/>
			<p style="color:#d43f3a;margin-top:5px;font-size:12px;">&nbsp;<c:out value='${FAILMSG}'/></p>
			<br>
			<a href="javascript:void(0)" id="loginBtn" class="easyui-linkbutton" style="height:40px;background:#0AE;color:#FFF" data-options="width:200" onclick="javascript:doSubmit()">登 录</a>
        	
        	<%
        		String pjc = CommonProperties.DB_PROJECTCODE;
        		if(pjc != null){
        			String[] pjcArr = pjc.split("[,]");
        			if(pjcArr.length == 1){
        	%>
        			<input type="hidden" ID="projectCode" name="projectCode" value="<%=pjcArr[0] %>"> 
					<%}else{ %>
					<a id='projectName' href='javascript:openProjectCode()' class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-defect_define'" style='float:right;margin-top:10px;'>选择地域</a>
					<a id='updatePsw' href='javascript:openUpdatePsw()' class="easyui-linkbutton" data-options="plain:true" style='float:left;margin-top:10px;'>修改密码</a>
					
					<input type="hidden" ID="projectCode" name="projectCode"> 	
					<%} %>
				<%} %>
			<select id="UserList" name="userName" style="display:none"></select>
			<input type="hidden" ID="UserSignedData" name="UserSignedData">
			<input type="hidden" ID="UserCert" name="UserCert"> 
			<input type="hidden" value="1236"  name="loginType" />
		</form>
	  </div>	
  </div>

<div id="backgrounddiv" class="backgrounddiv">
	<img id="bgimg" width="100%"  height="100%" src="<c:out value='${pageContext.request.contextPath }'/>/resources/images/loginbg2.jpg" ></img>
<div id="footer" class="footer" style="color:#999;text-align:center;font-size:10px;line-height: 30px;"></div></div>

  <!-- <div style="position:absolute;bottom:20px ;text-align: center;width:100%;color:#fff;">COPYRIGHT ©2016 上海药联信息服务有限公司</div>
 -->
  	
  	<script>  	
  	var strServerRan = "<%=strRandom %>";
  	function clearAll(){
  	  	$("#input_emid").textbox("setValue","");
  	  	$("#input_pswd").textbox("setValue","");
  	}
  	function doSubmit(){
  	  	if($("#input_emid").textbox("getValue") ==""||$("#input_pswd").textbox("getValue") ==""||$("#verifyCode").textbox("getValue") ==""){
			return;
  	  	}	
  		$("#input_pswd").textbox("setValue",$.md5($("#input_pswd").textbox("getValue")));

  		
		GetUser(function(cert) {
			$("#UserSignedData").val(getSignDate(cert));
			$("#UserCert").val(getBase64(cert));
			form1.submit();
		});

  	}

  	function openProjectCode(){
  		$.modalDialog({
  			title : "选择地域",
  			width : 300,
  			height : 200,
  			iconCls:'icon-defect_define',
  			href : "<c:out value='${pageContext.request.contextPath }'/>/sys/login/projectCodeView.htmlx"
  		});
  	}
  	
  	function openUpdatePsw(){
  		$.modalDialog({
  			title : "修改密码",
  			width : 400,
  			height : 250,
  			iconCls:'icon-defect_define',
  			href : "<c:out value='${pageContext.request.contextPath }'/>/sys/login/updatePsw.htmlx",
  			buttons : [ {
  				text : '保存',
  				iconCls : 'icon-ok',
  				handler : function() {
  					updatePswFunc();
  				}
  			}, {
  				text : '取消',
  				iconCls : 'icon-cancel',
  				handler : function() {
  					top.$.modalDialog.handler.dialog('destroy');
  					top.$.modalDialog.handler = undefined;
  				}
  			}]
  		});
  	}
  	
  	function changePJC(code,name){
  		$("#projectName").linkbutton({text:name+"平台"});
  		$("#projectCode").val(code);
  		$.cookie('projectCode', code);
  		$.cookie('projectName', name);
  	}
  	function reloadVerifyCode(){  
  	    $('#verifyCodeImage').attr('src', '<c:out value='${pageContext.request.contextPath}'/>/sys/login/vcimage.htmlx?f='+new Date());  
  	}
  	$(function(){
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
  		$('#input_emid').textbox('textbox').css({"margin":"5px 13px 13px 18px","padding":"0px","height":25});
  		$('#input_pswd').textbox('textbox').css({"margin":"5px 13px 13px 18px","padding":"0px","height":25});
  		$('#verifyCode').textbox('textbox').css({"margin":"5px 13px 13px 18px","padding":"0px","height":20});

  		$("#loginBtn").find(".l-btn-text").css("fontSize",14);
  		
  		var code = $.cookie('projectCode');
		var name = $.cookie('projectName');
		if(code != null && name != null){
			changePJC(code,name);
		} 
  		
		
		//logininfo
		$.ajax({
			url:"<c:out value='${pageContext.request.contextPath }'/>/sys/login/info.htmlx",
			dataType:"json",
			type:"POST",
			cache:false,
			success:function(data){
				if(data.FOOTER){
					$("#footer").html(data.FOOTER);
				}
				if(data.LOGO){
					$("#logoicon").attr("src","<c:out value='${pageContext.request.contextPath }'/>/resources/images/"+data.LOGO);
				}
			},
			error:function(){
				showErr("出错，请刷新重新操作");
			}
		});	
		
		
  		//当浏览器窗口大小改变时，设置显示内容的高度  
        $(document).keydown(function(event){  
        	  if(event.keyCode == 13){  
        		  doSubmit();   
        	  }  
        }); 
  	 })
  	</script>
  	</body>