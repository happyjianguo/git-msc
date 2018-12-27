<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >
	 	 <div id="tb" class="search-bar" >
	状态: 
            <input class="easyui-combobox" style="width:100px" id="status"/>
            <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
             <span class="datagrid-btn-separator split-line" ></span>
            <shiro:hasPermission name="b2b:report:contractMX:export">
                <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-export',plain:true" onclick="exportexcel()">导出明细</a>
            </shiro:hasPermission>
         </div>
	<div class="single-dg">
		<table  id="dg" ></table>
	</div>

</body>
</html>
<script>

//初始化
function dosearch(){
	var date=new Date();
	var year = date.getFullYear();
	var month = date.getMonth()+1;
	var newmonth = "";
	var newda= "";
	var data="";
	if(month/10 <=1){
		newmonth = "0"+month;
	}
	var da = date.getDate();
	if(da/10 <= 1){
	 	newda = "0"+da;
	}else{
		newda=da;
	}
	var sysdate = year+"-"+newmonth+"-"+newda;
	var status = $("#status").datebox('getValue');
	console.log(status);
	if(status == 1){
		data = {
			"query['t#status_L_EQ']": "effect",	
			"query['t#contract.endValidDate_S_GE']": sysdate
			};
	}else if(status== 2){
		data = {
			"query['t#status_L_EQ']": "effect",	
			"query['t#contract.endValidDate_S_LT']": sysdate
			};
	}else{
		data={};
	}
	//$('#dg').datagrid('load',data);
    $('#dg').datagrid({
        url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/contractMX/page.htmlx",
        queryParams:data
    });
    $('#dg').datagrid('enableFilter', [{
        field:'price',
        type:'text',
        isDisabled:1
    },{
        field:'contractNum',
        type:'text',
        isDisabled:1
    },{
        field:'leftNum',
        type:'text',
        isDisabled:1
    },{
        field:'donePer',
        type:'text',
        isDisabled:1
    },{
        field:'purchasePlanNum',
        type:'text',
        isDisabled:1
    },{
        field:'purchaseNum',
        type:'text',
        isDisabled:1
    },{
        field:'deliveryNum',
        type:'text',
        isDisabled:1
    },{
        field:'returnsNum',
        type:'text',
        isDisabled:1
    },{
        field:'closedNum',
        type:'text',
        isDisabled:1
    },{
        field:'contract.startValidDate',
        type:'datebox',
        op:['EQ','GE'],
        fieldType:'S',
        options:{
            editable:false
        }
    },{
        field:'contract.endValidDate',
        type:'datebox',
        op:['EQ','GE'],
        fieldType:'S',
        options:{
            editable:false
        }
    },{
        field:'contract.effectiveDate',
        type:'datebox',
        op:['EQ','GE'],
        fieldType:'D',
        options:{
            editable:false
        }
    }]);
}
$(function(){
	
	//datagrid
	$('#dg').datagrid({

		//fitColumns:true,
		striped:true,
		singleSelect:true,
		rownumbers:true,
		border:true,
		height :  $(this).height() - $(".search-bar").height() -16,
		pagination:true,
		pageSize:20,
		pageNumber:1,
		remoteFilter: true,
		columns:[[
					{field:'contract.hospitalName',title:'医院名称',width:120,align:'center',
						formatter: function(value,row,index){
							return row.hospitalName;
					}},
					{field:'product.code',title:'药品编码',width:80,align:'center',
						formatter: function(value,row,index){
							return row.productCode;
					}},
		        	{field:'product.name',title:'药品名称',width:120,align:'center',
						formatter: function(value,row,index){
							return row.productName;
					}},
		        	{field:'product.dosageFormName',title:'剂型',width:80,align:'center',
						formatter: function(value,row,index){
							return row.dosageFormName;
					}},	
		        	{field:'product.model',title:'规格',width:80,align:'center',
						formatter: function(value,row,index){
							return row.model;
					}},		
		        	{field:'product.packDesc',title:'包装',width:80,align:'center',
						formatter: function(value,row,index){
							return row.packDesc;
					}},	
		        	{field:'product.unitName',title:'单位',width:80,align:'center',
						formatter: function(value,row,index){
							return row.unitName;
					}},
					{field:'contract.vendorName',title:'供应商',width:120,align:'center',
						formatter: function(value,row,index){
							return row.vendorName;
					}},
		        	{field:'product.producerName',title:'生产企业',width:120,align:'center',
						formatter: function(value,row,index){
							return row.producerName;
					}},
		        	{field:'price',title:'单价',width:80,align:'center'},	
		        	{field:'contractNum',title:'合同量',width:80,align:'center'},		
		        	{field:'leftNum',title:'剩余合同量',width:80,align:'center',formatter: function(value,row,index){
		        			if(row.leftNum<0){
		        				return 0;
		        			}else{
		        				return row.leftNum;
		        			}			
					}},		    
		        	{field:'donePer',title:'执行率',sortable:true,width:80,align:'center',formatter: function(value,row,index){
                        if(row.leftNum<0){
                            return "100%";
                        }else{
                            return row.donePer;
                        }
                    }},
					{field:'purchasePlanNum',title:'采购计划量',width:80,align:'center'},
		        	{field:'purchaseNum',title:'采购确认量',width:80,align:'center'},
		        	{field:'deliveryNum',title:'配送数量',width:80,align:'center'},	
		        	/* {field:'MODEL',title:'入库数量',width:80,align:'center'},		 */
		        	{field:'returnsNum',title:'退货数量',width:80,align:'center'},		
		        	{field:'closedNum',title:'结案数量',width:80,align:'center'},	
		        	{field:'contract.code',title:'合同编号',width:150,align:'center',
						formatter: function(value,row,index){
							return row.contractCode;
					}},
					{field:'code',title:'合同明细编号',width:180,align:'center',
						formatter: function(value,row,index){
							return row.contractMXCode;
					}},
		        	{field:'contract.startValidDate',title:'合同有效期起',width:120,align:'center',
						formatter: function(value,row,index){
							return row.startValidDate;
					}},
		        	{field:'contract.endValidDate',title:'合同有效期止',width:120,align:'center',
						formatter: function(value,row,index){
							return row.endValidDate;
					}},	
		        	{field:'contract.effectiveDate',title:'合同生效时间',width:120,align:'center',
						formatter: function(value,row,index){
							if (row.effectiveDate){
								return $.format.date(row.effectiveDate,"yyyy-MM-dd HH:mm");
							}
					}}
		        	
		   		]],
				<%--toolbar: [{--%>
					<%--iconCls: 'icon-export',--%>
					<%--text:"导出明细",--%>
					<%--handler: function(){--%>
						<%--var date=new Date();--%>
						<%--var year = date.getFullYear();--%>
						<%--var month = date.getMonth()+1;--%>
						<%--var newmonth = "";--%>
						<%--var newda= "";--%>
						<%--var data="";--%>
						<%--if(month/10 <=1){--%>
							<%--newmonth = "0"+month;--%>
						<%--}--%>
						<%--var da = date.getDate();--%>
						<%----%>
						<%--if(da/10 <= 1){--%>
						 	<%--newda = "0"+da;--%>
						<%--}else{--%>
							<%--newda=da;--%>
						<%--}--%>
						<%--var sysdate = year+"-"+newmonth+"-"+newda;--%>
						<%--var ddd = "to_char"--%>
						<%--console.log(sysdate);--%>
						<%--var status = $("#status").datebox('getValue');--%>
						<%--if(status == 1){--%>
							<%--data = {--%>
								<%--"query['t#status_L_EQ']": "effect",	--%>
								<%--"query['t#contract.endValidDate_S_GE']": sysdate--%>
								<%--};--%>
						<%--}else if(status== 2){--%>
							<%--data = {--%>
								<%--"query['t#status_L_EQ']": "effect",	--%>
								<%--"query['t#contract.endValidDate_S_LT']": sysdate--%>
								<%--};--%>
						<%--}else{--%>
							<%--data={};--%>
						<%--}--%>
						<%--var url =" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/contractMX/export.htmlx?";--%>
						<%--for (var k in data) {--%>
							<%--url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(data[k]);--%>
						<%--}--%>
						<%--window.open(url,'_target');--%>
					<%--}--%>
				<%--}],--%>
				queryParams:{
					"query['t#code_S_EQ']":"<c:out value='${code}'/>"
				},
				onDblClickRow: function(index,row){
					top.addTab("三方合同签订 ", "/hospital/contract.htmlx?code="+row.contractCode,"",true);
				}
	});

	$('#ss').searchbox({
		searcher:function(value, name) {
			dosearch();
		},
		menu:'#mm',
		prompt:'支持模糊搜索',
		width:220
	}); 
	$("#status").combobox({    
	    valueField:'label',    
	    textField:'value',  
	    panelHeight:160,
	    editable:false,
	    data:[{
	    	label: '',
			value: '全部'
		},{
			label: '1',
			value: '已签订'
		},{
			label: '2',
			value: '已过期'
		}],
		onSelect:function () {
            dosearch();
        }
	});
    $('#status').combobox('select', '1');
    /*$('#dg').datagrid('enableFilter', [{
        field:'price',
        type:'text',
        isDisabled:1
    },{
        field:'contractNum',
        type:'text',
        isDisabled:1
    },{
        field:'leftNum',
        type:'text',
        isDisabled:1
    },{
        field:'donePer',
        type:'text',
        isDisabled:1
    },{
        field:'purchasePlanNum',
        type:'text',
        isDisabled:1
    },{
        field:'purchaseNum',
        type:'text',
        isDisabled:1
    },{
        field:'deliveryNum',
        type:'text',
        isDisabled:1
    },{
        field:'returnsNum',
        type:'text',
        isDisabled:1
    },{
        field:'closedNum',
        type:'text',
        isDisabled:1
    },{
        field:'contract.startValidDate',
        type:'datebox',
        op:['EQ','GE'],
        fieldType:'S',
        options:{
            editable:false
        }
    },{
        field:'contract.endValidDate',
        type:'datebox',
        op:['EQ','GE'],
        fieldType:'S',
        options:{
            editable:false
        }
    },{
        field:'contract.effectiveDate',
        type:'datebox',
        op:['EQ','GE'],
        fieldType:'D',
        options:{
            editable:false
        }
    }]);*/
    //dosearch();
});

function exportexcel(){

        var date=new Date();
        var year = date.getFullYear();
        var month = date.getMonth()+1;
        var newmonth = "";
        var newda= "";
        var data="";
        if(month/10 <=1){
            newmonth = "0"+month;
        }
        var da = date.getDate();

        if(da/10 <= 1){
            newda = "0"+da;
        }else{
            newda=da;
        }
        var sysdate = year+"-"+newmonth+"-"+newda;
        var ddd = "to_char"
        console.log(sysdate);
        var status = $("#status").datebox('getValue');
        if(status == 1){
            data = {
                "query['t#status_L_EQ']": "effect",
                "query['t#contract.endValidDate_S_GE']": sysdate
            };
        }else if(status== 2){
            data = {
                "query['t#status_L_EQ']": "effect",
                "query['t#contract.endValidDate_S_LT']": sysdate
            };
        }else{
            data={};
        }
        var url =" <c:out value='${pageContext.request.contextPath }'/>/b2b/report/contractMX/export.htmlx?";
        for (var k in data) {
            url +="&"+encodeURIComponent(k)+"="+encodeURIComponent(data[k]);
        }
        window.open(url,'_target');
}

//=============ajax===============

</script>