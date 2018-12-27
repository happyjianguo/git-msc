<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div style="line-height: 40px;font-size:20px;text-align:center;"><c:out value='${name}'/></div>

<div style="line-height: 30px;font-size:14px;margin-left:10px;">上年采购金额：<c:out value='${qnsum}'/></div>
<div style="line-height: 30px;font-size:14px;margin-left:10px;">今年采购金额：<c:out value='${jnsum}'/></div>
