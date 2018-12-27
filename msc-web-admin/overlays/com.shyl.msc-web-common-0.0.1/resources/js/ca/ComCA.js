document.writeln("<script src='"+webName+"/resources/js/ca/"+CAName+".js'></script>"); 

//获取证书列表
function GetUser(callback) {
	try{
		if(CAName=="Sgl_CA"){
			   var userList = GetUserList();
			   if (userList.length > 1) {
				   newCertDialog(userList, callback);
			   } else {
				   callback(userList[0]);
			   }
		} else {
			if (callback) callback();
		}
	}catch (e) {
	}
}

/*
获取证书Base64的内容并显示
*/
function getBase64(cert) {
	try{
		if(CAName=="Sgl_CA"){
			return GetUserCert(cert.sn);
		}
	}catch (e) {}
	return "";
}
/*
获取签名
*/
function getSignDate(cert) {
	try{
		if(CAName=="Sgl_CA"){
			return SignData(cert.sn, strServerRan);
		}
	}catch (e) {
	}
	return "";
}

/**
 * 创建CA列表页面
 * @param userList
 * @param callback
 * @returns
 */
function newCertDialog(userList,callback) {
	var layout = $("<div class='cert_layout'></div>");
	var certsDiv = $("<div class='cert_content'></div>");
	layout.css({"position":"absolute","top":"0","left":"0","right":"0","bottom":"0","z-index":"9991","background":"#000","filter":"alpha(opacity=10)","opacity":"0.1"});
	certsDiv.css({"position":"absolute","top":"30%","left":"40%","width":"360px","min-height":"100px;","background":"white","z-index":"9999",
	   "border":"1px solid #ccc","padding":"20px 10px 10px 10px","border-radius":"5px"
	   });
	var certUl = $("<ul></ul>");
	for (var i = 0; i < userList.length; i++) {
	   var user = userList[i];
	   var certLi = $("<li></li>");
	   var certIcon = $("<img/>")
	   var certInfo = $("<div></div>");
	   var p0 = $("<p>所有者："+user.name+"</p>");
	   var p1 = $("<p>颁发者：<i>&nbsp;</i>"+(user.type ==2?"北京数字认证股份有限公司":"广东省电子商务认证有限公司")+"</p>");
	   p0.css({"padding":"0 0 0 0","margin":"0 0 0 0"});
	   p1.css({"padding":"0 0 0 0","margin":"0 0 0 0"});
	   p1.find("i").css({"padding-left":"15px","background":"url('"+webName+"/resources/images/"+(user.type ==2?"BJCA":"NETCA")+".ico') no-repeat ","background-size":"15px 15px"});
	  
	   certIcon.attr("src", webName+"/resources/images/cert.png");
	   certIcon.css({"width":50,"height":50,"float":"left","margin":"5px"});
	   
	   certInfo.css({"width":250,"line-height":"25px","float":"left","padding":"5px"});
	   certInfo.append(p0).append(p1);
	   
	   certLi.attr(user);
	   certLi.css({"padding":"5px 5px 5px 5px","margin":"0 0 0 0","height":60,"width":350,"border-bottom":"1px dashed #eee"});
	  
	   certLi.hover(function() {
		   $(this).css({"background":"#eee","text-decoration":"underline"});
	   },function(){
		   $(this).css({"background":"none","text-decoration":"none"});
	   })
	   certLi.click(function() {
		   var cert = userList[$(this).index()];
		   callback(cert);
		   $(".cert_layout,.cert_content").remove();
	   });
	   certLi.append(certIcon);
	   certLi.append(certInfo);
	   certUl.append(certLi);
	   
	}
	certUl.css({"padding":"0 0 0 0","margin":"0 0 0 0"});
	certsDiv.append(certUl);
	var close = $("<div style='line-height:25px;width:95%;text-align:right;'><span style='cursor:pointer;'>取消</span></div>");
	close.find("span").click(function(){
	   $(".cert_layout,.cert_content").remove();
	});
	certsDiv.append(close);
	$(document.body).append(layout);
	$(document.body).append(certsDiv);
}

/**
 * 根据oid获取证书信息
 * @param oid
 * @returns
 */
function getCertByOid(oid) {
	var cert = null; 
	if (CAName=="Sgl_CA") {
		var userList = GetUserList();
		if (userList.length > 0) {
			for (var i = 0; i < userList.length; i++) {
				var user = userList[i];
				if (user.type == 2) {
					var o = B_GetCertId(user.sn);
					if (o == oid) {
						cert = user;
						break;
					}
				} else {
					var o = W_GetCertId(user.sn);
					if (o == oid) {
						cert = user;
						break;
					}
				}
			}
		}
	}
	return cert;
}
/**
 * 获取选中证书的id
 * @param cert
 * @returns
 */
function getCertOid(cert) {
	try{
		if(CAName=="Sgl_CA"){
			return GetCertId(cert.sn);
		}
	}catch (e) {
		throw e;
	}
}