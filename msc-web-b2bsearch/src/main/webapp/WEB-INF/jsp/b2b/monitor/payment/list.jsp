<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="" />

<html>

<body class="easyui-layout">
	<div class="single-dg">
		<table id="dg"></table>
	</div>

</body>
</html>

<script>
//搜索
function dosearch(){
	var startDate="";
	var toDate="";
	var hospitalName="";
	var vendorName ="";
	if($('#startDate').datebox('getValue')!=""){
	  	startDate = $('#startDate').datebox('getValue');		
	}
	if($('#toDate').datebox('getValue')!=""){
		toDate = $('#toDate').datebox('getValue');
	}
	
	var data = {
		"query['t#payDate_D_GE']": startDate,
		"query['t#payDate_D_LE']": toDate
	};
	data["query['t#"+$('#ss').searchbox("getName")+"_S_LK']"] = $('#ss').searchbox("getValue");
	$('#dg').datagrid('load',data);
}

//初始化
$(function(){
	$('#ss').searchbox({
		searcher:function(value, name) {
			dosearch();
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() - 4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/payment/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'code',title:'付款单号',width:20,align:'center'},
		        	{field:'settlementCode',title:'结算单号',width:20,align:'center'},
		        	{field:'hospitalName',title:'医疗机构',width:15,align:'center'},
		        	{field:'vendorName',title:'供应商',width:15,align:'center'},
/* 		        	{field:'accNo',title:'账号',width:15,align:'center'}, */
		        	{field:'sum',title:'付款金额',width:10,align:'right',
						formatter: function(value,row,index){
							if (row.sum){
								return common.fmoney(row.sum);
							}
						}},
		        	{field:'payDate',title:'付款日期',width:15,align:'center',
						formatter: function(value,row,index){
							if (row.payDate){
								return $.format.date(row.payDate,"yyyy-MM-dd");
							}
						}},
					{field:'operation',title:'操作',width:10,align:'center',
			    		formatter: function(value,row,index){
			    			var str = "<img title='结算单查询("+row.settlementCode+")' onclick=openPre('"+row.settlementCode+"') src =' <c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/undo.png' style='cursor: pointer;'/>";
			    			return str;
						}
					}
		   		]],
		onLoadSuccess:function(data){
			$('#dg').datagrid('doCellTip',{delay:500}); 
		},
		queryParams:{
			"query['t#settlementCode_S_EQ']":"<c:out value='${settlementCode}'/>"
		}
	});
	
	$('#dg').datagrid('enableFilter', [{
		 field:'sum',
		 type:'text',
		 isDisabled:1
	},{
		 field:'operation',
		 type:'text',
		 isDisabled:1
	},{
       field:'payDate',
       type:'datebox',
       op:['EQ','GE'],
       fieldType:'D',
       options:{
       	editable:false
       }
   }]);
	
});
function openPre(code){
	top.addTab("结算单查询 ", "/b2b/monitor/settlement.htmlx?code="+code);
}

</script>