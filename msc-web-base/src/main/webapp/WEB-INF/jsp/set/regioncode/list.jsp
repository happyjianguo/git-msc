<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />


<body class="easyui-layout">			
	<div class="single-dg">
		<table  id="dg" ></table>
	</div> 
</body>
</html>

<script>
//初始化
$(function(){

	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'code',title:'地区编码',width:10,align:'center',sortable:true},
		        	{field:'name',title:'地区名称',width:10,align:'center',sortable:true},
		        	{field:'fullName',title:'全称',width:10,align:'center'},
		        	{field:'isDisabled',title:'是否禁用',width:10,align:'center',
			        		formatter: function(value,row,index){
								if (row.isDisabled == 1){
									return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png\" />";
								}
							}
		    		}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addOpen();	
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delFunc();
			}
		}],
		onDblClickRow: function(index,field,value){
			editOpen();
		}
	});

	$('#dg').datagrid('enableFilter', [{
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


//删除
function delFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(selobj.id);
		}
	});
}



//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加地区",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/add.htmlx",
		onLoad:function(){
			
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				f.submit();
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

//弹窗修改
function editOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "修改角色",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/edit.htmlx",
		queryParams:{
			treePath:selrow.treePath
		},
		onLoad:function(){
			if(selrow){
				var f = top.$.modalDialog.handler.find("#form1");
				f.form("load", selrow);
			}
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				f.submit();
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

//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/regioncode/del.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("删除成功！");
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}


</script>