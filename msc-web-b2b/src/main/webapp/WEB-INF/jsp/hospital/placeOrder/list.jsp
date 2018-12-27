<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="订单管理列表" />
<style type="text/css">
.datagrid-toolbar .file{
    position: absolute;
    right: 0px;
    top: 0px;
    opacity: 0;
    height:30px;
    width:70px;
    -ms-filter: 'alpha(opacity=0)';
    filter:alpha(opacity=0); 
    cursor: pointer;
    font-size:0;
}
</style>
<html>

<body class="easyui-layout" >
	<form id="fileId" method="POST" enctype="multipart/form-data">
		<div class="single-dg"> 
			<table  id="dg"></table>
		</div>
	</form>
</body>
</html>
<script>
//初始化
$(function(){
	$("#fileId").on("change","#myfile",function(){
		if($(this).val() == "") return;
		$.messager.confirm('确认信息', '确认导入资料么?', function(r){
			if (r){
				$("#fileId").submit();
			}else{
				location.reload(true);
			}
		});
	});
	
	$("#fileId").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/cart/upload.htmlx",
		onSubmit : function() {
			top.$.messager.progress({
				title : '提示',
				text : '数据导入中，请稍后....'
			});
			return true;
		},
		success : function(result) {
			$('#myfile').val("");
			top.$.messager.progress('close');
			result = $.parseJSON(result);
			if(result.success){
				//$('#dg').datagrid('reload');
				parent.addTab("购物车", "/hospital/cart.htmlx", null);
				showMsg(result.msg);
			}else{
				importErrOpen(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	var editRow;
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(this).height()-4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/placeOrder/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'CODE',title:'药品编码',width:20,align:'center'},
			{field:'NAME',title:'药品名称',width:20,align:'center'},
			{field:'PINYIN',title:'拼音',width:10,align:'center'},
			{field:'PRODUCERNAME',title:'生产企业',width:15,align:'center'},
			{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
			{field:'MODEL',title:'规格',width:10,align:'center'},
			{field:'PACKDESC',title:'包装',width:10,align:'center'},
        	{field:'UNITNAME',title:'单位',width:10,align:'center'},
        	{field:'VENDORNAME',title:'供应商',width:20,align:'center'},
        	{field:'FINALPRICE',title:'价格（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.FINALPRICE){
						return common.fmoney(row.FINALPRICE);
					}
				}},
        	{field:'GOODSNUM',title:'数量',width:10,align:'center',editor:{type:'numberbox',options:{validType:'pinteger'}}},
        	{field:'do',title:'操作',width:10,align:'center',
        		formatter: function(value,row,index){
        			if(row.GOODSNUM)
						return "<a class='dgbtn' href='#' onclick='addCart("+index+")' class='easyui-linkbutton'>更新</a>";
					else
						return "<a class='dgbtn' href='#' onclick='addCart("+index+")' class='easyui-linkbutton'>加入</a>";
				}}
   		]],
   		toolbar: [{
			iconCls: 'icon-import',
			id:'fileImport',
			text:"导入",
			handler: function(){
			}
		},'-',{
			iconCls: 'icon-xls',
			text:"模板下载",
			handler: function(){
				$.messager.confirm('确认信息', '确认下载?', function(r){
					if (r){
						window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/placeOrder/exportExcel.htmlx");
					}
				});
				//location.href = " <c:out value='${pageContext.request.contextPath }'/>/resources/template/orderTemplate.xlsx";
			}
		} ],
		onAfterEdit: function (rowIndex, rowData, changes) {
            //endEdit该方法触发此事件
            //editRow = undefined;
        },
		onClickRow: function(index,row){
			 /* if (editRow != undefined) {
				 $('#dg').datagrid("endEdit", editRow);
             }
             if (editRow == undefined) {
            	 $('#dg').datagrid("beginEdit", index);
                 editRow = index;
             } */
			$('#dg').datagrid('beginEdit', index);
		},
		onLoadSuccess:function(data){
			$('.dgbtn').linkbutton({iconCls:'icon-return_material',plain:true,height:20}); 
			$('#dg').datagrid('doCellTip',{delay:500}); 
			$("#fileImport").find($("span")[0]).append('<input type="file" name="myfile" class="file" id="myfile"/>');
		}
	});
	
	$('#dg').datagrid('enableFilter', [{
		 field:'FINALPRICE',
		 type:'text',
		 isDisabled:1
	},{
		 field:'GOODSNUM',
		 type:'text',
		 isDisabled:1
	},{
		 field:'do',
		 type:'text',
		 isDisabled:1
	},{
        field:'VENDORNAME',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            valueField:'ID',    
    	    textField:'NAME',
            url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/placeOrder/vendorList.htmlx",
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'VENDORCODE');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'VENDORCODE',
                        op: 'EQ',
                        fieldType:'b#L',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
    }]
	
	);
	
});

function addCart(index){
	var row = $("#dg").datagrid("getRows")[index];
	
	var goodsPriceId = row.GOODSPRICEID
	
	var ed2 = $('#dg').datagrid('getEditor', {index:index,field:'GOODSNUM'});
	if(ed2 == null)
		return;
	var goodsNum = ed2.target.val();
	if(goodsNum == ""){
		showErr("数量不能为空");
		return false;
	}
	
	var goodsId = row.ID;
	addCartAjax(goodsId,goodsPriceId,goodsNum);
	
}

function addCartAjax(goodsId,goodsPriceId,goodsNum){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/placeOrder/add.htmlx",
		data:{
			"goodsId": goodsId,
			"goodsPriceId": goodsPriceId,
			"goodsNum":goodsNum
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				//$('#dg').datagrid('reload');
				showMsg("加入成功");
			} else{
				showErr("出错，请刷新重新操作");
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function onClickRow(index){
	$('#dg').datagrid('selectRow', index).datagrid('beginEdit', index);
}

//弹窗导入失败
function importErrOpen(data) {
	if(!data)
		return;
	top.$.modalDialog({
		title : "导入失败",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/cart/importErr.htmlx",
		onLoad:function(){
			top.setImportErr(data);
		}
	});
}

</script>