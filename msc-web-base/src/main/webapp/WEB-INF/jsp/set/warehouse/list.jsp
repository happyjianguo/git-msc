<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout">
	<div class="single-dg">
		<div id="toolbar" class="search-bar" >
			<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addOpen()">添加</a>
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/warehouse/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'hospital.fullName',title:'医院',width:10,align:'center',
        		formatter: function(value,row,index){
        			if(row.hospital){
        				return row.hospital.fullName;
        			}
				}
   			},
        	{field:'code',title:'库房编码',width:10,align:'center'},
        	{field:'name',title:'库房名称',width:10,align:'center'},
        	{field:'addr',title:'详细地址',width:20,align:'center'},
        	{field:'contact',title:'联系人',width:10,align:'center'},
        	{field:'phone',title:'联系电话',width:10,align:'center'},
        	{field:'type',title:'类型',width:10,align:'center',
        		formatter: function(value,row,index){
					if (row.type == 'big'){
						return "药库";
					}
        			if (row.type == 'small'){
        				return "药房";
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
			editOpen();
		}
	});

	$('#dg').datagrid('enableFilter', [{
		field:'type',
	       type:'combobox',
	       options:{
	           panelHeight:'auto',
	           editable:false,
	           data:[{value:'',text:'-请选择-'},
	                 {value:'big',text:'药库'},
	                 {value:'small',text:'药房'}],
	           onChange:function(value){
	               if (value == '') {
	            	   $('#dg').datagrid('removeFilterRule', 'type');
	               }else {
		               	$('#dg').datagrid('addFilterRule', {
		                       field: 'type',
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
function editOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "修改",
		width : 500,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/warehouse/edit.htmlx",
		queryParams:{
			"regionCode":selrow.regionCode==null?"":selrow.regionCode
		},
		onLoad:function(){
			if(selrow){
				var f = top.$.modalDialog.handler.find("#form1");
				if(!isempty(selrow.registryDate)){
					selrow.registryDate = $.format.date(selrow.registryDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.openDate)){
					selrow.openDate = $.format.date(selrow.openDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.poutDate)){
					selrow.poutDate = $.format.date(selrow.poutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.outDate)){
					selrow.outDate = $.format.date(selrow.outDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.creditOutDate)){
					selrow.creditOutDate = $.format.date(selrow.creditOutDate,"yyyy-MM-dd");
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