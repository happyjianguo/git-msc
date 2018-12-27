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
<shiro:hasPermission name="supervise:monthlyPurchase:exportExcel">
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
		 fieldType:'l#S'
	},{
		 field:'NAME',
		 type:'text',
		 fieldType:'l#S'
	},{
		 field:'DOSAGEFORMNAME',
		 type:'text',
		 fieldType:'l#S'
	},{
		 field:'PACKDESC',
		 type:'text',
		 fieldType:'l#S'
	},{
		 field:'PRODUCERNAME',
		 type:'text',
		 fieldType:'l#S'
	},{
		 field:'NUM',
		 type:'text',
		 isDisabled:1
	},{
		 field:'GPONUM',
		 type:'text',
		 isDisabled:1
	},{
		 field:'NOTGPONUM',
		 type:'text',
		 isDisabled:1
	},{
		 field:'AMT',
		 type:'text',
		 isDisabled:1
	},{
		 field:'GPOAMT',
		 type:'text',
		 isDisabled:1
	},{
		 field:'NOTGPOAMT',
		 type:'text',
		 isDisabled:1
	},{
		 field:'OUTNUM',
		 type:'text',
		 isDisabled:1
	},{
		 field:'PRICE',
		 type:'text',
		 isDisabled:1
	},{
		 field:'OUTSUM',
		 type:'text',
		 isDisabled:1
	},{
		 field:'INRATIO',
		 type:'text',
		 isDisabled:1
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
	var hospitalCode = $('#hospitalCode').combobox('getValue');

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
	var hospitalCode = $('#hospitalCode').combobox('getValue');
	var data = {
			"query['l#month_S_GE']": startMonth,
			"query['l#month_S_LE']": toMonth,
			"query['l#hospitalCode_S_EQ']": hospitalCode
		};
	var url = " <c:out value='${pageContext.request.contextPath }'/>/supervise/monthlyPurchase/exportExcel.htmlx?";
	for(var k in data){
		url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(data[k]);
	}
	$.messager.confirm('确认信息', '确认下载?', function(r){
		if (r){
			window.open(url);
		}
	});
}

//初始化
$(function(){	
	$('#hospitalCode').combogrid({
		idField:'code',    
		textField:'fullName',
		url: ' <c:out value='${pageContext.request.contextPath }'/>/set/hospital/listByCounty.htmlx',
		pagination:false,
		queryParams:{
			"query['t#isDisabled_I_EQ']" : 0
		},
	    columns: [[
	        {field:'code',title:'机构编码',width:150},
	        {field:'fullName',title:'机构名称',width:200}
	    ]],
	    width:160,
	    panelWidth:360,
		delay:800,
		prompt:"名称模糊搜索",
		keyHandler: { 
	        enter: function(e) {
                $('#hospitalCode').combogrid('grid').datagrid("reload",{
        			"query['t#isDisabled_I_EQ']" : 0,
        			"query['t#fullName_S_LK']":$('#hospitalCode').combogrid("getText")});
                $('#hospitalCode').combogrid("setValue", $('#hospitalCode').combogrid("getText"));
	        }
        }
	}).combobox("initClear");
	$('#hospitalCode').combogrid('grid').datagrid("reload",{
		"query['t#isDisabled_I_EQ']" : 0,
		"query['t#fullName_S_LK']":$('#hospitalCode').combogrid("getText")});
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() -45,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/monthlyPurchase/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'MONTH',title:'年月',width:100,align:'center'},
		        	{field:'HOSPITALNAME',title:'医院名称',width:140,align:'center'},
		        	{field:'CODE',title:'药品编码',width:100,align:'center'},
		        	{field:'NAME',title:'药品名称',width:100,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:100,align:'center'},
		        	{field:'MODEL',title:'规格',width:100,align:'center'},
		        	{field:'PACKDESC',title:'包装规格',width:100,align:'center'},
		        	{field:'PRODUCERNAME',title:'生产厂家',width:100,align:'center'},
		        	{field:'NUM',title:'采购数量',width:100,align:'center'},
		        	{field:'GPONUM',title:'GPO采购数量',width:100,align:'center'},
		        	{field:'NOTGPONUM',title:'非GPO采购数量',width:100,align:'center'},
		        	{field:'AMT',title:'采购金额',width:100,align:'center'},	
		        	{field:'GPOAMT',title:'GPO采购金额',width:100,align:'center'},
		        	{field:'NOTGPOAMT',title:'非GPO采购金额',width:100,align:'center'},
					{field:'OUTNUM',title:'上月出库数量',width:110,align:'center'},
					{field:'PRICE',title:'单价',width:100,align:'center',formatter: function(value,row,index){
						if(row.OUTNUM!=null&&row.OUTNUM!=0){
							return parseFloat(row.OUTSUM/row.OUTNUM).toFixed(1);
						}
					}},
					{field:'OUTSUM',title:'上月出库总金额',width:130,align:'center'},
					{field:'INRATIO',title:'<span title=" (本月采购金额-上月出库总金额) / 上月出库总金额 " class="tip">采购增长率(%)</span>',width:130,align:'center',formatter: function(value,row,index){
						if(row.OUTNUM!=null&&row.OUTNUM!=0){
							return parseFloat((row.AMT-row.OUTSUM)*100/row.OUTSUM).toFixed(1);
						}
					}},
					{field:'ISGPOPURCHASE',title:'是否GPO药品',width:110,align:'center',
		        		formatter: function(value,row,index){
		        			if (row.ISGPOPURCHASE ==1){
		    					return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
		    				}
		    		}}
		   		]],
		toolbar: []
	});
	
});

</script>