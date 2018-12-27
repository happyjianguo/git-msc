<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
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

</style>

<body class="easyui-layout"  >
    <div data-options="region:'north',title:'',collapsible:false"  class="my-north" style="height:300px;">
		<shiro:hasPermission name="dm:productPrice:add">
			<div id="toolbar" class="search-bar" >
		        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addLeftOpen()">添加</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		    </div>
	    </shiro:hasPermission>	
		<table  id="dg" ></table>
    </div>
    <div data-options="region:'center',title:''"  class="my-center">
    	<form id="fileId" method="POST" enctype="multipart/form-data">
			<input type="file" name="myfile" class="file" id="myfile" />
		</form>
		<shiro:hasPermission name="dm:productPrice:add">
			<div id="toolbar2" class="search-bar" >
		        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addOpen()">添加</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="delFunc()">删除</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-import',plain:true"  onclick='$("#myfile").click();'>导入</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		  		<a href="<c:out value='${pageContext.request.contextPath }'/>/resources/template/ppTemplate.xlsx"  class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true" target="_blank">模板下载</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true"  onclick="dotaskAjax()">价格生效</a>
		    </div>
	    </shiro:hasPermission>
        <table id="dg2"></table>
    </div>
</body>

</body>
</html>

<script>
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
	url :" <c:out value='${pageContext.request.contextPath }'/>/dm/productPrice/upload.htmlx",
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
			$('#dg').datagrid('reload');
			//parent.addTab("购物车", "/hospital/cart.htmlx", null);
			showMsg(result.msg);
		}else{
            showErr(result.msg);
		}
	},
	error:function(){
		showErr("出错，请刷新重新操作");
	}
});

//搜索
function searchList(val,name){
	var val = $('#ss').searchbox("getValue");
	var name = $('#ss').searchbox("getName");

	var data = {"query['t#isDisabled_I_EQ']": 0};
	var vendorCode = $('#vendor').combobox("getValue");
	if(vendorCode != null){
		data["query['t#vendorCode_L_EQ']"] = vendorCode;
	}
	if(name=="name"){
		data["query['t#productName_S_LK']"] = val;
	}else if(name=="code"){
		data["query['t#productCode_S_LK']"] = val;
	}
	$('#dg').datagrid('reload',data);	
}
//初始化
$(function(){
	$('#ss').searchbox({
		searcher:function(value,name){ 
			searchList(value,name);
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 
	
	$('#vendor').combobox({
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/company/list.htmlx",
	    valueField:'code',    
	    textField:'fullName', 
	    queryParams:{
	    	"query['t#isVendor_I_EQ']":1,
	    	"query['t#isDisabled_I_EQ']":0
		},
		editable:false,
		width:200,
		onChange:function(newValue,oldValue){
			searchList();
		}
	});
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(".my-north").height(),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productGPO/page.htmlx",
		queryParams:{
		},
		pageSize:10,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
					{field:'VENDORNAME',title:'供应商',width:20,align:'center'},
					{field:'PRODUCTCODE',title:'药品编码',width:20,align:'center'},
		        	{field:'PRODUCTNAME',title:'药品名称',width:20,align:'center'},
					{field:'PRODUCERNAME',title:'生产企业',width:20,align:'center'},
					{field:'MODEL',title:'规格',width:20,align:'center'},
					{field:'PACKDESC',title:'包装规格',width:20,align:'center'}
		        	
		   		]],
   		toolbar: "#toolbar",
		onLoadSuccess:function(){
			$('#dg').datagrid('doCellTip',{delay:500}); 
		},
		onClickRow: function(index,row){
			search();  
		}
	});
	
	$('#dg').datagrid('enableFilter', [{
        field:'VENDORNAME',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            url: " <c:out value='${pageContext.request.contextPath }'/>/set/company/list.htmlx",
    	    valueField:'code',    
    	    textField:'fullName', 
    	    queryParams:{
    	    	"query['t#isVendor_I_EQ']":1,
    	    	"query['t#isDisabled_I_EQ']":0
    		},
    		editable:false,
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'VENDORNAME');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'code',
                        op: 'EQ',
                        fieldType:'c#S',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
    },{
		 field:'PRODUCERNAME',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'MODEL',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'PACKDESC',
		 type:'text',
		 fieldType:'p#S'
	}]
	
	);
	
	//datagrid
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  '100%',
		pagination:true,
		
		pageSize:10,
		pageNumber:1,
		columns:[[
		        	/* {field:'vendorName',title:'配送企业',width:15,align:'center'}, */
		        	{field:'hospitalName',title:'价格类型',width:30,align:'center',
		        		formatter: function(value,row,index){
		   					if(row.tradeType == "patient"){
		   						return "个人价格";
		   					}
							if (row.hospitalName){
								return row.hospitalName;
							}else{
								return "医院统一价格";
							}
						}
		        	},
		        	{field:'finalPrice',title:'成交价',width:20,align:'right',
						formatter: function(value,row,index){
							if (row.finalPrice){
								return common.fmoney(row.finalPrice);
							}
						}},
		        	 {field:'beginDate',title:'生效日期',width:40,align:'center',
		        		formatter: function(value,row,index){
		        			var beginDate = "";
		        			var outDate = "";
		        			if(row.beginDate){
		        				beginDate = $.format.date(row.beginDate,"yyyy-MM-dd");
		        			}
		        			
							return beginDate ;
						}
		        	},
		        	/* 
		        	{field:'unitName',title:'单位',width:10,align:'center'}, */
		        	{field:'createdate',title:'新增日期',width:20,align:'center',
						formatter: function(value,row,index){
							if (row.createDate){
								return $.format.date(row.createDate,"yyyy-MM-dd");
							}
						}}, 
		        	{field:'isDisabled',title:'是否禁用',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.isDisabled == 1){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png\" />";
							}
						}
		        	}, 
		        	{field:'isEffected',title:'是否生效',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.isEffected == 0){
								return "待生效";
							}else if (row.isEffected == 1){
								return "生效";
							}else if (row.isEffected == 2){
								return "过期";
							}
						}
		        	}, 
		        	{field:'effectType',title:'统一价格生效方式',width:10,align:'center',
		        		formatter: function(value,row,index){
		        			if(row.tradeType == "hospital" && row.hospitalCode == null){
		        				if (row.effectType == 0){
									return "不覆盖指定价格";
								}else if (row.effectType == 1){
									return "覆盖所有";
								}
		        			}
						}
		        	}
		   		]],
		toolbar: "#toolbar2",
		onDblClickRow:function(index,row){
			//editOpen();
		},
		onLoadSuccess:function(){
			$('#dg').datagrid('doCellTip',{delay:500}); 
		}
	});
	
});

