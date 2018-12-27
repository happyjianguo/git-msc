<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="title" type="java.lang.String" required="true" description="页面标题" %>

<!DOCTYPE html>
<html lang="zh-cn">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	<title>${title }</title>
	
	<c:set var="base" value="${pageContext.request.contextPath }"/>
	<link rel="stylesheet" type="text/css" href="${base }/resources/js/jquery-easyui/themes/bootstrap/easyui.css" />
    <link rel="stylesheet" type="text/css" href="${base }/resources/css/base.css" />
    <link rel="stylesheet" type="text/css" href="${base }/resources/js/jquery-easyui/themes/icon.css" />
    
    <script  type="text/javascript" src="${base }/resources/js/jquery-easyui/jquery.min.js"></script>
    <script type="text/javascript" src="${base }/resources/js/jquery-easyui/jquery.md5.js"></script> 
    <script  type="text/javascript" src="${base }/resources/js/jquery-easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="${base }/resources/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script> 
  	<script type="text/javascript" src="${base }/resources/js/jquery-easyui/myVali.js"></script> 
  	<script type="text/javascript" src="${base }/resources/js/jquery-easyui/easyui_more.js"></script> 
  	<script type="text/javascript" src="${base }/resources/js/jquery-easyui/common.js"></script> 
  	<script type="text/javascript" src="${base }/resources/js/jquery-easyui/jquery-dateFormat.min.js"></script>
  	<script type="text/javascript" src="${base }/resources/js/jquery-easyui/treegrid-dnd.js"></script> 
  	<script type="text/javascript" src="${base }/resources/js/jquery-easyui/datagrid-filter.js"></script>
  </head>


  <body>
  
    <script>
  function showMsg(text){
	  top.showMsg(text);
	}

function showErr(text){
	top.showErr(text);
}

  </script>