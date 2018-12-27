<%@page import="com.shyl.sys.entity.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	User currentUser = session.getAttribute("currentUser")==null?new User():(User)session.getAttribute("currentUser");
	int orgType = currentUser.getOrganization().getOrgType();
%>
<div>
	<form id="form1" name="form1"  method="post">
		<table class="table-bordered">
		   <tr>
		   		<th>帐号:</th>
		   		<th>
					<input type="text" name="empId" class="easyui-validatebox easyui-textbox" data-options="required:true" style="width:200px" >
					
				</th>
				<th>邮箱:</th>
		   		<th>
		   			<input type="text" name="mail" class="easyui-validatebox easyui-textbox" data-options="validType:'email'" style="width:200px" >
		   		</th>
		   </tr>
		      <tr>
		   		<th>姓名:</th>
		   		<th>
					<input type="text" name="name" class="easyui-validatebox easyui-textbox" data-options="required:true" style="width:200px">
				</th>
				<th>职务:</th>
		   		<th>
		   			<input type="text" name="title" class="easyui-textbox" style="width:200px">
		   		</th>
		   </tr>
		      <tr>
		   		<th>手机:</th>
		   		<th>
					<input type="text" name="cell" class="easyui-validatebox easyui-textbox" data-options="validType:'mobile'"  style="width:200px">
				</th>
				<th>座机:</th>
		   		<th>
					<input type="text" name="ext" class="easyui-validatebox easyui-textbox" data-options="validType:'phone'"  style="width:200px">
				</th>
		   	</tr>
		   	<%
		   		String hide = "style='display:none'";
		   		String isRequire = "false";
		   		if(orgType == 5 || orgType == 9){
		   			hide = "";
		   			isRequire = "true";
		   		}
		   			
		   	%>
		    <tr <%=hide %>>
		   		<th>所属机构:</th>
		   		<th colspan="3">
		   			<input name="orgType" id="orgType">
		   			<input name="organization.id" id="orgId">
		   		</th>
		   		
		   	</tr>
		   	
		   	<tr>
		   		<th>CA证书码:</th>
		   		<th >
		   			<input type="text" name="clientCert" id="clientCert"  class="easyui-textbox" style="width:200px">
		   			<a href="#"  class="easyui-linkbutton" data-options="plain:true"  onclick="getCert()">获取证书</a>
		  		
		   		</th>
		   		<th>身份证:</th>
		   		<th >
		   			<input type="text" name="idcard" class="easyui-textbox" style="width:200px">
		   		</th>
		   	</tr>
		   	<%
		   	if(orgType == 5 || orgType == 9){
		   	
		   	%>
		   	<tr>
		   		<th >是否机构管理员:</th>
		   		<th colspan="3">
		   			<input type="checkbox" name="isAdmin" value="1">
		   		</th>
		   		
		   	</tr>
		   	<%} %>
		</table>
		<select id="UserList" name="userName" style="display:none"></select>
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
		 "query['t#orgType_I_EQ']":type
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
			 "query['t#orgType_I_EQ']":"999"
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

        }
	});
	
	$('#orgId').combogrid('grid').datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false,
		displayMsg:"共{total}记录"
	});	
	
	/* $('#orgId').combobox({
		editable:false
	}); */
	
	$("#form1").form({
		url :"${pageContext.request.contextPath}/sys/user/add.htmlx",
		onSubmit : function() {
			//$("#pwd").textbox('setValue',$.md5($("#pwd").textbox('getValue')));
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
				showMsg(result.msg);
			}else{
				showMsg(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});


	//弹窗修改
	function editOpen() {
		top.$.modalDialog({
			title : "编辑成员",
			width : 600,
			height : 400,
			href : "${pageContext.request.contextPath}/sys/user/edit.htmlx",
			onLoad:function(){
				var selrow = $('#dg').datagrid('getSelected');
				if(selrow){
					var f = parent.$.modalDialog.handler.find("#form1");
					f.form("load", selrow);
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
});

//弹窗修改
function editOpen() {
	top.$.modalDialog({
		title : "编辑成员",
		width : 600,
		height : 400,
		href : "${pageContext.request.contextPath}/sys/user/edit.htmlx",
		onLoad:function(){
			var selrow = $('#dg').datagrid('getSelected');
			if(selrow){
				var f = parent.$.modalDialog.handler.find("#form1");
				f.form("load", selrow);
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
/*
获取证书Base64的内容并显示
*/
function getCert() {
	GetUser(function(cert) {
	    $('#clientCert').textbox("setValue", getCertOid(cert));
	})
}
</script>