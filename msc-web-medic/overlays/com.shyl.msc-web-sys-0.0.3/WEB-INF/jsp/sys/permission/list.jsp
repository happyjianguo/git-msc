<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout"  >
	<div  data-options="region:'west',title:'用户组',collapsible:false" style="width:600px;background: rgb(238, 238, 238);">
    	<div style="padding:3px;">
			<table  id="dg" ></table>
		</div>
    </div>
          
    <div data-options="region:'center',title:'授权列表'" style="background: rgb(238, 238, 238);" >
    	<div style="padding:6px;">
        	<input  id ="parentId"  ></input>
		</div>
		<div style="padding:0px 3px 3px 3px;">      		
			<table id="tg" ></table>
		</div>
    </div>           
</body>

</html>
<script>


//点击查询 
function search(menuId){
	var deptNode = $('#dg').datagrid('getSelected');
	if(deptNode == null)
		return;
	$('#tg').treegrid("unselectAll");
	$('#tg').treegrid({	
		url:'${pageContext.request.contextPath}/sys/permission/list.htmlx',  
	    queryParams:{  
	        groupId:deptNode.id,
	        //parentId:$("#parentId").combobox("getValue")
	        parentId:menuId
	    }
	});  
}

//查询机构一级菜单
function queryLvlone(orgType){
	
	$('#parentId').combobox({    
	    url:'${pageContext.request.contextPath}/sys/menu/lvlone.htmlx',
	    queryParams:{  
	    	"orgType":orgType
	    }
	});
}

$(function(){
	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height :  $(this).height()-40,
		url:"${pageContext.request.contextPath}/sys/group/page.htmlx",
		pagination:true,
		pageSize:10,
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
		onClickRow: function(index,row){
			queryLvlone(row.orgType);
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
	
	//菜单
	$('#parentId').combobox({    
	    valueField:'id',    
	    textField:'segmentStr',
	    editable:false,
	    onSelect:function(record){
	    	search(record.id);
	    }, 
	    onLoadSuccess: function () { //数据加载完毕事件
            var data = $('#parentId').combobox('getData');
	   		if (data.length > 0) {
                $("#parentId").combobox('select', data[0].id);
            }
	    }
	});
	
	var selectArr = new Array();
	//授权列表
	$('#tg').treegrid({
		idField:'id',    
	    treeField:'name',
	    idFiled:'id',
	    textFiled:'name',
	    parentField:'parentId',
	    iconCls:'icon',
	    method:'post',
	    animate:true,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		height :  $(this).height()-70,
		columns:[[
		        	{field:'name',width:'20%',title:'名称'},
		        	{field:'url',width:'30%',title:'权限路径',
			        	formatter: function(value,row,index){
							if(row.segmentStr){
								$('#tg').treegrid('select',row.id);
							}else{
								$('#tg').treegrid('unselect',row.id);
							}
							return row.url;
						}
				    },
				    {field:'permCode',width:'30%',title:'权限编码'}
		   		]],		   		
		onClickRow: function(row){
			var selobjs = $('#tg').treegrid('getSelections');
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
		},
		onLoadSuccess: function(row,data){
	
		}
	
	});
})
//=============ajax===============
function addAjax(resourceId){
	var groupNode = $('#dg').datagrid('getSelected');
		$.ajax({
			url:"${pageContext.request.contextPath }/sys/permission/add.htmlx",
			data:{
				resourceId:resourceId,
				groupId:groupNode.id,
				parentId:$("#parentId").combobox("getValue")
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

	function delAjax(resourceId){
		var groupNode = $('#dg').datagrid('getSelected');
		$.ajax({
			url:"${pageContext.request.contextPath }/sys/permission/del.htmlx",
			data:{
				resourceId:resourceId,
				groupId:groupNode.id,
				parentId:$("#parentId").combobox("getValue")
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