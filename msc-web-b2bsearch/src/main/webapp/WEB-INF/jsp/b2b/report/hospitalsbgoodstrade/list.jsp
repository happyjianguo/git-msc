<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >  
	<div class="search-bar">
					<form id="form1">
					医疗机构
					<input id="name" name="name" class="easyui-validatebox  easyui-textbox" /> 
					时间段
					<input id="yearS" name="yearS" /> <input id="monthS" name="monthS" /> -
	   				<input id="yearE" name="yearE" /> <input id="monthE" name="monthE" /> 
 					<a id="btn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="dosearch()" >查询  </a> 
					
					</form>
	</div>
	<div class="single-dg">
	<table  id="dg" ></table>
	</div>
	



</body>
</html>
<script>
function dosearch(){
	var isValid = $("#form1").form('validate');
	if(!isValid)
		return;
	var para1 = $("#name").textbox("getValue");
	var para2 = $("#yearS").combo('getValue');
	var para3 = $("#monthS").combo('getValue');
	var para4 = $("#yearE").combo('getValue');
	var para5 = $("#monthE").combo('getValue');
	$('#dg').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/hospitalsbgoodstrade/page.htmlx",
		queryParams:{
			"name":para1,
			"yearS":para2,
			"monthS":para3,
			"yearE":para4,
			"monthE":para5
	    }
	}); 
}


//初始化
$(function(){
	
	initYearCombobox("yearS");
	initMonthCombobox("monthS");
	initYearCombobox("yearE");
	initMonthCombobox("monthE");
	
	/* //下拉
	$('#hospitalId').combogrid({
		idField:'id',    
		textField:'fullName',
		url: " <c:out value='${pageContext.request.contextPath }'/>/dm/hospital/page.htmlx",
		pagination:true,
		pageSize:20,
		pageNumber:1,
	    columns: [[
	        {field:'fullName',title:'医院名称',width:260}
	    ]],
	    panelWidth:280,
		required: false,
		keyHandler: {
            query: function(q) {
                //动态搜索
                $('#hospitalId').combogrid('grid').datagrid("reload",{"query['t#fullName_S_LK']":q});
                $('#hospitalId').combogrid("setValue", q);
            }

        }
	});
	
	$('#hospitalId').combogrid('grid').datagrid('getPager').pagination({
		showPageList:false,
		showRefresh:false,
		displayMsg:"共{total}记录"
	});	 */
	
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		columns:[[
		        	{field:'HOSPITALNAME',title:'医疗机构',width:10,align:'center'},
		        	{field:'SUM',title:'总交易金额（元）',width:20,align:'right',
						formatter: function(value,row,index){
							if (row.SUM){
								return common.fmoney(row.SUM);
							}
						}},
		        	{field:'SBSUM',title:'社保交易金额（元）',width:30,align:'right',
						formatter: function(value,row,index){
							if (row.SBSUM){
								return common.fmoney(row.SBSUM);
							}
						}},
		        	{field:'bl',title:'社保所占比例',width:20,align:'center',
						formatter: function(value,row,index){
							var v = row.SBSUM/row.SUM*100;
							v = v.toFixed(2);
							return v +"%";
						}
		        	}
		   		]]
	});
	dosearch();
});



//=============ajax===============

</script>