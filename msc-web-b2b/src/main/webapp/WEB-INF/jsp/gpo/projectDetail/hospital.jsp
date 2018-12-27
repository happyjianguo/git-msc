<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<div class="single-dg">
<input type="hidden" id="projectDetailId">
	<table  id="dg" ></table>
</div>

<script>
//搜索
function search(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		border:true,
		height : 355,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/hospital.htmlx",
		remoteFilter: true,
		columns:[[
        	{field:'hospitalCode',title:'医院编码',width:10,align:'center'},
        	{field:'hospitalName',title:'医院名称',width:10,align:'center'},
        	{field:'num',title:'数量',width:10,align:'center'}
   		]],
	    queryParams:{
	    	"projectDetailId":$("#projectDetailId").val()
	    }
	});
	$('#dg').datagrid('enableFilter',[{
		 field:'num',
		 type:'text',
		 isDisabled:1
	}]);
	
}

</script>