<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
					
<div class="single-dg">
		<div id="toolbar" class="search-bar" >
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addOpen()">添加</a>
	        <span class="datagrid-btn-separator split-line" ></span>
	  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="delFunc()">删除</a>
	    </div>
	<table  id="dg" ></table>
</div>

</body>
</html>

<script>
//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/prescriptProduct/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'product.code',title:'药品编码',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.product){
								return row.product.code;
							}
						}
		        	},
		        	{field:'product.name',title:'药品名称',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.product){
								return row.product.name;
							}
						}
		        	},
		        	{field:'product.genericName',title:'通用名',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.product){
								return row.product.genericName;
							}
						}
		        	},
		        	{field:'product.dosageFormName',title:'剂型',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.product){
								return row.product.dosageFormName;
							}
						}
		        	},
		        	{field:'product.producerName',title:'生产企业',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.product){
								return row.product.producerName;
							}
						}
		        	},
		        	{field:'product.model',title:'规格',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.product){
								return row.product.model;
							}
						}
		        	},
		        	{field:'product.packDesc',title:'包装规格',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.product){
								return row.product.packDesc;
							}
						}
		        	},		      
		        	{field:'product.isDisabled',title:'是否禁用',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.product.isDisabled == 1){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png\" />";
							}
						}
		        	}
		   		]],
		toolbar: "#toolbar",
		onDblClickRow: function(index,field,value){
			editOpen();
		}
	});

	$('#dg').datagrid('enableFilter', [{
        field:'product.isDisabled',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'未禁用'},
                  {value:'1',text:'禁用'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'product.isDisabled');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'product.isDisabled',
                        op: 'EQ',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	}]
	
	);
	
});


//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/prescriptProduct/add.htmlx",
		onLoad:function(){
			
		},
		onDestroy:function(){
			$('#dg').datagrid("reload");
		}
	});
}

//删除
function delFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delAjax(selobj.id);
		}
	});
}

//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/prescriptProduct/del.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("删除成功！");
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}


</script>