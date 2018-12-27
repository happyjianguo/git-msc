<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
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

.datagrid-cell-c2-segmentBD,.datagrid-cell-c2-segmentStr{
	color:#0081c2;
}
</style>
<body class="easyui-layout" >
	<div id="tt">
		<div title="招标项目" class="my-tabs">
			<table id="dg" class="single-dg"></table>
		</div>
		<div title="招标明细" class="my-tabs">
			<form id="fileId" method="POST" enctype="multipart/form-data">
				<input type="file" name="myfile" class="file" id="myfile" style="display: none" /> 
				<input type='hidden' id="projectId" name='projectId' />
			</form>
			<table id="dg1" class="single-dg"></table>
		</div>
	</div>

</body>
</html>

<script>
var detailRow = '';
//初始化
$(function(){
	$('#tt').tabs({
		plain:true,
		justified:true
	});
	
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() - 33,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/directory/page.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
        	{field:'code',title:'项目编号',width:10,align:'center'},
        	{field:'name',title:'项目名称',width:10,align:'center'},
        	{field:'startDate',title:'开始日期',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.startDate){
						return $.format.date(row.startDate,"yyyy-MM-dd");
					}
				}
        	},
        	{field:'endDate',title:'截至日期',width:10,align:'center',
				formatter: function(value,row,index){
					if (row.endDate){
						return $.format.date(row.endDate,"yyyy-MM-dd");
					}
				}
        	},
        	{field:'projectStusName',title:'项目名称',width:10,align:'center'}
   		]],
		onDblClickRow:function(index,row) {
			$("#tt").tabs("select",1);
        	searchDefectsList(row);
		}
	});
	
	
	$('#dg').datagrid('enableFilter',[{
        field:'type',
        type:'combobox',
        options:{
            panelHeight:'auto',
            editable:false,
            data:[{value:'',text:'-请选择-'},
                  {value:'base',text:'基本药物'},
                  {value:'notBase',text:'非基本药物'}
                  ],
            onChange:function(value){
                if (value == ''){
                	$('#dg').datagrid('removeFilterRule', 'type');
                } else {
                	$('#dg').datagrid('addFilterRule', {
                        field: 'type',
                        op: 'EQ',
                        fieldType:'S',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
    },{
        field:'startDate',
        type:'datebox',
        op:['EQ','GE'],
        fieldType:'D',
        options:{
        	editable:false
        }
    },{
        field:'endDate',
        type:'datebox',
        op:['EQ','GE'],
        fieldType:'D',
        options:{
        	editable:false
        }
    },{
        field:'details',
        type:'text',
    	isDisabled:1
    }]);
	
	window.detailsTable={};
	var options = {formatter:myformatter,parser:myparser,editable:false,onChange:changeValue};
	$('#dg1').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		border:true,
		height :  $(this).height() - 33,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/directory/mxpage.htmlx",
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
		        	
		        	{field:'projectCode',title:'项目编码',width:10,align:'center',formatter: function(value,row,index){
		        		return row.project.code;
		        	}},
		        	{field:'projectName',title:'项目名称',width:10,align:'center',formatter: function(value,row,index){
		        		return row.project.name;
		        	}},
		        	{field:'directory.genericName',title:'通用名',width:10,align:'center',formatter: function(value,row,index){
		        		return row.directory.genericName;
		        	}},
		        	{field:'directory.dosageFormName',title:'剂型',width:10,align:'center',formatter: function(value,row,index){
		        		return row.directory.dosageFormName;
		        	}},
		        	{field:'directory.model',title:'规格',width:10,align:'center',formatter: function(value,row,index){
		        		return row.directory.model;
		        	}},
		        	{field:'directory.qualityLevel',title:'质量层次',width:10,align:'center',formatter: function(value,row,index){
		        		return row.directory.qualityLevel;
		        	}},
		        	{field:'directory.minUnit',title:'最小制剂单位',width:10,align:'center',formatter: function(value,row,index){
		        		return row.directory.minUnit;
		        	}},
		        	{field:'num',title:'医院报量数',width:10,align:'center'},
		        	{field:'segmentBD',title:'价格',width:10,align:'center', editor: { type: 'numberbox', options: {min:0,precision:2} }},
		        	{field:'segmentStr',title:'生产厂家',width:30,align:'center',editor: { type: 'combobox', options: {
		        		valueField:'PRODUCERID',    
		    			textField:'PRODUCERNAME'
		        	} },
					formatter: function(value,row,index){
						if(row.segmentStr.indexOf("_")>0){
							return row.segmentStr.split("_")[1];
						}else
							return row.segmentStr;
					}}
		   		]],
		toolbar: [
		 <% 
			String isStart = "1";
		 	if("1".equals(isStart)) {
		%>
		 {
			iconCls: 'icon-ok',
			text:"投标",
			id:'ok1_btn',
			handler: function(){
				submit();	
			}
		},{
			iconCls: 'icon-import',
			id:'fileImport',
			id:'import1_btn',
			text:"导入投标",
			handler: function(){}
		},
		<%}%>
		{
			iconCls: 'icon-export',
			text:"导出投标模板",
			id:'export1_btn',
			handler: function(){
				window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/plan/export.htmlx?projectCode="+detailRow.code);
			}
		}],
		onLoadSuccess:function(data){
			//$('#dg1').datagrid('doCellTip',{delay:500}); 
		},
		onClickRow:function(index,row){
			var v = "";
			if(row.segmentStr.indexOf("_")>0){
				v = row.segmentStr.split("_")[0];
			}
			$('#dg1').datagrid('beginEdit', index);
			var ed = $('#dg1').datagrid('getEditor', {index:index,field:'segmentStr'});
			$(ed.target).combobox({
				url: ' <c:out value='${pageContext.request.contextPath }'/>/vendor/directory/producerComb.htmlx',
    			queryParams:{
    			    "productName":row.directory.genericName,
    			},
    			value:v
			});
		}
	});

	
	$('#dg1').datagrid('enableFilter', 
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
	
	
});

