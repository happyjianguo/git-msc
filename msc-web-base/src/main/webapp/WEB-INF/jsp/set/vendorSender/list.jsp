<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout"  >
            <div  data-options="region:'west',title:'供应商',collapsible:false" class="my-west" style="width:350px;" >
					<table  id="dg" ></table>
            </div>
            
            <div data-options="region:'center',title:'配送商'" style="background: rgb(238, 238, 238);">
            	<div style="padding:6px 0px 3px 3px">
					<input id="ss" />
						<div id="mm">
							<div data-options="name:'name' ">配送商名称</div>
							<div data-options="name:'code'">配送商编号</div>
						</div>
					<!-- <a id="btn"  class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="save()" >保存  </a> -->
				</div>
            	<div style="padding:3px;">
					<table id="dg2" ></table>
				</div>
            </div>

			

</body>

</html>
<script>

$(function(){

	$('#ss').searchbox({
		searcher:function(value,name){ 
			search();
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 

	//分子公司下拉选单
/* 	$('#compName').combobox({    
	    url:' <c:out value='${pageContext.request.contextPath }'/>/ccm/companySetup/getByCompSel.htmlx',  
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
		height :  $(this).height()-34,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/vendorSender/page.htmlx",
		pageSize:10,
		pageNumber:1,
		columns:[[
		        	{field:'code',title:'供应商编号',width:50,align:'center'},
		        	{field:'fullName',title:'供应商名称',width:50,align:'center'}
		   		]],
		
		onClickRow: function(index,row){
			search(row);  
		},
		onLoadSuccess:function(data){
			var rows=data.rows;
			//大于1行默认选中
			if(rows.length > 0) {
				$(this).datagrid('selectRow', 0);
				search(rows[0]);
			}
		}
		

	});

	$("#dg").datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false
	})	
	//组成员
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		rownumbers:true,
		height :  $(this).height()-70,
		pagination:true,
		columns:[[
					{field:'code',title:'配送商编号',width:15,align:'center'},
					{field:'fullName',title:'配送商名称',width:15,align:'center'}
		   		]],
		onLoadSuccess: function(data){
				 $.each(data.rows,function(index,row){
					if(row.wbcode == 1){
						$('#dg2').datagrid('selectRow', index);
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
   				addAjax(row.code);
   			}else{
   				delAjax(row.code);
   			}
		}
	});
	
	
})

//搜索
function search(selrow){
if(selrow == null){
	selrow = $('#dg').datagrid('getSelected');
}
	var val = $('#ss').searchbox("getValue");
	var name = $('#ss').searchbox("getName");

	var data = {"vendorCode":selrow.code};
	
	if(name=="name"){
		data["query['t#fullName_S_LK']"] = val;
	}else if(name=="code"){
		data["query['t#code_S_LK']"] = val;
	}

	$('#dg2').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/set/vendorSender/mxpage.htmlx",  
	    queryParams:data
	}); 
}

//保存
function save(){
	var nodes = $('#dg2').datagrid('getSelections');
	var user_arr = new Array();
	$.each(nodes,function(index){
		user_arr[index] = this.ID;
	});
	var groupNode = $('#dg').datagrid('getSelected');
	saveAjax(groupNode.id,user_arr);
}


//=============ajax===============


function addAjax(senderCode){
	var selrow = $('#dg').datagrid('getSelected');
		$.ajax({
			url:" <c:out value='${pageContext.request.contextPath }'/>/set/vendorSender/add.htmlx",
			data:{
				"senderCode":senderCode,
				"vendorCode":selrow.code
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

	function delAjax(senderCode){
		var selrow = $('#dg').datagrid('getSelected');
		$.ajax({
			url:" <c:out value='${pageContext.request.contextPath }'/>/set/vendorSender/del.htmlx",
			data:{
				"senderCode":senderCode,
				"vendorCode":selrow.code
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

function saveAjax(groupId,user_arr){
	var searchkey = $("#ss").searchbox('getValue');
	//var options = $("#dg2").datagrid('getPager').data("pagination").options;
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/sys/group/save.htmlx",
		data:{
			olduserId:olduserId,
			userId:user_arr,
			groupId:groupId,
			//page:options.pageNumber,
			//rows:options.pageSize,
			"query['t#group.id_L_EQ']":groupId,
			name:searchkey
		},
		dataType:"json",
		traditional: true,//支持传数组参数
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg2').datagrid('reload'); 
				showMsg("组访问权限已更新");		
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
}
</script>