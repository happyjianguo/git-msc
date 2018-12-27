<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

	<div style="padding: 5px">
		<form id="form1">
		上层菜单：${parentName}
		</form>
	</div>

	<div style="padding: 0px 5px">
		<table id="dg"></table>
	</div>



	<script>
	var sysId  = "${sysId}";
	var orgType  = ${orgType};
	var parentId = ${parentId};
	
	function filterFunc(){
		$('#dg').datagrid('removeFilterRule');
		$('#dg').datagrid('enableFilter', 
				[]);
	}
	//初始化
	$(function(){
		
		
		//datagrid
		$('#dg').datagrid({
			fitColumns:true,
			striped:true,
			singleSelect:false,
			rownumbers:true,
			border:true,
			height :  335,
			pagination:true,
			url:"${pageContext.request.contextPath}/sys/menu/resourcePage.htmlx",
			queryParams:{  
				 "sysId":sysId,
				 "orgType":orgType,
				 "parentId":parentId
			},
			pageSize:10,
			pageNumber:1,
			remoteFilter: true,
			columns:[[
			        	{field:'name',title:'资源名称',width:30,align:'center'},
			        	{field:'url',title:'资源路径',width:30,align:'left'},
			        	{field:'icon',title:'资源图标',width:15,align:'center'}
			   		]],
	   		onClickRow:function(index,row){
	   			var selobjs = $('#dg').datagrid('getSelections');
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
	   		onLoadSuccess:function(data){
				$.each(data.rows,function(index,row){
					if(row.state == 1){
						$('#dg').datagrid('selectRow', index);
					}
				}); 
			}
			
		});
		
		filterFunc();
	});

	


	

	//=============ajax===============
	

	function addAjax(resourceId){
		
		$.ajax({
			url:"${pageContext.request.contextPath }/sys/menu/add.htmlx",
			data:{
				"resourceId":resourceId,
				"sysId":sysId,
				"orgType":orgType,
				"parentId":parentId
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
		$.ajax({
			url:"${pageContext.request.contextPath }/sys/menu/del.htmlx",
			data:{
				"resourceId":resourceId,
				"orgType":orgType,
				"parentId":parentId
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
	