<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout"  >
	<div class="single-dg" >
		<table  id="dg" ></table>
	</div>
</body>
</html>
<script>
//初始化
$(function(){
	
	//医院设置
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/hospital/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'code',title:'单位编码',width:20,align:'center'},
        	{field:'fullName',title:'单位名称',width:20,align:'center'},
        	{field:'wbcode',title:'地区',width:20,align:'center'},
        	{field:'isDisabled',title:'是否禁用',width:10,align:'center',
        		formatter: function(value,row,index){
					if (row.isDisabled == 1){
						return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png\" />";
					}
				}
   			}
   		]],
		onClickRow: function(index,row){
		},
		onDblClickRow: function(index,field,value){
		}
	});
	
	$('#dg').datagrid('enableFilter', [{
		 field:'wbcode',
		 type:'text',
		 isDisabled:1
	},{
		field:'isDisabled',
	       type:'combobox',
	       options:{
	           panelHeight:'auto',
	           editable:false,
	           data:[{value:'',text:'-请选择-'},
	                 {value:'0',text:'未禁用'},
	                 {value:'1',text:'禁用'}],
	           onChange:function(value){
	               if (value == '') {
	            	   $('#dg').datagrid('removeFilterRule', 'isDisabled');
	               }else {
		               	$('#dg').datagrid('addFilterRule', {
		                       field: 'isDisabled',
		                       op: 'EQ',
		                       fieldType:'I',
		                       value: value
		                   });
	              }
	              $('#dg').datagrid('doFilter');
	           }
	       }
   }]);

});
</script>