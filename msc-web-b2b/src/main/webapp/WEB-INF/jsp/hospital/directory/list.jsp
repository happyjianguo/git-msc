<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<style type="text/css">
.file {
	position: absolute;
	top: 0px;
	left: 0px;
	height: 30px;
	width: 85px;
	filter: alpha(opacity : 0);
	opacity: 0;
	cursor: pointer;
}

.file_form {
	width: 85px;
	height: 20px;
	float: left;
	margin: 0px 5px 0px 0px;
}
.datagrid-cell-c1-GOODSNUM{
	color:#0081c2;
}
</style>
<body class="easyui-layout" >
<form id="fileId" method="POST" enctype="multipart/form-data">
	<input type="file" name="myfile" class="file" id="myfile" />
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
		$.messager.confirm('确认信息', '确认导入资料吗?', function(r){
			if (r){
				$("#fileId").submit();
			}else{
				location.reload(true);
			}
		});
	});
	
	$("#fileId").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/upload.htmlx",
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
				parent.addTab("合同明细", "/hospital/contractDetail.htmlx", null);
				showMsg(result.msg);
			}else{
				showErr(result.msg);
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
		height : $(this).height() - 4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/directory/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'code',title:'药品编码',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.directory.product){
						return row.directory.product.code;
					}
				}
        	},
        	{field:'name',title:'药品名称',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.directory.product){
						return row.directory.product.name;
					}
				}
        	},
        	{field:'genericName',title:'通用名',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.directory.product){
						return row.directory.product.genericName;
					}
				}
        	},

        	{field:'dosageFormName',title:'剂型',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.directory.product){
						return row.directory.product.dosageFormName;
					}
				}},
        	{field:'model',title:'规格',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.directory.product){
						return row.directory.product.model;
					}
				}},
        	{field:'packDesc',title:'包装',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.directory.product){
						return row.directory.product.packDesc;
					}
				}},
        	{field:'unitName',title:'单位',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.directory.product){
						return row.directory.product.unitName;
					}
				}},
        	{field:'producerName',title:'生产企业',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.directory.product){
						return row.directory.product.producerName;
					}
				}},
        	{field:'price',title:'价格（元）',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.directory.price){
						return common.fmoney(row.directory.price);
					}
				}},
			 {field:'gpoName',title:'gpo名称',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.directory.gpoName){
						return row.directory.gpoName;
					}
				}},
        	{field:'vendorName',title:'供应商名称',width:10,align:'center'},
        	{field:'num',title:'数量',width:10,align:'center',editor:{type:'numberbox',options:{validType:'pinteger'}}},
 	        {field:'options',title:'操作',width:10,align:'center',
        		formatter: function(value,row,index){
					return "<a class='dgbtn' href='#' onclick='addDetail("+index+")' class='easyui-linkbutton'>加入</a>";
			}}
   		]],
   		toolbar: [{
			iconCls: 'icon-import',
			text:"导入",
			handler: function(){
				$("#myfile").click();
			}
		},'-',{
			iconCls: 'icon-xls',
			text:"模板下载",
			handler: function(){
				$.messager.confirm('确认信息', '确认下载?', function(r){
					if (r){
						window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/directory/exportExcel.htmlx");
					}
				});
				//location.href = " <c:out value='${pageContext.request.contextPath }'/>/resources/template/orderTemplate.xlsx";
			}
		} ],
		onDblClickRow:function(index,row) {
			
		},
		onClickRow: function(index,row){
			$('#dg').datagrid('beginEdit', index);
		},
	});
	$('#dg').datagrid('enableFilter', 
		[{
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
			 field:'price',
			 type:'text',
			 fieldType:'d#BD'
		},{
			 field:'gpoName',
			 type:'text',
			 fieldType:'d#S'
		},{
			 field:'vendorName',
			 type:'text',
			 fieldType:'dv#S'
		},{
	        field:'num',
	        type:'text',
	    	isDisabled:1
	    },{
	        field:'options',
	        type:'text',
	    	isDisabled:1
	    }]);
	

});


function addDetail(index){
	var row = $("#dg").datagrid("getRows")[index];
	var price = row.directory.price;
	var productId = row.directory.product.id;
	var gpoCode = row.directory.gpoCode;
	var vendorCode = row.vendorCode;
	
	var ed2 = $('#dg').datagrid('getEditor', {index:index,field:'num'});
	if(ed2 == null)
		return;
	var num = ed2.target.val();
	if(num == ""){
		showErr("数量不能为空");
		return false;
	}
	addDetailAjax(productId, price, num, gpoCode, vendorCode);
	
}

function addDetailAjax(productId, price, num, gpoCode, vendorCode){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/add.htmlx",
		data:{
			"productId": productId,
			"price": price,
			"num":num,
			"gpoCode":gpoCode,
			"vendorCode":vendorCode
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				//$('#dg').datagrid('reload');
				showMsg("加入成功");
			} else{
				showErr(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function detailFunc(id){
	top.addTab("报量内容","/hospital/contractDetail.htmlx?contractId="+id);
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

//搜索
function search(val,name){
	var data = {};
	data["query['t#" + name + "_S_LK']"] = val;
	$('#dg').datagrid('load',data);
}
//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/delete.htmlx",
		data:{id:id},
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

function submitOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail.htmlx",
		buttons : [{
			text : '提交',
			iconCls : 'icon-ok',
			handler : function() {
				addContract();
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
function addContract(){
	top.$.modalDialog({
		title : "添加",
		width : 500,
		height : 300,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/add.htmlx",
		buttons : [{
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				
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
</script>