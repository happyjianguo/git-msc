<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>
<script src="<c:out value='${pageContext.request.contextPath }'/>/resources/js/echarts.min.js"></script>

<body class="easyui-layout" >
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
	var id= selobj.id;
	
	$.messager.confirm('确认信息', '确认要删除此专家?', function(r){
		if (r){
			delAjax(id);
		}
	});
}


//初始化
$(function(){
	
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height()-3,
		pagination:true,
		url:"<c:out value='${pageContext.request.contextPath }'/>/set/expert/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'code',title:'编号',width:20,align:'center'},
		        	{field:'name',title:'姓名',width:20,align:'center'},
		        	{field:'courseName',title:'所属学科',width:20,align:'center'},
		        	{field:'sex',title:'性别',width:15,align:'center',
						formatter: function(value,row,index){
							if (row.sex==1){
								return "男";
							}else if (row.sex==0){
								return "女";
							}
						}},
		        	{field:'mobile',title:'手机',width:20,align:'center'},
 		        	{field:'tel',title:'固定电话',width:20,align:'center'}, 
		        	{field:'orgType',title:'机构类型',width:15,align:'center',
						formatter: function(value,row,index){
							if (row.orgType==1){
								return "医院";
							}
						}
		        	},
		        	{field:'orgName',title:'机构名称',width:30,align:'left'}
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
				delFunc();
			}
		}],
		onDblClickRow: function(index,field,value){
			editOpen();
		}
	});
	
	$('#dg').datagrid('enableFilter', [{
        field:'orgType',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'1',text:'医院'}],
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
    },{
        field:'sex',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'1',text:'男'},
                  {value:'0',text:'女'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'sex');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'sex',
                        op: 'EQ',
                        fieldType:'S',
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
		title : "添加专家",
		width : 700,
		height : 400,
		iconCls: 'icon-add',
		href : "<c:out value='${pageContext.request.contextPath }'/>/set/expert/add.htmlx",
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
		title : "编辑专家",
		width : 700,
		height : 400,
		iconCls: 'icon-edit',
		href : "<c:out value='${pageContext.request.contextPath }'/>/set/expert/edit.htmlx",
		queryParams:{
			/* "orgType":selrow.organization.orgType,
			"orgId":selrow.organization.id */
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
				url:"<c:out value='${pageContext.request.contextPath }'/>/set/expert/del.htmlx",
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

</script>