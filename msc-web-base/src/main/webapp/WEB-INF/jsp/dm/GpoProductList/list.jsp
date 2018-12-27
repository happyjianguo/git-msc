<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
<form id="fileId" method="POST" enctype="multipart/form-data">
	<input type="file" name="myfile" class="file" id="myfile" style="display:none"/>
</form>
<div class="single-dg">
	<table  id="dg" ></table>
</div>

</body>
</html>

<script>
//初始化
$(function(){
	$("#myfile").change(function(){
		if ($("#myfile").val() == "") return;
		$.messager.confirm('确认信息', '确认导入遴选目录么?', function(r){
			if (r){
				$("#fileId").submit();
			}else{
				location.reload(true);
			}
		});
	});
	
	$("#fileId").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/GpoProductList/imp.htmlx",
		onSubmit : function() {
			return true;
		},
		success : function(result) {
			$('#myfile').val("");
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
		singleSelect:false,
		rownumbers:true,
		border:true,
		height : $(this).height() - 4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/GpoProductList/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		    {field:'product.code',title:'药品编码',width:10,align:'center',
        		formatter: function(value,row,index){
        			return row.product.code
        		}
		    },
        	{field:'product.name',title:'药品名称',width:10,align:'center',
        		formatter: function(value,row,index){
        			return row.product.name;
        	}},
        	{field:'product.model',title:'规格',width:10,align:'center',
           		formatter: function(value,row,index){
           			return row.product.model;
           		}},
        	{field:'product.packDesc',title:'包装规格',width:10,align:'center',
           		formatter: function(value,row,index){
           			return row.product.packDesc;
          	}},
        	{field:'product.producerName',title:'生产厂家',width:10,align:'center',
           		formatter: function(value,row,index){
           			return row.product.producerName;
          	}},
        	{field:'vendorName',title:'供应商名称',width:10,align:'center'},
        	{field:'price',title:'药品价格',width:10,align:'center'}
   		]],
		toolbar: ['-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delFunc();	
			}
		},'-',{
			iconCls: 'icon-import',
			text:"导入",
			handler: function(){
				$("#myfile").click();
			}
		},'-',{
			iconCls: 'icon-import',
			text:"导出模板",
			handler: function(){
				window.open(" <c:out value='${pageContext.request.contextPath }'/>/dm/GpoProductList/exp.htmlx");
			}
		}]
	});
	$('#dg').datagrid('enableFilter', 
			[{
		        field:'product.model',
		        type:'text',
		        isDisabled:1
		    },{
		    	field:'price',
		        type:'text',
		    	isDisabled:1
		    }]);
	
});


//删除
function delFunc(){
	var rows = $('#dg').datagrid('getSelections');
	if(rows == null || rows.length == 0){
		return;
	}
	var arr = [];
	$(rows).each(function() {
		arr.push(this.id);
	});
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(arr);
		}
	});
}

//=============ajax===============
function delAjax(ids){
	$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/dm/GpoProductList/delete.htmlx",
				data:{idStr:ids.join(",")},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dg').datagrid('reload');  
						showMsg("删除成功！");
					} else {
						showMsg(data.msg);
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}

</script>