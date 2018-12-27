<%@page import="com.shyl.common.web.datasource.ApplicationContextHolder"%>
<%@page import="com.shyl.msc.common.CommonProperties"%>
<%@page import="com.shyl.common.web.util.VerifyCodeUtil"%>
<%@ page import="com.shyl.common.cache.dao.VerifyCodeDAO" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
<OBJECT  id="obj" classid="clsid:3BC3C868-95B5-47ED-8686-E0E3E94EF366" style="display:none;"></OBJECT>

<div id="tt">
	    <div title="合同列表" class="my-tabs" >
	       <div id="toolbar" class="search-bar" style="height:30px;">
			 <shiro:hasPermission name="hospital:contract:sign"> 
		        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-ok',plain:true" onclick="okFunc()">签订</a>
		        <span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="delFunc()">作废</a>
		  		<span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="paraOpen()">合同终止申请</a>
		  		<span class="datagrid-btn-separator split-line" ></span>
		    </shiro:hasPermission>
		  	<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true"  onclick="filterFunc()">数据过滤</a>
		    药品编码: 
	 	<input id="productCode" class="easyui-textbox" style="width:100px"/>
	 	药品名称: 
	 	<input id="productName" class="easyui-textbox" style="width:100px"/>
	 	拼音检索: 
	 	<input id="pinyin" class="easyui-textbox" style="width:100px"/>
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
	    </div>
  		<select id="UserList" name="userName" style="display:none"></select>
		<input type="hidden" ID="UserSignedData" name="UserSignedData">
		<input type="hidden" ID="UserCert" name="UserCert"> 
		<table  id="dg" ></table>
	    </div>
	    <div title="合同明细" class="my-tabs" >
	    
	    	<div id="tbd" class="search-bar" >
	    	<shiro:hasPermission name="hospital:contract:sign">
	    		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true"  onclick="deleteD()">删除</a>
		  		<span class="datagrid-btn-separator split-line" ></span>
		  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="paraOpenD()">合同终止申请</a>
		  		<span class="datagrid-btn-separator split-line" ></span>
		  	</shiro:hasPermission>
	    		<b>三方合同编号：</b><i id="contractCode"></i>
	    		<span class="datagrid-btn-separator split-line" ></span>
	    		<b>医院名称：</b><i id="hospitalName"></i>
	    		<span class="datagrid-btn-separator split-line" ></span>
	    		<b>供应商名称：</b><i id="vendorName"></i>
	    		<span class="datagrid-btn-separator split-line" ></span>
	    	</div>
	       <table id="dgDetail"></table>
	    </div>
	</div>
</body>
</html>
<%
String isCA = request.getAttribute("ISCAUSED")==null?"0":(String)request.getAttribute("ISCAUSED");
String strRandom = "";
String test = "";
if(isCA.equals("1")){
	strRandom = VerifyCodeUtil.generateTextCode(0, 4, null);

	VerifyCodeDAO verifyCodeDAO = (VerifyCodeDAO)ApplicationContextHolder.getBean("verifyCodeDAO");
    verifyCodeDAO.save("pdfSignRandom/"+request.getSession().getId(), strRandom);
} 
%>
<script>
  var webName = " <c:out value='${pageContext.request.contextPath }'/>";
  var CAName = "Sgl_CA";
  var strServerRan = "<%=strRandom%>";
  var tes = "<%=test%>";
  //console.log(strServerRan);
</script>
<script  type="text/javascript" src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js/ca/ComCA.js"></script>
<script>
//搜索
function dosearch(){
	var productCode = "";
	var productName = "";
	var pinyin="";
	if($('#productCode').textbox('getValue')!=""){
		productCode=$('#productCode').textbox('getValue');
	}
	if($('#productName').textbox('getValue')!=""){
		productName=$('#productName').textbox('getValue');	
	}
	if($('#pinyin').textbox('getValue')!=""){
		pinyin=$('#pinyin').textbox('getValue').toUpperCase();	
	}
	var data = {
		"query['p#code_S_EQ']":productCode,
		"query['p#name_S_LK']":productName,
		"query['p#pinyin_S_LK']":pinyin
	};
	$('#dg').datagrid('load',data);
}

