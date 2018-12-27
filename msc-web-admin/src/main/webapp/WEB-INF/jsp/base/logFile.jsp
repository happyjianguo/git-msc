<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="tag" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<tag:head title=""/>

<html>

<body class="easyui-layout">
<div data-options="region:'north',title:'',collapsible:false" class="my-north" style="height:100%;">

    <div id="tt">
        <div title="日志列表" class="my-tabs">
            <div id="toolbar" class="search-bar" style="height:30px;">
                <input name="type" id="type">
                <input name="lines" id="lines">
            </div>
            <table id="dg" class="single-dg"></table>
        </div>
        <div title="在线查看" class="my-tabs">
            <div id="toolbar2" class="btn-group">
                <button type="button" class="btn btn-default" onclick="stopLoof()">
                    <i class="fa fa-stop text-red"> 停止</i>
                </button>
                <button type="button" class="btn btn-default" onclick="watchFunc()">
                    <i class="fa fa-refresh"> 刷新</i>
                </button>
            </div>
            <div class="pull-right" style="float: right">
                <input type="checkbox" id="scrollFlag" name="scrollFlag" value="1" checked/>
                <label for="scrollFlag">始终滚到底部</label>
            </div>
            <div style="margin-top:5px;">
                <div id="watch"
                     style="white-space: pre; width:100%;height:400px;padding:5px;overflow-y: scroll;overflow-x: scroll;"></div>
            </div>
            <div id="righttoolbar2" class="pull-right">

            </div>
        </div>
    </div>
</div>
</body>
</html>


<script>
    $('#tt').tabs({
        plain: true,
        justified: true
    });

    function selectData() {
        var type = $("#type").combobox("getValue");
        $('#dg').datagrid({
            url: "<c:out value='${pageContext.request.contextPath }'/>/base/logFile/list.htmlx",
            queryParams: {
                "type": type
            }
        });
    }

    //初始化
    $(function () {
        $("#type").combobox({
            valueField: 'label',
            textField: 'value',
            panelHeight: 100,
            width: 100,
            editable: false,
            value: 'tomcat',
            data: [{
                label: 'tomcat',
                value: 'tomcat'
            }, {
                label: 'base',
                value: 'base'
            }, {
                label: 'b2b',
                value: 'b2b'
            }, {
                label: 'supervise',
                value: 'supervise'
            }],
            onSelect: function () {
                selectData();

            }
        });
        $("#lines").combobox({
            valueField: 'label',
            textField: 'value',
            panelHeight: 100,
            width: 100,
            editable: false,
            value: '200',
            data: [{
                label: '200',
                value: '在线查看'
            }, {
                label: '5000',
                value: '下载5000行'
            }, {
                label: 'all',
                value: '下载整个文件'
            }],
            onSelect: function () {
                selectData();

            }
        });
        //datagrid
        $('#dg').datagrid({
            fitColumns: true,
            striped: true,
            singleSelect: true,
            rownumbers: true,
            height: $(this).height() - 60,
            toolbar: "#toolbar",
            url: "<c:out value='${pageContext.request.contextPath }'/>/base/logFile/list.htmlx",
            columns: [[
                {
                    field: 'name',
                    title: '文件名',
                    width: 30, formatter: function (value, row, index) {
                    return '<a href="javascript:download(\'' + row.name + '\')">' + row.name + '</a>';
                }
                },
                {
                    field: 'length',
                    title: '文件大小',
                    width: 20
                },
                {
                    field: 'canRead',
                    title: '是否可读',
                    width: 10
                }
            ]]
        })
    });

    var freshFileName;
    var fileLen = 0;

    function download(fileName) {
        if ($("#lines").val() <= 200) {
            freshFileName = fileName;
            $("#tt").tabs("select", 1);
            watchFunc();
        } else {
            window.open("${pageContext.request.contextPath}/logFile/download.htmlx?fileName=" + fileName + "&lines=" + $("#lines").val() + "&type=" + $("#type").val());
        }
    }

    var interval = null;

    function watchFunc() {
        fileLen = 0;
        $("#watch").text("");
        startLoof();
    }

    function stopLoof() {
        window.clearInterval(interval);
        $(".fa-stop").removeClass("fa-stop text-red").addClass("fa-play text-green").text(" 启动").parent().unbind("click").bind("click", function () {
            startLoof();
        });
    }

    function startLoof() {
        window.clearInterval(interval);
        loofFunc();
        interval = window.setInterval(function () {
            loofFunc();
        }, 2000);
        $(".fa-play").removeClass("fa-play text-green").addClass("fa-stop text-red").text(" 暂停").parent().unbind("click").bind("click", function () {
            stopLoof();
        });
    }

    function loofFunc() {
        if (!freshFileName) {
            return;
        }
        $.ajax({
            url: "${pageContext.request.contextPath }/base/logFile/read.htmlx",
            showProgressBar: false,
            data: {
                "fileName": freshFileName,
                "lines": $("#lines").val(),
                "type": $("#type").val(),
                "start": fileLen
            },
            dataType: "json",
            type: "POST",
            cache: false,
            //traditional: true,//支持传数组参数
            success: function (data) {
                fileLen = data.len;
                var s = "";
                $.each(data.list, function () {
                    s += this + "\n";
                })
                $("#watch").append(s);
                $("#righttoolbar2").text(data.len);
                if ($("#scrollFlag").val() == 1) {
                    $("#watch").scrollTop($("#watch").prop("scrollHeight"));
                }
            },
            error: function () {
                showErr("出错，请刷新重新操作");
            }
        });
    }
</script>
