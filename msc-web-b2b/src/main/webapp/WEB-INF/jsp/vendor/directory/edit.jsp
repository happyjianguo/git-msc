<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1" method="post">
		<table class="table-bordered">
			<tr>
				<th>通用名:</th>
				<th><input type="text" class="easyui-textbox" value="<c:out value='${genericName }'/>" style="width: 200px;" readonly="readonly">
				</th>
			</tr>
			<tr>
				<th>药品名称：</th>
				<th><input type="text" name="productName"
					class="easyui-validatebox  easyui-textbox"
					data-options="required:true" style="width: 200px;"></th>
			</tr>
			<tr>
				<th>规格：</th>
				<th><input type="text" id="model" name="model"
					class="easyui-validatebox  easyui-textbox"
					data-options="required:true" style="width: 200px;"></th>
			</tr>
			<tr>
				<th>厂家:</th>
				<th>
				<input type="hidden" id="producerCode" name="producerCode">
				<input id="producerName" name="producerName" style="width: 200px;">
				</th>
			</tr>
			<tr>
				<th>价格（元）：</th>
				<th><input type="text" name="price"
					class="easyui-validatebox  easyui-numberbox"
					data-options="min:0,precision:2,required:true" style="width: 200px;"/></th>
			</tr>
		</table>
		<input type="hidden" name="id" value="">
	</form>


</div>



<script>
	//初始化
	$(function() {
		$('#producerName').combogrid({
			idField:'fullName',    
			textField:'fullName',
			url: " <c:out value='${pageContext.request.contextPath }'/>/set/company/page.htmlx",
			queryParams:{
			    "query['t#isDisabled_I_EQ']":0,
			    "query['t#isProducer_I_EQ']":1
			},
			pagination:true,
			pageSize:20,
			pageNumber:1,
		    columns: [[
				{field:'code',title:'编码',width:100},
		        {field:'fullName',title:'生成企业名称',width:280}
		    ]],
		    width:200,
		    panelWidth:400,
			required: true,
			delay:800,
			keyHandler: {
	            query: function(q) {
	                //动态搜索
	                $('#producerName').combogrid('grid').datagrid("reload",{"query['t#fullName_S_LK']":q,"query['t#isDisabled_I_EQ']":0,"query['t#isProducer_I_EQ']":1});
	                $('#producerName').combogrid("setValue", q);
	            }
	        },
			onSelect : function(rowIndex, rowData) {
				$("#producerCode").val(rowData.code);
			},
		});
		$('#producerName').combogrid('grid').datagrid('getPager').pagination({
			showPageList:false,
			showRefresh:false,
			displayMsg:"共{total}记录"
		});

		$("#form1").form({
			url : " <c:out value='${pageContext.request.contextPath }'/>/tender/directoryVendor/edit.htmlx",
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
				}
				showErr(result.msg);
			},
			error : function() {
				showErr("出错，请刷新重新操作");
			}
		});
	});
</script>