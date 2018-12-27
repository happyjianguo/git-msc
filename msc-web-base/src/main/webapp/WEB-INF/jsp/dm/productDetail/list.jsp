<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<body class="easyui-layout" >
	<div id="tt" class="easyui-tabs" >
	    <div title="药品列表"  class="my-tabs" >
	        <table  id="dg"></table>
	    </div>
	    <div title="供应关系" class="my-tabs" >
	        <form id="fileIdDetail" method="POST" enctype="multipart/form-data" style="display:none;">
				<input type="file" name="myfileDetail" class="file" id="myfileDetail" />
			</form>
			<div id="toolbar" class="search-bar" >
		        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="subaddOpen()">添加</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="delVendorFunc()">删除</a>
		  		<span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-import',plain:true"  onclick='$("#myfileDetail").click()'>导入</a>
		  		<span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true"  onclick="downloadTemp()">模板下载</a>
		    	<span style="float:right">
		    		<b>药品编码：</b><i id="code"></i>
		    		<span class="datagrid-btn-separator split-line" ></span>
		    		<b>药品名称：</b><i id="name"></i>
		    		<span class="datagrid-btn-separator split-line" ></span>
		    		<b>生产企业：</b><i id="producerName"></i>
		    	</span>
		    </div>
		    <table id="dgDetail"></table>
		   
			
			
	    </div>
	</div>
</body>
</html>

<script>
//初始化
$(function(){
	$('#tt').tabs({
		plain:true,
		justified:true
	});
	$("#myfileDetail").change(function(){
		if($(this).val() == "") return;
		$.messager.confirm('确认信息', '确认导入资料吗?', function(r){
			if (r){
				$("#fileIdDetail").submit();
			}else{
				location.reload(true);
			}
		});
	});
	
	$("#fileIdDetail").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/productDetail/import.htmlx",
		onSubmit : function() {
			return true;
		},
		success : function(result) {
			$('#myfileDetail').val("");
			result = $.parseJSON(result);
			if(result.success){
				$('#dg').datagrid('reload');
				showMsg(result.msg);
			}else{
				showMsg(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(this).height()-33,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productDetail/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'code',title:'药品编码',width:10,align:'center'},
        	{field:'name',title:'药品名称',width:10,align:'center'},
        	{field:'genericName',title:'通用名',width:10,align:'center'},
        	{field:'dosageFormName',title:'剂型',width:10,align:'center'},
        	{field:'model',title:'规格',width:10,align:'center'},
        	{field:'packDesc',title:'包装',width:10,align:'center'},
        	{field:'unitName',title:'单位',width:10,align:'center'},
        	{field:'producerName',title:'生产企业',width:10,align:'center'},
        	{field:'gpoName',title:'gpo名称',width:10,align:'center'}
   		]],
		queryParams:{
			"query['a#code_S_LK']":"<c:out value='${projectCode}'/>"
		},
		onDblClickRow: function(index,row){		
			$("#tt").tabs("select",1);
	    	searchDefectsList(row);
		}
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
		 field:'genericName',
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
		 field:'unitName',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'producerName',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'gpoName',
		 type:'text',
		 fieldType:'d#S'
	}]);
	

	$('#dgDetail').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(this).height()-33,
		pagination:true,
		pageSize:10,
		pageNumber:1,
		columns:[[
			{field:'hospitalCode',title:'医院编码',width:50,align:'center'},
			{field:'hospitalName',title:'医院',width:50,align:'center'},
			{field:'vendorCode',title:'供应商编码',width:50,align:'center'},
			{field:'vendorName',title:'供应商',width:50,align:'center'},
			{field:'price',title:'价格（元）',width:50,align:'center',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}}
		]],
		toolbar: $("#toolbar"),
		onLoadSuccess:function(data){
			$.each(data.rows,function(index,row){
				$('#dgDetail').datagrid('beginEdit', index);
			}); 
			$('#dgDetail').datagrid('doCellTip',{delay:500}); 
		}
	
	});
});
//查询
function searchDefectsList(row){
	$("#code").html(row.code);
	$("#name").html(row.name);
	$("#producerName").html(row.producerName);
	
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productDetail/mxlist.htmlx", 
	    queryParams:{
	    	"query['t#product.id_L_EQ']":row.id,
	    }  
	});
}
//模版下载
function downloadTemp(){
	$.messager.confirm('确认信息', '确认下载?', function(r){
		if (r){
			window.open(" <c:out value='${pageContext.request.contextPath }'/>/dm/productDetail/exportExcel.htmlx");
		}
	});
}
//弹窗选择供应商
function subaddOpen(){
	var selobj = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "添加",
		width : 400,
		height : 200,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/productDetail/add.htmlx",
		queryParams:{
			"productId":selobj.id
		},
		buttons : [{
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
		}],
		onLoad:function(){
			
		},
		onDestroy:function(){
			$('#dgDetail').datagrid("reload");
		}
	});
}
function delVendorFunc(){
	var selobj = $('#dgDetail').datagrid('getSelected');
	if(selobj == null){
		showErr("请选择一笔数据");
		return;
	}
	var id= $('#dgDetail').datagrid('getSelected').id;
	
	$.messager.confirm('确认信息', '确认要删除此目录?', function(r){
		if (r){
			delVendorAjax(id);
		}
	});
}
function delVendorAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productDetail/delete.htmlx",
		data:{
			"id":id,
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				showMsg("删除成功！");
				$('#dgDetail').datagrid('reload');
			}else{
				showErr(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
}

//搜索
function query(){
	var data = {};
	data["query['a#code_S_LK']"] = $("#projectCode").textbox("getValue");
	$('#dg').datagrid('load',data);
}
</script>