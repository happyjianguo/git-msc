<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >
<style>
a,a:ACTIVE,a:VISITED,a:HOVER{text-decoration: none;}
.datagrid-row-selected a,.datagrid-row-selected a:ACTIVE,.datagrid-row-selected a:VISITED,.datagrid-row-selected a:HOVER{color:white;text-decoration: none;}
.datagrid-toolbar .file{
    position: absolute;
    left: 0px;
    top: 0px;
    opacity: 0;
    height:30px;
    width:70px;
    -ms-filter: 'alpha(opacity=0)';
    filter:alpha(opacity=0); 
    cursor: pointer;
    font-size:0;
}
</style>

<div class="search-bar">
	状态：<select id="status" class="easyui-combobox">
	<option value="0">全部</option>
	<option value="1">未报量</option>
	<option value="2">已报量</option>
	</select>
	<a href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="query_btn">查询</a>
</div>
					
<div class="single-dg">
	<table  id="dg" ></table>
</div>
<script language="javascript">
function filterFunc(){
	$('#dg').datagrid('enableFilter', 
		[{
	        field:'GENERICNAME',
	        type:'text',
	        op:'LK',
			fieldType:'c#S'
	    },{
	        field:'DOSAGEFORMNAME',
	        type:'text',
	        op:'LK',
			fieldType:'c#S'
	    },{
	        field:'MODEL',
	        type:'text',
	        op:'LK',
			fieldType:'c#S'
	    },{
	        field:'QUALITYLEVEL',
	        type:'text',
	        op:'LK',
			fieldType:'c#S'
	    },{
	        field:'NOTE',
	        type:'text',
	        op:'LK',
			fieldType:'c#S'
	    },{
	        field:'NUM',
	        type:'text',
	    	isDisabled:1
	    },{
	        field:'STARTMONTH',
	        type:'text',
	    	isDisabled:1
	    },{
	        field:'ENDMONTH',
	        type:'text',
	    	isDisabled:1
	    },{
	        field:'details',
	        type:'text',
	    	isDisabled:1
	    }]);
}
//初始化
$(function(){
	window.detailsTable={};
	var options = {formatter:myformatter,parser:myparser,editable:false,onChange:changeValue};
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/plan/planPage.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	{field:'GENERICNAME',title:'通用名',width:20,align:'center'},
		        	{field:'DOSAGEFORMNAME',title:'剂型',width:12,align:'center'},
		        	{field:'MODEL',title:'规格',width:14,align:'center'},
		        	{field:'QUALITYLEVEL',title:'质量层次',width:11,align:'center'},
		        	{field:'NOTE',title:'报名企业',width:11,align:'center'},
		        	{field:'MINUNIT',title:'最小使用单位',width:11,align:'center'},
		        	{field:'NUM',title:'采购量',width:17,align:'center', editor: { type: 'numberbox', options: {min:0,precision:0,onChange:changeValue} }},
		        	{field:'STARTMONTH',title:'计划开始',width:10,align:'center', editor: { type: 'datebox',options:options}},
		        	{field:'ENDMONTH',title:'计划结束',width:10,align:'center', editor: { type: 'datebox',options:options}},
		        	{field:'details',title:'明细',width:5,align:'center', formatter: function(value,row,index){
						return "<a href=\"javascript:;\" onclick=\"setDetails("+index+")\">明细</a>";
					}}
		   		]],
		toolbar: [
		 <% 
			String isStart = String.valueOf(request.getAttribute("isStart"));
		 	if("1".equals(isStart)) {
		%>
		 {
			iconCls: 'icon-ok',
			text:"报量",
			handler: function(){
				submit();	
			}
		},{
			iconCls: 'icon-no',
			text:"清除",
			handler: function(){
				doDelete();	
			}
		},{
			iconCls: 'icon-import',
			id:'fileImport',
			text:"导入报量",
			handler: function(){}
		},
		<%}%>
		{
			iconCls: 'icon-export',
			text:"导出报量模板",
			handler: function(){
				window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/plan/export.htmlx?projectCode=<c:out value='${projectCode}'/>");
			}
		}],
		onLoadSuccess:function(data){
			$('#dg').datagrid('doCellTip',{delay:500}); 
			if ($("#form1").length >0) return;
			$("#fileImport").children().append('<form id="form1" method="POST" enctype="multipart/form-data" ><input type="file" name="myfile" class="file" id="myfile"/></form>');
			$("#myfile").on("change", function(){
				if($(this).val() == "") return;
				$.messager.confirm('确认信息', '确认导入药品资料么?', function(r){
					if (r){
						$("#form1").submit();
					}else{
						location.reload(true);
					}
				});
			});
			$("#form1").form({
				url :" <c:out value='${pageContext.request.contextPath }'/>/hospital/plan/imp.htmlx",
				onSubmit : function() {return true;},
				success : function(result) {
					result = $.parseJSON(result);
					if(result.success){
						$('#dg').datagrid('reload');
						var file = $("#myfile") ;
						file.after(file.clone().val(""));   
						file.remove();  
						showMsg(result.msg);
					}else{
						showMsg(result.msg);
					}
				},
				error:function(){
					showErr("出错，请刷新重新操作");
				}
			});
		},
		onClickRow:function(index,row){
			 <% 
			 	if("1".equals(isStart)) {
			%>
			$('#dg').datagrid('beginEdit', index);
			<%}%> 
		},queryParams:{
			"query['b#code_S_EQ']":"<c:out value='${projectCode}'/>"
		}
	});
	$("#query_btn").click(function() {
		var data = {};
		data["query['b#code_S_EQ']"]="<c:out value='${projectCode}'/>";
		data["status"]=$("#status").combobox("getValue");
		$('#dg').datagrid('load',data);
		
	});
	
	filterFunc();
});

