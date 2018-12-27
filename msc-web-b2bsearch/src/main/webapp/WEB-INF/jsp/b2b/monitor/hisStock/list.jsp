<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="库存信息查询" />

<html>

<body class="easyui-layout"  >
	<div class="single-dg"><table id="dg"></table></div>
</body>

</html>
<script>
//搜索
function dosearch(){
	var startDate="";
	var toDate="";
	if($('#startDate').datebox('getValue')!=""){
	  	startDate = $('#startDate').datebox('getValue');		
	}
	if($('#toDate').datebox('getValue')!=""){
		toDate = $('#toDate').datebox('getValue');
	}
	
	var data = {
		"query['t#stockDate_S_GE']": startDate,
		"query['t#stockDate_S_LE']": toDate
	};
	$('#dg').datagrid('load',data);
}
$(function(){
	$("#hospitalName").combobox({    
	    url:" <c:out value='${pageContext.request.contextPath }'/>/set/hospital/list.htmlx",  
	    valueField:'id',    
	    textField:'fullName',   
	    width:180,
	    onSelect:function(a,b){}
	});
	//日期
	$('#startDate').datebox({
		editable:false
	});
	$('#toDate').datebox({
		editable:false
	});
	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height : $(this).height() - 4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/hisStock/page.htmlx",
		pageSize:20,
		pageNumber:1,
		pagination:true,
		remoteFilter: true,
		columns:[[
		        	{field:'stockDate',title:'日期',width:10,align:'center',
						formatter: function(value,row,index){
								return row.stockDate?$.format.date(row.stockDate,"yyyy-MM-dd HH:mm"):"";
						}
					}, 
					
					<c:if test="${orgType ne 1 }">
					{field:'hospitalName',title:'医院名称',width:15,align:'center'},
					</c:if>
					{field:'productCode',title:'药品编码',width:15,align:'center'},
					{field:'productName',title:'药品名称',width:15,align:'center'},
					//{field:'beginNum',title:'期初库存数量',width:15,align:'center'},
					//{field:'beginAmt',title:'期初库存金额',width:15,align:'center'},
					{field:'endNum',title:'期末库存数量',width:15,align:'right'},
					{field:'endAmt',title:'期末库存金额',width:15,align:'right'}
		   		]]
	});
	$('#dg').datagrid('enableFilter', 
			[{
		        field:'stockDate',
		        type:'datebox',
		        op:['EQ','GE'],
		        fieldType:'S',
		        options:{
		        	editable:false
		        }
		    },{
		        field:'endNum',
		        type:'text',
		    	isDisabled:1
		    },{
		        field:'endAmt',
		        type:'text',
		    	isDisabled:1
		    }]);
})

</script>