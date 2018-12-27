<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout">
	<div class="search-bar" >
		<input id="ss" style="display:none"/>
		<div id="mm">
			<div data-options="name:'fullName' ">医疗机构</div>
			<div data-options="name:'code'">结算单号</div>
		</div> 
		<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" onclick="addPara()">付款</a>
	</div>
	<div class="single-dg">
		<table id="dg" ></table>
	</div>

</body>
</html>

<script>
//搜索
function search(val,name){
	if(name=='fullName'){
		$('#dg').datagrid('load',{
			"query['t#hospitalName_S_LK']": val
		});	 
	}
	else if(name=='code'){
		$('#dg').datagrid('load',{
			"query['t#code_S_LK']": val
		});	 
	}
	
}

//初始化
$(function(){
	//搜索框
	$("#ss").searchbox({
		searcher:function(value,name){
			search(value,name);
		},
		prompt:'支持模糊搜索',
		menu:'#mm',
		width:200
	});

	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/pay/page.htmlx",
		columns:[[
		        	{field:'code',title:'结算单号',width:30,align:'center'},
		        	{field:'hospitalName',title:'医疗机构',width:15,align:'center'},
		        	{field:'accBeginDate',title:'账期起始日期',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.accBeginDate){
								return $.format.date(row.accBeginDate,"yyyy-MM-dd");
							}
						}},
		        	{field:'accEndDate',title:'账期结束日期',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.accEndDate){
								return $.format.date(row.accEndDate,"yyyy-MM-dd");
							}
						}},
		        	{field:'sum',title:'结算金额',width:10,align:'right',
							formatter: function(value,row,index){
								if (row.sum){
									return common.fmoney(row.sum);
								}
							}},
		        	{field:'paidAmt',title:'已付款',width:10,align:'right',
								formatter: function(value,row,index){
									if (row.paidAmt){
										return common.fmoney(row.paidAmt);
									}
								}},
		        	{field:'status',title:'状态',width:15,align:'center',
						formatter: function(value,row,index){
							if (row.status == 'unpay'){
								return "未付款";
							}else if(row.status == 'paying'){
								return "付款中";
							}else if(row.status == 'paid'){
								return "已付款";
							}
						}
		        	}
		   		]],
		onDblClickRow:function(index,row){
			addPara(row);	
		},
		onLoadSuccess:function(data){
			var rows=data.rows;
			//大于1行默认选中
			if(rows.length > 0) {
				$(this).datagrid('selectRow', 0);
			}
			$('#dg2').datagrid('doCellTip',{delay:500}); 
		}
	});

	

});
//弹窗信息
function addPara() {
	var row = $('#dg').datagrid('getSelected');
	if(row == null){
		showMsg("请至少选择一笔");
		return;
	}
	top.$.modalDialog({
		title : "信息",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/pay/para.htmlx",
		onLoad:function(){
			if(row){
				var f = top.$.modalDialog.handler.find("#form1");
				f.find("#settleId").val(row.id);
				f.find("#maxAmt_h").val(row.sum - row.paidAmt);
				f.find("#maxAmt").html( common.fmoney(row.sum - row.paidAmt));
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

//弹窗成功
function successOpen(data) {alert(11);
	if(!data)
		return;
	top.$.modalDialog({
		title : "付款成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/pay/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}

</script>