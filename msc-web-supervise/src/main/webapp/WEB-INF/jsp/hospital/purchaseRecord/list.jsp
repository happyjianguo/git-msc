<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<script src=" <c:out value='${pageContext.request.contextPath }'/>/resources/js//My97DatePicker/WdatePicker.js"></script>

<html>
<body class="easyui-layout" >
	<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:600px;"  >
		 <div id="tb" class="search-bar" >
			上报年月: 
			<input type="text" id="startMonth" name="startMonth" class="Wdate" onFocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM'})"  />
	        ~ 
	        <input type="text" id="toMonth" name="toMonth" class="Wdate" onFocus="WdatePicker({isShowClear:false,lang:'zh-cn',dateFmt:'yyyy-MM'})"  />	
	       	医院：
	       	<input id="hospitalCode" name="hospitalCode" />
	        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
	        <span class="datagrid-btn-separator split-line" ></span>
	  		<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-xls',plain:true"  onclick="doexport()">导出Excel</a>
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
	$('#dg').datagrid('enableFilter', []);
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
					"/b2b/monitor/purchaseRecord/exportExcel.htmlx"+
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
		url: " <c:out value='${pageContext.request.contextPath }'/>/set/hospital/page.htmlx",
		queryParams:{
	    	"query['t#isDisabled_I_EQ']": 0
		},
		pagination:true,
		pageSize:10,
		pageNumber:1,
		delay:800,
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
                $('#hospitalCode').combogrid('grid').datagrid("reload",{"query['l#fullName_S_LK']":q});
                $('#hospitalCode').combogrid("setValue", q);
            }

        },
        onLoadSuccess:function(data){
			$('#dg').datagrid('doCellTip',{delay:500}); 
			$(".tip").tooltip({  
                onShow: function(){  
                    $(this).tooltip('tip').css({   
                        width:'260',          
                        boxShadow: '2px 2px 4px #292929'                          
                    });  
                }  
            });
		}
	});
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() -40,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseRecord/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'MONTH',title:'年月',width:10,align:'center'},
		        	{field:'HOSPITALNAME',title:'医院名称',width:14,align:'center'},
		        	{field:'CODE',title:'药品编码',width:10,align:'center'},
		        	{field:'NAME',title:'药品名称',width:10,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
		        	{field:'MODEL',title:'规格',width:10,align:'center'},
		        	{field:'PACKDESC',title:'包装规格',width:10,align:'center'},
		        	{field:'PRODUCERNAME',title:'生产厂家',width:10,align:'center'},
		        	{field:'PLATFORM',title:'交易平台',width:10,align:'center'},
		        	{field:'PRODUCTTRANID',title:'省药交产品ID',width:11,align:'center'},
		        	{field:'NUM',title:'采购数量',width:10,align:'center'},
		        	{field:'AMT',title:'采购金额',width:10,align:'center'},	
					{field:'OUTNUM',title:'上月出库数量',width:11,align:'center'},
					{field:'PRICE',title:'单价',width:10,align:'center',formatter: function(value,row,index){
						if(row.OUTNUM!=null&&row.OUTNUM!=0){
							return parseFloat(row.OUTSUM/row.OUTNUM).toFixed(1);
						}
					}},
					{field:'OUTSUM',title:'上月出库总金额',width:13,align:'center'},
					{field:'INRATIO',title:'<span title=" (本月采购金额-上月出库总金额) / 上月出库总金额 " class="tip">采购增长率(%)</span>',width:10,align:'center',formatter: function(value,row,index){
						if(row.OUTNUM!=null&&row.OUTNUM!=0){
							return parseFloat((row.AMT-row.OUTSUM)*100/row.OUTSUM).toFixed(1);
						}
					}}
		   		]],
		toolbar: []
	});
	
});

</script>