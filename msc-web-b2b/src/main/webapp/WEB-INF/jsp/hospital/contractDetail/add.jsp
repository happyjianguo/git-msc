<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1" method="post">
		<table class="table-bordered">
			<tr>
				<th>三方合同：</th>
				<th><input name="contract.id" id="contract"
					data-options="required:true" style="width: 150px;"/></th>
			</tr>
			<tr>
				<th>产品：</th>
				<th><input name="product.id" id="product" data-options="required:true" style="width: 150px;" /></th>
			</tr>
			<tr>
				<th>单价：</th>
				<th><input type="text" name="price" id="price"  class="easyui-validatebox  easyui-numberbox"  data-options="required:true,min:0,precision:2"/></th>
			</tr>
			<tr>
				<th>合同数量：</th>
				<th><input type="text" name="contractNum"
					 class="easyui-validatebox  easyui-numberbox" data-options="required:true,min:0,precision:0" /></th>
			</tr>
		</table>
	</form>


</div>

<script>
	//初始化
	$(function() {
		$('#contract').combogrid({
			idField : 'id',
			textField : 'code',
			url : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/page.htmlx",
			pagination : true,
			pageSize : 10,
			pageNumber : 1,
			queryParams:{
			    "query['t#hospitalCode_S_EQ']": '<c:out value='${hospitalCode}'/>',
			    "query['t#status_S_EQ']":'unsigned'
			},
		    panelWidth:500,
			required: true,
			columns: [[
		        {field:'code',title:'合同编号',width:80},
		        {field:'vendorName',title:'供应商名称',width:120},
		        {field:'gpoName',title:'gpo名称',width:120,align:'center'},
		        {field:'startValidDate',title:'有效期起',width:80,align:'center'},
		        {field:'endValidDate',title:'有效期止',width:80,align:'center'},
		        {field:'effectiveDate',title:'生效时间',width:80,align:'center',
					formatter: function(value,row,index){
						if (row.effectiveDate){
							return $.format.date(row.effectiveDate,"yyyy-MM-dd HH:mm");
						}
					}
	        	},
		    ]],
		});
		$('#contract').combogrid('grid').datagrid('getPager').pagination({
			showPageList:false,
			showRefresh:false,
			displayMsg:"共{total}记录"
		});	
		//下拉
		$('#product').combogrid({
				idField : 'ID',
				textField : 'GENERICNAME',
				url : " <c:out value='${pageContext.request.contextPath }'/>/set/directory/combopage.htmlx",
				pagination : true,
				pageSize : 10,
				pageNumber : 1,
				delay:800,
				columns : [ [ {
					field : 'CODE',
					title : '目录编码',
					width : 60
				}, {
					field : 'NAME',
					title : '药品名称',
					width : 100
				},{
					field : 'GENERICNAME',
					title : '通用名',
					width : 100
				},{
					field : 'PRICE',
					title : '单价',
					width : 50
				}, {
					field : 'DOSAGEFORMNAME',
					title : '剂型',
					width : 60,
					align : 'center'
				}, {
					field : 'MODEL',
					title : '规格',
					width : 60,
					align : 'center'
				}, {
					field : 'PACKDESC',
					title : '包装',
					width : 60,
					align : 'center'
				}, {
					field : 'UNITNAME',
					title : '单位',
					width : 60,
					align : 'center'
				}, {
					field : 'PRODUCERNAME',
					title : '生产企业',
					width : 120,
					align : 'center'
				} ] ],
				panelWidth : 700,
				required : true,
				keyHandler : {
					query : function(q) {
						//动态搜索
						$('#product')
								.combogrid('grid')
								.datagrid(
										"reload",
										{
											"query['t#genericName_S_LK']" : q
										});
						$('#product').combogrid("setValue", q);
					}

				},
				onSelect : function(rowIndex, rowData) {
					$("#price").numberbox("setValue",rowData.PRICE);
				}
			});

		$('#product').combogrid('grid').datagrid('getPager').pagination({
			showPageList : false,
			showRefresh : false,
			displayMsg : "共{total}记录"
		});
		$("#form1").form({
			url : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/add.htmlx",
			onSubmit : function() {
				top.$.messager.progress({
					title : '提示',
					text : '数据处理中，请稍后....'
				});
				var isValid = $(this).form('validate');
				if (!isValid) {
					top.$.messager.progress('close');
				}
				return isValid;
			},
			success : function(result) {
				top.$.messager.progress('close');
				result = $.parseJSON(result);
				if (result.success) {
					top.$.modalDialog.openner
							.datagrid('reload');
					top.$.modalDialog.handler.dialog('close');
					showMsg("新增成功！");
				} else {
					showErr(result.msg);
				}
			},
			error : function() {
				showErr("出错，请刷新重新操作");
			}
		});
	});
</script>