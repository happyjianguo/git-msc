<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >
	 	 <div id="tb" class="search-bar" >
	 	 项目: 
	 	<input class="easyui-combobox"  id="projectId"/>
	状态: 
	 	<input class="easyui-combobox" style="width:100px" id="status"/>
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
        </div>
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>

</body>
</html>
<script>

//初始化
function dosearch(){
	var data;	
	var projectId = $("#projectId").combobox('getValue');
	var status = $("#status").datebox('getValue');
	if(status == 1){
		data = {	
			"query['t#status_L_EQ']": "win",
			"query['t#projectDetail.project.id_L_EQ']": projectId
		};
	}else if(status== 2){
		data = {
			"query['t#status_L_EQ']": "unwin",
			"query['t#projectDetail.project.id_L_EQ']": projectId
		};
	}else if(status== 3){
		data = {
			"query['t#status_L_EQ']": "declare",
			"query['t#projectDetail.project.id_L_EQ']": projectId
		};
	}else{
		data={
			"query['t#status_L_NE']": "undeclare",
			"query['t#projectDetail.project.id_L_EQ']": projectId
		};
	}
	$('#dg').datagrid('load',data);
}
$(function(){
	$("#projectId").combobox({
	    url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEvalResult/projectComb.htmlx",  
	    valueField:'id',    
	    textField:'name',   
	    width:'auto',
	    editable:false, 
	    onSelect:function(data){
	    	/* $("#doneBtn").linkbutton('enable');
	    	$('#dg').datagrid({
	    		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEval/page.htmlx",
	    		queryParams:{  
	    			"query['t#project.id_L_EQ']": data.id
	    		}  
	    	}); */
	    }
	});
	//datagrid
	$('#dg').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectEvalResult/page.htmlx",
		//fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[			
			{field:'projectDetail.project.code',title:'项目编号',width:120,align:'center',
				formatter: function(value,row,index){
					return row.projectDetail.project.code;
			}},
			{field:'projectDetail.project.name',title:'项目名称',width:120,align:'center',
				formatter: function(value,row,index){
					return row.projectDetail.project.name;
			}},
			{field:'projectDetail.project.gpoName',title:'GPO',width:120,align:'center',
				formatter: function(value,row,index){
					return row.projectDetail.project.gpoName;
			}},			
        	{field:'productName',title:'药品名称',width:120,align:'center'},        
        	{field:'model',title:'规格',width:80,align:'center'},		       	
			{field:'producerName',title:'生产企业',width:120,align:'center'},
			{field:'vendorName',title:'供应商',width:120,align:'center'},
			{field:'price',title:'价格',width:80,align:'center'},
			{field:'status',title:'是否中标',width:120,align:'center',
				formatter: function(value,row,index){
					if(row.status == "declare"){
						return "申报";
					}
					if(row.status == "unwin"){
						return "未中标";
					}
					if(row.status == "win"){
						return "中标";
					}
			}}
   		]],
		toolbar: [],
		queryParams:{
			"query['t#status_L_NE']": "undeclare"
		}
	});
	$('#ss').searchbox({
		searcher:function(value, name) {
			dosearch();
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 
 $("#status").combobox({    
	    valueField:'label',    
	    textField:'value',  
	    panelHeight:160,
	    editable:false,
	    data:[{
	    	label: '',
			value: '全部'
		},{
			label: '1',
			value: '中标'
		},{
			label: '2',
			value: '未中标'
		},{
			label: '3',
			value: '申报'
		}],
		onChange:function(value){
			dosearch();
		}
	});

});


</script>