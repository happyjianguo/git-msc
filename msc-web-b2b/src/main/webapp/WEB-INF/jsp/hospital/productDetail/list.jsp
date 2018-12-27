<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
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
<body class="easyui-layout" >

	<div class="single-dg">
		<table  id="dg" ></table>
	</div>
</body>
</html>

<script>
    var isVail=true;
//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() - 4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/productDetail/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'code',title:'药品编码',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.code;
					}
				}
        	},
        	{field:'name',title:'药品名称',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.name;
					}
				}
        	},
        	{field:'genericName',title:'通用名',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.genericName;
					}
				}
        	},

        	{field:'dosageFormName',title:'剂型',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.dosageFormName;
					}
				}},
        	{field:'model',title:'规格',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.model;
					}
				}},
        	{field:'packDesc',title:'包装',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.packDesc;
					}
				}},
        	{field:'unitName',title:'单位',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.unitName;
					}
				}},
            {field:'authorizeNo',title:'国药准字',width:10,align:'center',
                formatter: function(value,row,index){
                    if (row.product){
                        return row.product.authorizeNo;
                    }
                }},
        	{field:'producerName',title:'生产企业',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.producerName;
					}
				}},
        	{field:'price',title:'价格（元）',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}},
			 {field:'gpoName',title:'gpo名称',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.gpoName;
					}
				}},
        	{field:'vendorName',title:'供应商名称',width:10,align:'center'},
        	{field:'num',title:'数量',width:10,align:'center',editor:{type:'numberbox',options:{validType:'pinteger'}}},
 	        {field:'options',title:'操作',width:10,align:'center',
        		formatter: function(value,row,index){
					return "<a class='dgbtn' href='#'  onclick='addDetail("+index+")' class='easyui-linkbutton'>加入</a>";
			}}
   		]],
   		toolbar: [{
			iconCls: 'icon-ok',
			text:"批量加入",
			id : "addList",
			handler: function(){
				addDetailList();
			}
		},'-',{
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
						window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/productDetail/exportExcel.htmlx?query[product.isGPOPurchase_I_EQ]=1");
					}
				});
				//location.href = " <c:out value='${pageContext.request.contextPath }'/>/resources/template/orderTemplate.xlsx";
			}
		} ],
		onDblClickRow:function(index,row) {
			
		},
		onClickRow: function(index,row){
			$('#dg').datagrid('beginEdit', index);
		},
		onLoadSuccess:function(data){
			if ($("#fileId").length > 0) {
				return;
			}
			$("#fileImport").find("span:first").append('<form id="fileId" method="POST" enctype="multipart/form-data"><input type="file" name="myfile" class="file" id="myfile"/></form>');
			$("#myfile").on("change",function(){
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
				url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/upload.htmlx",
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
						parent.addTab("合同明细", "/hospital/contractDetail.htmlx", null);
					}else{
						showErr(result.msg);
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
			});
		},
		queryParams:{
			"query[product.isGPOPurchase_I_EQ]":1
		}
	});
	$('#dg').datagrid('enableFilter', 
		[{
			 field:'code',
			 type:'text',
			 fieldType:'product#S'
		},{
			 field:'name',
			 type:'text',
			 fieldType:'product#S'
		},{
			 field:'genericName',
			 type:'text',
			 fieldType:'product#S'
		},{
			 field:'dosageFormName',
			 type:'text',
			 fieldType:'product#S'
		},{
			 field:'model',
			 type:'text',
			 fieldType:'product#S'
		},{
			 field:'packDesc',
			 type:'text',
			 fieldType:'product#S'
		},{
			 field:'unitName',
			 type:'text',
			 fieldType:'product#S'
		},{
			 field:'producerName',
			 type:'text',
			 fieldType:'product#S'
		},{
			 field:'price',
			 type:'text',
			 fieldType:'BD'
		},{
			 field:'gpoName',
			 type:'text',
			 fieldType:'product#S'
		},{
			 field:'vendorName',
			 type:'text',
			 fieldType:'S'
		},{
	        field:'num',
	        type:'text',
	    	isDisabled:1
	    },{
	        field:'options',
	        type:'text',
	    	isDisabled:1
	    }]);

});