function searchDefectsList(row){
	detailRow = row;
	if(detailRow.projectStus == 'tender') {
		$("#ok1_btn").linkbutton('enable');
		$("#no1_btn").linkbutton('enable');
		$("#import1_btn").linkbutton('enable');
		$("#export1_btn").linkbutton('enable');
	}else{
		$("#ok1_btn").linkbutton('disable');
		$("#no1_btn").linkbutton('disable');
		$("#import1_btn").linkbutton('disable');
		$("#export1_btn").linkbutton('disable');
	}
	
	$('#dg1').datagrid('load', {
			"projectId":row.id
	});
}
function submit() {
	if(detailRow.projectStus != 'tender') {
		showErr("目前状态无法进行投标");
		return;
	}
	var selobjs = $('#dg1').datagrid('getRows');
	var arr = [];
	$.each(selobjs,function(i,row) {
	 	var priceEd = $('#dg1').datagrid('getEditor', { index: i, field: 'segmentBD' });
	 	if (priceEd) {
	 		var price = $(priceEd.target).numberbox('getValue');
			if($.trim(price)=="") {
				return true;
			}
			
			var producerEd = $('#dg1').datagrid('getEditor', { index: i, field: 'segmentStr' });
			var producerCode = $(producerEd.target).combobox('getValue');
			var producerName = $(producerEd.target).combobox('getText');
			if($.trim(producerCode)=="") {
				return true;
			}
			
    		var plan = {};
    		var projectDetail = {};
    		projectDetail.id = row.id;
    	 	plan.projectDetail = projectDetail;
	     	plan.price = $.trim(price);
	     	plan.id = row.segmentLong;
	     	plan.producerCode = producerCode;
	     	plan.producerName = producerName;
	     	arr.push(plan);
		 }
	});
	if (arr.length == 0) {
		showMsg("请输入药品投标信息");
		return;
	}
	$.ajax({
		url:" <c:out value='${pageContext.request.contextPath }'/>/vendor/directory/tender.htmlx",
		data:{
			"fastjson":JSON.stringify(arr)
		},
		dataType:"json",
		type:"POST",
		cache:false,
		traditional: true,//支持传数组参数
		success:function(data){
			if(data.success){
				$('#dg1').datagrid('reload'); 
				showMsg("投标成功！");
			} else {
				showMsg(data.msg);
			}
		},
		error:function(){
			$('#dg1').datagrid('reload'); 
			showErr("出错，请重新操作");
		}
	});	
}

