<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>

<body class="easyui-layout">
	<div data-options="region:'north'" style="height:50%;"><div style="padding:5px">
			<div id="mm">
				<div data-options="name:'productName' ">药品名称</div>
				<div data-options="name:'authorizeNo'">国药准字</div>
				<div data-options="name:'standardCode'">本位码</div>
			</div>
		    <input id="ss"/>
        <shiro:hasPermission name="hospital:goodsSourceRev:exportExcel1">
            <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-xls'" onclick="exportexcel()">导出</a>
        </shiro:hasPermission>
		</div>
		<table id="dg1" ></table>
	</div>
	
	<div  data-options="region:'center',title:'标准目录'" style="height:50%;background: rgb(238, 238, 238);">
		<div style="padding:5px">
			<div id="mm1">
				<div data-options="name:'name' ">药品名称</div>
				<div data-options="name:'code'">药品编码</div>
				<div data-options="name:'authorizeNo'">国药准字</div>
				<div data-options="name:'standardCode'">本位码</div>
			</div>
		    <input id="ss1"/>
			通用名：<input id="productName" class="easyui-validatebox  easyui-textbox"/>
			厂家：<input id="producerName" class="easyui-validatebox  easyui-textbox"/>
			<a  class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="query_btn_0" >查询  </a>
		</div>
		<table  id="dg2" ></table>
	</div>
	
