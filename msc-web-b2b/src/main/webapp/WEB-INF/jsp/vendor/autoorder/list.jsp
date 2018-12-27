<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />


<body class="easyui-layout"  >
            <div  data-options="region:'west',title:'医疗机构',collapsible:false" style="width:350px;background: rgb(238, 238, 238);padding:2px">
					<table  id="dg" ></table>
            </div>
            
            <div data-options="region:'center',title:'补货计划'" style="background: rgb(238, 238, 238);">
            	<div style="padding:2px;">
            		<input type="hidden" id="warehouseCode" /> 
            		<input type="hidden" id="urgencyLevel" />
					<input type="hidden" id="requireDate" />
					<table id="dg2" ></table>
				</div>
            </div>
</body>

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
		height :  $(this).height()-40,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/autoorder/hospitalPage.htmlx",
		pageSize:10,
		pageNumber:1,
		columns:[[
					{field:'HOSPITALNAME',title:'医院名称',width:40,align:'center'},
		        	{field:'NUM',title:'报警数',width:15,align:'center'}
		   		]],
		onLoadSuccess:function(data){
			var rows=data.rows;
			//大于1行默认选中
			if(rows.length > 0) {
				$(this).datagrid('selectRow', 0);
	        	searchDefectsList(rows[0]);
			}
			$('#dg').datagrid('doCellTip',{delay:500}); 
		},
		onClickRow: function(index,row){
			searchDefectsList(row);  
		}
	});
	
	$('#dg').datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false,
		displayMsg:"共{total}记录"
	});	
	
	//datagrid
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		border:true,
		height :  $(this).height()-40,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		columns:[[
		        	{field:'PRODUCTCODE',title:'药品编码',width:30,align:'center'},
		        	{field:'PRODUCTNAME',title:'药品名称',width:15,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
		        	{field:'MODEL',title:'规格',width:10,align:'center'},
		        	{field:'PACKDESC',title:'包装',width:10,align:'center'},
		        	{field:'STOCKUPLIMIT',title:'上限',width:10,align:'center'},
		        	{field:'STOCKDOWNLIMIT',title:'下限',width:10,align:'center'},
		        	{field:'STOCKNUM',title:'库存',width:10,align:'center'},
		        	{field:'FINALPRICE',title:'价格',width:10,align:'right',
						formatter: function(value,row,index){
							if (row.FINALPRICE){
								return common.fmoney(row.FINALPRICE);
							}
						}},
		        	{field:'NUM',title:'补货数量',width:10,align:'center', editor: { type: 'numberbox', options: { required: true,min:1,precision:0 } }}
		   		]],
		toolbar: [{
			iconCls: 'icon-ok',
			text:"确认补货",
			handler: function(){
				addPara();	
			}
		}],
		onDblClickRow: function(index,field,value){
			editOpen();
		},
		onLoadSuccess:function(data){
			$.each(data.rows,function(index,row){
				$('#dg2').datagrid('beginEdit', index);
			}); 
			$('#dg2').datagrid('doCellTip',{delay:500}); 
		}
		
	});

	
	//弹窗信息
	function addPara() {
		var selrow = $('#dg').datagrid('getSelected');
		var selobjs = $('#dg2').datagrid('getSelections');
		if(selobjs.length == 0){
			showMsg("请至少选择一笔");
			return;
		}
		var eachflag = 1;
		$.each(selobjs,function(index,row){
			 var ed = $('#dg2').datagrid('getEditor', { index: $('#dg2').datagrid("getRowIndex",row) , field: 'NUM' });
		     var number = $(ed.target).numberbox('getValue');
		     if(number>row.STOCKUPLIMIT - row.STOCKNUM){
		    	 showErr(row.PRODUCTNAME+"的补货数量"+number+"超上限"+(row.STOCKUPLIMIT - row.STOCKNUM));
		    	 eachflag = 0;
		     }
		}); 
		if(eachflag == 0)
			return;
		
		
		top.$.modalDialog({
			title : "信息",
			width : 600,
			height : 400,
			href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/autoorder/para.htmlx",
			queryParams:{
				"hospitalCode":selrow.HOSPITALCODE
			},
			onLoad:function(){
			},
			buttons : [ {
				text : '保存',
				iconCls : 'icon-ok',
				handler : function() {
					var f = top.$.modalDialog.handler.find("#form1");
					var isValid = f.form('validate');
					if(!isValid)
						return;
					
					var urgencyLevel = f.find("#urgencyLevel").combo("getValue");
					var warehouseCode = f.find("#warehouseCode").combo("getValue");
					var requireDate = f.find("#requireDate").datetimespinner('getValue');
					top.$.modalDialog.handler.dialog('close');
					
					$("#warehouseCode").val(warehouseCode);
					$("#urgencyLevel").val(urgencyLevel);
					$("#requireDate").val(requireDate);
					mkorder();
					
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
	
	
	
});

//弹窗成功
function successOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "下单成功",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/vendor/autoorder/success.htmlx",
		onLoad:function(){
			top.setData(data);
		}
	});
}

//搜索
function searchDefectsList(row){
	$('#dg2').datagrid({  
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/autoorder/page.htmlx",
	    queryParams:{
	    	hospitalCode:row.HOSPITALCODE
	    }
	}); 
}
//下单
function mkorder(){
	var selobjs = $('#dg2').datagrid('getSelections');
	var goodspriceids = new Array();
	var nums = new Array();
	var eachflag = 1;
	$.each(selobjs,function(index,row){
		 var ed = $('#dg2').datagrid('getEditor', { index: $('#dg2').datagrid("getRowIndex",row) , field: 'NUM' });
	     var number = $(ed.target).numberbox('getValue');
	     if(number>0){
	    	 goodspriceids[goodspriceids.length] = row.GOODSPRICEID;
		     nums[nums.length] = number;
	     }
	}); 
	if(eachflag == 0)
		return;
	$.messager.confirm('确认信息', '请核对补货数量，确认补货吗?', function(r){
		if (r){
			mkorderAjax(goodspriceids,nums);
		}
	});
	
}



//=============ajax===============
function mkorderAjax(goodspriceids,nums){
	var selrow = $('#dg').datagrid('getSelected');
	var urgencyLevel = $("#urgencyLevel").val();	
	var warehouseCode = $("#warehouseCode").val();
	var requireDate = $("#requireDate").val();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/autoorder/mkorder.htmlx",
		data:{
			"hospitalCode":selrow.HOSPITALCODE,
			"goodspriceids":goodspriceids,
			"nums":nums,
			"uLevel":urgencyLevel,
			"warehouseCode":warehouseCode,
			"requireDate":requireDate
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg2').datagrid('reload');  
				showMsg("下单成功！");
				successOpen(data.data);
			} 
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}


</script>