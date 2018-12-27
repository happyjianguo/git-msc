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
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/vendor.htmlx",
		remoteFilter: true,
		columns:[[
        	{field:'vendorCode',title:'供应商编码',width:10,align:'center'},
        	{field:'vendorName',title:'供应商名称',width:10,align:'center'},
        	{field:'price',title:'报价',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}},
        	{field:'producerCode',title:'厂商编码',width:10,align:'center'},
        	{field:'producerName',title:'厂商名称',width:10,align:'center'},
        	{field:'status',title:'是否中标',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.status=='win'){
						return "中标";
					}else if (row.status=='unwin'){
						return "未中标";
					}
				}},
        	{field:'productName',title:'对应药品',width:10,align:'center',
					formatter: function(value,row,index){
						if (row.status=='win'){
							return row.productName;
						}
					}}
   		]],
	    queryParams:{
	    	"projectDetailId":$("#projectDetailId").val()
	    }
	});
	$('#dg').datagrid('enableFilter',[{
		 field:'price',
		 type:'text',
		 isDisabled:1
	}]);
	
}

</script>