function submit() {
	
	var selobjs = $('#dg').datagrid('getRows');
	var arr = [];
	var f = true;
	$.each(selobjs,function(i,row) {
	 	var num = $('#dg').datagrid('getEditor', { index: i, field: 'NUM' });
		var startMonth = $('#dg').datagrid('getEditor', { index: i, field: 'STARTMONTH' });
		var endMonth = $('#dg').datagrid('getEditor', { index: i, field: 'ENDMONTH' });
	 	if (num && startMonth && endMonth) {
     		num = $(num.target).numberbox('getValue');
			startMonth = $(startMonth.target).datebox('getValue');
			endMonth = $(endMonth.target).datebox('getValue');
			if($.trim(num)=="") {
				return;
			}
			if($.trim(startMonth)=="") {
				showMsg("请输入计划开始日期");
				return f = false;
			}
			if($.trim(endMonth)=="") {
				showMsg("请输入计划结束日期");
				return f = false;
			}
			if(endMonth < startMonth) {
				showMsg("结束日期不能小于开始日期");
				return f = false;
			}
    		var plan = {};
    	 	plan.projectDetailId = row.ID;
	     	plan.num = $.trim(num);
	     	plan.startMonth = startMonth;
	     	plan.endMonth = endMonth;
	     	if (window.detailsTable["detail"+i] && window.detailsTable["detail"+i].length >0) {
 		     	plan.details = window.detailsTable["detail"+i];
	 		    plan.isEdit = window.detailsTable["isEdit"+i];
	     	}
	     	arr.push(plan);
		 }
	});
	if (!f) return;
	if (arr.length == 0) {
		showMsg("请输入药品报量信息");
		return;
	}
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/plan/setup.htmlx",
		data:{
			jsonStr:JSON.stringify(arr)
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg').datagrid('reload'); 
				showMsg("提报成功！");
				window.detailsTable = [];
			} else {
				showMsg(data.msg);
			}
		},
		error:function(){
			$('#dg').datagrid('reload'); 
			showErr("出错，请重新操作");
		}
	});	
}
function myformatter(date){
    var y = date.getFullYear();
    var m = date.getMonth()+1;
    var d = date.getDate();
    return y+'-'+(m<10?('0'+m):m);
}