var isFilter=0;
function filterFunc(){
	if(isFilter == 1) return;
	isFilter=1;
	$('#dg').datagrid({remoteFilter: true});
	$('#dg').datagrid('enableFilter', 
			[{
		        field:'STATUS',
		        type:'combobox',
		        options:{
		            panelHeight:'auto',
		            editable:false,
		            data:[{value:'',text:'-请选择-'},
		                {value:'unsigned',text:'待签订'},
		                {value:'rejected',text:'已驳回'},
		                {value:'hospitalSigned',text:'医院签订'},
		                {value:'signed',text:'已签订'},
		                {value:'executed',text:'执行完'},
		                {value:'cancel',text:'已作废'},
		                {value:'stop',text:'已终止'}
		            ],
		            onChange:function(value){
		                if (value == ''){
		                	$('#dg').datagrid('removeFilterRule', 'status');
		                } else {
		                	$('#dg').datagrid('addFilterRule', {
		                        field: 'status',
		                        op: 'EQ',
		                        fieldType:'L',
		                        value: value
		                    });
		                }
		                $('#dg').datagrid('doFilter');
		            }
		        }
		    },{
		        field:'STARTVALIDDATE',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'S',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'ENDVALIDDATE',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'S',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'EFFECTIVEDATE',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'D',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'HOSPITALCONFIRMDATE',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'D',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'VENDORCONFIRMDATE',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'D',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'GPOCONFIRMDATE',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'D',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'FILEPATH',
		        type:'text',
		    	isDisabled:1
		    }]);
}
//初始化
$(function(){
	$('#tt').tabs({
		plain:true,
		justified:true
	});
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(this).height()-($("#toolbar").length ==0?0:30),
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/page.htmlx",
		queryParams:{  
	    	"query['t#code_S_EQ']":"<c:out value='${code}'/>",
	    },
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'CODE',title:'三方合同编号',width:20,align:'center'},
        	{field:'HOSPITALNAME',title:'医院名称',width:10,align:'center'},
        	{field:'GPONAME',title:'GPO名称',width:10,align:'center'},
        	{field:'VENDORNAME',title:'供应商名称',width:10,align:'center'},
        	{field:'HOSPITALCONFIRMDATE',title:'医院确认时间',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.HOSPITALCONFIRMDATE){
						return $.format.date(row.HOSPITALCONFIRMDATE,"yyyy-MM-dd HH:mm");
					}
				}
        	},
        	{field:'GPOCONFIRMDATE',title:'gpo确认时间',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.GPOCONFIRMDATE){
						return $.format.date(row.GPOCONFIRMDATE,"yyyy-MM-dd HH:mm");
					}
				}
        	},
        	{field:'VENDORCONFIRMDATE',title:'供应商确认时间',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.VENDORCONFIRMDATE){
						return $.format.date(row.VENDORCONFIRMDATE,"yyyy-MM-dd HH:mm");
					}
				}
        	},
        	{field:'STARTVALIDDATE',title:'有效期起',width:10,align:'center'},
        	{field:'ENDVALIDDATE',title:'有效期止',width:10,align:'center'},
        	{field:'EFFECTIVEDATE',title:'生效时间',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.EFFECTIVEDATE){
						return $.format.date(row.EFFECTIVEDATE,"yyyy-MM-dd HH:mm");
					}
				}
        	},
        	{field:'FILEPATH',title:'签订PDF',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.FILEPATH){
						return "<a href=\" <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/readfile.htmlx?id="+row.ID+"\" target='_bank'><img src=\" <c:out value='${pageContext.request.contextPath }'/>/resources/images/fileDown.png\" width=16 height=16  />点击下载</a>";
					}
				}
        	},
        	{field:'STATUS',title:'状态',width:10,align:'center',
				formatter: function(value,row,index){
					if (value == "1"){
						return "待签订";
					} else if (value == "3"){
						return "已驳回";
					} else if (value == "2"){
						return "医院签订";
					} else  if(value == "4"){
						return "已签订";
					} else if(value == "5"){
						return "执行完";
					} else if(value == "6"){
						return "已作废";
					} else if(value == "7"){
						return "已终止";
					}
				}}
   		]],
		toolbar:"#toolbar",
		onDblClickRow:function(index,row) {
			$("#tt").tabs("select",1);
        	searchDefectsList(row);
		},
		onLoadSuccess: function(data){
			//var rows=$(this).datagrid("getRows");
			//大于1行默认选中
			//if($(this).datagrid("getRows").length > 0) {
				//$(this).datagrid('selectRow', 0);
	        	//searchDefectsList($(this).datagrid("getRows")[0]);
			//}
			$('#dg').datagrid('doCellTip',{delay:500}); 
		},
	});
	
		
	$('#dgDetail').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(this).height()-($("#tbd").length ==0?0:50),
		pagination:true,
		pageSize:20,
		pageNumber:1,
		showFooter: true,
		toolbar:"#tbd",
		columns:[[
        	{field:'productCode',title:'药品编码',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.code;
					}
				}
        	},
        	{field:'productName',title:'药品名称',width:20,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.name;
					}
				}
        	},
        	{field:'product.genericName',title:'通用名',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.genericName;
					}
				}
        	},
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
        	{field:'product.producerName',title:'生产企业',width:15,align:'center',
				formatter: function(value,row,index){
					if (row.product){
						return row.product.producerName;
					}
				}},
        	{field:'price',title:'单价（元）',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.price){
						return common.fmoney(row.price);
					}
				}},
        	{field:'contractNum',title:'合同数量',width:10,align:'right'},
        	{field:'contractAmt',title:'合同金额',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.contractAmt){
						return common.fmoney(row.contractAmt);
					}
				}},
        	{field:'purchasePlanNum',title:'采购计划数量',width:10,align:'right'},
        	{field:'purchaseNum',title:'采购数量',width:10,align:'right'},
        	{field:'deliveryNum',title:'配送数量',width:10,align:'right'},
        	{field:'returnsNum',title:'退货数量',width:10,align:'right'},
        	{field:'status',title:'是否终止',width:10,align:'right',
				formatter: function(value,row,index){
					if (row.status == "stop"){
						return "已终止";
					}
				}
        	}
   		]],		
	});
});



