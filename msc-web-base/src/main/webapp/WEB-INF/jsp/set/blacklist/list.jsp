<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

					
<body class="easyui-layout">
<!-- <div class="search-bar" >
		<input id="ss" />
		<div id="mm">
			<div data-options="name:'title' ">标题</div>
			<div data-options="name:'content' ">内容</div>
		</div>
		</div>
		-->
		<div class="single-dg">
	<table  id="dg" ></table>
</div> 

</body>
</html>

<script>
//初始化
$(function(){
	/* $('#ss').searchbox({
		searcher:function(value,name){ 
			search(value,name);
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	});  */
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height()-3,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/blacklist/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'company.code',title:'企业编号',width:20,align:'center',
		        		formatter: function(value,row,index){
		        			if(row.company)
							return row.company.code;
						}},
		        	{field:'company.fullName',title:'企业名称',width:20,align:'center',
						formatter: function(value,row,index){
							if(row.company)
							return row.company.fullName;
						}},
		    		{field:'joinReason',title:'加入原因',width:20,align:'center'},
		    		{field:'disabledReason',title:'解除原因',width:10,align:'center'},
					{field:'isDisabled',title:'是否禁用',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.isDisabled == 1){
								return "<img src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png' />";
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
		},'-',{
			iconCls: 'icon-no',
			text:"解除",
			handler: function(){
				editOpen();
			}
		}],
		onDblClickRow: function(index,field,value){
			editOpen();
		}
	});

	$('#dg').datagrid('enableFilter', [{
        field:'isDisabled',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'未禁用'},
                  {value:'1',text:'已禁用'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'isDisabled');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'isDisabled',
                        op: 'LK',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
    }]);
	
});


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

//发布 or 取消
function statusFunc(id,status){
	statusAjax(id,status);
}
//搜索
function search(val,name){
	if(name=="title"){
		$('#dg').datagrid('load',{
			"query['t#title_S_LK']": val
		});	
	}else if(name=="content"){
		$('#dg').datagrid('load',{
			"query['t#content_S_LK']": val
		});	
	}
	
}

//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/blacklist/add.htmlx",
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
		title : "修改",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/blacklist/edit.htmlx",
		queryParams:{
			"id":selrow.id
		},
		onLoad:function(){
			if(selrow){
				var f = top.$.modalDialog.handler.find("#form1");
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
function statusAjax(id,status){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/notice/status.htmlx",
		data:{
			"id":id,
			"status":status
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				//showMsg("操作成功！");
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/notice/del.htmlx",
		data:"id="+id,
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("删除成功！");
			}else{
				showErr(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}


</script>