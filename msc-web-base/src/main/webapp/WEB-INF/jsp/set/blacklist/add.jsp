<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div>
	<form id="form1" name="form1"  method="post" >
		<table class="table-bordered">
		   <tr>
				<th>企业：</th>
		   		<th>
		   			<input  id="company" name="company.id"   >
		   		</th>
		   </tr>
		   <tr>
		   		<th>加入原因：</th>
		   		<th>
		   			<input  id="joinReason" name="joinReason" class="easyui-textbox-67" data-options="multiline:true" style="width:200px;height:100px;">
					
				</th>
		   </tr>
		</table>
	</form>


</div>



<script>
//初始化
$(function(){
    $("input.easyui-textbox-67").textbox({validType: 'maxLength[100]'});//暂时控制

	$('#company').combogrid({
		idField:'id',    
		textField:'fullName',
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/company/page.htmlx",
		 queryParams:{
		    	"query['t#isDisabled_I_EQ']":0
			},
		pagination:true,
		pageSize:20,
		pageNumber:1,
	    columns: [[
	        {field:'fullName',title:'企业名称',width:280}
	    ]],
	    width:200,
	    panelWidth:300,
		required: false,
		delay:800,
		keyHandler: {
            query: function(q) {
                //动态搜索
                $('#company').combogrid('grid').datagrid("reload",{"query['t#fullName_S_LK']":q,"query['t#isDisabled_I_EQ']":0});
                $('#company').combogrid("setValue", q);
            }

        }
	});
	
	$('#company').combogrid('grid').datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false,
		displayMsg:"共{total}记录"
	});	 
	
	
	
	$("#form1").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/set/blacklist/add.htmlx",
		onSubmit : function() {
			top.$.messager.progress({
				title : '提示',
				text : '数据处理中，请稍后....'
			});
			var isValid = $(this).form('validate');
			
			if (!isValid) {
				alert(1);
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
				showMsg("新增成功！");
			}else{
				showErr(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
});

</script>