function doDelete() {
	if(detailRow.projectStus != 'report') {
		showErr("目前状态无法进行清除");
		return;
	}
	var selrow = $('#dg1').datagrid("getSelected");
	if (selrow && selrow.HOSPITALPLANID) {
		$.messager.confirm('确认信息', '确认清除报量数据吗?', function(r) {
			if(r){
				$.ajax({
					url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/plan/delete.htmlx",
					data:{"hospitalPlanId":selrow.HOSPITALPLANID},
					dataType:"json",
					type:"POST",
					cache:false,
					traditional: true,//支持传数组参数
					success:function(data){
						if(data.success){
							$('#dg1').datagrid('reload'); 
							showMsg(data.msg);
						} else {
							showMsg(data.msg);
						}
					},
					error:function(){
						$('#dg1').datagrid('reload'); 
						showMsg("出错，请重新操作");
					}
				});	
			}
		});
	} else {
		showMsg("选择报过量的数据清除");
	}
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
		var row = $("#dg1").datagrid("getRows")[index];
		var startMonth = $('#dg1').datagrid('getEditor', { index: index, field: 'STARTMONTH' });
		var endMonth = $('#dg1').datagrid('getEditor', { index: index, field: 'ENDMONTH' });
		var num = $('#dg1').datagrid('getEditor', { index: index, field: 'NUM' });
		if (!num) {
			num =  $('#dg1').datagrid("getRows")[index]["NUM"];
			if (!num) return;
		}
		if (startMonth) {
			startMonth = $(startMonth.target).datebox('getValue').replace("-","");
		} else {
			startMonth =  $('#dg1').datagrid("getRows")[index]["STARTMONTH"];
		}
		if (endMonth) {
			endMonth = $(endMonth.target).datebox('getValue').replace("-","");
		} else {
			endMonth =  $('#dg1').datagrid("getRows")[index]["ENDMONTH"];
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
				console.log(getDetails(index));
				var panel = top.$.modalDialog.handler.find("#dg").datagrid("loadData", getDetails(index));
			},
			buttons : [{
				text : '设置',
				iconCls : 'icon-ok',
				handler : function() {
					top.$.modalDialog.openner= $('#dg1');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
					var ret = top.getDetailSetup();
					if (ret && ret!=null){
						window.detailsTable["detail"+index]=ret.details;
						var num_edit = $('#dg1').datagrid('getEditor', { index: index, field: 'NUM' });
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
	var row = $("#dg1").datagrid("getRows")[index];
	if(!window.detailsTable["detail"+index]) {

		var num = $('#dg1').datagrid('getEditor', { index: index, field: 'NUM' });
		var startMonth = $('#dg1').datagrid('getEditor', { index: index, field: 'STARTMONTH' });
		var endMonth = $('#dg1').datagrid('getEditor', { index: index, field: 'ENDMONTH' });
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
	var row = $('#dg1').datagrid("getSelected");
	var index = $('#dg1').datagrid("getRowIndex",row);
	var num = $('#dg1').datagrid('getEditor', { index: index, field: 'NUM' });
	if(window.detailsTable["detail"+index]) {
		window.detailsTable["detail"+index]=undefined;
		window.detailsTable["isEdit"+index]=true;
	} else {
		window.detailsTable["isEdit"+index]=true;
	}
}

</script>