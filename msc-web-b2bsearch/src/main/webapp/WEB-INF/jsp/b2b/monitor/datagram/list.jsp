<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<tag:head title="数据报文查询" />

<html>

<body class="easyui-layout"  >
	<div style="padding:5px">
		<div id="search-bar" style="padding:6px">
			<div id="mm">
				<div data-options="name:'senderName' ">发送方名称</div>
				<div data-options="name:'senderCode'">发送方编码</div>
			</div>
		    <input id="ss"/>
			报文类型
			<input class="easyui-combobox" style="width:160px" id="dataType"/>
			发送日期
			<input class="easyui-datebox" style="width:110px" id="startDate"/> -
			<input class="easyui-datebox" style="width:110px" id="toDate"/>
			<a id="search_btn"  class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询  </a> 
			
		</div>
	</div>
	<div style="padding:5px">
		<table  id="dg" ></table>
	</div>
</body>

</html>
<script>
$(function(){
	function doSearch() {
		var startDate = $("#startDate").datebox("getValue");
		var toDate = $("#toDate").datebox("getValue");
		var dataType = $("#dataType").combo("getValue");
		var name = $('#ss').searchbox("getName");
		var value = $('#ss').searchbox("getValue");
		var query = {
				"query['t#datagramType_S_EQ']":dataType,
				"query['t#sendDate_D_GE']":startDate,
				"query['t#sendDate_D_LE']":toDate
			};
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

	//日期
	$('#startDate').datebox({
		editable:false
	});
	$('#toDate').datebox({
		editable:false
	});
	//报文类型json，用于格式化后台反馈前台的信息
	var dataTypeJson = {"product_getToHis":"药品基本信息下载(医院)","product_getToCom":"药品基本信息下载 (GPO)",
					"warehouse_getToCom":"配送点下载","productPrice_send":"价格上传",
					"purchaseOrderPlan_send":"订单计划上传","purchaseOrderPlan_get":"订单计划下载",
					"purchaseOrderPlan_fedback":"订单计划反馈上传","purchaseOrder_send":"订单上传",
					"deliveryOrder_send":"配送单上传","deliveryOrder_get":"配送单下载",
					"returnsOrder_send":"退货单上传","inoutbound_send":"入库单上传",
					"inoutbound_get":"入库单下载","invoice_send":"发票上传","hospitalStock_send":"库存上传",
					"goodsHospital_send":"药品映射关系信息上传","settlement_send":"结算单上传","payment_send":"付款单上传",
					"returnsRequest_get":"退货申请单下载","returnsRequest_fedback":"退货申请单反馈上传",
					"purchaserClosedRequest_fedback":"结案申请单反馈上传","purchaserClosedRequest_send":"结案申请单上传"
			};
	var dataTypeArr = [{label: "",value: "全部"}];
	//转换查询条件数组，方便设置下拉
	for (var k in dataTypeJson) {
		var s ={};s.label=k;s.value=dataTypeJson[k];
		dataTypeArr.push(s);
	}
	
	$("#dataType").combobox({
	    valueField:'label',    
	    textField:'value',  
	    panelHeight:160,
	    editable:false,
	    data:dataTypeArr
	})
	//用户组
	$('#dg').datagrid({
		fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height:$(this).height()-60,
		url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/datagram/page.htmlx",
		pageSize:10,
		pageNumber:1,
		pagination:true,
		idField:'id',
		columns:[[
		        	{field:'datagramType',title:'发送类型',width:15,align:'center',
						formatter: function(value,row,index){
							//获取数据类型
							var val = dataTypeJson[row.datagramType];
							return val?val:'';
						}
					},
		        	{field:'sendData',title:'发送时间',width:10,align:'center',
						formatter: function(value,row,index){
								return row.sendDate?$.format.date(row.sendDate,"yyyy-MM-dd HH:mm:ss"):"";
						}
					}, 
					{field:'senderCode',title:'发送方编码',width:15,align:'center'},
					{field:'senderName',title:'发送方名称',width:15,align:'center'},
					{field:'ip',title:'IP',width:15,align:'center'},
					{field:'dataType',title:'数据格式',width:15,align:'center',
						formatter: function(value,row,index) { 
							return row.dataType==2?'xml':'json'; 
						}
					}
		   		]],
		onClickRow: function(index,row){
			var id=$('#dg').datagrid('getSelected').id;
			//显示JSON格式
			top.$.modalDialog({
				title : "报文数据",
				width : 700,
				height : 400,
				href : " <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/datagram/data.htmlx?id="+id
			});//end top.$.modalDialog
		}
	});
})

</script>