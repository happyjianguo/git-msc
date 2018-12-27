<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >			
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>
</body>
</html>
<script>
function audit(){
	var selrow = $('#dg').datagrid('getSelected');
	if(selrow== null){
		showErr("请先选择一笔订单");
		return;
	}
	var id = selrow.id;
	var status = selrow.status;
	if(status == 'agree' || status == 'disagree'){
		showErr("该笔数据已经审核");
		return;
	}
	top.$.modalDialog({
		title : "合同终止申请",
		width : 600,
		height : 300,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/contractClosedRequest/add.htmlx",
		onLoad:function(){
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				var isValid = f.form('validate');
				if(!isValid)
					return;
				var status = f.find("#status").textbox("getValue");
				var reply = f.find("#reply").textbox("getValue");
				commitAjax(id,status,reply);
				top.$.modalDialog.handler.dialog('close');
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
//检索
function query(){
	var startDate="";
	var toDate="";
	var status = $("#status").datebox('getValue');
	if($('#startDate').datebox('getValue')!=""){
	  	startDate = $('#startDate').datebox('getValue');		
	}
	if($('#toDate').datebox('getValue')!=""){
		toDate = $('#toDate').datebox('getValue');
	}	
	
	$('#dg').datagrid('load',{"query['t#createDate_D_GE']": startDate,"query['t#createDate_D_LE']": toDate,"query['t#status_S_EQ']":status});
}

//删除
function delCp(){

	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		$.messager.alert('错误','没有选中行!','info');
		return;
	}
	var status = selobj.status;
	if(status != 'unaudit'){
		$.messager.alert('错误','只可删除未审核申请单!','info');
		return;
	}
	var id= selobj.id;
	
	$.messager.confirm('确认信息', '确认要删除此申请单?', function(r){
		if (r){
			delAjax(id);
		}
	});
}

function commitAjax(id,status,reply){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/contractClosedRequest/commit.htmlx",
		data:{
			id:id,
			status:status,
			reply:reply
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');
				showMsg(data.msg);
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}




//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "合同终止申请",
		width : 800,
		height : 550,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/contractClosedRequest/add.htmlx",
		onLoad:function(){
		},
		buttons : [ {
			text : '提交',
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


	

//初始化
$(function(){
	$("#status").combobox({    
	    valueField:'label',    
	    textField:'value',  
	    panelHeight:160,
	    editable:false,
	    data:[{
	    	label: '',
			value: '全部'
		},{
			label: 'unaudit',
			value: '未审核'
		},{
			label: 'agree',
			value: '同意结案'
		},{
			label: 'disagree',
			value: '不同意结案'
		}]
	});
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/contractClosedRequest/page.htmlx",
		pageSize:10,
		pageNumber:1,
		checkOnSelect:true,
		remoteFilter: true,
		columns:[[
				{field:'code',title:'编号',width:15,align:'center'},
				{field:'contractCode',title:'合同编号',width:15,align:'center'},
				{field:'contractDetailCode',title:'合同明细编号',width:20,align:'center'},
		        	{field:'closedRequestDate',title:'合同终止申请时间',width:15,align:'center',
						formatter: function(value,row,index){
							if (row.closedRequestDate){
								return $.format.date(row.closedRequestDate,"yyyy-MM-dd HH:mm");
							}
						}
					}, 
					{field:'closedMan',title:'结案申请人',width:10,align:'center'},
					{field:'reason',title:'原因',width:20,align:'center'},
					{field:'reply',title:'答复',width:20,align:'center'},
					{field:'status',title:'状态',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.status == 'unaudit'){
								return "未审核";
							}else if(row.status == 'agree'){
								return "同意终止";
							}else if(row.status == 'disagree'){
								return "不同意终止";
							}
						}
					}
   		]],
   		toolbar: [{
			iconCls: 'icon-ok',
			text:"审核",
			handler: function(){
				audit();	
			}
		}],
		onLoadSuccess:function(data){
			$('#dg').datagrid('doCellTip',{delay:500});
		}
	});
	
	$('#dg').datagrid('enableFilter', 
			[{
		        field:'closedRequestDate',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'D',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'status',
		        type:'combobox',
		        options:{
		            panelHeight:'auto',
		            editable:false,
		            data:[{value:'',text:'-请选择-'},
		                {value:'0',text:'未审核'},
		                {value:'1',text:'同意终止'},
		                {value:'2',text:'不同意终止'}
		                
		            ],
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'status');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'status',
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
function openPre(code){
	top.addTab("订单查询 ", "/b2b/monitor/contractClosedRequest.htmlx?code="+code);
}
</script>