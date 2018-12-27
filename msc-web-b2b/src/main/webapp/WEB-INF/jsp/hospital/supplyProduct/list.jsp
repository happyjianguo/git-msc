<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<style type="text/css">
.datagrid-toolbar .file{
    position: absolute;
    right: 0px;
    top: 0px;
    opacity: 0;
    height:30px;
    width:70px;
    -ms-filter: 'alpha(opacity=0)';
    filter:alpha(opacity=0); 
    cursor: pointer;
    font-size:0;
}
</style>
<html>

<body class="easyui-layout">
	<div class="single-dg">
		<table id="dg"></table>
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
		singleSelect:false,
		rownumbers:true,
		border:true,
		height :  $(this).height() -4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/supplyProduct/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		            {field: "ck",checkbox:true},
		        	{field:'MONTH',title:'年月',width:10,align:'center'},
		        	{field:'CODE',title:'药品编码',width:10,align:'center'},
		        	{field:'NAME',title:'药品名称',width:10,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
					
		        	{field:'MODEL',title:'规格',width:10,align:'center'},
		        	{field:'PACKDESC',title:'包装规格',width:10,align:'center'},
		        	{field:'PRODUCERNAME',title:'生产厂家',width:10,align:'center'},
		        	{field:'VENDORCODE',title:'供应商编码',width:10,align:'center'},	
		        	{field:'VENDORNAME',title:'供应商名称',width:20,align:'center'}	,
		        	{field:'INTERNALCODE',title:'是否对照',width:10,align:'center',
		        		formatter: function(value,row,index){
		        			if (row.INTERNALCODE !=null){
		    					return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
		    				}
		    		}}
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
		},'-',{
			iconCls: 'icon-import',
			id:'fileImport',
			text:"导入",
			handler: function(){
			}
		},'-',{
			iconCls: 'icon-xls',
			text:"模板下载",
			handler: function(){
				window.open(" <c:out value='${pageContext.request.contextPath }'/>/resources/template/supplyProductTemplate.xls");			
			}
		}],
		onLoadSuccess:function(data){
			if ($("#fileId").length > 0) {
				return;
			}
			$("#fileImport").find("span:first").append('<form id="fileId" method="POST" enctype="multipart/form-data"><input type="file" name="myfile" class="file" id="myfile"/></form>');
			$("#myfile").on("change",function(){
				if($(this).val() == "") return;
				$.messager.confirm('确认信息', '确认导入资料么?', function(r){
					if (r){
						$("#fileId").submit();
					}else{
						location.reload(true);
					}
				});
			});
			$("#fileId").form({
				url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/supplyProduct/upload.htmlx",
				onSubmit : function() {
					top.$.messager.progress({
						title : '提示',
						text : '数据导入中，请稍后....'
					});
					return true;
				},
				success : function(result) {
					$('#myfile').val("");
					top.$.messager.progress('close');
					result = $.parseJSON(result);
					if(result.success){
						$('#dg').datagrid('reload');
						showMsg(result.msg);
					}else{
						showErr(result.msg);
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
			});
		}
	});
	$('#dg').datagrid('enableFilter', [{
		 field:'MONTH',
		 type:'text',
		 fieldType:'l#S'
	},{
		 field:'HOSPITALNAME',
		 type:'text',
		 fieldType:'l#S'
	},{
		 field:'CODE',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'NAME',
		 type:'text',
		 fieldType:'p#S'
	},{
	     field:'DOSAGEFORMNAME',
		 type:'text',
		 fieldType:'p#S'
	},{
	     field:'MODEL',
		 type:'text',
		 fieldType:'p#S'
	},{
	     field:'PACKDESC',
		 type:'text',
		 fieldType:'p#S'
	},{
	     field:'PRODUCERNAME',
		 type:'text',
		 fieldType:'p#S'
	},{
	     field:'VENDORCODE',
		 type:'text',
		 fieldType:'p#S'
	},{
	     field:'VENDORNAME',
		 type:'text',
		 fieldType:'p#S'
	},{
		field:'INTERNALCODE',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
            	if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'INTERNALCODE');
                } else {
                	var op = (value == "1"?"NOT":"IS");
                	$('#dg').datagrid('addFilterRule', {
                        field: 'INTERNALCODE',
                        op: op,
                        fieldType:'NULL',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	}]);
	
});
//弹窗新增
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/supplyProduct/add.htmlx",
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

//删除
function delFunc(){
	var selobjs = $('#dg').datagrid('getSelections');
	if(selobjs == null || selobjs.length ==0){
		showMsg("请选择要删除的数据");
		return;
	}
	var ids = [];
	$(selobjs).each(function() {
		ids.push(this.ID);
	});
	if(selobjs == null){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(ids);
		}
	});
}

//=============ajax===============
function delAjax(ids){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/supplyProduct/delete.htmlx",
		data:{
			"ids":ids
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("删除成功！");
			} else{
				showMsg("该数据已被使用，无法删除");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}
</script>