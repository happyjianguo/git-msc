<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js//My97DatePicker/WdatePicker.js"></script>

<html>
<body class="easyui-layout" >
	<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:100%;"  >
		 <div id="tb" class="search-bar" >
			上报年月: 
			<input type="text" id="startMonth" name="startMonth" class="Wdate" onFocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM'})"  />
	        ~ 
	        <input type="text" id="toMonth" name="toMonth" class="Wdate" onFocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM'})"  />	
	       	医院：
	       	<input id="hospitalCode" name="hospitalCode" />
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
	        <span class="datagrid-btn-separator split-line" ></span>
			<shiro:hasPermission name="b2b:monitor:supplyProduct:exportExcel">
				<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true"  onclick="doexport()">导出Excel</a>
			</shiro:hasPermission>
	        <span class="datagrid-btn-separator split-line" ></span>
	  		<a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true"  onclick="filterFunc()">数据过滤</a>
	    </div>
	    <div class="single-dg">
			<table id="dg"></table>
		</div>
	</div>
</body>
</html>

<script>
function filterFunc(){
	$('#dg').datagrid({remoteFilter: true});
	$('#dg').datagrid('enableFilter', [{
		 field:'MONTH',
		 type:'text',
		 fieldType:'l#S'
	},{
		 field:'HOSPITALNAME',
		 type:'text',
		 fieldType:'l#S'
	},{
		 field:'CODE',
		 type:'text',
		 fieldType:'p#S'
	},{
		 field:'NAME',
		 type:'text',
		 fieldType:'p#S'
	},{
	     field:'DOSAGEFORMNAME',
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
	},{
	     field:'PRODUCERNAME',
		 type:'text',
		 fieldType:'p#S'
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
                        fieldType:'p#I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
	     field:'VENDORCODE',
		 type:'text',
		 fieldType:'p#S'
	},{
	     field:'VENDORNAME',
		 type:'text',
		 fieldType:'p#S'
	},{
		field:'INTERNALCODE',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'0',text:'否'},
                  {value:'1',text:'是'}],
            onChange:function(value){
            	if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'INTERNALCODE');
                } else {
                	var op = (value == "1"?"NOT":"IS");
                	$('#dg').datagrid('addFilterRule', {
                        field: 'INTERNALCODE',
                        op: op,
                        fieldType:'NULL',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	}]);
	$('#dg').datagrid('reload');
}
//搜索
function dosearch(){
	var startMonth = "";
	var toMonth = "";
	if(document.getElementById('startMonth').value != ""){
	  	startMonth = document.getElementById('startMonth').value;		
	}
	if(document.getElementById('toMonth').value != ""){
		toMonth = document.getElementById('toMonth').value;
	}
	var hospitalCode = $('#hospitalCode').textbox('getValue');

	var data = {
		"query['l#month_S_GE']": startMonth,
		"query['l#month_S_LE']": toMonth,
		"query['l#hospitalCode_S_EQ']": hospitalCode
	};

	$('#dg').datagrid('load',data);
}

//导出excel
function doexport(){
	var startMonth = "";
	var toMonth = "";
	if(document.getElementById('startMonth').value != ""){
	  	startMonth = document.getElementById('startMonth').value;		
	}
	if(document.getElementById('toMonth').value != ""){
		toMonth = document.getElementById('toMonth').value;
	}
	var hospitalCode = $('#hospitalCode').textbox('getValue');

	$.messager.confirm('确认信息', '确认下载?', function(r){
		if (r){
			window.open(" <c:out value='${pageContext.request.contextPath }'/>"+
					"/b2b/monitor/supplyProduct/exportExcel.htmlx"+
					"?startMonth="+startMonth+"&toMonth="+toMonth+
					"&hospitalCode="+hospitalCode);
		}
	});
}

//初始化
$(function(){	
	$('#hospitalCode').combogrid({
		idField:'code',    
		textField:'fullName',
        striped: true,
        editable:true,
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/hospital/page.htmlx",
		queryParams:{
	    	"query['t#isDisabled_I_EQ']": 0
		},
		pagination:true,
		pageSize:10,
		pageNumber:1,
		delay:400,
	    columns: [[
	        {field:'code',title:'医院编码',width:100},
	        {field:'fullName',title:'医院名称',width:400}
	    ]],
	    panelWidth:510,
	    panelHeight:310,
	    onClickRow: function(index,row){  
	    	$('#hospitalName').textbox("setValue", row.fullName);
		},
		keyHandler: {
            query: function(q) {
                //动态搜索
				$('#hospitalCode').combogrid('grid').datagrid("reload",{"query['t#fullName_S_LK']":q});
				$('#hospitalCode').combogrid("setValue", q);
            },
            enter : function() {
                var query = $("#hospitalCode").combogrid("getText");
                $('#hospitalCode').combogrid('grid').datagrid("reload",{"query['t#fullName_S_LK']":query});
                $('#hospitalCode').combogrid("setValue", query);
            }
        }
	});
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() -45,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/supplyProduct/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'MONTH',title:'年月',width:10,align:'center'},
		        	{field:'HOSPITALNAME',title:'医院名称',width:10,align:'center'},
		        	{field:'CODE',title:'药品编码',width:10,align:'center'},
		        	{field:'NAME',title:'药品名称',width:10,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
		        	{field:'MODEL',title:'规格',width:10,align:'center'},
		        	{field:'PACKDESC',title:'包装规格',width:10,align:'center'},
		        	{field:'PRODUCERNAME',title:'生产厂家',width:10,align:'center'},
		        	{field:'ISGPOPURCHASE',title:'是否GPO药品',width:10,align:'center',
		        		formatter: function(value,row,index){
		        			if (row.ISGPOPURCHASE ==1){
		    					return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
		    				}
		    		}},
		        	{field:'VENDORCODE',title:'供应商编码',width:10,align:'center'},	
		        	{field:'VENDORNAME',title:'供应商名称',width:20,align:'center'}	,
		        	{field:'INTERNALCODE',title:'是否对照',width:10,align:'center',
		        		formatter: function(value,row,index){
		        			if (row.INTERNALCODE !=null){
		    					return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
		    				}
		    		}}
		   		]],
		toolbar: []
	});
	
	
});

</script>