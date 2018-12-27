<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<tag:head title="医院在用药品目录" />
<style>
#toolbar{
width:100%;
}
#search{
margin-right:30px;float:right;
}
</style>
<html>

<body class="easyui-layout"  >
<div data-options="region:'center',title:''" >
	<div id="toolbar" class="search-bar" >
		<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="add()">新增辅助药品</a>
		<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="deleteDrug()">取消</a>
<shiro:hasPermission name="supervise:medicineHospital:export">
   		<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true" onclick="exportexcel()">导出</a>
</shiro:hasPermission>
   		<a href="#" id="search" class="easyui-linkbutton" data-options="iconCls:'icon-filter',plain:true" onclick="filterFunc()">数据过滤</a>
	</div>
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>
</div>
</body>

</html>
<script>

$(function(){
	var jsonStr = "<c:out value='${jsonStr}'/>";
	if (jsonStr == "") {
		jsonStr = "{}";
	}
	//用户组
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		width:"100%",
		height:$(this).height()-10,
		url:"<c:out value='${pageContext.request.contextPath }'/>/supervise/medicineHospital/page.htmlx",
		pageSize:20,
		pageNumber:1,
		pagination:true,
		remoteFilter: true,
		queryParams:eval("("+decodeURIComponent(jsonStr)+")"),
		columns:[[
					<c:if test="${orgType ne 1 }">
					{field:'HOSPITALNAME',title:'医院名称',width:135,align:'center'},
					</c:if>
					{field:'PRODUCTCODE',title:'药品编码',width:75,align:'center'},
					{field:'INTERNALCODE',title:'医院内部编码',width:100,align:'center'},
					{field:'PRODUCTNAME',title:'药品名称',width:100,align:'center'},
					{field:'MODEL',title:'规格',width:100,align:'center'},
					{field:'PACKDESC',title:'包装',width:100,align:'center'},
					{field:'DOSAGEFORMNAME',title:'剂型',width:100,align:'center'},
					{field:'PRODUCERNAME',title:'生产厂家',width:100,align:'center'},
					{field:'ISCOMPARE',title:'是否对照',width:100,align:'center',formatter: function(value,row,index){
						if (row.ISCOMPARE==1) {
							return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
						}
					}},
		        	{field:'AUTHORIZENO',title:'国药准字',width:100,align:'center'},
		        	{field:'IMPORTFILENO',title:'注册证号',width:100,align:'center'},
		        	{field:'DDD',title:'DDD',width:100,align:'center'},
		        	{field:'AUXILIARYTYPE',title:'是否辅助用药',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.AUXILIARYTYPE ==1){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
						}},
		        	{field:'ISGPOPURCHASE',title:'是否GPO药品',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.ISGPOPURCHASE==1){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
					}},
		        	{field:'GPONAME',title:'GPO名称',width:100,align:'center'},
		        	{field:'BASEDRUGTYPE',title:'是否基药',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.BASEDRUGTYPE != undefined&&row.BASEDRUGTYPE!=null){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
						}},
		        	{field:'ABSDRUGTYPE',title:'是否抗菌药物',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.ABSDRUGTYPE != undefined&&row.ABSDRUGTYPE!=null){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
						}},
		        	{field:'ISURGENT',title:'是否急救药品',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.ISURGENT==1){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
						}},
		        	{field:'ISDISABLED',title:'是否禁用',width:100,align:'center',
		        		formatter: function(value,row,index){
							if (row.ISDISABLED == 1){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/disable.png' />";
							}
						}
		        	}
		   		]],
		   		toolbar: "#toolbar",
		   		onLoadSuccess:function(data){
		   			
		   		}
});


});
function filterFunc(){
	$('#dg').datagrid({remoteFilter: true});
	$('#dg').datagrid('enableFilter', [{
		 field: 'AUTHORIZENO',
		 type:'text',
         fieldType:'a#S'
	},{
		 field: 'IMPORTFILENO',
		 type:'text',
         fieldType:'a#S'
	},{
		 field: 'DDD',
		 type:'text',
         fieldType:'a#S'
	},{
		 field: 'GPONAME',
		 type:'text',
         fieldType:'a#S'
	},{
        field:'ISDISABLED',
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
                        fieldType:'t#I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'AUXILIARYTYPE',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'AUXILIARYTYPE');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'AUXILIARYTYPE',
                        op: 'EQ',
                        fieldType:'t#I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'ISGPOPURCHASE',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'ISGPOPURCHASE');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'ISGPOPURCHASE',
                        op: 'EQ',
                        fieldType:'a#I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'BASEDRUGTYPE',
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
                        fieldType:'a#NULL',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'ABSDRUGTYPE',
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
                        fieldType:'a#NULL',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'ISURGENT',
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
                        fieldType:'a#I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'ISCOMPARE',
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
                		"query[a#CODE_S_EQ]":''
                    });
                } else {
                    if(value=="0"){
                    	$('#dg').datagrid("load",{
                        		"query[a#CODE_NULL_IS]":0
                        });
                    }else{
                    	$('#dg').datagrid("load",{
                        	"query[a#CODE_NULL_NOT]":1
                        });
                    }
                }
            }
        }
	}]);
	$('#dg').datagrid('reload');
}	
//=============ajax===============
function setAuxiliary(array, isAuxiliary){
	$.ajax({
		url:"<c:out value='${pageContext.request.contextPath }'/>/supervise/medicineHospital/setAuxiliary.htmlx",
		data:{
			"array":array,
			"isAuxiliary":isAuxiliary
		},
		dataType:"json",
		type:"POST",
		cache:false,
		success:function(data){
			if(data.success){
				if(isAuxiliary==1){
					showMsg("新增成功！");
				}else{
					showMsg("删除成功！");
				}
			}else{
				showErr(data.msg);
			}
			$('#dg').datagrid("reload");
		},
		error:function(){
			showErr("出错，请刷新重新操作");	
		}
	});	
	
}
function add(){
	var selobjs = $('#dg').datagrid('getSelections');
		console.log(selobjs);
		if(selobjs.length==0){
			showMsg("请选中一行！");
		}else{
			var array = new Array();
 			$.each(selobjs,function(name,value){
 				array.push(value['ID']);
 			});
 			setAuxiliary(array,1);
		}	
}
function deleteDrug(){
	var selobjs = $('#dg').datagrid('getSelections');
		var array = new Array();
			$.each(selobjs,function(name,value){
				array.push(value['ID']);
			});
			setAuxiliary(array,0);	
}
function exportexcel(){
	$.messager.confirm('确认信息', '确认下载?', function(r){
			if (r){
				window.open(" <c:out value='${pageContext.request.contextPath }'/>"+
						"/supervise/medicineHospital/export.htmlx");
			}
		});
}
</script>