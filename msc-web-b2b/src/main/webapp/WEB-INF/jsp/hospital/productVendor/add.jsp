<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div style="padding:2px;">
	<table  id="dg"></table>
</div>



<script>
    var productId = "<c:out value='${productId }'/>";
    var isVail=true;
    //初始化
    $(function() {

        //datagrid
        $('#dg').datagrid({
            fitColumns:true,
            striped:true,
            singleSelect:true,
            rownumbers:true,
            border:true,
            height :320,
            url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/productVendor/choose.htmlx",
            columns:[[
                {field:'vendorCode',title:'供应商编码',width:170,align:'center'},
                {field:'vendorName',title:'供应商',width:190,align:'center'},
                {field:'price',title:'价格（元）',width:170,align:'center',
                    formatter: function(value,row,index){
                        if (row.price){
                            return common.fmoney(row.price);
                        }
                    }}
            ]],
            queryParams:{
                "query['t#product.id_L_EQ']":productId
            },
            onDblClickRow: function(index,row){
                $("#tt").tabs("select",1);
                searchDefectsList(row);
            }
        });



    });
    function addProductDetail(){
        var selobj = $('#dg').datagrid('getSelected');
        if(selobj == null){
            showErr("请选择供应商");
        }
        if(isVail){
            $.ajax({
                url:" <c:out value='${pageContext.request.contextPath }'/>/hospital/productVendor/add.htmlx",
                data:{
                    "id":productId,
                    "gpoProductId":selobj.id
                },
                dataType:"json",
                type:"POST",
                cache:false,
                beforeSend: function(){
                    isVail=false;
                },
                success:function(data){
                    if(data.success){
                        showMsg("保存成功");
                        top.$.modalDialog.openner.datagrid('reload');
                        top.$.modalDialog.handler.dialog('close');
                    }else{
                        showErr(data.msg);
                    }
                    isVail=true;
                },
                error:function(){
                    showErr("出错，请刷新重新操作");
                }
            });
		}

    }
</script>