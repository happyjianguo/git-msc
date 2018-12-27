<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />


<body class="easyui-layout"  >
         <div  data-options="region:'west',title:'医疗机构',collapsible:false" style="width:350px;background: rgb(238, 238, 238);padding:2px">
		<table  id="dg" ></table>
         </div>
         
         <div data-options="region:'center',title:'发票清单'" style="background: rgb(238, 238, 238);">
         	<div style="padding:2px;">
		<input type="hidden" id="payDate" />
		<table id="dg2" ></table>
	</div>
         </div>
</body>

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
		height :  $(this).height()-40,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/settle/hospitalPage.htmlx",
		pageSize:10,
		pageNumber:1,
		columns:[[
					{field:'HOSPITALNAME',title:'医院名称',width:40,align:'center'},
		        	{field:'NUM',title:'发票数',width:15,align:'center'}
		   		]],
		onLoadSuccess:function(data){
			var rows=data.rows;
			//大于1行默认选中
			if(rows.length > 0) {
				$(this).datagrid('selectRow', 0);
	        	searchDefectsList(rows[0]);
			}
			$('#dg').datagrid('doCellTip',{delay:500}); 
		},
		onClickRow: function(index,row){
			searchDefectsList(row);  
		}
	});
	
	$('#dg').datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false,
		displayMsg:"共{total}记录"
	});	
	
	//datagrid
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		border:true,
		height :  $(this).height()-40,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		columns:[[
					{field:'CODE',title:'单号',width:20,align:'center'},
					{field:'INTERNALCODE',title:'发票号码',width:20,align:'center'},
					{field:'ORDERDATE',title:'发票日期',width:20,align:'center',
						formatter: function(value,row,index){
							if (row.ORDERDATE){
								return $.format.date(row.ORDERDATE,"yyyy-MM-dd");
							}
						}
					}, 
					{field:'ISRED',title:'是否冲红',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.ISRED == '0'){
								return "否";
							}else {
								return "是";
							}
						}
					},
					/* {field:'SUM',title:'金额（元）',width:10,align:'right',
						formatter: function(value,row,index){
							if (row.SUM){
								if (row.ISRED == '0')
									return common.fmoney(row.SUM);
								else
									return "-"+common.fmoney(row.SUM);
							}
						}}, */
					{field:'SUM',title:'含税金额（元）',width:10,align:'right',
							formatter: function(value,row,index){
								if (row.SUM){
									if (row.ISRED == '1')
										return "-"+common.fmoney(row.SUM);
									else
										return common.fmoney(row.SUM);
								}
							}}
		   		]],
		toolbar: [{
			iconCls: 'icon-ok',
			text:"结算",
			handler: function(){
				addPara();	
			}
		}],
		onDblClickRow: function(index,field,value){
			editOpen();
		},
		onLoadSuccess:function(data){
			$.each(data.rows,function(index,row){
				$('#dg2').datagrid('beginEdit', index);
			}); 
			$('#dg2').datagrid('doCellTip',{delay:500}); 
		}
	});

	
	//弹窗信息
	function addPara() {
		var selrow = $('#dg').datagrid('getSelected');
		var selobjs = $('#dg2').datagrid('getSelections');
		if(selobjs.length == 0){
			showMsg("请至少选择一笔");
			return;
		}
		top.$.modalDialog({
			title : "信息",
			width : 400,
			height : 150,
			href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/settle/para.htmlx",
			onLoad:function(){
			},
			buttons : [ {
				text : '保存',
				iconCls : 'icon-ok',
				handler : function() {
					var f = top.$.modalDialog.handler.find("#form1");
					var isValid = f.form('validate');
					if(!isValid)
						return;
					var payDate = f.find("#payDate").datebox('getValue');
					top.$.modalDialog.handler.dialog('close');
					$("#payDate").val(payDate);
					mksettle();
					
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
	
	
	
});

//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "下单成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/settle/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}

//搜索
function searchDefectsList(row){
	$('#dg2').datagrid({  
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/settle/list.htmlx",
	    queryParams:{
	    	hospitalCode:row.HOSPITALCODE
	    }
	}); 
}
//下单
function mksettle(){
	var selobjs = $('#dg2').datagrid('getSelections');
	if(selobjs.length == 0){
		showMsg("请至少选择一笔");
		return;
	}
	var invoiceids = new Array();
	$.each(selobjs,function(index,row){
	     invoiceids[index] = row.ID;
	}); 
	$.messager.confirm('确认信息', '确定要结算吗?', function(r){
		if (r){
			mksettleAjax(invoiceids);
		}
	});
	
}
//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "结算成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/settle/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}


//=============ajax===============
function mksettleAjax(invoiceids){
	var selrow = $('#dg').datagrid('getSelected');
	var payDate = $("#payDate").val();

	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/settle/mksettle.htmlx",
		data:{
			"hospitalCode":selrow.HOSPITALCODE,
			"invoiceids":invoiceids,
			"accEndDate":payDate
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg2').datagrid('reload');  
				showMsg("结算成功！");
				successOpen(data.data);
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}


</script>