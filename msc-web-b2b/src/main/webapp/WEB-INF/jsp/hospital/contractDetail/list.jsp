<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
<div class="single-dg">
	<table id="detailDg"></table>
</div>
<script type="text/javascript">
//初始化
$(function(){
	//datagrid
	$('#detailDg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		border:true,
		height : $(this).height() - 4,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/page.htmlx",
		remoteFilter: true,
		columns:[[
			{field: "ck",checkbox:true},
        	{field:'contract.vendorName',title:'供应商',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.contract){
						return row.contract.vendorName;
					}
				}
        	},
        	{field:'product.code',title:'药品编码',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.code;
					}
				}
        	},
        	{field:'product.name',title:'药品名称',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.name;
					}
				}
        	},
        	{field:'product.genericName',title:'通用名',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.genericName;
					}
				}
        	},
        	{field:'product.dosageFormName',title:'剂型',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.dosageFormName;
					}
				}},
        	{field:'product.model',title:'规格',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.model;
					}
				}},
        	{field:'product.packDesc',title:'包装',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.packDesc;
					}
				}},
        	{field:'product.producerName',title:'生产企业',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.producerName;
					}
				}},
        	{field:'price',title:'单价（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}},
        	{field:'contractNum',title:'合同数量',width:10,align:'right', editor: { type: 'numberbox', options: { validType:'pinteger' } }},
        	{field:'do',title:'操作',width:10,align:'center',
        		formatter: function(value,row,index){
        			return "<a class='dgbtn' href='#' onclick='editCart("+index+")' class='easyui-linkbutton'>修改</a>";
				}}
   		]],
		toolbar: [{
			iconCls: 'icon-ok',
			text:"提交合同",
			handler: function(){
				addContract();	
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delFunc();	
			}
		}],
		onClickRow: function(index,row){
			$('#detailDg').datagrid('beginEdit', index);
		}
	});
	$('#detailDg').datagrid('enableFilter', 
		[{
	        field:'price',
	        type:'text',
	    	isDisabled:1
	    },{
	        field:'contractNum',
	        type:'text',
	    	isDisabled:1
	    },{
	        field:'contractAmt',
	        type:'text',
	    	isDisabled:1
	    }]);
	

});

function addContract(){
	<%
		String dateType = request.getAttribute("dateType") == null?"1":request.getAttribute("dateType").toString();
		if(dateType.equals("0")){
	%>
		$.messager.confirm('确认信息', '您确认要提交合同吗?', function(r) {
			if (r){
				addContractAjax();
			}
		});
		
	<%} else{%>
		var selobjs = $('#detailDg').datagrid('getRows');
		if(selobjs.length == 0){
			showMsg("没有合同明细");
			return;
		} 
		top.$.modalDialog({
			title : "提交合同",
			width : 300,
			height : 150,
			href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/add.htmlx",
			buttons : [{
				text : '保存',
				iconCls : 'icon-ok',
				handler : function() {
					var f = top.$.modalDialog.handler.find("#form1");
					var isValid = f.form('validate');
					if(!isValid)
						return;
					var deadline = f.find("#deadline").combo("getValue");
					top.$.modalDialog.handler.dialog('close');
					addContractAjax(deadline);
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
	<%} %>
}

function addContractAjax(deadline){
	$.ajax({
		url : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/add.htmlx",
		data:{
			"deadline":deadline
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				if(data.success){
					$('#detailDg').datagrid('reload');  
					showMsg("提交成功！");
					successOpen(data.data);
				} 
			} else {
				showMsg(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "下单成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}

function detailFunc(id){
	top.addTab("报量内容","/hospital/contractDetail.htmlx?contractId="+id);
}
//删除
function delFunc(){
	var selobjs = $('#detailDg').datagrid('getSelections');
	if(selobjs == null || selobjs.length ==0){
		showMsg("请选择要删除的数据");
		return;
	}
	var ids = [];
	$(selobjs).each(function() {
		ids.push(this.id);
	});
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(ids);
		}
	});
}

//=============ajax===============
function delAjax(ids){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/delete.htmlx",
		data:{
			"ids":ids
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#detailDg').datagrid('reload');  
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

function editCart(index){
	var row = $("#detailDg").datagrid("getRows")[index];
	
	var cartId = row.CARTID;
	
	var ed = $('#detailDg').datagrid('getEditor', {index:index,field:'contractNum'});
	if(ed == null)
		return;
	var isValid = $(ed.target).textbox('isValid');
	if(!isValid){
		return;
	}	
	var goodsNum = ed.target.val();
	if(goodsNum == ""){
		showErr("数量不能为空");
		return false;
	}
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/update.htmlx",
		data:{
			"id": row.id,
			"goodsNum":goodsNum
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				showMsg("修改成功");
				$("#detailDg").datagrid("reload");
			} else{
				showErr(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
}
</script>

</body>
</html>