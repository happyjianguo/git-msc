<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>

<body class="easyui-layout"  >
	
	
	<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:280px;" >
		<div style="padding:5px">
			<form id="fileId" method="POST" enctype="multipart/form-data" style="display:none;">
				<input type="file" name="myfile" class="file" id="myfile" />
			</form>
			<div id="mm2">
				<div data-options="name:'productName' ">药品名称</div>
				<div data-options="name:'yjCode'">药交ID</div>
				<div data-options="name:'authorizeNo'">国药准字</div>
				<div data-options="name:'standardCode'">本位码</div>
			</div>
		    <input id="ss2"/>
			医院：<input id="hospitalName" class="easyui-validatebox  easyui-textbox" style="width:80px;"/>
			状态：<select id="status" class="easyui-combobox" style="width:100px;">
			    <option value="">全部</option>
			    <option value="99">审核完成</option>
			    <option value="6">药师备案</option>
			    <option value="5">医院备案</option>
			    <option value="4">对照错误</option>
			    <option value="3">问题数据</option>
			    <option value="2">已对照</option>
			    <option value="1">自动对照</option>
			    <option value="0">未对照</option>
			</select>
			<a  class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="query_btn_1"  >查询  </a> 
		</div>
		<table id="dg2" ></table>
	</div>
	
	<div data-options="region:'center',title:''"   class="my-center" >
		<div style="padding:5px">
			<div id="mm1">
				<div data-options="name:'importFileNo'">注册证号</div>
				<div data-options="name:'authorizeNo'">国药准字</div>
				<div data-options="name:'name' ">药品名称</div>
				<div data-options="name:'code'">药品编码</div>
				<div data-options="name:'productGCode'">标准编码</div>
				<div data-options="name:'standardCode'">本位码</div>
			</div>
		    <input id="ss1"/>
			通用名：<input id="productName" class="easyui-validatebox  easyui-textbox" style="width:90px;"/>
			厂家：<input id="producerName" class="easyui-validatebox  easyui-textbox" style="width:90px;"/>
			<a  class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="query_btn_0" >查询  </a> 
		</div>
		<table  id="dg1" ></table>
	</div>
