<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout"  >
	<div  data-options="region:'west',title:'医院',collapsible:false" class="my-west" >
		<shiro:hasPermission name="set:hospital:add">
			<div id="toolbar" class="search-bar" >
		        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addOpen()">添加</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="delFunc()">删除</a>
		    </div>
	    </shiro:hasPermission>
		<table  id="dg" ></table>
	</div>
       
	<div data-options="region:'center',title:'配送点'" class="my-center" >
		<shiro:hasPermission name="set:hospital:add">
			<div id="toolbar2" class="search-bar" >
		        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="subaddOpen()">添加</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="subdel()">删除</a>
		    </div>
	    </shiro:hasPermission>
		<table id="dg2" ></table>
	</div>
</body>
</html>
<script>
//搜索
function search2(){
	var selrow = $('#dg').datagrid('getSelected');
	$('#dg2').datagrid({  
	    url:"<c:out value='${pageContext.request.contextPath }'/>/set/warehouse/queryByHospital.htmlx",  
	    queryParams:{
	        id:selrow.id
	    }
	}); 
}

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
//初始化
$(function(){
	
	//医院设置
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-35,
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
		toolbar: "#toolbar",
		onClickRow: function(index,row){
			search2();  
		},
		onDblClickRow: function(index,field,value){
			editOpen();
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
	
	//药房设置
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		editable:true,
		height :  $(this).height()-35,
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
					{field:'code',title:'编码',width:30,align:'center'},
					{field:'name',title:'名称',width:50,align:'center'},
					{field:'isDisabled',title:'是否禁用',width:20,align:'center',
		        		formatter: function(value,row,index){
							if (row.isDisabled == 1){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png\" />";
							}
						}
	    			}
		   		]],
		toolbar: "#toolbar2",
		onDblClickRow: function(index,field,value){
			subeditOpen();
		}
	});

});

//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加医疗机构",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/hospital/add.htmlx",
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
		title : "编辑医疗机构",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/hospital/edit.htmlx",
		queryParams:{
			"regionCode":selrow.regionCode==null?"":selrow.regionCode,
		},
		onLoad:function(){
			if(selrow){
				var f = parent.$.modalDialog.handler.find("#form1");
				if(!isempty(selrow.registryDate)){
					selrow.registryDate = $.format.date(selrow.registryDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.openDate)){
					selrow.openDate = $.format.date(selrow.openDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.hOutdate)){
					selrow.hOutdate = $.format.date(selrow.hOutdate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.outDate)){
					selrow.outDate = $.format.date(selrow.outDate,"yyyy-MM-dd");
				}
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

//弹窗增加
function subaddOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	
	if(selrow == null){
		$.messager.alert('提示','请先选择设置主档!','info');
		return;
	}
	top.$.modalDialog({
		title : "添加药房",
		width : 400,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/warehouse/add2.htmlx?hospitalCode="+selrow.code,
		onLoad:function(){
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg2');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				var pid = top.$.modalDialog.handler.find("#pid");
				
				pid.val(selrow.id);
				
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
function subeditOpen() {
	top.$.modalDialog({
		title : "编辑药房",
		width : 400,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/warehouse/edit2.htmlx",
		onLoad:function(){
			var selrow = $('#dg2').datagrid('getSelected');
			if(selrow){
				var f = parent.$.modalDialog.handler.find("#form1");
				f.form("load", selrow);
			}
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg2');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
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
//主档删除
function del(){

	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		$.messager.alert('错误','没有选中行!','info');
		return;
	}
	var id= selobj.id;
	
	$.messager.confirm('确认信息', '确认要删除此属性?', function(r){
		if (r){
			delAjax(id);
		}
	});
}

//明细删除
function subdel(){

	var selobj = $('#dg2').datagrid('getSelected');
	if(selobj == null){
		$.messager.alert('错误','没有选中行!','info');
		return;
	}
	var id= selobj.id;
	
	$.messager.confirm('确认信息', '确认要删除此明细?', function(r){
		if (r){
			subdelAjax(id);
		}
	});
}

//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/hospital/delete.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){				
				$('#dg').datagrid('reload');
				$('#dg2').datagrid('reload');
				showMsg(data.msg);
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
function subdelAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/warehouse/delete.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg2').datagrid('reload');
				showMsg(data.msg);
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>