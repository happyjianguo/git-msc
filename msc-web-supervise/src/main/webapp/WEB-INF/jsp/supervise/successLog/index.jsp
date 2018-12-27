<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="kettle日志" />

<html>

<body class="easyui-layout"  >
	<div class="single-dg">
		<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true" onclick="filterFunc()">数据过滤</a>
		<table  id="dg" ></table>
	</div>
</body>

</html>
<script>
$(function(){
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height:$(this).height()-30,
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/successLog/page.htmlx",
		pageSize:20,
		pageNumber:1,
		pagination:true,
		remoteFilter: true,
		columns:[[{field:'id',title:'ID',width:75,align:'center'},
					{field:'tableName',title:'表名',width:75,align:'center'},
					{field:'orgCode',title:'医院编码',width:100,align:'center'},
					{field:'inRow',title:'输入行数',width:150,align:'center'},
					{field:'saveRow',title:'保存行数',width:250,align:'center'},
					{field:'updateRow',title:'更新行数',width:250,align:'center'},
					{field:'abandonRow',title:'丢弃行数',width:100,align:'center'},
					{field:'pushDate',title:'查询日期',width:100,align:'center'}
		   		]]
	});
	
});
var isFilter=0;
function filterFunc(){
	if(isFilter == 1) return;
	isFilter=1;
	$('#dg').datagrid({remoteFilter: true});
	$('#dg').datagrid('enableFilter', 
			[{field:'tableName',
		        type:'text',
		        options:{
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'tableName');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'accBeginDate',
		                        op: 'LK',
		                        fieldType:'S',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
		        field:'orgCode',
		        type:'text',
		    	isDisabled:0
		    },
		    {
		        field:'id',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'inRow',
		        type:'text',
		       	isDisabled:1
		    },{
		        field:'saveRow',
		        type:'text',
		       	isDisabled:1
		        
		    },{
		        field:'updateRow',
		        type:'text',
		       	isDisabled:1
		        
		    },{
		        field:'abandonRow',
		        type:'text',
		       	isDisabled:1
		        
		    },{
		        field:'pushDate',
		        type:'text',
		       	isDisabled:1
		    }]);
	$('#dg').datagrid('reload');
}

</script>