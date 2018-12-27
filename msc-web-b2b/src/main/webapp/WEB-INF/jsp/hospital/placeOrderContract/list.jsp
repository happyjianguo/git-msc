<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="订单管理列表" />
<style type="text/css">
.file {
	position: absolute;
	top: 0px;
	left: 0px;
	height: 30px;
	width: 85px;
	filter: alpha(opacity : 0);
	opacity: 0;
	cursor: pointer;
}

.file_form {
	width: 85px;
	height: 20px;
	float: left;
	margin: 0px 5px 0px 0px;
}
.datagrid-cell-c1-cartNum{
	color:#0081c2;
}
</style>
<html>

<body class="easyui-layout" >
	<form id="fileId" method="POST" enctype="multipart/form-data">
		<input type="file" name="myfile" class="file" id="myfile" />
	</form>
	<div class="single-dg"> 
		<table  id="dg"></table>
	</div>
</body>
</html>
<script>
//初始化
$(function(){
	$("#myfile").change(function(){
		$.messager.confirm('确认信息', '确认导入药品资料么?', function(r){
			if (r){
				$("#fileId").submit();
			}else{
				location.reload(true);
			}
		});
	});
	
	$("#fileId").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/cartContract/upload.htmlx",
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
				parent.addTab("合同购物车", "/hospital/cartContract.htmlx", null);
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
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/placeOrderContract/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'product.code',title:'药品编码',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.code;
					}
				}},
			{field:'product.name',title:'药品名称',width:10,align:'center',
					formatter: function(value,row,index){
						if (row.product){
							return row.product.name;
						}
					}},
			{field:'product.pinyin',title:'拼音',width:10,align:'center',
						formatter: function(value,row,index){
							if (row.product){
								return row.product.pinyin;
							}
						}},
			{field:'product.producerName',title:'生产企业',width:15,align:'center',
							formatter: function(value,row,index){
								if (row.product){
									return row.product.producerName;
								}
							}},
			{field:'product.dosageFormName',title:'剂型',width:10,align:'center',
								formatter: function(value,row,index){
									if (row.product){
										return row.product.dosageFormName;
									}
								}},
			{field:'product.model',title:'规格',width:10,align:'center',
									formatter: function(value,row,index){
										if (row.product){
											return row.product.model;
										}
									}},
			{field:'product.packDesc',title:'包装',width:10,align:'center',
										formatter: function(value,row,index){
											if (row.product){
												return row.product.packDesc;
											}
										}},
        	{field:'product.unitName',title:'单位',width:10,align:'center',
											formatter: function(value,row,index){
												if (row.product){
													return row.product.unitName;
												}
											}},
        	{field:'contract.vendorName',title:'供应商',width:20,align:'center',
												formatter: function(value,row,index){
													if (row.contract){
														return row.contract.vendorName;
													}
												}},
        	{field:'contract.endValidDate',title:'截止日期',width:15,align:'right',
				formatter: function(value,row,index){
					if (row.contract){
						return row.contract.endValidDate;
					}
				}},
			{field:'lastNum',title:'可购余量',width:10,align:'right',
				formatter: function(value,row,index){
					return row.contractNum-row.purchasePlanNum-row.cartNum;
				}},
			{field:'price',title:'价格（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}},
        	{field:'cartNum',title:'数量',width:10,align:'center',editor:{type:'numberbox',options:{validType:'pinteger'}},
					formatter: function(value,row,index){
						if (row.cartNum >0){
							return row.cartNum;
						}
						
					}},
        	{field:'do',title:'操作',width:10,align:'center',
        		formatter: function(value,row,index){
        			if(row.cartNum)
						return "<a class='dgbtn' href='#' onclick='addCart("+index+")' class='easyui-linkbutton'>修改</a>";
					else 
						return "<a class='dgbtn' href='#' onclick='addCart("+index+")' class='easyui-linkbutton'>加入</a>";
				}}
   		]],
   		toolbar: [{
			iconCls: 'icon-import',
			text:"导入",
			handler: function(){
				$("#myfile").click();
			}
		},'-',{
			iconCls: 'icon-xls',
			text:"模板下载",
			handler: function(){
				$.messager.confirm('确认信息', '确认下载?', function(r){
					if (r){
						window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/placeOrderContract/exportExcel.htmlx");
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
		}
	});
	
	$('#dg').datagrid('enableFilter', [{
		 field:'price',
		 type:'text',
		 isDisabled:1
	},{
		 field:'contract.endValidDate',
		 type:'text',
		 isDisabled:1
	},{
		 field:'lastNum',
		 type:'text',
		 isDisabled:1
	},{
		 field:'cartNum',
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
	
	var contractDId = row.id;
	var lastNum = row.contractNum-row.purchasePlanNum-row.cartNum;
	
	var ed2 = $('#dg').datagrid('getEditor', {index:index,field:'cartNum'});
	if(ed2 == null)
		return;
	var isValid = $(ed2.target).textbox('isValid');
	if(!isValid){
		return;
	}		
	
	var goodsNum = ed2.target.val();
	if(goodsNum == ""){
		showErr("数量不能为空");
		return false;
	}
	if(goodsNum > lastNum){
		showErr("数量不能超过余量［"+lastNum+"］");
		return false;
	}
	
	addCartAjax(contractDId,goodsNum);
	
}

function addCartAjax(contractDId,goodsNum){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/placeOrderContract/add.htmlx",
		data:{
			"contractDId": contractDId,
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