<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout">
	<div class="single-dg">
		<div id="toolbar" class="search-bar" >
			<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true" onclick="auditOpen()">审核</a>
		</div>
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/warehouseTemp/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'externalCode',title:'外部配送点编码',width:10,align:'center'},
        	{field:'name',title:'配送点名称',width:10,align:'center'},
        	{field:'addr',title:'详细地址',width:20,align:'center'},
        	{field:'contact',title:'联系人',width:10,align:'center'},
        	{field:'phone',title:'联系电话',width:10,align:'center'},
        	{field:'longitude',title:'经度',width:10,align:'center'},
        	{field:'latitude',title:'纬度',width:10,align:'center'},
        	{field:'status',title:'类型',width:10,align:'center',
        		formatter: function(value,row,index){
					if (row.status == 'unaudit'){
						return "未审核";
					}else if (row.status == 'audit'){
        				return "审核";
					}
				}
   			},
   			{field:'isDisabled',title:'是否禁用',width:10,align:'center',
        		formatter: function(value,row,index){
					if (row.isDisabled == 1){
						return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png\" />";
					}
				}
   			}
   		]],
		toolbar: "#toolbar",
		onDblClickRow: function(index,field,value){
			//editOpen();
		}
	});

	$('#dg').datagrid('enableFilter', [{
		field:'status',
	       type:'combobox',
	       options:{
	           panelHeight:'auto',
	           editable:false,
	           data:[{value:'',text:'-请选择-'},
	                 {value:'unaudit',text:'未审核'},
	                 {value:'audit',text:'审核'}],
	           onChange:function(value){
	               if (value == '') {
	            	   $('#dg').datagrid('removeFilterRule', 'status');
	               }else {
		               	$('#dg').datagrid('addFilterRule', {
		                       field: 'status',
		                       op: 'EQ',
		                       fieldType:'S',
		                       value: value
		                   });
	              }

	               $('#dg').datagrid('doFilter');
	           }
	       }

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

//搜索
function search(val,name){
	if(name=="name"){
		$('#dg').datagrid('load',{
			"query['t#name_S_LK']": val
		});	
		
	}else if(name=="code"){
		$('#dg').datagrid('load',{
			"query['t#code_S_LK']": val
		});	
	}else if(name=='hospitalName'){
		$('#dg').datagrid('load',{
			"query['t#hospital.fullName_S_LK']": val
		});
	}
	
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



//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 500,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/warehouse/add.htmlx",
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
function auditOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	if(!selrow){
		showMsg("请选中一行");
		return;
	}
	
	top.$.modalDialog({
		title : "修改",
		width : 500,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/warehouseTemp/audit.htmlx",
		queryParams:{
		},
		onLoad:function(){
			if(selrow){
				var f = top.$.modalDialog.handler.find("#form1");
				f.form("load", selrow);
			}
		},
		buttons : [ {
			text : '审核通过并保存配送点',
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/warehouse/delete.htmlx",
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