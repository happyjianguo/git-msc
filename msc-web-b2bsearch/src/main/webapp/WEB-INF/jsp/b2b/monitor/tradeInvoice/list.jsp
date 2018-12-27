<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />
<html>
<body class="easyui-layout" >
<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:100%;">
    <div id="tb" class="search-bar" >
        药品编码:<input id="productCode" type="text" class="easyui-textbox" style="width:100px">
        药品名称:<input id="productName" type="text" class="easyui-textbox" style="width:100px">
        批号:<input id="batchCode" type="text" class="easyui-textbox" style="width:100px">
        发票日期: <input class="easyui-datebox" style="width:110px" id="startDate">
        ~ <input class="easyui-datebox" style="width:110px" id="toDate">
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="dosearch()">查询</a>
        <shiro:hasPermission name="admin:tradeInvoice:doCount">
            <a id="btn1"  class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="doCount()" >统计  </a>
        </shiro:hasPermission>
        <span class="datagrid-btn-separator split-line" ></span>
        <a href="#"  class="easyui-linkbutton search-filter" data-options="iconCls:'icon-filter',plain:true" onclick="filterFunc()">数据过滤</a>
    </div>
    <div id="tt">
        <div title="配送商发票信息" class="my-tabs">
            <table id="dgDetail" class="single-dg"></table>
        </div>
        <div title="gpo发票信息" class="my-tabs">
            <table id="dgDetail1" class="single-dg"></table>
        </div>
        <div title="生产企业发票信息" class="my-tabs">
            <table id="dgDetail2" class="single-dg"></table>
        </div>
    </div>
</div>

