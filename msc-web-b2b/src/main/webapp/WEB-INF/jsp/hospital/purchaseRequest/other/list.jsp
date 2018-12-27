<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="订单列表" />
<html>
<body class="easyui-layout">
	<div id="tt">
		<div title="订单列表" class="my-tabs">
			<div id="tb" class="search-bar">
				订单计划日期: <input class="easyui-datebox" style="width: 110px"
					id="startDate"> ~ <input class="easyui-datebox"
					style="width: 110px" id="toDate"> 状态: <input
					class="easyui-combobox" style="width: 100px" id="status" /> 药品编码: <input
					id="productCode" class="easyui-textbox" style="width: 150px" />
				药品名称: <input id="productName" class="easyui-textbox"
					style="width: 150px" />
					拼音检索: 
	 	<input id="pinyin" class="easyui-textbox" style="width:150px"/>
				<a href="#" class="easyui-linkbutton"
					data-options="iconCls:'icon-search',plain:true"
					onclick="dosearch()">查询</a>
				<span class="datagrid-btn-separator split-line"></span>
				<a href="#" class="easyui-linkbutton"
					data-options="iconCls:'icon-ok',plain:true" onclick="addOpen()">结案申请</a>
				<a href="#" class="easyui-linkbutton search-filter"
					data-options="iconCls:'icon-filter',plain:true"
					onclick="filterFunc()">数据过滤</a>
			</div>
			<table id="dg"></table>
		</div>
		<div title="订单明细" class="my-tabs">
			<div id="tbd" class="search-bar">
				<b>订单号：</b><i id="orderCode"></i> <span
					class="datagrid-btn-separator split-line"></span> <b>医疗机构：</b><i
					id="hospitalName"></i> <span
					class="datagrid-btn-separator split-line"></span> <b>供应商名称：</b><i
					id="vendorName"></i> <span
					class="datagrid-btn-separator split-line"></span>
				<a href="#" class="easyui-linkbutton"
					data-options="iconCls:'icon-ok',plain:true" onclick="closeDetail()">明细结案申请</a>
			</div>
			<table id="dgDetail"></table>
		</div>
	</div>