<script>
$(function(){
	function seachProduct() {
		var name = $('#ss1').searchbox("getName");
		var value = $('#ss1').searchbox("getValue");
		var selobj = $('#dg1').datagrid('getSelected');
		var productCode = selobj?selobj.PRODUCTCODE:"";
		productCode = productCode || "";
		var data = {
			productCode:productCode,
			productName:$("#productName").textbox("getValue"),
			producerName:$("#producerName").textbox("getValue")
		};
		data["query[p#"+name+"_S_LK]"] = value;
		$('#dg2').datagrid("load", data);
	}
	function doSave(status) {
		var selobj1 = $('#dg1').datagrid('getSelected');
		if(selobj1 == null){
			showMsg("请选择需要核对的数据");
			return;
		}
		var selobj2 = $('#dg2').datagrid('getSelected');
		var msg = "保存并核对";
		var editors = $('#dg1').datagrid('getEditors', $('#dg1').datagrid('getRowIndex', selobj1));
		var data={
			id:selobj1.ID,
			convertRatio:$(editors[0].target).numberbox('getValue'),
			model:$(editors[1].target).textbox('getValue'),
			producerName:$(editors[2].target).textbox('getValue'),
			convertRatio0:$(editors[3].target).numberbox('getValue'),
			unitName:$(editors[4].target).textbox('getValue'),
			yjCode:$(editors[5].target).textbox('getValue'),
			authorizeNo:$(editors[6].target).textbox('getValue'),
			standardCode:$(editors[7].target).textbox('getValue')
		};
		if (status == 1) {
			if(selobj2 == null) {
				showMsg("请选择标准数据");
				return;
			}
			data.productCode=selobj2.CODE;
			data.status = 99;
		} else {
			msg = "保持并备案";
		}
		$.messager.confirm("确认信息", "确认"+msg+"数据吗？", function(r){
			if (r){
				$.ajax({
					url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceError/doSave.htmlx",
					data:data,
					dataType:"json",
					type:"POST",
					cache:false,
					traditional: true,//支持传数组参数
					success:function(data){
						if(data.success){
							$('#dg2').datagrid('reload');
							$('#dg1').datagrid('reload'); 
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
	$('#dg1').datagrid({
		url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceError/sourcePage.htmlx",
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		pageSize:10,
		height:$(this).height()*0.5-35,
		pageNumber:1,
		pagination:true,
		idField:'id',
		columns:[[
					{field:'STATUS',title:'状态',width:70,align:'center',formatter:function(v,r,i) {
						return v == 3?'错误数据':'对照错误';
					}},
		        	{field:'CONVERTRATIO',title:'转换(标准/医院)',width:60,align:'center',
		        		editor:{type:'numberbox', options: { min:0,precision:2 }}
		        	},
					{field:'GENERICNAME',title:'通用名',width:100,align:'center'},
					{field:'MODEL',title:'规格',width:100,align:'center',
		        		editor:{type:'textbox'}
		        	},
					{field:'PRODUCERNAME',title:'厂家',width:150,align:'center',
		        		editor:{type:'textbox'}
		        	},
					{field:'CONVERTRATIO0',title:'转换比',width:100,align:'center',
		        		editor:{type:'numberbox', options: { min:0,precision:0 }}
		        	},
					{field:'UNITNAME',title:'单位',width:100,align:'center',
		        		editor:{type:'textbox'}
		        	},
					{field:'YJCODE',title:'药交ID',width:100,align:'center',
		        		editor:{type:'textbox'}
		        	},
					{field:'AUTHORIZENO',title:'国药准字',width:100,align:'center',
		        		editor:{type:'textbox'}
		        	},
					{field:'STANDARDCODE',title:'本位码',width:100,align:'center',
		        		editor:{type:'textbox'}
		        	},
					{field:'YBDRUGSNO',title:'医保编码',width:100,align:'center'},
					{field:'DOSAGEFORMNAME',title:'剂型',width:100,align:'center'},
					{field:'WJCODE',title:'物价ID',width:150,align:'center'},
					{field:'PRODUCTNAME',title:'药品名称',width:150,align:'center'},
					{field:'HOSPITALNAME',title:'医院名称',width:150,align:'center'},
					{field:'FINALPRICE',title:'零售价',width:100,align:'center'},
					{field:'VENDORNAME',title:'供应商',width:100,align:'center'},
					{field:'BIDDINGPRICE',title:'成交价',width:100,align:'center'},
					{field:'INTERNALCODE',title:'内部药品编码',width:150,align:'center'}
		   		]],
			toolbar: [{
					iconCls: 'icon-ok',
					text:"保存并核对",
					handler: function(){
						doSave(1);	
					}
				},{
					iconCls: 'icon-ok',
					text:"保存并备案",
					handler: function(){
						doSave(0);	
					}
				}
				<%--,{--%>
					<%--iconCls: 'icon-export',--%>
					<%--text:"导出",--%>
					<%--handler: function() {--%>
						<%--window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceRev/exportExcel.htmlx?query[status_L_GE]=3&query[status_L_LE]=4");--%>
					<%--}--%>
			<%--}--%>
			],
			onLoadSuccess:function(data){
				$('#dg1').datagrid('doCellTip',{delay:500}); 
			},
			onClickRow: function(index,row) {
				$('#dg1').datagrid('beginEdit', index);
				$('#ss1').searchbox("setValue","");
				$('#dg2').datagrid({
					url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceError/newProductPage.htmlx",
					queryParams:{
						productCode:row.PRODUCTCODE||"",
						productName:row.GENERICNAME,
						producerName:row.PRODUCERNAME
					}
				});
			}
	});
	
	//用户组
	$('#dg2').datagrid({
		fitColumns:false,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		width:"100%",
		height:$(this).height()*0.5-70,
		pageSize:10,
		pageNumber:1,
		pagination:true,
		columns:[[
					{field:'GENERICNAME',title:'通用名',width:150,align:'center'},
					{field:'MODEL',title:'药品规格',width:100,align:'center'},
					{field:'PACKDESC',title:'包装规格',width:100,align:'center'},
					{field:'PRODUCERNAME',title:'厂家',width:150,align:'center'},
					{field:'AUTHORIZENO',title:'国药准字',width:100,align:'center'},
					{field:'IMPORTFILENO',title:'注册号',width:100,align:'center'},
					{field:'STANDARDCODE',title:'本位码',width:150,align:'center'},
					{field:'YBDRUGSNO',title:'医保编码',width:100,align:'center'},
					{field:'NAME',title:'药品名称',width:200,align:'center'},
					{field:'CODE',title:'药品编码',width:100,align:'center'},
					{field:'DOSAGEFORMNAME',title:'剂型',width:100,align:'center'}
		   		]],
		onLoadSuccess:function(data) {
			if (data.productWords&&data.productWords.length > 0) {
				$("#productName").textbox("setValue", data.productWords.join(" "));
			}
			if (data.producerWords&&data.producerWords.length > 0) {
				$("#producerName").textbox("setValue", data.producerWords.join(" "));
			}
			var selobj = $('#dg1').datagrid('getSelected');
			var productCode = selobj?selobj.PRODUCTCODE:"";
			if (productCode) {
				$.each(data.rows,function(index,row) {
					if (productCode == row.CODE)
						$('#dg2').datagrid("selectRow",index);
				});
			}
			$('#dg2').datagrid('doCellTip',{delay:500}); 
		}
	});
	$("#query_btn_0").click(seachProduct);
	$('#ss').searchbox({
		searcher:function(value, name) {
			var data={};
			data["query[t#"+name+"_S_LK]"] = value;
			$('#dg1').datagrid("load", data);
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 
	$('#ss1').searchbox({
		searcher:function(value, name) {
			seachProduct();
		},
		menu:'#mm1',
		prompt:'支持模糊搜索',
		width:220
	}); 
	
});
function exportexcel(){
    window.open(" <c:out value='${pageContext.request.contextPath }'/>/hospital/goodsSourceRev/exportExcel.htmlx?query[status_L_GE]=3&query[status_L_LE]=4");
}

</script>
</body>
</html>