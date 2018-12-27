<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<div class="single-dg">
<input type="hidden" id="projectId">
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
		height : 455,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/directory.htmlx",
		remoteFilter: true,
		columns:[[
        	{field:'genericName',title:'通用名',width:10,align:'center'},
        	{field:'rcDosageFormName',title:'推荐剂型',width:10,align:'center'},
        	{field:'dosageFormName',title:'剂型',width:10,align:'center'},
        	{field:'model',title:'规格',width:10,align:'center'},
        	{field:'qualityLevel',title:'质量层次',width:10,align:'center'},
        	{field:'minUnit',title:'最小制剂单位',width:10,align:'center'},
        	{field:'producerNames',title:'生产厂家',width:10,align:'center'},
        	{field:'note',title:'备注',width:10,align:'center'}
   		]],
		onLoadSuccess: function(data) {
			 $.each(data.rows,function(index,row){
				if(row.projectCode == "1"){
					$('#dg').datagrid('selectRow', index);
				}
			}); 
		},
		onClickRow: function(index,row){
			var selobjs = $('#dg').datagrid('getSelections');
  			var isSelected = 0;
  			$.each(selobjs,function(index){
  				if(this.id == row.id){
  					isSelected = 1;
  					return false;
  				}
  			});
  			if(isSelected){
  				addAjax(row);
  			}else{
  				delAjax(row);
  			}
		},
	    queryParams:{
	    	"projectId":$("#projectId").val()
	    }
	});
	$('#dg').datagrid('enableFilter',[{
		 field:'genericName',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'dosageFormName',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'model',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'qualityLevel',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'minUnit',
		 type:'text',
		 fieldType:'t#S'
	}]);
	
}

//=============ajax===============
function addAjax(row){
	var selrow = $('#dg').datagrid('getSelected');
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/add.htmlx",
		data:{
			"projectId":$("#projectId").val(),
			"directoryId":row.id
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				showMsg("新增成功！");
			}else{
				showErr(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function delAjax(row){
	var selrow = $('#dg').datagrid('getSelected');
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/delete.htmlx",
		data:{
			"projectId":$("#projectId").val(),
			"directoryId":row.id
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				showMsg("删除成功！");
			}else{
				showErr(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

</script>