</body>
</html>
<script>
	var isFilter = 0;
	function filterFunc() {
		if (isFilter == 1)
			return;
		isFilter = 1;
		$('#dg').datagrid({
			remoteFilter : true
		});
		$('#dg').datagrid('enableFilter', [ {
			field : 'ORDERDATE',
			type : 'datebox',
			options : {
				onChange : function(value) {
					if (value == '') {
						$('#dg').datagrid('removeFilterRule', 'ORDERDATE');
					} else {
						$('#dg').datagrid('addFilterRule', {
							field : 'ORDERDATE',
							op : 'LK',
							fieldType : 'D',
							value : value
						});
					}
					$('#dg').datagrid('doFilter');
				}
			}
		}, {
			field : 'REQUIREDATE',
			type : 'datebox',
			options : {
				onChange : function(value) {
					if (value == '') {
						$('#dg').datagrid('removeFilterRule', 'REQUIREDATE');
					} else {
						$('#dg').datagrid('addFilterRule', {
							field : 'REQUIREDATE',
							op : 'LK',
							fieldType : 'D',
							value : value
						});
					}
					$('#dg').datagrid('doFilter');
				}
			}
		}, {
			field : 'NUM',
			type : 'text',
			isDisabled : 1

		}, {
			field : 'DELIVERYNUM',
			type : 'text',
			isDisabled : 1

		}, {
			field : 'INOUTBOUNDNUM',
			type : 'text',
			isDisabled : 1

		}, {
			field : 'RETURNSNUM',
			type : 'text',
			isDisabled : 1

		}, {
			field : 'SUM',
			type : 'text',
			isDisabled : 1

		}, {
			field : 'STATUS',
			type : 'text',
			isDisabled : 1

		}, {
			field : 'OPERATION',
			type : 'text',
			isDisabled : 1

		} ]);
		$('#dg').datagrid('reload');
	}

	//弹窗增加
	function addOpen() {
		var selobj = $('#dg').datagrid('getSelected');
		if (selobj == null) {
			$.messager.alert('错误', '没有选中行!', 'info');
			return;
		}
		var status = selobj.STATUS;
		if (status == "2" || status == "receivied" || status == "3") {
			$.messager.alert('错误', '该状态不能申请结案');
			return;
		}
		var id = selobj.ID;
		top.$.modalDialog({
			title : "结案申请",
			width : 600,
			height : 300,
			href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseRequest/other/add.htmlx",
			onLoad : function() {
			},
			buttons : [ {
				text : '保存',
				iconCls : 'icon-ok',
				handler : function() {
					top.$.modalDialog.openner = $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
					var f = top.$.modalDialog.handler.find("#form1");
					var isValid = f.form('validate');
					if (!isValid)
						return;
					var reason = f.find("#reason").textbox("getValue");
					closedAjax(id, reason);
					top.$.modalDialog.handler.dialog('close');
				}
			}, {
				text : '取消',
				iconCls : 'icon-cancel',
				handler : function() {
					top.$.modalDialog.handler.dialog('destroy');
					top.$.modalDialog.handler = undefined;
				}
			} ]
		});
	}
	function closeDetail() {
		var selobj = $('#dg').datagrid('getSelected');
		var selobjD = $('#dgDetail').datagrid('getSelected');
		if (selobjD == null) {
			showErr("请选中一笔数据");
			return;
		}
		var status = selobj.STATUS;
		if (status == "2" || status == "receivied" || status == "3") {
			$.messager.alert('错误', '该状态不能申请结案');
			return;
		}
		var id = selobj.ID;
		top.$.modalDialog({
			title : "结案申请",
			width : 600,
			height : 300,
			href : " <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseRequest/other/add.htmlx",
			onLoad : function() {
			},
			buttons : [ {
				text : '保存',
				iconCls : 'icon-ok',
				handler : function() {
					top.$.modalDialog.openner = $('#dg');//因为添加成功之后，需要刷新这个treegrid，所以先预定义好
					var f = top.$.modalDialog.handler.find("#form1");
					var isValid = f.form('validate');
					if (!isValid)
						return;
					var reason = f.find("#reason").textbox("getValue");
					closeDetailAjax(id, reason);
					top.$.modalDialog.handler.dialog('close');
				}
			}, {
				text : '取消',
				iconCls : 'icon-cancel',
				handler : function() {
					top.$.modalDialog.handler.dialog('destroy');
					top.$.modalDialog.handler = undefined;
				}
			} ]
		});
	}
	function closeDetailAjax(id, reason) {
		var selobjs = $('#dgDetail').datagrid('getSelections');
		var datas = new Array();
		var flagindex = 0;
		$.each(selobjs, function(index, row) {
			var data = new Object();
			data.productCode = row.productCode;
			data.code = row.code;
			datas[flagindex++] = data;
		});
		var data = JSON.stringify(datas);
		//alert(data);
		$.ajax({
			url : " <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchaseOrder/closedDetail.htmlx",
			data : {
				data : data,
				id : id,
				reason : reason
			},
			dataType : "json",
			type : "POST",
			cache : false,
			success : function(data) {
				if (data.success) {
					$('#dgDetail').datagrid('reload');
					showMsg(data.msg);
				} else {
					showErr(data.msg);
				}

			},
			error : function() {
				showErr("出错，请刷新重新操作");
			}
		});
	}
	function closedAjax(id, reason) {
		$.ajax({
			url : " <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/purchaseOrder/closed.htmlx",
			data : {
				id : id,
				reason : reason
			},
			dataType : "json",
			type : "POST",
			cache : false,
			success : function(data) {
				if (data.success) {
					$('#dg').datagrid('reload');
					showMsg(data.msg);
				}
			},
			error : function() {
				showErr("出错，请刷新重新操作");
			}
		});
	}
	//搜索
	function dosearch() {
		var startDate = "";
		var toDate = "";
		var productCode = "";
		var productName = "";
		var pinyin="";
		if($('#productCode').textbox('getValue')!=""){
			productCode=$('#productCode').textbox('getValue');
		}
		if($('#productName').textbox('getValue')!=""){
			productName=$('#productName').textbox('getValue');	
		}
		if($('#pinyin').textbox('getValue')!=""){
			pinyin=$('#pinyin').textbox('getValue').toUpperCase();	
		}

		if ($('#startDate').datebox('getValue') != "") {
			startDate = $('#startDate').datebox('getValue') + " 00:00:00";
		}
		if ($('#toDate').datebox('getValue') != "") {
			toDate = $('#toDate').datebox('getValue') + " 23:59:59";
		}
		var status = $("#status").datebox('getValue');
		var data = {
			"query['t#createDate_D_GE']" : startDate,
			"query['t#createDate_D_LE']" : toDate,
			"query['t#status_S_EQ']" : status,
			"query['d#productCode_S_EQ']":productCode,
			"query['d#productName_S_LK']":productName,
			"query['p#pinyin_S_LK']":pinyin
			
		};

		$('#dg').datagrid('load', data);
	}
	//查询
	function searchDefectsList(row) {
		$("#orderCode").html(row.CODE);
		$("#hospitalName").html(row.HOSPITALNAME);
		$("#vendorName").html(row.VENDORNAME);
		$('#dgDetail').datagrid({
			url : " <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseRequest/other/mxlist.htmlx",
			queryParams : {
				"query['t#purchaseOrder.id_L_EQ']" : row.ID,
			}
		});
	}

	//初始化
	$(function() {
		$('#tt').tabs({
			plain : true,
			justified : true
		});
		$('#ss').searchbox({
			searcher : function(value, name) {
				dosearch();
			},
			menu : '#mm',
			prompt : '支持模糊搜索',
			width : 220
		});
		$("#status").combobox({
			valueField : 'label',
			textField : 'value',
			panelHeight : 'auto',
			editable : false,
			data : [ {
				label : '',
				value : '全部'
			}, {
				label : 'effect',
				value : '已生效'
			}, {
				label : 'sending',
				value : '配送中'
			} ],
			onChange : function(value) {
				dosearch();
			}
		});

		$('#dgDetail').datagrid({
			fitColumns : true,
			striped : true,
			singleSelect : false,
			rownumbers : true,
			border : true,
			height : $(this).height() - ($("#tbd").length == 0 ? 0 : 70),
			pagination : true,
			pageSize : 10,
			pageNumber : 1,
			showFooter : false,
			columns : [ [ {
				field : 'productCode',
				title : '药品编码',
				width : 10,
				align : 'center'
			}, {
				field : 'productName',
				title : '药品名称',
				width : 20,
				align : 'center'
			}, {
				field : 'producerName',
				title : '生产企业',
				width : 20,
				align : 'center'
			}, {
				field : 'price',
				title : '成交价（元）',
				width : 10,
				align : 'right',
				formatter : function(value, row, index) {
					if (row.price) {
						return common.fmoney(row.price);
					}
				}
			}, {
				field : 'dosageFormName',
				title : '剂型',
				width : 10,
				align : 'center'
			}, {
				field : 'model',
				title : '规格',
				width : 10,
				align : 'center'
			}, {
				field : 'unit',
				title : '单位',
				width : 10,
				align : 'center'
			}, {
				field : 'goodsNum',
				title : '采购数量',
				width : 10,
				align : 'center'
			}, {
				field : 'deliveryGoodsNum',
				title : '配送数量',
				width : 10,
				align : 'center'
			}, {
				field : 'inOutBoundGoodsNum',
				title : '入库数量',
				width : 10,
				align : 'center'
			}, {
				field : 'returnsGoodsNum',
				title : '退货数量',
				width : 10,
				align : 'center'
			}, {
				field : 'goodsSum',
				title : '采购金额 （元）',
				width : 10,
				align : 'right',
				formatter : function(value, row, index) {
					if (row.goodsSum) {
						return common.fmoney(row.goodsSum);
					}
				}
			} ] ],
			onLoadSuccess : function(data) {
				$('#dg').datagrid('doCellTip', {
					delay : 500
				});
			},
		});
		$('#dg').datagrid({
			fitColumns : true,
			striped : true,
			singleSelect : true,
			rownumbers : true,
			border : true,
			height : $(this).height() - ($("#tb").length == 0 ? 0 : 35),
			pagination : true,
			url : " <c:out value='${pageContext.request.contextPath }'/>/hospital/purchaseRequest/other/page.htmlx",
			pageSize : 10,
			pageNumber : 1,
			toolbar : "#tb",
			columns : [ [ {
				field : 'CODE',
				title : '订单号',
				width : 25,
				align : 'center'
			}, {
				field : 'ORDERDATE',
				title : '订单日期',
				width : 15,
				align : 'center',
				formatter : function(value, row, index) {
					if (row.ORDERDATE) {
						return $.format.date(row.ORDERDATE, "yyyy-MM-dd HH:mm:ss");
					}
				}
			}, {
				field : 'REQUIREDATE',
				title : '要求配货时间',
				width : 15,
				align : 'center',
				formatter : function(value, row, index) {
					if (row.REQUIREDATE) {
						return $.format.date(row.REQUIREDATE, "yyyy-MM-dd HH:mm:ss");
					}
				}
			}, {
				field : 'HOSPITALNAME',
				title : '医疗机构',
				width : 15,
				align : 'center'
			}, {
				field : 'VENDORNAME',
				title : '供应商',
				width : 15,
				align : 'center'
			},
			/*         	{field:'warehouseName',title:'收货地点',width:10,align:'center'}, */
			{
				field : 'NUM',
				title : '采购数量',
				width : 10,
				align : 'center'
			}, {
				field : 'DELIVERYNUM',
				title : '配送数量',
				width : 10,
				align : 'center'
			}, {
				field : 'INOUTBOUNDNUM',
				title : '入库数量',
				width : 10,
				align : 'center'
			}, {
				field : 'RETURNSNUM',
				title : '退货数量',
				width : 10,
				align : 'center'
			}, {
				field : 'SUM',
				title : '采购金额（元）',
				width : 15,
				align : 'right',
				formatter : function(value, row, index) {
					if (row.SUM) {
						return common.fmoney(row.SUM);
					}
				}
			}, {
				field : 'STATUS',
				title : '状态',
				width : 10,
				align : 'center',
				formatter : function(value, row, index) {
					if (row.STATUS == '0') {
						return "已生效";
					} else if (row.STATUS == '1') {
						return "配送中";
					} else if (row.STATUS == '2') {
						return "配送完成";
					} else if (row.STATUS == '3') {
						return "强行结案";
					}
				}
			} ] ],
			onDblClickRow : function(index, row) {
				$("#tt").tabs("select", 1);
				searchDefectsList(row);
			},
			onLoadSuccess : function(data) {

				var rows = $(this).datagrid("getRows");
				//大于1行默认选中
				if ($(this).datagrid("getRows").length > 0) {
					$(this).datagrid('selectRow', 0);
					searchDefectsList($(this).datagrid("getRows")[0]);
				}
				$('#dg').datagrid('doCellTip', {
					delay : 500
				});
			},
			queryParams : {
				"query['t#code_S_EQ']" : "<c:out value='${code}'/>",
				"query['t#purchaseOrderPlanCode_S_EQ']" : "<c:out value='${purchaseOrderPlanCode}'/>"
			}
		});
	});
</script>