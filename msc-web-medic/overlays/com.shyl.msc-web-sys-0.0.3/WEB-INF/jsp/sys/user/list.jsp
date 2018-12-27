<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<style type="text/css">
	.file {
		position: absolute;
		top: 0px;
		left: 0px;
		height: 30px;
		width: 85px;
		filter: alpha(opacity : 0);
		opacity: 0;
		cursor: pointer;
	}
	
	.file_form {
		width: 85px;
		height: 20px;
		float:left;
		margin: 0px 5px 0px 0px;
	}
</style>

<html>
<script src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>

<body class="easyui-layout" >
    
    
<!-- <div class="search-bar">

	<input id="ss" class="easyui-searchbox" style="width: 218px;display:none" 
	data-options="
		menu:'#mm',
		prompt:'支持模糊搜索',
		searcher:function(value,name){
	        search(value,name);
	    }" />
	    <div id="mm">
		<div data-options="name:'empId' ">帐号</div>
		<div data-options="name:'name'">用户名</div>
		<div data-options="name:'cell'">手机</div>
	</div>  -->
	 <%-- <a id="check"  class="easyui-linkbutton"  > 
	    <form  id="fileId" style="position: relative;" method="POST" enctype="multipart/form-data" >
			<img width="14" height="15" style="margin-bottom:-3px; margin-left:6px;"  src="${pageContext.request.contextPath}/resources/js/jquery-easyui/themes/icons2/import.png"></img>
			<font style="font-size: 12px;margin-left:4px;margin-top:-2px;">导入</font>
			<input type="file" name="myfile" class="file" id="myfile"/>
			&nbsp;
		</form> 
	</a>
	 <a   class="easyui-linkbutton" href="${pageContext.request.contextPath}/resources/template/userTp.xls" >模版下载  </a>  --%>
</div>
<div class="single-dg">
<table  id="dg"   ></table>
</div>



</body>
</html>
    
<script>

//删除
function delFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		$.messager.alert('错误','没有选中行!','info');
		return;
	}
	var id= $('#dg').datagrid('getSelected').id;
	
	$.messager.confirm('确认信息', '确认要删除此成员?', function(r){
		if (r){
			delAjax(id);
		}
	});
}

//重置密码
function resetPwd(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	var id= selobj.id;
	var empId = selobj.empId;
	$.messager.confirm('确认信息', '确认要重置密码吗?', function(r){
		if (r){
			resetPwdAjax(id,empId);
		}
	});
	
}
//手动解锁
function unlockFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	var id= selobj.id;
	var empId = selobj.empId;
	$.messager.confirm('确认信息', '确认要解锁帐号吗?', function(r){
		if (r){
			unlockAjax(id,empId);
		}
	});
	
}
//禁用
function disabledFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	var id= selobj.id;
	var empId = selobj.empId;
	$.messager.confirm('确认信息', '确认要禁用帐号吗?', function(r){
		if (r){
			disabledAjax(id,empId);
		}
	});
	
}
//解禁
function abledFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	var id= selobj.id;
	var empId = selobj.empId;
	$.messager.confirm('确认信息', '确认要对帐号解禁吗?', function(r){
		if (r){
			abledAjax(id,empId);
		}
	});
	
}
//搜索
function search(val,name){
	if(name=='empId'){
	$('#dg').datagrid('load',{
		"query['t#empId_S_LK']": val
	});	 
	}
	else if(name=='name'){
		$('#dg').datagrid('load',{
			"query['t#name_S_LK']": val
		});	 
		}
	else if(name=='cell'){
		$('#dg').datagrid('load',{
			"query['t#cell_S_LK']": val
		});	 
		}
	else if(name=='title'){
		$('#dg').datagrid('load',{
			"query['t#title_S_LK']": val
		});	 
		}
}


