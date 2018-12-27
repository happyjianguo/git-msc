<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >
		<div data-options="region:'west',title:'国家药品数据'" style="width:50%;background: rgb(238, 238, 238);">
			<table id="dg" class="single-dg"></table>
		</div>
		<div data-options="region:'center',title:'补充材料'" style="width:50%;background: rgb(238, 238, 238);">
			<table id="dg1" class="single-dg" style=""></table>
		</div>
</body>
</html>

<script>
//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:false,
		border:true,
		height : $(this).height()-30,
		pagination:true,
		url:"<c:out value='${pageContext.request.contextPath }'/>/menu/productData/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'productName',title:'药品名称',width:100,align:'center'},
		        	{field:'englishName',title:'英文名',width:80,align:'center'},
		        	{field:'tradeName',title:'商品名',width:75,align:'center'},
		        	{field:'authorizeNo',title:'批准文号',width:80,align:'center'},
		        	{field:'oldAuthorizeNo',title:'原批准文号',width:100,align:'center'},
		        	{field:'splitAuthorizeNo',title:'分包装批准文号',width:100,align:'center'},
		        	{field:'producerName',title:'厂家',width:150,align:'center'},
		        	{field:'producerEName',title:'厂商英文名',width:150,align:'center'},
		        	{field:'dosageFormName',title:'剂型',width:75,align:'center'},
		        	{field:'model',title:'规格',width:150,align:'center'},
		        	{field:'splitProducerName',title:'分包装公司',width:120,align:'center'},
		        	{field:'packDesc',title:'包装规格',width:100,align:'center'},
		        	{field:'drugType',title:'药品性质',width:100,align:'center'},
		        	{field:'standardCode',title:'本位码',width:100,align:'center'}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"启动国产药品同步",
			handler: function(){
				$.ajax({
					url:"<c:out value='${pageContext.request.contextPath }'/>/menu/productData/snyc.htmlx?tableid=25",
					dataType:"json",
					type:"POST",
					cache:false,
					success:function(data) {
						showMsg(data.msg);
					},
					error:function(){
						showErr("出错，请刷新重新操作");
					}
				});	
			}
		},{
			iconCls: 'icon-add',
			text:"启动进口药品同步",
			handler: function(){
				$.ajax({
					url:"<c:out value='${pageContext.request.contextPath }'/>/menu/productData/snyc.htmlx?tableid=36",
					dataType:"json",
					type:"POST",
					cache:false,
					success:function(data) {
						showMsg(data.msg);
					},
					error:function(){
						showErr("出错，请刷新重新操作");
					}
				});	
			}
		},{
			iconCls: 'icon-stop',
			text:"停止同步线程",
			handler: function(){
				$.ajax({
					url:"<c:out value='${pageContext.request.contextPath }'/>/menu/productData/stop.htmlx",
					dataType:"json",
					type:"POST",
					cache:false,
					success:function(data) {
						showMsg(data.msg);
					},
					error:function(){
						showErr("出错，请刷新重新操作");
					}
				});	
			}
		},{
			iconCls: 'icon-add',
			text:"新增到药品库",
			handler: function(){
				addMedicine();
			}
		}],
		onLoadSuccess:function(data) {
			 $.each(data.rows,function(index,row){
				$('#dg').datagrid('beginEdit', index);
			});  
			$('#dg').datagrid('doCellTip',{delay:500}); 
		},onClickRow: function(index,row) {
			$('#dg1').datagrid({
				url:"<c:out value='${pageContext.request.contextPath }'/>/menu/supplement/page.htmlx",
				queryParams:{
					"query['authorizeNo_S_LK']":row.authorizeNo
				}
			});
			$('#dg1').datagrid('enableFilter');
		}
	});
	$('#dg').datagrid('enableFilter');
	$('#dg1').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:false,
		border:true,
		height : $(this).height()-30,
		pagination:true,
		url:"<c:out value='${pageContext.request.contextPath }'/>/menu/supplement/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'productName',title:'药品名称',width:150,align:'center'},
		        	{field:'model',title:'规格',width:150,align:'center'},
		        	{field:'packDesc',title:'包装规格',width:150,align:'center'},
		        	{field:'producerName',title:'厂家',width:150,align:'center'},
		        	{field:'authorizeNo',title:'原批准文号',width:100,align:'center'}
		   		]],
		   		toolbar: [{
					iconCls: 'icon-add',
					text:"启动药品规格同步",
					handler: function(){
						$.ajax({
							url:"<c:out value='${pageContext.request.contextPath }'/>/menu/productData/snyc.htmlx?tableid=63",
							dataType:"json",
							type:"POST",
							cache:false,
							success:function(data) {
								showMsg(data.msg);
							},
							error:function(){
								showErr("出错，请刷新重新操作");
							}
						});	
					}
				},{
					iconCls: 'icon-stop',
					text:"停止同步线程",
					handler: function(){
						$.ajax({
							url:"<c:out value='${pageContext.request.contextPath }'/>/menu/productData/stop.htmlx",
							dataType:"json",
							type:"POST",
							cache:false,
							success:function(data) {
								showMsg(data.msg);
							},
							error:function(){
								showErr("出错，请刷新重新操作");
							}
						});	
					}
				}],
		   		onLoadSuccess:function(data) {
		   		    var selobj = $('#dg').datagrid('getSelected');
					var productName = selobj?selobj.productName:"";
					if (productName) {
						$.each(data.rows,function(index,row) {
							if (productName == row.productName)
								$('#dg1').datagrid("selectRow",index);
						});
					} 
					$('#dg1').datagrid('doCellTip',{delay:500}); 
				}
			});
	$('#dg1').datagrid('enableFilter');
});
function addMedicine() {
	var selrow1 = $('#dg').datagrid('getSelected');
	var selrow2 = $("#dg1").datagrid('getSelected');
	selrow1.model=selrow2.model;
	selrow1.packDesc=selrow2.packDesc;
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 500,
		href : "<c:out value='${pageContext.request.contextPath }'/>/menu/productData/add.htmlx",
		onLoad:function() {
			var f = top.$.modalDialog.handler.find("#form1");
			if(selrow1) {
    			/* f.find("#drugId").val(selrow.drug.id);
    			f.find("#name").val(selrow1.productName); 
    			console.log(f.find("#name").val());*/
				f.form("load", selrow1);
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
</script>