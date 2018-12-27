<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >
					
<div class="single-dg">
	<shiro:hasPermission name="dm:product:add">
		<div id="toolbar" class="search-bar" >
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addOpen()">添加</a>
	        <span class="datagrid-btn-separator split-line" ></span>
	  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-no',plain:true"  onclick="delFunc()">删除</a>
	    </div>
    </shiro:hasPermission>
	<table  id="dg" ></table>
</div>

</body>
</html>

<script>
//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -4,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/product/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'code',title:'药品编码',width:100,align:'center'},
		        	{field:'productGCode',title:'标准编码',width:100,align:'center'},
		        	{field:'name',title:'药品名称',width:100,align:'center'},
		        	{field:'genericName',title:'通用名',width:100,align:'center'},
		        	{field:'dosageFormName',title:'剂型',width:100,align:'center'},
		        	{field:'producerName',title:'生产企业',width:100,align:'center'},
		        	{field:'model',title:'规格',width:100,align:'center'},
		        	{field:'packDesc',title:'包装规格',width:100,align:'center'},
		        	{field:'authorizeNo',title:'批准文号',width:100,align:'center'},
            		{field:'standardCode',title:'本位码',width:100,align:'center'},
		        	{field:'nationalAuthorizeNo',title:'国药准字',width:100,align:'center'},
		        	{field:'importFileNo',title:'注册证号',width:100,align:'center'},
            		{field:'nationalCode',title:'国家药品代码',width:100,align:'center'},
                    {field:'isNationalCodeCheck',title:'是否国家药品对照',width:120,align:'center',
                        formatter: function(value,row,index){
                            if(row.nationalCode != undefined && row.nationalCode!=null){
                                return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
                            }
                        }
                    },
		        	{field:'isGPOPurchase',title:'是否GPO药品',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.isGPOPurchase==1){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
							}
					}},
		        	{field:'gpoName',title:'GPO名称',width:100,align:'center'},
		        	{field:'isBaseDrugType',title:'是否基药',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.baseDrugType != undefined&&row.baseDrugType!=null){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
							}
						}},
                    {field:'countryBaseDrugType',title:'是否国基药品',width:100,align:'center',
                        formatter: function(value,row,index){
                            if (row.baseDrugType != undefined&&row.baseDrugType!=null&&row.baseDrugType=='13'){
                                return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
                            }
                        }},
                    {field:'provinceBaseDrugType',title:'是否省基药品',width:100,align:'center',
                        formatter: function(value,row,index){
                            if (row.baseDrugType != undefined&&row.baseDrugType!=null&&row.baseDrugType=='17'){
                                return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
                            }
                        }},
		        	{field:'absDrugType',title:'是否抗菌药物',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.absDrugType != undefined&&row.absDrugType!=null){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
							}
						}},
		        	{field:'isUrgent',title:'是否急救药品',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.isUrgent==1){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
							}
						}},
		        	{field:'isDisabled',title:'是否禁用',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.isDisabled == 1){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png\" />";
							}
						}
		        	},
					{field:'isHealth',title:'是否社康药品',width:100,align:'center',
						formatter: function(value,row,index){
							if (row.isHealth == 1){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
							}
						}
					},
		        	{field:'isNationalNegotiations',title:'是否国家谈判药品',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.isNationalNegotiations == 1){
								return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
							}
						}
		        	}
		   		]],
		toolbar: "#toolbar",
		onDblClickRow: function(index,field,value){
			editOpen();
		}
	});

	$('#dg').datagrid('enableFilter', [{
        field:'isDisabled',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'未禁用'},
                  {value:'1',text:'禁用'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'isDisabled');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'isDisabled',
                        op: 'EQ',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'isGPOPurchase',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'isGPOPurchase');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'isGPOPurchase',
                        op: 'EQ',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
            field:'isNationalCodeCheck',
            type:'combobox',
            options:{
                panelHeight:'auto',
                editable:false,
                data:[{value:'',text:'-请选择-'},
                    {value:'0',text:'否'},
                    {value:'1',text:'是'}],
                onChange:function(value){
                    if (value == ''){
                        $('#dg').datagrid("load",{
                            "query[t#nationalCode_S_EQ]":''
                        });
                    } else {
                        if(value=="0"){
                            $('#dg').datagrid("load",{
                                "query[t#nationalCode_NULL_IS]":0
                            });
                        }else{
                            $('#dg').datagrid("load",{
                                "query[t#nationalCode_NULL_NOT]":1
                            });
                        }
                    }
                    $('#dg').datagrid('doFilter');
                }
            }
        },{
        field:'isBaseDrugType',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'baseDrugType');
                } else {
                	var op = (value == "1"?"NOT":"IS");
                	$('#dg').datagrid('addFilterRule', {
                        field: 'baseDrugType',
                        op: op,
                        fieldType:'NULL',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
            field:'provinceBaseDrugType',
            type:'combobox',
            options:{
                panelHeight:'auto',
                editable:false,
                data:[{value:'',text:'-请选择-'},
                    {value:'0',text:'否'},
                    {value:'1',text:'是'}],
                onChange:function(value){
                    var op = (value == "1"?"EQ":"NE");
                    if (value == ''){
                        $('#dg').datagrid('removeFilterRule', 'baseDrugType');
                    } else {
                        $('#dg').datagrid('addFilterRule', {
                            field: 'baseDrugType',
                            op: op,
                            fieldType:'L',
                            value: "17"
                        });
                    }
                    $('#dg').datagrid('doFilter');
                }
            }
        },{
            field:'countryBaseDrugType',
            type:'combobox',
            options:{
                panelHeight:'auto',
                editable:false,
                data:[{value:'',text:'-请选择-'},
                    {value:'0',text:'否'},
                    {value:'1',text:'是'}],
                onChange:function(value){
                    var op = (value == "1"?"EQ":"NE");
                    if (value == ''){
                        $('#dg').datagrid('removeFilterRule', 'baseDrugType');
                    } else {
                        $('#dg').datagrid('addFilterRule', {
                            field: 'baseDrugType',
                            op: op,
                            fieldType:'L',
                            value: "13"
                        });
                    }
                    $('#dg').datagrid('doFilter');
                }
            }
        },{
        field:'absDrugType',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'absDrugType');
                } else {
                	var op = (value == "1"?"NOT":"IS");
                	$('#dg').datagrid('addFilterRule', {
                        field: 'absDrugType',
                        op: op,
                        fieldType:'NULL',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'isUrgent',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'isUrgent');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'isUrgent',
                        op: 'EQ',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
            field:'isNationalNegotiations',
            type:'combobox',
            options:{
                panelHeight:'auto',
                editable:false,
                data:[{value:'',text:'-请选择-'},
                    {value:'0',text:'否'},
                    {value:'1',text:'是'}],
                onChange:function(value){
                    if (value == ''){
                        $('#dg').datagrid('removeFilterRule', 'isNationalNegotiations');
                    } else {
                        $('#dg').datagrid('addFilterRule', {
                            field: 'isNationalNegotiations',
                            op: 'EQ',
                            fieldType:'I',
                            value: value
                        });
                    }
                    $('#dg').datagrid('doFilter');
                }
            }
        },{
        field:'isHealth',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'isHealth');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'isHealth',
                        op: 'EQ',
                        fieldType:'I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	}]
	
	);
	
});


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