//弹窗增加
function paraOpen(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		showErr("请选中一笔数据");
		return;
	}
  	if(selobj.STATUS != "4"){
		showErr("请选择［已签订］的合同");
		return;
	}  
  	
	var id= selobj.ID;
	top.$.modalDialog({
		title : "合同终止申请",
		width : 600,
		height : 300,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/close.htmlx",
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
				var reason = f.find("#reason").textbox("getValue");
				closeAjax(id, reason);
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

//弹窗增加
function paraOpenD(){
	var selobj = $('#dg').datagrid('getSelected');
	var selobjD = $('#dgDetail').datagrid('getSelected');
	if(selobjD == null){
		showErr("请选中一笔数据");
		return;
	}
  	if(selobj.STATUS != "4"){
		showErr("请选择［已签订］的合同");
		return;
	}  
	var id= selobjD.id;
	top.$.modalDialog({
		title : "合同终止申请",
		width : 600,
		height : 300,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/close.htmlx",
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
				var reason = f.find("#reason").textbox("getValue");
				closeDAjax(id, reason);
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


//查询
function searchDefectsList(row){
	$("#contractCode").html(row.CODE);
	$("#hospitalName").html(row.HOSPITALNAME);
	$("#vendorName").html(row.VENDORNAME);
	$('#dgDetail').datagrid({  
	    url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/mxlist.htmlx",  
	    queryParams:{  
	    	"query['t#contract.id_L_EQ']":row.ID,
	    }  
	});
}
function detailFunc(id){
	top.addTab("合同明细","/hospital/contractDetail.htmlx?contractId="+id);
}
//作废
function delFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
  	if(selobj.STATUS != "1" && selobj.STATUS != "3"){
		showErr("请选择［待签订］或者［被驳回］的合同");
		return;
	}  
  	
	$.messager.confirm('确认信息', '您确认要作废合同吗?', function(r){
		if (r){
			delAjax(selobj.ID);
		}
	});
}


//医院签订
function okFunc(){
	var selobj = $('#dg').datagrid('getSelected');
	if(selobj == null){
		return;
	}
 	if(selobj.STATUS != "1" && selobj.STATUS != "3"){
		showErr("请选择［待签订］或者［被驳回］的合同");
		return;
	}   
 	
 	<% if(isCA.equals("1")) { %>
 		okwztAjax(selobj);
 	<% }else{ %>
	 	$.messager.confirm('确认信息', '您确认要签订合同吗?', function(r) {
			if (r){
				okAjax(selobj.ID);
			}
		});
  	<%} %>
}


//搜索
function deleteD(){
	var selobj = $('#dg').datagrid('getSelected');
	var selobjD = $('#dgDetail').datagrid('getSelected');
	if(selobjD == null){
		showErr("请选中一笔数据");
		return;
	}
	
  	if(selobj.STATUS != "1"){
		showErr("请选择［待签订］的合同");
		return;
	}  
  	$.messager.confirm('确认信息', '您确认要删除吗?', function(r){
		if (r){
			deleteDAjax(selobjD.id);
		}
	});
  	
}
//=============ajax===============
function okAjax(id){
	var UserCert = $('#UserCert').val();
	var UserSignedData =   $('#UserSignedData').val();
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/ok.htmlx",
		data:{
			"id":id,
			"UserCert":UserCert,
			"UserSignedData":UserSignedData
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload');
				showMsg("签订成功！");
			} else {
				showMsg(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function okwztAjax(selobj){
	var id = selobj.ID;
	var cert = getCertByOid("<c:out value='${oid}'/>");
	if (cert == null || cert == undefined) {
		showMsg("当前账号绑定证书与插入证书不一致");
		return false;
	}
	var url = "?id="+selobj.CODE+"&PageNum="+selobj.PAGENUM+"&PageX="+selobj.HOSPITALX+"&PageY="+selobj.HOSPITALY+"&SizeW=75&SizeH=75&Oid=<c:out value='${currentUser.clientCert}'/>";
	
	var UserCert = getBase64(cert);
	var UserSignedData =  getSignDate(cert);
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/okwzt.htmlx",
		data:{
			"id":id,
			"UserCert":UserCert,
			"UserSignedData":UserSignedData
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				url = data.data + url;
				window.open(url,"_blank");
                $.ajax({
                    type:'post',
                    url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/checkContract.htmlx",
                    data:{'id':id},
                    async:true,
                    dataType:'json',
                    success:function(data){
                        if(data.success){
                            showMsg("签订成功！");
                            $('#dg').datagrid('reload');
                        }
                    }
                })
			} else {
				showMsg(data.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});	
}

function delAjax(id){
	$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/delete.htmlx",
				data:{id:id},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dg').datagrid('reload');  
						showMsg("合同作废成功！");
					} else {
						showMsg(data.msg);
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}

function closeAjax(id,reason){
	$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/close.htmlx",
				data:{
					"id":id,
					"reason":reason
				},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dg').datagrid('reload');  
						showMsg("申请成功！");
					} else {
						showMsg(data.msg);
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}


function closeDAjax(id,reason){
	$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/close.htmlx",
				data:{
					"id":id,
					"reason":reason
				},
				dataType:"json",
				type:"POST",
				cache:false,
				success:function(data){
					if(data.success){
						$('#dgDetail').datagrid('reload');  
						showMsg("申请成功！");
					} else {
						showMsg(data.msg);
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
	});	
}

function deleteDAjax(id){
	$.ajax({
			url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/contractDetail/deleteD.htmlx",
			data:{
				"id":id
			},
			dataType:"json",
			type:"POST",
			cache:false,
			success:function(data){
				if(data.success){
					$('#dgDetail').datagrid('reload');  
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

//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 500,
		height : 300,
		href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/contract/add.htmlx",
		buttons : [{
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

</script>