<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1" method="post">
		<table class="table-bordered">
			<tr>
				<th>医院：</th>
				<th><input id="hospital" name="hospitalCode"/>
					<input type="hidden" name="hospitalName" id="hospitalName" /></th>
			</tr>
			<tr>
				<th>供应商：</th>
				<th><input id="vendor" name="vendorCode" />
					<input type="hidden" name="vendorName" id="vendorName" /></th>
			</tr>
			<tr>
				<th>价格（元）：</th>
				<th>
					<input type="text" name="price" class="easyui-validatebox  easyui-numberbox" data-options="min:0,precision:2,required:true"  />
				</th>
			</tr>
		</table>
		<input type="hidden" name="product.id" value="<c:out value='${productId }'/>">
	</form>


</div>



<script>
	//初始化
	$(function() {

		$('#vendor').combobox({
			url: " <c:out value='${pageContext.request.contextPath }'/>/set/company/list.htmlx",
		    valueField:'code',    
		    textField:'fullName', 
		    queryParams:{
		    	"query['t#isVendor_I_EQ']":1,
		    	"query['t#isDisabled_I_EQ']":0
			},
			required: true,
			editable:false,
			width:200,
			//panelHeight:'auto',
			onSelect: function(record){
				$("#vendorName").val(record.fullName);
			}
		});
		
		$('#hospital').combogrid({
			idField:'code',    
			textField:'fullName', 
			url: " <c:out value='${pageContext.request.contextPath }'/>/set/hospital/page.htmlx",
		    queryParams:{
		    	"query['t#isDisabled_I_EQ']":0
			},
			pagination : true,
			pageSize : 10,
			pageNumber : 1,
			width:200,
			panelWidth:300,
			delay:800,
			columns : [ [ {
				field : 'code',
				title : '编码',
				width : 100
			}, {
				field : 'fullName',
				title : '医院名称',
				width : 180
			}]],//panelHeight:'auto',
			required : true,
			keyHandler : {
				query : function(q) {
					//动态搜索
					$('#hospital')
							.combogrid('grid')
							.datagrid(
									"reload",
									{
										"query['t#fullName_S_LK']" : q
									});
					$('#hospital').combogrid("setValue", q);
				}

			},
			onSelect: function(rowIndex, rowData){
				$("#hospitalName").val(rowData.fullName);
			}
		});
		$('#hospital').combogrid('grid').datagrid('getPager').pagination({
			showPageList : false,
			showRefresh : false,
			displayMsg : "共{total}记录"
		});
		$("#form1")
				.form(
						{
							url : " <c:out value='${pageContext.request.contextPath }'/>/dm/productDetail/add.htmlx",
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
									//top.$.modalDialog.openner.datagrid('reload');
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