//left删除
function delLeftFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			delLeftAjax(selobj.ID);
		}
	});
}
//删除
function delFunc(){
	var selobj = $('#dg2').datagrid('getSelected');
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
function search(){
	var selrow = $('#dg').datagrid('getSelected');
	$('#dg2').datagrid({  
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productPrice/page.htmlx",
	    queryParams:{
	    	productCode:selrow.PRODUCTCODE,
	    	vendorCode:selrow.VENDORCODE
	    }
	}); 
}

//left弹窗增加
function addLeftOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 420,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/productGPO/add.htmlx",
		onLoad:function(){
			
		},
		onDestroy:function(){
			$('#dg').datagrid("reload");
		}
	});
}
//弹窗增加
function addOpen() {
	var selrow = $('#dg').datagrid('getSelected');//alert(selrow.PRODUCTCODE);alert(selrow.VENDORCODE);
	top.$.modalDialog({
		title : "添加",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/productPrice/add.htmlx",
		queryParams:{
			productCode:selrow.PRODUCTCODE,
			vendorCode:selrow.VENDORCODE,
	    },
		onLoad:function(){
			
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg2');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
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
	var selrow = $('#dg2').datagrid('getSelected');
	top.$.modalDialog({
		title : "修改",
		width : 600,
		height : 400,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/productPrice/edit.htmlx",
		queryParams:{
			"id":selrow.id
		},
		onLoad:function(){
			if(selrow){
				var f = top.$.modalDialog.handler.find("#form1");

				/* if(!isempty(selrow.beginDate)){
					selrow.beginDate = $.format.date(selrow.beginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.outDate)){
					selrow.outDate = $.format.date(selrow.outDate,"yyyy-MM-dd");
				} */
				f.form("load", selrow);
			}
		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg2');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
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
function dotaskAjax(){
	var selrow = $('#dg2').datagrid('getSelected');
	if(selrow){
		$.messager.confirm('确认信息', '您确认要生效价格吗?', function(r){
			if (r){
				$.ajax({
					url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productPrice/dotask.htmlx",
					data:"id="+selrow.id,
					dataType:"json",
					type:"POST",
					cache:false,
					success:function(data){
						showMsg("执行成功");
					},
					error:function(){
						showErr("出错，请刷新重新操作");
					}
				});	
			}
		});
	}
	
	
}
function delLeftAjax(id){
	$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productGPO/del.htmlx",
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

function delAjax(id){
	$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/dm/productPrice/del.htmlx",
				data:"id="+id,
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dg2').datagrid('reload');  
						showMsg(data.msg);
					} 
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}


</script>