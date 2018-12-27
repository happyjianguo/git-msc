<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags"%>
<tag:head title="" />

<body class="easyui-layout" >

<div class="single-dg">
    <div id="toolbar" class="search-bar" >
        <%--<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="addOpen()">添加</a>
        <span class="datagrid-btn-separator split-line" ></span>--%>
            <%--<a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-import',plain:true"  onclick="importFunc()">导入</a>
            <a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-export',plain:true"  onclick="downloadFunc()">模版导出</a>--%>
        <shiro:hasPermission name="dm:goodsCountry:export">
             <a href="#"  class="easyui-linkbutton" data-options="iconCls:'icon-export',plain:true"  onclick="exportFunc()">导出</a>
        </shiro:hasPermission>
    </div>
    <table  id="dg" ></table>
</div>
<form id="form1" method="POST" enctype="multipart/form-data">
    <input type="file" name="myfile" class="file" id="myfile" style="display: none;" />
</form>
</body>
</html>

<script>
    //初始化
    $(function(){
        //datagrid
        $('#dg').datagrid({
            fitColumns:false,
            striped:true,
            singleSelect:true,
            rownumbers:true,
            border:true,
            height : $(this).height() -4,
            pagination:true,
            url:" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsCountry/page.htmlx",
            pageSize:20,
            pageNumber:1,
            remoteFilter: true,
            columns:[[
                {field:'HOSPITALCODE',title:'医院编码',width:100,align:'center'},
                {field:'HOSPITALNAME',title:'医院名称',width:100,align:'center'},
                {field:'INTERNALCODE',title:'医院药品内部编码',width:100,align:'center'},
                {field:'PRODUCTNAME',title:'医院药品名称',width:100,align:'center'},
                {field:'MODEL',title:'医院规格',width:100,align:'center'},
                {field:'PACKDESC',title:'医院药品包装规格',width:100,align:'center'},
                {field:'DOSAGEFORMNAME',title:'医院剂型',width:100,align:'center'},
                {field:'PRODUCERNAME',title:'医院生产厂家',width:100,align:'center'},

                {field:'SPRODUCTCODE',title:'药品编码',width:100,align:'center'},
                {field:'SPRODUCTNAME',title:'药品名称',width:100,align:'center'},
                {field:'SMODEL',title:'药品规格',width:100,align:'center'},
                {field:'SPACKDESC',title:'药品包装规格',width:100,align:'center'},
                {field:'SDOSAGEFORMNAME',title:'药品剂型',width:100,align:'center'},
                {field:'SPRODUCERNAME',title:'药品生产厂家',width:100,align:'center'},
                {field:'SAUTHORIZENO',title:'药品批准文号',width:100,align:'center'},

                {field:'COUNTRYPRODUCTCODE',title:'国家药品编码',width:100,align:'center'},
                {field:'COUNTRYPRODUCTNAME',title:'国家药品名称',width:100,align:'center'},
                {field:'COUNTRYMODEL',title:'国家药品规格',width:100,align:'center'},
                {field:'CPACKDESC',title:'国家包装规格',width:100,align:'center'},
                {field:'COUNTRYDOSAGEFORMNAME',title:'国家剂型',width:100,align:'center'},
                {field:'COUNTRYPRODUCERNAME',title:'国家生产厂家',width:100,align:'center'},
                {field:'COUNTRYAUTHORIZENO',title:'国家批准文号',width:100,align:'center'},
                {field:'ISNATIONALCODECHECK',title:'是否国家药品对照',width:120,align:'center',
                    formatter: function(value,row,index){
                        if(row.SNATIONALCODE != undefined && row.SNATIONALCODE!=null){
                            return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
                        }
                    }
                },
                {field:'SISGPOPURCHASE',title:'是否GPO药品',width:100,align:'center',
                    formatter: function(value,row,index){
                        if (row.SISGPOPURCHASE==1){
                            return "<img src =\"<c:out value='${pageContext.request.contextPath }'/>/resources/js/jquery-easyui/themes/icons/ok.png\" />";
                        }
                    }}
            ]],
            toolbar: "#toolbar",
            onDblClickRow: function(index,field,value){
                editOpen();
            }
        });

        $("#dg").datagrid('enableFilter',[{
            field:'SISGPOPURCHASE',
            type:'combobox',
            options:{
                panelHeight:'auto',
                editable:false,
                data:[{value:'',text:'-请选择-'},
                    {value:'0',text:'否'},
                    {value:'1',text:'是'}],
                onChange:function(value){
                    if (value == ''){
                        $('#dg').datagrid('removeFilterRule', 'SISGPOPURCHASE');
                    } else {
                        $('#dg').datagrid('addFilterRule', {
                            field: 'SISGPOPURCHASE',
                            op: 'EQ',
                            fieldType:'I',
                            value: value
                        });
                    }
                    $('#dg').datagrid('doFilter');
                }
            }
        },{
            field:'ISNATIONALCODECHECK',
            type:'combobox',
            options:{
                panelHeight:'auto',
                editable:false,
                data:[{value:'',text:'-请选择-'},
                    {value:'0',text:'否'},
                    {value:'1',text:'是'}],
                onChange:function(value){
                    if (value == ''){
                        $('#dg').datagrid("load",{
                            "query[t#SNATIONALCODE_S_EQ]":''
                        });
                    } else {
                        if(value=="0"){
                            $('#dg').datagrid("load",{
                                "query[t#SNATIONALCODE_NULL_IS]":0
                            });
                        }else{
                            $('#dg').datagrid("load",{
                                "query[t#SNATIONALCODE_NULL_NOT]":1
                            });
                        }
                    }
                }
            }
        }]);
    });

    function exportFunc() {
        var url = " <c:out value='${pageContext.request.contextPath }'/>/dm/goodsCountry/export.htmlx";
        $.messager.confirm('确认信息', '确认下载?', function(r){
            if (r){
                window.open(url);
            }
        });
    }

    function downloadFunc(){
        $.messager.confirm('确认信息', '确认下载?', function(r){
            if (r){
                window.open(" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsCountry/exportTemplate.htmlx");
            }
        });
    }

    function importFunc(){
        $("#myfile").click();
    }
    $("#myfile").change(function(){
        $.messager.confirm('确认信息', '确认导入?', function(r){
            if (r){
                $("#form1").submit();
            }else{
                location.reload(false);
            }
        });
    });

    $("#form1").form({
        url :" <c:out value='${pageContext.request.contextPath }'/>/dm/goodsCountry/upload.htmlx",
            onSubmit : function() {
            top.$.messager.progress({
                title : '提示',
                text : '数据导入中，请稍后....'
            });
            return true;
        },
        success : function(result) {
            $('#form1').val("");
            top.$.messager.progress('close');
            result = $.parseJSON(result);
            if(result.success){
                $('#dg').datagrid('reload');
                showMsg(result.msg);
            }else{
                showErr("出错，提示信息："+result.msg);
                importErrOpen(result.msg);
            }
        },
        error:function(){
            showErr("出错，请刷新重新操作");
        }

    });

</script>