function addDetail(index){
	var row = $("#dg").datagrid("getRows")[index];
	var productId = row.product.id;
	var gpoCode = row.product.gpoCode;
	var vendorCode = row.vendorCode;
	var price = row.price;
	var ed2 = $('#dg').datagrid('getEditor', {index:index,field:'num'});
	if(ed2 == null)
		return;
	var num = ed2.target.val();
	if(num == ""){
		showErr("数量不能为空");
		return false;
	}
	var isValid = $(ed2.target).textbox('isValid');
	if(!isValid){
		showErr("请输入正整数");
		return false;
	}			
	if (price == null || price == "" ||price <= 0 ) {
		//showErr("价格尚未维护，无法加入生成合同");
		//return false;
	}
	addDetailAjax(productId, num, gpoCode, vendorCode,index);
	
}

function addDetailAjax(productId, num, gpoCode, vendorCode,index){
    if(isVail){
        $.ajax({
            url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/add.htmlx",
            data:{
                "productId": productId,
                "num":num,
                "gpoCode":gpoCode,
                "vendorCode":vendorCode
            },
            dataType:"json",
            type:"POST",
            cache:false,
            beforeSend: function(){
                isVail=false;
            },
            success:function(data){
                if(data.success){
                    //$('#dg').datagrid('reload');
                    showMsg("加入成功");
                } else{
                    showErr(data.msg);
                }
                isVail=true;
            },
            error:function(jqXHR, textStatus, errorThrown){
                if(errorThrown != 'abort'){
                    showErr("出错，请刷新重新操作");
                }
            }
        });
	}
}

function detailFunc(id){
	top.addTab("报量内容","/hospital/contractDetail.htmlx?contractId="+id);
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

//搜索
function search(val,name){
	var data = {};
	data["query['t#" + name + "_S_LK']"] = val;
	$('#dg').datagrid('load',data);
}

function addDetailList(){
	var selobjs = $('#dg').datagrid('getRows');
	if(selobjs.length == 0){
		showErr("请至少选择一行");
		return;
	}
	
	var datas = new Array();
	var flag = true;
	var flagindex = 0;
	$.each(selobjs,function(index,row){
		 var selIndex = $('#dg').datagrid("getRowIndex",row);
		 var ed = $('#dg').datagrid('getEditor', { index: selIndex, field: 'num' });
		 if(ed != null){
			 var number = $(ed.target).numberbox('getValue');
			 var productId = row.product.id;
			 var gpoCode = row.product.gpoCode;
			 var vendorCode = row.vendorCode;
		
		     if(number&&number!=null){
		   		 var data = new Object();
			     data.number = number;
			     data.productId = row.product.id;
			     data.gpoCode = row.product.gpoCode;
			     data.vendorCode = row.vendorCode;
			     datas[flagindex++] = data;
		     }
		 }
	     
	   	 
	});
	if(datas.length == 0){
		showErr("请输入数量");
	}
	if(flag && datas.length > 0){
		$.messager.confirm('确认信息', '您确认要批量操作吗?', function(r){
			if (r){
				addDetailListAjax(datas);
			}
		});
	}
	
}
function addDetailListAjax(datas){
	var data = JSON.stringify(datas);
	if(isVail){
        $.ajax({
            url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/addList.htmlx",
            data:{
                "data": data
            },
            dataType:"json",
            type:"POST",
            cache:false,
            beforeSend: function(){
                isVail=false;
            },
            success:function(data){
                if(data.success){
                    //$('#dg').datagrid('reload');
                    showMsg("批量操作成功");
                } else{
                    showErr(data.msg);
                }
                isVail=true;
            },
            error:function(jqXHR, textStatus, errorThrown){
                if(errorThrown != 'abort'){
                    showErr("出错，请刷新重新操作");
                }
            }
        });
	}
}

//=============ajax===============
function delAjax(id){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/delete.htmlx",
		data:{id:id},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("删除成功！");
			} else {
				showMsg(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function submitOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail.htmlx",
		buttons : [{
			text : '提交',
			iconCls : 'icon-ok',
			handler : function() {
				addContract();
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
function addContract(){
	top.$.modalDialog({
		title : "添加",
		width : 500,
		height : 300,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/add.htmlx",
		buttons : [{
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				
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
</script>