function myparser(s){
    if (!s) return new Date();
    var ss = (s.split('-'));
    var y = parseInt(ss[0],10);
    var m = parseInt(ss[1],10);
    return new Date(y,m-1);
} 
function setDetails(index) {
	setTimeout(function(){
		var row = $("#dg").datagrid("getRows")[index];
		var startMonth = $('#dg').datagrid('getEditor', { index: index, field: 'STARTMONTH' });
		var endMonth = $('#dg').datagrid('getEditor', { index: index, field: 'ENDMONTH' });
		var num = $('#dg').datagrid('getEditor', { index: index, field: 'NUM' });
		if (!num) {
			num =  $('#dg').datagrid("getRows")[index]["NUM"];
			if (!num) return;
		}
		if (startMonth) {
			startMonth = $(startMonth.target).datebox('getValue').replace("-","");
		} else {
			startMonth =  $('#dg').datagrid("getRows")[index]["STARTMONTH"];
		}
		if (endMonth) {
			endMonth = $(endMonth.target).datebox('getValue').replace("-","");
		} else {
			endMonth =  $('#dg').datagrid("getRows")[index]["ENDMONTH"];
		}
		if (!startMonth) {
			showMsg("计划开始月份不能为空");
			return;
		}
		if (!endMonth) {
			showMsg("计划结束月份不能为空");
			return;
		}
		if (startMonth > endMonth) {
			showMsg("计划结束日期不能大于计划结束日期");
			return;
		}
		top.$.modalDialog({
			title : "添加",
			width : 500,
			height : 300,
			href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/plan/details.htmlx",
			onLoad:function(){
				var panel = top.$.modalDialog.handler.find("#dg").datagrid("loadData", getDetails(index));
			},
			buttons : [{
				text : '设置',
				iconCls : 'icon-ok',
				handler : function() {
					top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
					var ret = top.getDetailSetup();
					if (ret && ret!=null){
						window.detailsTable["detail"+index]=ret.details;
						var num_edit = $('#dg').datagrid('getEditor', { index: index, field: 'NUM' });
						var total_num = Number($(num_edit.target).numberbox("getValue"));
						if (total_num != ret.total) {
							window.stopChangeValue=true;
							$(num_edit.target).numberbox('setValue',ret.total);
						}
					}
					top.$.modalDialog.handler.dialog('destroy');
					top.$.modalDialog.handler = undefined;
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
		
	},200);
	
	
}
function getDetails(index) {
	var row = $("#dg").datagrid("getRows")[index];
	if(!window.detailsTable["detail"+index]) {

		var num = $('#dg').datagrid('getEditor', { index: index, field: 'NUM' });
		var startMonth = $('#dg').datagrid('getEditor', { index: index, field: 'STARTMONTH' });
		var endMonth = $('#dg').datagrid('getEditor', { index: index, field: 'ENDMONTH' });
		num = $(num.target).numberbox('getValue');
		if (!num) {
			num=0;
		}
		if (window.detailsTable["notFirst"+index] != true && num == row["NUM"] && row.HOSPITALPLANID) {
			$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/plan/details.htmlx",
				data:{hospitalPlanId:row.HOSPITALPLANID},
				dataType:"json",
				type:"POST",
				cache:false,
				async: false,
				traditional: true,//支持传数组参数
				success:function(data){
					window.detailsTable["detail"+index]=data;
				}
			});
			window.detailsTable["notFirst"+index] = true;
			return window.detailsTable["detail"+index];
		}
		month0 = parseInt($(startMonth.target).datebox('getValue').replace("-",""));
		month1 = parseInt($(endMonth.target).datebox('getValue').replace("-",""));
		var a = parseInt(month1/100) - parseInt(month0/100);
		var b = parseInt(month1%100) - parseInt(month0%100);
		if (a != 0) {
			a = a*12;
		}
		var cycle = b + a + 1;
		var detailNum = parseInt(num/cycle);
		var data = [];
		for(var j=0;j<cycle;j++) {
			if (j + 1== cycle) {
				detailNum = num;
			} else {
				num = parseInt((num - detailNum).toFixed(0));
			}
			
			var year = parseInt(month0/100);
			var month = j+(month0%100);
			if (month > 12) {
				month = month - 12;
				year = year + 1;
			}
			if (month < 10) {
				month = "0" + month;
			}
			data.push({month:(year +"-" + month),num:detailNum});
		}
		window.detailsTable["detail"+index]=data;
	}
	return window.detailsTable["detail"+index];
}
function changeValue() {
	if (window.stopChangeValue) {
		window.stopChangeValue=false;
		return;
	}
	var rowIndex=$(this).parents(".datagrid-row").attr("datagrid-row-index");
	var row = $('#dg').datagrid("getSelected");
	var index = $('#dg').datagrid("getRowIndex",row);
	var num = $('#dg').datagrid('getEditor', { index: index, field: 'NUM' });
	if(window.detailsTable["detail"+index]) {
		window.detailsTable["detail"+index]=undefined;
		window.detailsTable["isEdit"+index]=true;
	} else {
		window.detailsTable["isEdit"+index]=true;
	}
}
function readfile(fileid) {
	window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/plan/readfile.htmlx?fileid="+fileid);
}
function doDelete() {
	var selrow = $('#dg').datagrid("getSelected");
	if (selrow && selrow.HOSPITALPLANID) {
		$.messager.confirm('确认信息', '确认清除报量数据吗?', function(r) {
			$.ajax({
				url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/plan/delete.htmlx",
				data:{hospitalPlanId:selrow.HOSPITALPLANID},
				dataType:"json",
				type:"POST",
				cache:false,
				traditional: true,//支持传数组参数
				success:function(data){
					if(data.success){
						$('#dg').datagrid('reload'); 
						showMsg(data.msg);
					} else {
						showMsg(data.msg);
					}
				},
				error:function(){
					$('#dg').datagrid('reload'); 
					showMsg("出错，请重新操作");
				}
			});	
		});
	} else {
		showMsg("选择报过量的数据清除");
	}
}
</script>
</body>
</html>