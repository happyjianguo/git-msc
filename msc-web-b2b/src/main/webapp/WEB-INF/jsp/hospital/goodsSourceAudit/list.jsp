<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>

<body class="easyui-layout">
	<div data-options="region:'center'" style="height:100%;">
		<div style="padding:5px">
				药品名称：<input id="productName" class="easyui-validatebox  easyui-textbox" style="width:80px;"/>
				</select>
				中西药分类：<select  id="productGCode" class="easyui-combobox">
					<option value="">全部</option>
					<option value="Z">中药</option>
					<option value="X">西药</option>
				</select>
				<a  class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="query_btn"  >查询  </a>
            <shiro:hasPermission name="hospital:goodsSourceRev:exportExcel">
            <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls'" onclick="exportexcel()">导出</a>
            </shiro:hasPermission>
        </div>
        <table id="dg" ></table>
	</div>
	
<script>
$(function(){
	function audit(status) {
		var selobjs = $('#dg').datagrid('getSelections');
		if(selobjs == null || selobjs.length ==0){
			showMsg("请选择对照数据");
			return;
		}
		var ids = [];
		$(selobjs).each(function() {
			ids.push(this.ID);
		});
     	var msg = "审核通过";
     	if (status!=1) {
     		msg = "审核不通过";
     	}
		$.messager.confirm("确认信息", "确认"+msg+"吗?", function(r){
			if (r){
				$.ajax({
					url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceAudit/audit.htmlx",
					data:{
						"ids":ids.join(","),
						"status":status
					},
					dataType:"json",
					type:"POST",
					cache:false,
					traditional: true,//支持传数组参数
					success:function(data){
						if(data.success){
							$('#dg').datagrid('reload'); 
							showMsg(msg+"成功！");
						} else {
							showMsg(msg+"失败！");
						}
					},
					error:function(){
						$('#dg').datagrid('reload'); 
						showErr("出错，请重新操作");
					}
				});	
			}
		});
	}
	//用户组
	$('#dg').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceAudit/sourcePage.htmlx",
		fitColumns:false,
		striped:true,
		singleSelect:false,
		rownumbers:true,
		height:$(this).height()-35,
		pageSize:10,
		pageNumber:1,
		pagination:true,
		idField:'ID',
		columns:[[{  
                        field: "ck",  
                        checkbox: true  
                    },
					{field:'PRODUCTNAME',title:'医院药品名称',width:100,align:'center'},
					{field:'PRODUCTNAME0',title:'监管药品名称',width:150,align:'center'},
					{field:'MODEL',title:'医院规格',width:100,align:'center'},
					{field:'MODEL0',title:'监管规格',width:100,align:'center'},
					{field:'PACKDESC0',title:'监管包装规格',width:100,align:'center'},
					{field:'PRODUCERNAME',title:'医院厂家',width:150,align:'center'},
					{field:'PRODUCERNAME0',title:'监管药品厂家',width:150,align:'center'},
					{field:'CONVERTRATIO0',title:'医院转换比',width:100,align:'center'},
					{field:'CONVERTRATIO1',title:'监管转换比',width:100,align:'center'},
					{field:'UNITNAME',title:'医院单位',width:50,align:'center'},
					{field:'AUTHORIZENO',title:'医院国药准字',width:100,align:'center'},
					{field:'AUTHORIZENO0',title:'监管国药准字',width:100,align:'center',
						formatter: function(value,row,index) {
							return (value==""||!value)?row.IMPORTFILENO:row.AUTHORIZENO0;
						}
					},
					{field:'STANDARDCODE',title:'医院本位码',width:100,align:'center'},
					{field:'STANDARDCODE0',title:'监管本位码',width:100,align:'center'},
					{field:'PRODUCTCODE',title:'监管药品编码',width:150,align:'center'}
		   		]],
			toolbar: [{
					iconCls: 'icon-ok',
					text:"审核通过",
					handler: function(){
						audit(1);	
					}
				},{
					iconCls: 'icon-no',
					text:"审核不通过",
					handler: function(){
						audit(0);	
					}
				}
				<%--,{--%>
					<%--iconCls: 'icon-export',--%>
					<%--text:"导出",--%>
					<%--handler: function() {--%>
						<%--window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceRev/exportExcel.htmlx?query[status_L_EQ]=2");--%>
					<%--}--%>
			<%--}--%>
			],
			onLoadSuccess:function(data){
				$('#dg').datagrid("clearSelections");
				$('#dg').datagrid('doCellTip',{delay:500}); 
			}
	});
	$("#query_btn").click(function() {
		$('#dg').datagrid("load",{
			"query[t#status_L_EQ]":2,
			"query[b#productGCode_S_LK]":$("#productGCode").combobox("getValue"),
			"query[productName_S_LK]":$("#productName").textbox("getValue")
		});
	});
});
function exportexcel(){
    window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceRev/exportExcel.htmlx?query[status_L_EQ]=2");
}

</script>
</body>
</html>