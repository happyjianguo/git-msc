<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js//My97DatePicker/WdatePicker.js"></script>
<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>年月:</th>
		   		<th>
					<input type="text" id="month" name="month" class="Wdate" onFocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM'})"  />
				</th>			
				<th>药品：</th>
				<th>
					<input id="productId" name="product.id"  data-options="required:true" />
				</th>
			</tr>
			<tr>
		   		<th>短缺原因:</th>
		   		<th colspan="3">
					<input type="text" name="reason" class="easyui-validatebox  easyui-textbox" style="width: 450px" />				
				</th>			
			</tr>
		</table>
	</form>
</div>

<script>
//初始化
$(function(){
	$('#month').validatebox({ 
		required:true 
	});
	//下拉
	$('#productId').combogrid({
		idField:'id',    
		textField:'name',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/product/page.htmlx",
		pagination:true,
		pageSize:10,
		pageNumber:1,
		delay:800,
	    columns: [[
	        {field:'code',title:'药品编码',width:60},
	        {field:'name',title:'药品名称',width:150},
	        {field:'dosageFormName',title:'剂型',width:60,align:'center'}, 
	        {field:'model',title:'规格',width:60,align:'center'},
            {field:'packDesc',title:'包装',width:170,align:'center'},
	        {field:'producerName',title:'生产厂家',width:170,align:'center'}
	    ]],
	    panelWidth:510,
	    panelHeight:310,
		required: true,
		keyHandler: {
            query: function(q) {
                //动态搜索
                $('#productId').combogrid('grid').datagrid("reload",{"query['t#name_S_LK']":q});
                $('#productId').combogrid("setValue", q);
            }

        }
	});
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/lackProduct/add.htmlx",
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
			if(result.success){
				top.$.modalDialog.openner.datagrid('reload');
				top.$.modalDialog.handler.dialog('close');
				showMsg("添加成功");
			}else{
				showMsg(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});

});
</script>