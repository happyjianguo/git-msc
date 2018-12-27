<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="药品对照查询" />

<html>

<body class="easyui-layout"  >
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>
</body>

</html>
<script>
$(function(){
	function doSearch() {
		var hospitalName = ($("#hospitalName").length==0? "":$("#hospitalName").combo("getText"));
		var query = {"query['t#hospitalName_S_LK']":hospitalName};
		var name = $('#ss').searchbox("getName");
		var value = $('#ss').searchbox("getValue");
		query["query['t#"+name+"_S_LK']"] = value;
		$('#dg').datagrid('load',query); 
	}
	$("#search_btn").click(doSearch);
	$('#ss').searchbox({
		searcher:function(value, name) {
			doSearch();
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 
	$("#hospitalName").combobox({    
	    url:" <c:out value='${pageContext.request.contextPath }'/>/set/hospital/list.htmlx",  
	    valueField:'id',    
	    textField:'fullName',   
	    width:180,
	    onSelect:function(a,b){}
	});
	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height:$(this).height()-4,
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsHospital/page.htmlx",
		pageSize:20,
		pageNumber:1,
		pagination:true,
		remoteFilter: true,
		columns:[[
					<c:if test="${orgType ne 1 }">
					{field:'HOSPITALNAME',title:'医院名称',width:15,align:'center'},
					</c:if>
					{field:'PRODUCTCODE',title:'药品编码',width:15,align:'center'},
					{field:'INTERNALCODE',title:'医院内部编码',width:15,align:'center'},
					{field:'PRODUCTNAME',title:'药品名称',width:15,align:'center'},
					{field:'GENERICNAME',title:'通用名',width:15,align:'center'},
					{field:'DOSAGEFORMNAME',title:'剂型',width:15,align:'center'},
					{field:'MODEL',title:'规格',width:15,align:'center'},
					{field:'UNITNAME',title:'单位',width:15,align:'center'},
					{field:'PACKDESC',title:'包装规格',width:15,align:'center'},
					{field:'PRODUCERNAME',title:'生产企业名称',width:15,align:'center'},
					{field:'ISGPOPURCHASE',title:'是否GPO药品',width:14,align:'center',
						formatter: function(value,row,index){
							if (row.ISGPOPURCHASE ==1){
								return "<img src ='<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png' />";
							}
						}
					},
					{field:'CREATEDATE',title:'上传日期',width:15,align:'center',sortable:true,
						formatter: function(value,row,index){
							if (row.CREATEDATE){
								return $.format.date(row.CREATEDATE,"yyyy-MM-dd");
							}
						}
            }
        ]]
	});
	$('#dg').datagrid('enableFilter', [{
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
                    $('#dg').datagrid('removeFilterRule', 'isGPOPurchase');
                } else {
                    $('#dg').datagrid('addFilterRule', {
                        field: 'isGPOPurchase',
                        op: 'EQ',
                        fieldType:'p#I',
                        value: value
                    });
                }
                $('#dg').datagrid('doFilter');
            }
        }
	},{
        field:'CREATEDATE',
        type:'text',
        isDisabled:1
    }]);
	$("#mapping_btn").click(function() {
		top.$.modalDialog({
			title : "添加",
			width : 1100,
			height : 500,
			href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSource/mapping.htmlx",
			onDestroy:function(){
				$('#dg').datagrid("reload");
			}
		});
	});
})

</script>