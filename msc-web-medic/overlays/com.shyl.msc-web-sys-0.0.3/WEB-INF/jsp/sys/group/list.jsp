<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout"  >
	<div  data-options="region:'west',title:'用户组',collapsible:false" style="width:600px;background: rgb(238, 238, 238);padding:3px">
		<table  id="dg" ></table>
    </div>
         
    <div data-options="region:'center',title:'组成员'" style="background: rgb(238, 238, 238);">
        <div style="padding:6px 0px 3px 3px">
			<input id="ss"  />
			<!-- <a id="btn"  class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="save()" >保存  </a> -->
		</div>
        <div style="padding:3px;">
			<table id="dg2" ></table>
		</div>
	</div>		
</body>

</html>
<script>

var ur_dg;


//删除
function del(){

	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	var id= selobj.id;
	
	$.messager.confirm('确认信息', '确认要删除此用户组?', function(r){
		if (r){
			leftdelAjax(id);
		}
	});
}

var olduserId = new Array();

$(function(){

	//搜索框
	$("#ss").searchbox({
		searcher:function(value,name){
			search(value);
		},
		prompt:'搜索在此输入姓名',
		width:150
	});

	//分子公司下拉选单
/* 	$('#compName').combobox({    
	    url:'${pageContext.request.contextPath}/ccm/companySetup/getByCompSel.htmlx',  
	    valueField:'compCode',    
	    textField:'compName',   
	    onSelect:function(a,b){
	    	search();
	    }
	}); */

	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height :  $(this).height()-40,
		pagination:true,
		url:"${pageContext.request.contextPath}/sys/group/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'orgType',title:'机构类型',width:30,align:'center',
						formatter: function(value,row,index){
							if (row.orgType==1){
								return "医院";
							}else if (row.orgType==2){
								return "供应商";
							}else if (row.orgType==3){
								return "监管机构";
							}else if (row.orgType==5){
								return "系统运维";
							}else if (row.orgType==6){
								return "GPO";
							}
						}
		        	},
		        	{field:'organization.orgName',title:'机构名称',width:40,align:'center',
						formatter: function(value,row,index){
							return row.organization.orgName;
						}},
		        	{field:'name',title:'组名',width:30,align:'center'}
		   		]],
		toolbar: [{
			iconCls: 'icon-add',
			text:"添加",
			handler: function(){
				addOpen();			
			}
		},'-',{
			iconCls: 'icon-no',
			text:"删除",
			handler: function(){
				del();
			}
		},'-',{
			iconCls: 'icon-filter',
			text:"只显示大组",
			handler: function(){
				showPG();
			}
		}],
		onClickRow: function(index,row){
			search();  
		},
		onDblClickRow: function(index,row){
			editOpen();		
		}
		

	});

	$("#dg").datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false
	})	
	
	$('#dg').datagrid('enableFilter', [{
        field:'orgType',
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
                	$('#dg').datagrid('removeFilterRule', 'orgType');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'orgType',
                        op: 'EQ',
                        fieldType:'S',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
    }]);
	
	
	//组成员
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		rownumbers:true,
		height :  $(this).height()-70,
		pagination:true,
		columns:[[
					{field:'empId',title:'帐号',width:15,align:'center'},
					{field:'name',title:'姓名',width:15,align:'center'},
					{field:'title',title:'职务',width:15,align:'center'}
		   		]],
		onLoadSuccess: function(data){
			 $.each(data.rows,function(index,row){
				if(row.isSelected == 1){
					$('#dg2').datagrid('selectRow', index);
				}else{
					$('#dg2').datagrid('unselectRow',index);
				}
			}); 
		},
		onClickRow: function(index,row){
			var selobjs = $('#dg2').datagrid('getSelections');
   			var isSelected = 0;
   			$.each(selobjs,function(index){
   				if(this.id == row.id){
   					isSelected = 1;
   					return false;
   				}
   			});

   			if(isSelected){
   				addAjax(row.id);
   			}else{
   				delAjax(row.id);
   			}
		}
	});
	
	
})

//搜索
function search(){
	//var nodes = $('#dg2').datagrid('clearSelections');
	var selrow = $('#dg').datagrid('getSelected');
	var searchkey = $("#ss").searchbox('getValue');

	$('#dg2').datagrid({  
	    url:'${pageContext.request.contextPath}/sys/group/groupUserPage.htmlx',  
	    queryParams:{
	    	groupId:selrow.id,
	        name:searchkey
	    }
	}); 
}

function showPG(){
	$('#dg').datagrid('load',{
		"query[t#parentId_L_EQ]": '-1'
	});
}



//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 400,
		height : 250,
		href : "${pageContext.request.contextPath}/sys/group/add.htmlx",
		onLoad:function(){
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

//弹窗修改
function editOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "编辑",
		width : 400,
		height : 250,
		href : "${pageContext.request.contextPath}/sys/group/edit.htmlx",
		queryParams:{
			"orgType":selrow.organization.orgType,
			"orgId":selrow.organization.id
		},
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

//=============ajax===============
function leftdelAjax(id){
	$.ajax({
				url:"${pageContext.request.contextPath }/sys/group/del.htmlx",
				data:"id="+id,
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						var nodes = $('#dg2').datagrid('clearSelections');
						$('#dg').datagrid('reload');
						showMsg("用户组及其成员已删除");
					} else{
						showErr("出错，请刷新重新操作");
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}

function addAjax(userId){
	var groupNode = $('#dg').datagrid('getSelected');
		$.ajax({
			url:"${pageContext.request.contextPath }/sys/groupuser/add.htmlx",
			data:{
				userId:userId,
				groupId:groupNode.id
			},
			dataType:"json",
			type:"POST",
			cache:false,
			success:function(data){
				if(data.success){
					showMsg("新增成功！");
				}else{
					showErr(data.msg);
				}
			},
			error:function(){
				//top.$.modalDialog.openner.datagrid('reload');
				//top.$.modalDialog.handler.dialog('close');
				showErr("出错，请刷新重新操作");
			}
		});	
	}

	function delAjax(userId){
		var groupNode = $('#dg').datagrid('getSelected');
		$.ajax({
			url:"${pageContext.request.contextPath }/sys/groupuser/del.htmlx",
			data:{
				userId:userId,
				groupId:groupNode.id
			},
			dataType:"json",
			type:"POST",
			cache:false,
			success:function(data){
				if(data.success){
					showMsg("删除成功！");
				}else{
					showErr(data.msg);
				}
			},
			error:function(){
				//top.$.modalDialog.openner.datagrid('reload');
				//top.$.modalDialog.handler.dialog('close');
				showErr("出错，请刷新重新操作");
			}
		});	
	}
</script>