//初始化
$(function(){
	
	$("#myfile").change(function(){
		$.messager.confirm('确认信息', '确认导入人员资料么?', function(r){
			if (r){
				$("#fileId").submit();
			}else{
				location.reload(true);
			}
		});
	});
	
	$("#fileId").form({
		url :"${pageContext.request.contextPath}/sys/user/upload.htmlx",
		onSubmit : function() {
			return true;
		},
		success : function(result) {
			result = $.parseJSON(result);
			if(result.success){
				location.reload(true);
				showMsg(result.map.msg);
			}else{
				location.reload(true);
				showMsg(result.map.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height()-3,
		pagination:true,
		url:"${pageContext.request.contextPath}/sys/user/page.htmlx",
		 queryParams:{  
		    	/* "query['t#empId_S_NE']":"admin" */
		}  ,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'empId',title:'帐号',width:10,align:'center'},
		        	{field:'name',title:'姓名',width:20,align:'center'},
		        	/* {field:'mail',title:'邮件地址',width:30,align:'center'},
		        	{field:'cell',title:'手机',width:20,align:'center'}, */
/* 		        	{field:'ext',title:'座机',width:20,align:'center'}, */
		        	{field:'organization.orgType',title:'机构类型',width:20,align:'center',
						formatter: function(value,row,index){
							if (row.organization.orgType==1){
								return "医院";
							}else if (row.organization.orgType==2){
								return "供应商";
							}else if (row.organization.orgType==3){
								return "监管机构";
							}else if (row.organization.orgType==5){
								return "系统运维";
							}else if (row.organization.orgType==6){
								return "GPO";
							}
						}
		        	},
		        	{field:'organization.orgName',title:'机构名称',width:20,align:'center',
						formatter: function(value,row,index){
							if (row.organization){
								return row.organization.orgName;
							}
						}},
					{field:'isAdmin',title:'是否管理员',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.isAdmin == 1){
								return "<img src ='${pageContext.request.contextPath}/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
						}
		    		},
		    		{field:'isLocked',title:'是否锁定',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.isLocked == 1){
								return "<img src ='${pageContext.request.contextPath}/resources/js/jquery-easyui/themes/icons/disable.png' />";
							}
						}
		    		},
					{field:'isDisabled',title:'是否禁用',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.isDisabled == 1){
								return "<img src ='${pageContext.request.contextPath}/resources/js/jquery-easyui/themes/icons/disable.png' />";
							}
						}
		    		}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addOpen();	
			}
		}/* ,'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				delFunc();
			}
		},'-' */,{
			iconCls: 'icon-key',
			text:"重置密码",
			handler: function(){
				resetPwd();
			}
		},'-',{
			iconCls: 'icon-setup',
			text:"手动解锁",
			handler: function(){
				unlockFunc();
			}
		},'-',{
			iconCls: 'icon-group_key',
			text:"禁用",
			handler: function(){
				disabledFunc();
			}
		},'-',{
			iconCls: 'icon-user',
			text:"解禁",
			handler: function(){
				abledFunc();
			}
		}],
		onDblClickRow: function(index,field,value){
			editOpen();
		}
	});
	
	$('#dg').datagrid('enableFilter', [{
        field:'organization.orgType',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'1',text:'医院'},
                  {value:'2',text:'供应商'},
                  {value:'3',text:'监管机构'},
            	  {value:'5',text:'系统运维'},
            	  {value:'6',text:'GPO'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'organization.orgType');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'organization.orgType',
                        op: 'EQ',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
    },{
        field:'isAdmin',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'1',text:'管理员'},
                  {value:'0',text:'子账号'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'isAdmin');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'isAdmin',
                        op: 'EQ',
                        fieldType:'S',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
    },{
        field:'isLocked',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'未锁定'},
                  {value:'1',text:'锁定'}
                  ],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'isLocked');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'isLocked',
                        op: 'EQ',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
    },{
        field:'isDisabled',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'未禁用'},
                  {value:'1',text:'禁用'}
                  ],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'isDisabled');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'isDisabled',
                        op: 'EQ',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
    }]);


});

//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加成员",
		width : 700,
		height : 400,
		href : "${pageContext.request.contextPath}/sys/user/add.htmlx",
		onLoad:function(){
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				//top.submitFunc();
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

//弹窗修改
function editOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "编辑成员",
		width : 700,
		height : 400,
		href : "${pageContext.request.contextPath}/sys/user/edit.htmlx",
		queryParams:{
			"orgType":selrow.organization.orgType,
			"orgId":selrow.organization.id
		},
		onLoad:function(){
			
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
//=============ajax===============
function delAjax(id){
	$.ajax({
				url:"<c:out value='${pageContext.request.contextPath }'/>/sys/user/del.htmlx",
				data:"id="+id,
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dg').datagrid('reload');
						showMsg("删除成功");
					} else{
						showErr("出错，请刷新重新操作");
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
	
}

function resetPwdAjax(id,empId){
	$.ajax({
				url:"<c:out value='${pageContext.request.contextPath }'/>/sys/user/resetPwd.htmlx",
				data:{
					"id":id,
					"empId":$.md5(empId)
				},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dg').datagrid('reload');
						showMsg("重置密码成功");
					} else{
						showErr("出错，请刷新重新操作");
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
	
}

function unlockAjax(id,empId){
	$.ajax({
				url:"<c:out value='${pageContext.request.contextPath }'/>/sys/user/unlock.htmlx",
				data:{
					"id":id
				},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dg').datagrid('reload');
						showMsg("手动解锁成功");
					} else{
						showErr("出错，请刷新重新操作");
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
	
}

function disabledAjax(id,empId){
	$.ajax({
				url:"<c:out value='${pageContext.request.contextPath }'/>/sys/user/disabled.htmlx",
				data:{
					"id":id
				},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dg').datagrid('reload');
						showMsg("帐号禁用成功");
					} else{
						showErr("出错，请刷新重新操作");
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
	
}

function abledAjax(id,empId){
	$.ajax({
				url:"<c:out value='${pageContext.request.contextPath }'/>/sys/user/abled.htmlx",
				data:{
					"id":id
				},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dg').datagrid('reload');
						showMsg("帐号解禁成功");
					} else{
						showErr("出错，请刷新重新操作");
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
	
}
</script>