//弹窗增加
function addOpen() {
	top.$.modalDialog({
		title : "添加",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/product/add.htmlx",
		onLoad:function(){
			
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

//弹窗修改
function editOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "修改",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/dm/product/edit.htmlx",
		queryParams:{
			"drugId":selrow.drugId,
			"producerId":selrow.producer==null?"":selrow.producerId
		},
		onLoad:function(){
			if(selrow){
				selrow.directoryId=selrow.directoryId||undefined;
				var f = top.$.modalDialog.handler.find("#form1");
				if(!isempty(selrow.authorizeBeginDate)){
					selrow.authorizeBeginDate = $.format.date(selrow.authorizeBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.authorizeOutDate)){
					selrow.authorizeOutDate = $.format.date(selrow.authorizeOutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.importBeginDate)){
					selrow.importBeginDate = $.format.date(selrow.importBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.importOutDate)){
					selrow.importOutDate = $.format.date(selrow.importOutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.patentBeginDate)){
					selrow.patentBeginDate = $.format.date(selrow.patentBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.patentOutDate)){
					selrow.patentOutDate = $.format.date(selrow.patentOutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.protectBeginDate)){
					selrow.protectBeginDate = $.format.date(selrow.protectBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.protectOutDate)){
					selrow.protectOutDate = $.format.date(selrow.protectOutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.newDrugBeginDate)){
					selrow.newDrugBeginDate = $.format.date(selrow.newDrugBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.newDrugOutDate)){
					selrow.newDrugOutDate = $.format.date(selrow.newDrugOutDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.consignBeginDate)){
					selrow.consignBeginDate = $.format.date(selrow.consignBeginDate,"yyyy-MM-dd");
				}
				if(!isempty(selrow.consignOutDate)){
					selrow.consignOutDate = $.format.date(selrow.consignOutDate,"yyyy-MM-dd");
				}
				if(selrow.baseDrugType==null){
					selrow.baseDrugType = "";
    			}
    			if (selrow.isGPOPurchase == "1") {
					f.find("#gpoId").combobox({required:true})
					f.find(".gpo_sp").show();
				} else {
					f.find('#gpoId').validatebox({required:false});
					f.find(".gpo_sp").hide();
				}
    			if(selrow.insuranceDrugType==null){
    				selrow.insuranceDrugType = "";
    			}
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
				url:" <c:out value='${pageContext.request.contextPath }'/>/dm/product/del.htmlx",
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


</script>