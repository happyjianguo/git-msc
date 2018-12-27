<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<div class="single-dg">
<input type="hidden" id="projectDetailId">
	<table  id="dg" ></table>
</div>

<script>
//搜索
function search(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		border:true,
		height : 355,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/choose.htmlx",
		remoteFilter: true,
		columns:[[
        	{field:'vendorCode',title:'供应商编码',width:10,align:'center'},
        	{field:'vendorName',title:'供应商名称',width:10,align:'center'},
        	{field:'productName',title:'药品名称',width:10,align:'center'},
        	{field:'model',title:'规格',width:10,align:'center'},
        	{field:'producerCode',title:'厂商编码',width:10,align:'center'},
        	{field:'producerName',title:'厂商名称',width:10,align:'center'},
        	{field:'price',title:'价格',width:10,align:'center'},
        	{field:'productId',title:'对应药品',width:10,align:'center',
				editor:{
					type:'combogrid',
					options:{
						idField:'id',    
						textField:'name',
						url: " <c:out value='${pageContext.request.contextPath }'/>/dm/product/page.htmlx",
						 queryParams:{  
							 "query['t#isDisabled_I_NE']":"1",
						}, 
						pagination:true,
						pageSize:20,
						pageNumber:1,
						panelAlign:"right",
					    columns: [[
					        {field:'code',title:'药品编码',width:80},
					        {field:'name',title:'药品名称',width:150},
					        {field:'dosageFormName',title:'剂型',width:80},
					        {field:'model',title:'规格',width:80},
					        {field:'packDesc',title:'包装规格',width:80},
					        {field:'producerName',title:'生产厂家',width:150}
					    ]],
					    width:200,
					    panelWidth:650,
						required: true,
						delay:800,
						prompt:"名称模糊搜索",
						keyHandler: {
				            query: function(q) {
				            	/* var type = $('#orgType').combobox('getValue');
				            	if(type == 4){// 供应商c 取资料同 供应商
				            		type = 2;
				            	}
				                //动态搜索
				                $('#orgId').combogrid('grid').datagrid("reload",{"query['t#orgName_S_LK']":q, "query['t#orgType_I_EQ']":type});
				                $('#orgId').combogrid("setValue", q); */
				            }

				        }
					}
				},
				formatter: function(value,row,index){
					return row.productId;
				}
			},
			{field:'status',title:'评标结果',width:10,align:'center',
				editor:{
					type:'combobox',
					options:{
						valueField:'label',    
					    textField:'value',panelHeight:'auto',
						data: [{
							label: 'unwin',
							value: '未中标'
						},{
							label: 'win',
							value: '中标'
						}]
					}
				},
				formatter: function(value,row,index){
					if(row.status =="unwin"){
						return "未中标";
					}else if(row.status =="win"){
						return "中标";
					}
					return "";
				}
			},
        	{field:'do',title:'操作',width:10,align:'center',
				formatter: function(value,row,index){
					return "<a class='dgbtn2' href='#' onclick='doChoose("+index+")' class='easyui-linkbutton'>确认</a>";
			}}
   		]],
	    queryParams:{
	    	"projectDetailId":$("#projectDetailId").val()
	    },
		onClickRow:function(index,row){
			$('#dg').datagrid('beginEdit', index);
		}
	});
	$('#dg').datagrid('enableFilter',[{
		 field:'genericName',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'dosageFormName',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'model',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'qualityLevel',
		 type:'text',
		 fieldType:'t#S'
	},{
		 field:'minUnit',
		 type:'text',
		 fieldType:'t#S'
	}]);
	
}

function doChoose(index){
	var row = $("#dg").datagrid("getRows")[index];

	var ed1 = $('#dg').datagrid('getEditor', {index:index,field:'productId'});
	if(ed1 == null){
		showErr("请选择对应药品");
		return;
	}
	var ed = $('#dg').datagrid('getEditor', {index:index,field:'status'});
	if(ed == null){
		showErr("请确定评标结果");
		return;
	}
	
	
	var isValid = $(ed.target).textbox('isValid');
	if(!isValid){
		return;
	}		
	var status = $(ed.target).combobox("getValue");
	var productId = $(ed1.target).combobox("getValue");
	doChooseAjax(row.id,productId,status);
}

//=============ajax===============
function doChooseAjax(id,productId,status){
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/gpo/projectDetail/dochoose.htmlx",
		data:{
			"id":id,
			"status":status,
			"productId":productId
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');  
				showMsg("操作成功！");
			} else {
				showMsg(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

</script>