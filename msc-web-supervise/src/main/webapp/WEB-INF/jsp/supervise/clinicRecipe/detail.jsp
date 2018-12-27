<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="tt">
	    <div title="药品清单" class="my-tabs" >
			<table  id="dg1" ></table>
	    </div>
	    <div title="诊断" class="my-tabs" >
	       <table id="dg2"></table>
	    </div>
	</div>
<script>
//初始化
$(function(){
	$('#tt').tabs({
		plain:true,
		justified:true
	});
	$('#dg2').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :$(this).height(),
		pagination:false,
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/clinicRecipe/diagnosis.htmlx",
		queryParams:{  
	    	"query['outSno_S_EQ']":"<c:out value='${outSno}'/>",
	    	"query['hospitalCode_S_EQ']":"<c:out value='${hospitalCode}'/>"
	    },
		columns:[[
        	{field:'diagDate',title:'诊断日期',width:20,align:'center',formatter:function(value,row,index) {
        		if (null!=value) {
        			var reg =/^\s*$/;
					if(value.length!=0&&!reg.test(value)){
						return $.format.date(value, "yyyy-MM-dd HH:mm");
					}
				} else {
					return "-";
				}
        	}},
        	{field:'diagName',title:'诊断名称',width:20,align:'center',formatter:function(value,row,index){ 
        		if (null!=value) {
        			var reg =/^\s*$/;
					if(value.length!=0&&!reg.test(value)){
						return value;
					}
				} else {
					return "-";
				}
        	}}
   		]],
		onLoadSuccess: function(data){
			$('#dg1').datagrid('doCellTip',{delay:500}); 
		}
	});
	
		
	$('#dg1').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :370,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/supervise/clinicRecipe/recipeItem.htmlx",
		pageSize:20,
		pageNumber:1,
		showFooter: false,
		toolbar:"#tbd",
		queryParams:{  
	    	"query['outSno_S_EQ']":"<c:out value='${outSno}'/>",
	    	"query['rpSno_S_EQ']":"<c:out value='${rpSno}'/>",
	    	"query['hospitalCode_S_EQ']":"<c:out value='${hospitalCode}'/>"
	    },
		columns:[[
        	{field:'PRODUCTCODE',title:'药品编码',width:11,align:'center'},
        	{field:'PRODUCTNAME',title:'药品名称',width:20,align:'center'},
        	{field:'DOSAGEFORMNAME',title:'剂型',width:10,align:'center'},
        	{field:'MODEL',title:'规格',width:10,align:'center'},
        	{field:'PACKDESC',title:'包装',width:10,align:'center'},
        	{field:'PRODUCERNAME',title:'生产企业',width:15,align:'center'},
        	{field:'ISGPOPURCHASE',title:'是否GPO药品',width:16,align:'center',
        		formatter: function(value,row,index){
        			if (row.ISGPOPURCHASE==1){
						return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
					}
			}},
        	{field:'SUM',title:'金额',width:14,align:'right',
				formatter: function(value,row,index){
					if (value){
						return common.fmoney(value);
					}
				}},
        	{field:'FREQUENCYNAME',title:'用药频率',width:11,align:'right'},
        	{field:'DOSAONE',title:'一次剂量',width:12,align:'right',
				formatter: function(value,row,index){
						return parseFloat(row.DOSAONE)+row.DOSAONEUNIT;
				}},
        	{field:'DAYS',title:'天数',width:10,align:'right'}
   		]]		
	});
});

</script>