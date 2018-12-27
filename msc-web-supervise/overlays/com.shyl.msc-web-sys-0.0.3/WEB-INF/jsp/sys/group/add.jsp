<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.shyl.sys.entity.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	User currentUser = session.getAttribute("currentUser")==null?new User():(User)session.getAttribute("currentUser");
	int orgType = currentUser.getOrganization().getOrgType();

%>
<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
			<%
		   		String hide = "style='display:none'";
		   		String isRequire = "false";
		   		if(orgType == 5 || orgType == 9){
		   			hide = "";
		   			isRequire = "true";
		   		}
		   	%>
			<tr  <%=hide %>>
		   		<th>机构类型:</th>
		   		<th>
					<input name="orgType" id="orgType">
				</th>
		   </tr>
		   <tr  <%=hide %>>
		   		<th>机构名称:</th>
		   		<th>
		   			<input name="organization.id" id="orgId">
				</th>
		   </tr>
		   
		   <tr>
		   		<th>组名:</th>
		   		<th>
					<input type="text" name="name"  id="name" class="easyui-validatebox  easyui-textbox" data-options="required:true" style="width:200px" >
				</th>
		   </tr>
		</table>
	</form>
</div>



<script>
function toNextCombox(){
	var type = $('#orgType').combobox('getValue');
	if(type == 4){// 供应商c 取资料同 供应商
		type = 2;
	}
	$('#orgId').combogrid("setValue","");
	 $('#orgId').combogrid('grid').datagrid("reload",{
		 "query['t#orgType_I_EQ']":type,
	});
}

//初始化
$(function(){
	//$("input").textbox({validType:'length[0,100]'});
	$('#orgType').combobox({
	    valueField:'label',    
	    textField:'value',
	    width:80,
	    data: [{
			label: '1',
			value: '医院'
		},{
			label: '2',
			value: '供应商'
		},{
			label: '3',
			value: '监管机构'
		},{
			label: '5',
			value: '系统运维'
		},{
			label: '6',
			value: 'GPO'
		}],
		required: <%=isRequire %>,
		editable:false,
		onSelect:function(record){
			toNextCombox();
			
		}
	});
	
	//下拉
	$('#orgId').combogrid({
		idField:'id',    
		textField:'orgName',
		url: '${pageContext.request.contextPath}/sys/organization/list.htmlx',
		 queryParams:{  
			 "query['t#orgType_I_EQ']":"999",
		}, 
		pagination:false,
		pageSize:10,
		pageNumber:1,
	    columns: [[
	        {field:'orgCode',title:'机构编码',width:150},
	        {field:'orgName',title:'机构名称',width:200}
	    ]],
	    width:200,
	    panelWidth:360,
		required: <%=isRequire %>,
		delay:800,
		prompt:"名称模糊搜索",
		keyHandler: {
            query: function(q) {
            	var type = $('#orgType').combobox('getValue');
            	if(type == 4){// 供应商c 取资料同 供应商
            		type = 2;
            	}
                //动态搜索
                $('#orgId').combogrid('grid').datagrid("reload",{"query['t#orgName_S_LK']":q, "query['t#orgType_I_EQ']":type});
                $('#orgId').combogrid("setValue", q);
            }

        },
        onChange:function(newValue, oldValue){
        	
			$("#name").textbox("setValue",$('#orgId').combogrid("getText"));
			
		}
	});
		
	
	$("#form1").form({
		url :"${pageContext.request.contextPath}/sys/group/add.htmlx",
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
				showMsg("新增成功！");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
});
</script>