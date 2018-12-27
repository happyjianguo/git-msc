<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<html>

<body class="easyui-layout" >			
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>

</body>
</html>

<script>

//初始化
$(function(){
	//datagrid
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height : $(this).height() -3,
		pagination:true,
		url:" <c:out value='${pageContext.request.contextPath }'/>/set/companyCertReg/page.htmlx",
		queryParams:{
		},
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
					{field:'sendTime',title:'接收时间',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.sendTime){
								return $.format.date(row.sendTime,"yyyy-MM-dd HH:mm");
							}
						}
					},
					{field:'declarant.name',title:'申报公司名称',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.declarant){
								return row.declarant.fullName;
							}
						}
					},
					{field:'company.name',title:'证照公司名称',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.company){
								return row.company.fullName;
							}
						}
	    			},
		        	{field:'companyType',title:'公司类型',width:10,align:'center',
		        		formatter: function(value,row,index){
		        			var ss = ""
							if (row.company){
								if(row.company.isProducer == 1){
									ss += "厂商";
								}
								if(row.company.isVendor == 1){
									if(ss != ""){
										ss += "、";
									}
									ss += "供应商";
								}
							}
		        			return ss;
						}
	    			},
		        	{field:'typeName',title:'证照类型',width:10,align:'center'},
		        	{field:'code',title:'证照代码',width:10,align:'center'},
		        	{field:'name',title:'证照名称',width:10,align:'center'},
		        	{field:'validDate',title:'证照有效期',width:10,align:'center'},
	    			{field:'auditStatus',title:'审核状态',width:10,align:'center',
		        		formatter: function(value,row,index){
							if (row.auditStatus){
								if(row.auditStatus == 'send'){
									return "审核中";
								}
								if(row.auditStatus == 'pass'){
									return "审核通过";
								}
								if(row.auditStatus == 'back'){
									return "已退回";
								}							
							}
						}
	    			}
		   		]],
		toolbar:  [],
		onDblClickRow: function(index,field,value){
			editOpen();
		}
	});

	$('#dg').datagrid('enableFilter', [{
        
	}]);
	
});

//弹窗修改
function editOpen() {
	var selrow = $('#dg').datagrid('getSelected');
	top.$.modalDialog({
		title : "修改",
		width : 800,
		height : 500,
		href : " <c:out value='${pageContext.request.contextPath }'/>/set/companyCertReg/edit.htmlx",
		queryParams:{
			id:selrow.id
		},
		onLoad:function(){

		},
		buttons : [ {
			text : '保存',
			iconCls : 'icon-ok',
			handler : function() {
				top.$.modalDialog.openner= $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
				var f = top.$.modalDialog.handler.find("#form1");
				f.submit();
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
}

</script>