</body>
</html>
<script>
    function filterFunc() {
        var tab = $('#tt').tabs('getSelected');
        var index = $("#tt").tabs('getTabIndex',tab);
        var $1 = '';
        if(index == 0 ){
            $1 = $('#dgDetail');
        }else if (index == 1){
            $1 = $('#dgDetail1');
        }else {
            $1 = $('#dgDetail2');
        }
        $1.datagrid({
            remoteFilter : true
        });
        $1.datagrid('enableFilter', [ {
            field : 'goodsNum',
            type : 'text',
            isDisabled : 1
        },{
            field : 'goodsSum',
            type : 'text',
            isDisabled : 1
        },{
            field : 'filePath',
            type : 'text',
            isDisabled : 1
        },{
            field:'isExistsParent',
            type:'combobox',
            options:{
                panelHeight:'auto',
                editable:false,
                data:[{value:'',text:'-请选择-'},
                    {value:'0',text:'否'},
                    {value:'1',text:'是'}],
                onChange:function(value){
                    if (value == ''){
                        $1.datagrid('removeFilterRule', 'isExistsParent');
                    } else {
                        $1.datagrid('addFilterRule', {
                            field: 'isExistsParent',
                            op: 'EQ',
                            fieldType:'I',
                            value: value
                        });
                    }
                    $1.datagrid('doFilter');
                }
            }
        }
      ]);
    }
    //var selectTab = 0;
    var isRedFlag = 0;
    //初始化
    $(function(){
        $('#tt').tabs({
            plain:true,
            justified:true
        });
        /*$("#tt").tabs({
            border:false,
            onSelect : function (title) {
                alert(title);
            }
        });*/
        $('#dgDetail').datagrid({
            fitColumns:true,
            striped:true,
            singleSelect:true,
            rownumbers:true,
            border:true,
            height : $(this).height() - $(".search-bar").height() -39,
            url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/tradeInvoice/mxlist.htmlx",
            pagination:true,
            pageSize:20,
            pageNumber:1,
            remoteFilter: true,
            showFooter: true,
            columns:[[
                {field:'productCode',title:'药品编码',width:10,align:'center',
                    formatter:function(value,row){
                        if(row.productCode){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.productCode + '</span>';
                            return content;
                        }
                    }
                },
                {field:'productName',title:'药品名称',width:12,align:'center',
                    formatter:function(value,row){
                        if(row.productName){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.productName + '</span>';
                            return content;
                        }
                    }
                },
                {field:'dosageFormName',title:'剂型',width:10,align:'center',
                    formatter:function(value,row){
                        if(row.dosageFormName){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.dosageFormName + '</span>';
                            return content;
                        }
                    }
                },
                {field:'model',title:'规格',width:10,align:'center',
                    formatter:function(value,row){
                        if(row.model){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.model + '</span>';
                            return content;
                        }
                    }
                },
                {field:'packDesc',title:'包装规格',width:10,align:'center',
                    formatter:function(value,row){
                        if(row.packDesc){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.packDesc + '</span>';
                            return content;
                        }
                    }
                },
                {field:'producerName',title:'生产企业',width:20,align:'center',
                    formatter:function(value,row){
                        if(row.producerName){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.producerName + '</span>';
                            return content;
                        }
                    }
                },
                {field:'batchCode',title:'批号',width:10,align:'center',
                    formatter: function(value,row,index) {
                        if (row.batchCode) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.batchCode + '</span>';
                        }
                    }
                },
                {field:'tradeInvoice.vendorName',title:'供应商',width:18,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.tradeInvoice.vendorName + '</span>';
                        }
                    }
                },
                {field:'tradeInvoice.customerName',title:'医院',width:18,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.tradeInvoice.customerName + '</span>';
                        }
                    }
                },
                {field:'tradeInvoice.code',title:'发票代号',width:18,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.tradeInvoice.code + '</span>';
                        }
                    }
                },
                {field:'tradeInvoice.internalCode',title:'发票号码',width:15,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.tradeInvoice.internalCode + '</span>';
                        }
                    }
                },
                {field:'tradeInvoice.orderDate',title:'发票日期',width:12,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + $.format.date(row.tradeInvoice.orderDate,"yyyy-MM-dd") + '</span>';
                        }
                    }
                },
                {field:'goodsNum',title:'数量',width:10,align:'center',
                    formatter: function(value,row,index) {
                        if (row.goodsNum) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.goodsNum + '</span>';
                        }
                    }
                },
                {field:'goodsSum',title:'金额',width:10,align:'center',
                    formatter: function(value,row,index) {
                        if (row.goodsSum) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.goodsSum + '</span>';
                        }
                    }
                },
                {field:'isExistsParent',title:'是否有上层发票',width:10,align:'center',
                    formatter: function(value,row,index) {
                        if (row.isExistsParent == 1) {
                            return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
                        }
                    }
                },
                {field:'filePath',title:'操作',width:10,align:'center',formatter: function(value,row,index){
                    if(row.tradeInvoice){
                        return "<a href= <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/tradeInvoice/readfile.htmlx?id="+row.tradeInvoice.id+" target='_bank'><img src= <c:out value='${pageContext.request.contextPath }'/>/resources/images/fileDown.png width=16 height=16  />查看附件</a>";
                    }
                }}
            ]],
            rowStyler: function(index,row){
                if(isRedFlag == 1)
                    return 'color:red;'; // return inline style
                // the function can return predefined css class and inline style
                // return {class:'r1', style:{'color:#fff'}};

            },queryParams:{
                "query['t#tradeInvoice.type_E_EQ']":'vendorToHospital'
            },onDblClickRow:function(index,row) {
                $("#tt").tabs("select",1);
                searchParent(row,1);
            }, onLoadSuccess:function(data){
                $(".tip").tooltip({
                    onShow: function(){
                        $(this).tooltip('tip').css({
                            width:'150',
                            boxShadow: '1px 1px 3px #292929'
                        });
                    }
                });
            }
        });
        $('#dgDetail1').datagrid({
            fitColumns:true,
            striped:true,
            singleSelect:true,
            rownumbers:true,
            border:true,
            height : $(this).height() - $(".search-bar").height() -39,
            url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/tradeInvoice/mxlist.htmlx",
            pagination:true,
            pageSize:20,
            pageNumber:1,
            remoteFilter: true,
            showFooter: true,
            columns:[[
                {field:'productCode',title:'药品编码',width:10,align:'center',
                    formatter:function(value,row){
                        if(row.productCode){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.productCode + '</span>';
                            return content;
                        }
                    }
                },
                {field:'productName',title:'药品名称',width:12,align:'center',
                    formatter:function(value,row){
                        if(row.productName){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.productName + '</span>';
                            return content;
                        }
                    }
                },
                {field:'dosageFormName',title:'剂型',width:10,align:'center',
                    formatter:function(value,row){
                        if(row.dosageFormName){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.dosageFormName + '</span>';
                            return content;
                        }
                    }
                },
                {field:'model',title:'规格',width:10,align:'center',
                    formatter:function(value,row){
                        if(row.model){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.model + '</span>';
                            return content;
                        }
                    }
                },
                {field:'packDesc',title:'包装规格',width:10,align:'center',
                    formatter:function(value,row){
                        if(row.packDesc){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.packDesc + '</span>';
                            return content;
                        }
                    }
                },
                {field:'producerName',title:'生产企业',width:20,align:'center',
                    formatter:function(value,row){
                        if(row.producerName){
                            var content = '<span title="双击可以钻取到下一页" class="tip">' + row.producerName + '</span>';
                            return content;
                        }
                    }
                },
                {field:'batchCode',title:'批号',width:10,align:'center',
                    formatter: function(value,row,index) {
                        if (row.batchCode) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.batchCode + '</span>';
                        }
                    }
                },
                {field:'tradeInvoice.gpoName',title:'GPO',width:10,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.tradeInvoice.gpoName + '</span>';
                        }
                    }
                },
                {field:'tradeInvoice.customerName',title:'供应商',width:15,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.tradeInvoice.customerName + '</span>';
                        }
                    }
                },
                {field:'tradeInvoice.code',title:'发票代号',width:18,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.tradeInvoice.code + '</span>';
                        }
                    }
                },
                {field:'tradeInvoice.internalCode',title:'发票号码',width:12,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.tradeInvoice.internalCode + '</span>';
                        }
                    }
                },
                {field:'tradeInvoice.orderDate',title:'发票日期',width:12,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + $.format.date(row.tradeInvoice.orderDate,"yyyy-MM-dd") + '</span>';
                        }
                    }
                },
                {field:'goodsNum',title:'数量',width:10,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.goodsNum + '</span>';
                        }
                    }
                },
                {field:'goodsSum',title:'金额',width:10,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return '<span title="双击可以钻取到下一页" class="tip">' + row.goodsSum + '</span>';
                        }
                    }
                },
                {field:'isExistsParent',title:'是否有上层发票',width:10,align:'center',
                    formatter: function(value,row,index) {
                        if (row.isExistsParent == 1) {
                            return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
                        }
                    }
                },
                {field:'filePath',title:'操作',width:10,align:'center',formatter: function(value,row,index){
                    if(row.tradeInvoice){
                        return "<a href= <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/tradeInvoice/readfile.htmlx?id="+row.tradeInvoice.id+" target='_bank'><img src= <c:out value='${pageContext.request.contextPath }'/>/resources/images/fileDown.png width=16 height=16  />查看附件</a>";
                    }
                }}
            ]],
            rowStyler: function(index,row){
                if(isRedFlag == 1)
                    return 'color:red;'; // return inline style
                // the function can return predefined css class and inline style
                // return {class:'r1', style:{'color:#fff'}};

            },queryParams:{
                "query['t#tradeInvoice.type_E_EQ']":'GPOToVendor'
            },onDblClickRow:function(index,row) {
                $("#tt").tabs("select",2);
                searchParent(row,2);
            }
        });
        $('#dgDetail2').datagrid({
            fitColumns:true,
            striped:true,
            singleSelect:true,
            rownumbers:true,
            border:true,
            height : $(this).height() - $(".search-bar").height() -39,
            url:" <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/tradeInvoice/mxlist.htmlx",
            pagination:true,
            pageSize:20,
            pageNumber:1,
            remoteFilter: true,
            showFooter: true,
            columns:[[
                {field:'productCode',title:'药品编码',width:10,align:'center'},
                {field:'productName',title:'药品名称',width:12,align:'center'},
                {field:'dosageFormName',title:'剂型',width:10,align:'center'},
                {field:'model',title:'规格',width:10,align:'center'},
                {field:'packDesc',title:'包装规格',width:10,align:'center'},
                {field:'producerName',title:'生产企业',width:20,align:'center'},
                {field:'batchCode',title:'批号',width:10,align:'center'},
                {field:'tradeInvoice.vendorName',title:'生产企业(总代)',width:20,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return row.tradeInvoice.vendorName;
                        }
                    }
                },
                {field:'tradeInvoice.customerName',title:'GPO',width:10,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return row.tradeInvoice.customerName;
                        }
                    }
                },
                {field:'tradeInvoice.code',title:'发票代号',width:18,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return row.tradeInvoice.code;
                        }
                    }
                },
                {field:'tradeInvoice.internalCode',title:'发票号码',width:12,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return row.tradeInvoice.internalCode;
                        }
                    }
                },
                {field:'tradeInvoice.orderDate',title:'发票日期',width:12,align:'center',
                    formatter: function(value,row,index) {
                        if (row.tradeInvoice) {
                            return $.format.date(row.tradeInvoice.orderDate,"yyyy-MM-dd");
                        }
                    }
                },
                {field:'goodsNum',title:'数量',width:10,align:'center'},
                {field:'goodsSum',title:'金额',width:10,align:'center'},
                {field:'filePath',title:'操作',width:10,align:'center',formatter: function(value,row,index){
                    if(row.tradeInvoice){
                        return "<a href= <c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/tradeInvoice/readfile.htmlx?id="+row.tradeInvoice.id+" target='_bank'><img src= <c:out value='${pageContext.request.contextPath }'/>/resources/images/fileDown.png width=16 height=16  />查看附件</a>";
                    }
                }}
            ]],
            rowStyler: function(index,row){
                if(isRedFlag == 1)
                    return 'color:red;'; // return inline style
                // the function can return predefined css class and inline style
                // return {class:'r1', style:{'color:#fff'}};

            },queryParams:{
                "query['t#tradeInvoice.type_E_EQ']":'producerToGPO'
            }
        });
    });

    //搜索
    function dosearch(){
        var startDate="";
        var toDate="";
        var productCode = $("#productCode").textbox("getValue");
        var productName = $("#productName").textbox("getValue");
        var batchCode = $("#batchCode").textbox("getValue");
        if($('#startDate').datebox('getValue')!=""){
            startDate = $('#startDate').datebox('getValue');
        }
        if($('#toDate').datebox('getValue')!=""){
            toDate = $('#toDate').datebox('getValue');
        }
        var data = {
            "query['t#orderDate_D_GE']": startDate,
            "query['t#orderDate_D_LE']": toDate,
            "query['t#productCode_S_LK']" : productCode,
            "query['t#productName_S_LK']" : productName,
            "query['t#batchCode_S_LK']" : batchCode,
            "query['t#tradeInvoice.type_E_EQ']":'vendorToHospital'
        };
        $('#dgDetail').datagrid('load',data);
    }

    function searchParent(row,index) {
        var data = {};
        if(index == 1){
            data = {
                "query['t#productCode_S_EQ']" : row.productCode,
                "query['t#batchCode_S_EQ']" : row.batchCode,
                "query['t#tradeInvoice.customerCode_S_EQ']" : row.tradeInvoice.vendorCode,
                "query['t#tradeInvoice.type_E_EQ']":'GPOToVendor'
            };
        }else if(index == 2){
            data = {
                "query['t#productCode_S_EQ']" : row.productCode,
                "query['t#batchCode_S_EQ']" : row.batchCode,
                "query['t#tradeInvoice.gpoCode_S_EQ']" : row.tradeInvoice.gpoCode,
                "query['t#tradeInvoice.type_E_EQ']":'producerToGPO'
            };
        }
        $('#dgDetail'+index).datagrid('load',data);
    }

    function doCount() {
        console.log(123);
        $.ajax({
            url:"<c:out value='${pageContext.request.contextPath }'/>/b2b/monitor/tradeInvoice/doCount.htmlx",
            data:{"projectCode":"01"},
            dataType:"json",
            type:"POST",
            cache:false,
            success:function(data){
                if(data.success){
                    showMsg("更新成功！" + data.msg);
                }else{
                    showErr(data.msg);
                }
            },
            error:function(){
                showErr("出错，请刷新重新操作");
            }
        });
    }
</script>