</div>
<script>
$(function(){
	function setError(status) {
		var selobj = $('#dg2').datagrid('getSelected');
		if(selobj == null){
			showMsg("请选择医院目录");
			return;
		}
		var msg = "";
		if (status == 0) {
			msg = "确认设置数据不完整吗？";
		} else if (status == 3) {
			msg = "确认设置该数据有问题吗？";
		} else if (status == 5) {
			msg = "确认设置数据备案吗？";
		}
		$.messager.confirm("确认信息", msg, function(r) {
			if (r) {
				$.ajax({
					url:" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/setError.htmlx",
					data:{
						"id":selobj.id,
						"status":status
					},
					dataType:"json",
					type:"POST",
					cache:false,
					traditional: true,//支持传数组参数
					success:function(data){
						if(data.success){
							$('#dg2').datagrid('reload');
							$('#dg1').datagrid('reload'); 
							showMsg("设置成功！");
						} else if(data.msg) {
							showMsg(data.msg);
						} else {
							showMsg("设置失败");
						
						}
					},
					error:function(){
						showErr("出错，请刷新重新操作");
					}
				});	
			}
		});
	}
	function doSetup(productCode, id, convertRatio) {
		$.ajax({
			url:" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/mapping.htmlx",
			data:{
				"productCode":productCode,
				"id":id,
				"convertRatio":convertRatio
			},
			dataType:"json",
			type:"POST",
			cache:false,
			traditional: true,//支持传数组参数
			success:function(data){
				if(data.success){
					$('#dg2').datagrid('reload');  
					showMsg("设置成功！");
				} else {
					showMsg("设置失败！");
				}
			},
			error:function(){
				showErr("出错，请刷新重新操作");
			}
		});	
	}
	function setup() {
		var selobj0 = $('#dg2').datagrid('getSelected');
		if(selobj0 == null){
			showMsg("请选择医院目录");
			return;
		}
		var selobj1 = $('#dg1').datagrid('getSelected');
		if(selobj1 == null){
			showMsg("请选择国家目录");
			return;
		}
		
	 	var ed = $('#dg2').datagrid('getEditor', { 
	 		index: $('#dg2').datagrid('getRowIndex', selobj0) ,
	 		field: 'convertRatio' });
	 	var id = selobj0.id;
     	var productCode = selobj1.CODE;
	 	var convertRatio = $(ed.target).numberbox('getValue');
		if (convertRatio.length == 0){
			showMsg("请输入转换比");
			return;
		}
		if (isNaN(convertRatio)){
			showMsg("转换比请输入数字");
			return;
		}
		if (selobj0.status == 4) {
			$.messager.confirm("确认信息", "药品已审核，确认要合并生成新药品吗?", function(r) {
				if (r) doSetup(productCode, id, convertRatio);
			});
     	} else {
     		doSetup(productCode, id, convertRatio);
     	}
	}
	function delAjax(id){
		$.ajax({
			url:" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/delete.htmlx",
			data:"id="+id,
			dataType:"json",
			type:"POST",
			cache:false,
			success:function(data){
				if(data.success){				
					$('#dg2').datagrid('reload');
					showMsg("删除成功");
				} 
			},
			error:function(){
				showErr("出错，请刷新重新操作");
			}
		});	
	}
	function delFunc() {
		var selobj = $('#dg2').datagrid('getSelected');
		if(selobj == null){
			$.messager.alert('错误','没有选中行!','info');
			return;
		}
		var id= selobj.id;
		$.messager.confirm('确认信息', '确认要删除此属性?', function(r){
			if (r){
				delAjax(id);
			}
		});
	}
	//用户组
	$('#dg1').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height:$(".my-center").height()-36,
		pageSize:10,
		pageNumber:1,
		pagination:true,
		columns:[[
					{field:'GENERICNAME',title:'通用名',width:150,align:'center'},
					{field:'MODEL',title:'规格',width:100,align:'center',
						formatter: function(value,row,index){
								return row.MODEL+"*"+row.PACKDESC;
						}
					},
					{field:'PRODUCERNAME',title:'厂家',width:150,align:'center'},
					{field:'AUTHORIZENO',title:'国药准字',width:110,align:'center'},
					{field:'IMPORTFILENO',title:'注册号',width:100,align:'center'},
					{field:'STANDARDCODE',title:'本位码',width:150,align:'center'},
					{field:'YBDRUGSNO',title:'医保编码',width:100,align:'center'},
					{field:'NAME',title:'药品名称',width:200,align:'center'},
					{field:'PRODUCTGCODE',title:'标准编码',width:100,align:'center'},
					{field:'DOSAGEFORMNAME',title:'剂型',width:100,align:'center'}
		   		]],
		onLoadSuccess:function(data) {
			if (data.productWords&&data.productWords.length > 0) {
				$("#productName").textbox("setValue", data.productWords.join(" "));
			}
			if (data.producerWords&&data.producerWords.length > 0) {
				$("#producerName").textbox("setValue", data.producerWords.join(" "));
			}
			var selobj = $('#dg2').datagrid('getSelected');
			var productCode = selobj?selobj.productCode:"";
			if (productCode) {
				$.each(data.rows,function(index,row) {
					if (productCode == row.CODE)
						$('#dg1').datagrid("selectRow",index);
				});
			}
			$('#dg1').datagrid('doCellTip',{delay:500}); 
		},
		toolbar: [{
			iconCls: 'icon-17-8',
			text:"药品数据库",
			handler: function(){
				top.$.modalDialog({
					title : "远程药品库数据",
					width : 1000,
					height : 500,
					href : " <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/productSource.htmlx",
					onLoad:function(){
						
					},
					buttons : [{
						text : '去药品库新增',
						iconCls : 'icon-17-8',
						handler : function() {
							window.open("http://49.4.89.196/medic/login.jsp");
						}
					}, {
						text : '获取数据并保存',
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
		},{
			iconCls: 'icon-export',
			text:"启动医院新增excel生成任务",
			handler: function() {
				$.ajax({
					url:" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/mkFileByNeedCreate.htmlx?key=isBak",
					dataType:"json",
					type:"POST",
					success:function(data){
						showMsg(data.msg);
					},
					error:function(){
						showErr("出错，请刷新重新操作");
					}
				});	
			}
		},{
			iconCls: 'icon-export',
			text:"导出未对照",
			handler: function() {
				window.open(" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/downFile.htmlx?key=unCompare");
			}
		},{
			iconCls: 'icon-export',
			text:"导出医院备案",
			handler: function() {
				window.open(" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/downFile.htmlx?key=isBak5");
			}
		}]
	});
	//用户组
	$('#dg2').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/sourcePage.htmlx",
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		height:$(".my-north").height()-32,
		pageSize:10,
		pageNumber:1,
		pagination:true,
		idField:'id',
		columns:[[
		        	{field:'convertRatio',title:'转换比(标准/医院)',width:60,align:'center',
		        		editor:{type:'numberbox', options: { min:0,precision:2 }},
		        	},
					{field:'genericName',title:'通用名',width:100,align:'center'},
					{field:'model',title:'规格',width:100,align:'center'},
					{field:'producerName',title:'厂家',width:150,align:'center'},
					{field:'authorizeNo',title:'国药准字',width:110,align:'center'},
					{field:'standardCode',title:'本位码',width:100,align:'center'},
					{field:'ybdrugsNO',title:'医保编码',width:100,align:'center'},
					{field:'convertRatio0',title:'转换比',width:100,align:'center'},
					{field:'doseUnit',title:'最小制剂单位',width:100,align:'center'},
					{field:'unitName',title:'单位',width:100,align:'center'},
					{field:'dosageFormName',title:'剂型',width:100,align:'center'},
					{field:'yjCode',title:'药交ID',width:100,align:'center'},
					{field:'priceFileNo',title:'物价ID',width:150,align:'center'},
					{field:'productName',title:'药品名称',width:150,align:'center'},
					{field:'hospitalName',title:'医院名称',width:150,align:'center'},
					{field:'finalPrice',title:'零售价',width:100,align:'center'},
					{field:'vendorName',title:'供应商',width:100,align:'center'},
					{field:'biddingPrice',title:'成交价',width:100,align:'center'},
					{field:'internalCode',title:'内部药品编码',width:150,align:'center'}
		   		]],
			toolbar: [{
					iconCls: 'icon-ok',
					text:"映射",
					handler: function(){
						setup();
					}
				},{
					iconCls: 'icon-no',
					text:"取消",
					handler: function(){
						setError(0);
					}
				},{
					iconCls: 'icon-no',
					text:"备案",
					handler: function(){
						setError(6);
					}
				},{
					iconCls: 'icon-no',
					text:"问题数据",
					handler: function(){
						setError(3);
					}
				},{
					iconCls: 'icon-no',
					text:"删除",
					handler: function(){
						delFunc();
					}
				},{
					iconCls: 'icon-import',
					text:"导入",
					handler: function(){
						$("#myfile").click();
					}
				},{
					iconCls: 'icon-export',
					text:"导出",
					handler: function() {
						var hospitalName = $("#hospitalName").textbox("getValue");
						if ($.trim(hospitalName) == "") {
							showMsg("医院名称不能为空");
							return false;
						}
						window.open(" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/exportExcel.htmlx?query[status_L_EQ]="+$("#status").combobox("getValue")+"&query[hospitalName_S_LK]="+$("#hospitalName").textbox("getValue"));
					}
			}],
			onLoadSuccess:function(data){
				$.each(data.rows,function(index,row){
					$('#dg2').datagrid('beginEdit', index);
				}); 
				$('#dg2').datagrid('doCellTip',{delay:500}); 
			},
			onClickRow: function(index,row) {
				$("#internalName").textbox("setValue", row.NAME);
				$('#dg1').datagrid({
					url:" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/productPage.htmlx",
					queryParams:{
						productCode:row.productCode||"",
						productName:row.genericName,
						//dosageFormName:row.dosageFormName,
						producerName:row.producerName
					}
				});
			}
	});
	

	$("#myfile").change(function(){
		$.messager.confirm('确认信息', '确认导入药品资料么?', function(r){
			if (r){
				$("#fileId").submit();
			}else{
				location.reload(true);
			}
		});
	});
	$("#fileId").form({
		url :" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsSource/importExcel.htmlx",
		onSubmit : function() {return true;},
		success : function(result) {
			$('#myfile').val("");
			result = $.parseJSON(result);
			if(result.success){
				$('#dg2').datagrid('reload');
				showMsg(result.msg);
			}else{
				showMsg(result.msg);
			}
		},
		error:function(){
			showErr("出错，请刷新重新操作");
		}
	});
	function searchHospital() {
		var name = $('#ss2').searchbox("getName");
		var value = $('#ss2').searchbox("getValue");
		var data = {
			"query[status_L_EQ]":$("#status").combobox("getValue"),
			"query[hospitalName_S_LK]":$("#hospitalName").textbox("getValue")
		}
		data["query["+name+"_S_LK]"] = value,
		$('#dg2').datagrid("load",data); 
	}
	function searchProduct() {
		var name = $('#ss1').searchbox("getName");
		var value = $('#ss1').searchbox("getValue");
        var selobj = $('#dg2').datagrid('getSelected');
		var productCode = selobj?selobj.PRODUCTCODE:"";
		productCode = productCode || "";
		var data = {
			productCode:productCode,
			productName:$("#productName").textbox("getValue"),
			producerName:$("#producerName").textbox("getValue"),
		};
		data["query[p#"+name+"_S_LK]"] = value;
		$('#dg1').datagrid("load", data);
	}
	$("#query_btn_0").click(searchProduct);
	$("#query_btn_1").click(searchHospital);
	$('#ss2').searchbox({
		searcher:function(value, name) {
			searchHospital();
		},
		menu:'#mm2',
		prompt:'支持模糊搜索',
		width:170
	}); 
	$('#ss1').searchbox({
		searcher:function(value, name) {
			searchProduct();
		},
		menu:'#mm1',
		prompt:'支持模糊搜索',
		width:160
	}); 
})

</script>
</body></html>