<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="" />
<style>
.datagrid-cell-c1-notes,.datagrid-cell-c1-finalPrice{
	color:#0081c2;
}
</style>
<body class="easyui-layout" >
	<div id="tb" class="search-bar" >
		状态: <input type="text" id="isInProductVendor">
    </div>
	<div  class="single-dg">
	 <table  id="dg"></table>
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
		height :$(this).height()-33,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/productVendor/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'code',title:'药品编码',width:10,align:'center'},
        	{field:'name',title:'药品名称',width:15,align:'center'},
        	{field:'dosageFormName',title:'剂型',width:10,align:'center'},
        	{field:'model',title:'规格',width:20,align:'center'},
        	{field:'packDesc',title:'包装',width:10,align:'center'},
            {field:'authorizeNo',title:'国药准字',width:10,align:'center'},
        	{field:'producerName',title:'生产企业',width:15,align:'center'},
        	{field:'notes',title:'供应商',width:15,align:'center'},
        	{field:'finalPrice',title:'价格',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.finalPrice){
						return common.fmoney(row.finalPrice);
					}
				}}
   		]],
		onDblClickRow: function(index,row){		
			addOpen();
		},
		toolbar: [{
			iconCls: 'icon-ok',
			text:"清除关系",
			handler: function(){
				$.messager.confirm('确认信息', '您确认要清除吗?', function(r){
					if (r){
						doClear();
					}
				});	
			}
		}]
	});
	$('#dg').datagrid('enableFilter',[{
		 field:'code',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'name',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'dosageFormName',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'model',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'packDesc',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'producerName',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'notes',
		 type:'text',
		 isDisabled:1
	},{
		 field:'finalPrice',
		 type:'text',
		 isDisabled:1
	}]);

	//下拉选单
	$('#isInProductVendor').combobox({
	    valueField:'value',    
	    textField:'name',
	    data:[{
	    	value:"",
	    	name:"全部"
	    },{
	    	value:"2",
	    	name:"未设置"
	    },{
	    	value:"1",
	    	name:"已设置"
	    }],
	    onSelect:function(a,b){
	    	$('#dg').datagrid("load",{
	    		isInProductVendor:$('#isInProductVendor').combobox('getValue')
	    	})
	    }
	});

});

//弹窗选择供应商
function addOpen(){
	var selobj = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "选择供应商 : "+selobj.name,
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/productVendor/add.htmlx",
		queryParams:{
			"productId":selobj.id
		},
		buttons : [{
			text : '保存',
			id : 'saveCon',
			iconCls : 'icon-ok',
			handler : function() {
			 	top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				//var f = top.$.modalDialog.handler.find("#form1");
				//f.submit();
                top.addProductDetail();
			}
		}, {
			text : '取消',
			iconCls : 'icon-cancel',
			handler : function() {
				top.$.modalDialog.handler.dialog('destroy');
				top.$.modalDialog.handler = undefined;
			}
		}],
		onLoad:function(){
			
		},
		onDestroy:function(){
			//$('#dg').datagrid("reload");
		}
	});
}
function doClear() {
	var selobj = $('#dg').datagrid('getSelected');
	if (selobj) {
		$.ajax({
			url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productDetail/delete.htmlx",
			data:{id:selobj.projectCode},
			dataType:"json",
			method:"post",
			success:function(data) {
				if(data.success){
					$('#dg').datagrid("reload");
					showMsg(data.msg);
                }else{
					showErr(data.msg);
				}
			}
		})
	}
}
</script>