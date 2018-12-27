//自己扩展的jquery函数
//压缩时请把编码改成ANSI
$.app = {

    /**初始化主页 layout，菜单，tab*/
    initIndex: function () {
        $.menus.initMenu();
        $.layouts.initLayout();
        

    },
    /**
     * 异步加载url内容到tab
     */
    loadingToCenterIframe: function (panel, url, loadingMessage, forceRefresh) {
        panel.data("url", url);

        var panelId = panel.prop("id");
        var iframeId = "iframe-" + panelId;
        var iframe = $("#" + iframeId);

        if (!iframe.length || forceRefresh) {
            if(!iframe.length) {
                iframe = $("iframe[tabs=true]:last").clone(true);
                iframe.prop("id", iframeId);
                $("iframe[tabs=true]:last").after(iframe);
            }

            $.app.waiting(loadingMessage);
            iframe.prop("src", url).one("load", function () {
                $.app.activeIframe(panelId, iframe);
                $.app.waitingOver();
            });

        } else {
            $.app.activeIframe(panelId, iframe);
        }

    },
    activeIframe: function (panelId, iframe) {
        if (!iframe) {
            iframe = $("#iframe-" + panelId);
        }
        var layout = $.layouts.layout;
        if (layout.panes.center.prop("id") == iframe.prop("id")) {
            return;
        }
        layout.panes.center.hide();
        layout.panes.center = iframe;
        layout.panes.center.show();
        layout.resizeAll();
        $.tabs.initTabScrollHideOrShowMoveBtn(panelId);
    },

    waiting : function(message, isSmall) {
        if(!message) {
            message = "装载中...";
        }

        message = '<img src="' + ctx + '/static/images/loading.gif" '+ (isSmall ? "width='20px'" : "") +'/> ' + message;
        if(!isSmall) {
            message = "<h4>"+message+"</h4>";
        }
        $.blockUI({
            fadeIn: 700,
            fadeOut: 700,
            showOverlay: false,
            css: {
                border: 'none',
                padding: '15px',
                backgroundColor: '#eee',
                '-webkit-border-radius': '10px',
                '-moz-border-radius': '10px',
                opacity:1,
                color: '#000',
                width: isSmall ? "40%" : "30%"

            },
            message: message
        });
    }
    ,
    waitingOver: function () {
        $.unblockUI();
    }
    ,
    /**
     * 当前显示的模态窗口队列
     */
    _modalDialogQueue:null,
    /**
     * 模态窗口
     * @title 标题
     * @param url
     * @param settings
     */
    modalDialog : function(title, url, settings) {

        $.app.waiting();
        var defaultSettings = {
            title : title,
            closeText : "关闭",
            closeOnEscape:false,
            height:300,
            width:600,
            modal:true,
            noTitle : false,
            close: function() {
                $(this).closest(".ui-dialog").remove();
            },
            _close : function(modal) {
                $(modal).dialog("close");
                if($.app._modalDialogQueue.length > 0) {
                    $.app._modalDialogQueue.pop();
                }
            },
            buttons:{
                '确定': function() {
                    if(settings.ok) {
                        if(settings.ok($(this))) {
                            settings._close(this);
                        }
                    } else {
                        settings._close(this);
                    }
                    if(settings.callback) {
                        settings.callback();
                    }
                },
                '关闭': function () {
                    settings._close(this);
                    if(settings.callback) {
                        settings.callback();
                    }
                }
            }
        };
        if(!settings) {
            settings = {};
        }
        settings = $.extend(true, {}, defaultSettings, settings);

        if(!settings.ok) {
            delete settings.buttons['确定'];
        }

        $.ajax({
            url: url,
            headers: { table:true }
        }).done(function (data) {
                $.app.waitingOver();
                var div = $("<div></div>").append(data);
                var dialog = div.dialog(settings)
                    .closest(".ui-dialog").data("url", url).removeClass("ui-widget-content")
                    .find(".ui-dialog-content ").removeClass("ui-widget-content").focus();

                if(settings.noTitle) {
                    dialog.closest(".ui-dialog").find(".ui-dialog-titlebar").addClass("no-title");
                }
                dialog.closest(".ui-dialog").focus();
                if(!$.app._modalDialogQueue) {
                    $.app._modalDialogQueue = new Array();
                }
                $.app._modalDialogQueue.push(dialog);
                $.table.initTable(div.find(".table"));

//            $.blockUI({
//                url : url,
//                theme:true,
//                showOverlay : true,
//                title : title,
//                message : data,
//                css : css,
//                themedCSS: css
//            });


            });
    }
    ,
    /**
     * 取消编辑
     */
    cancelModelDialog : function() {
        if($.app._modalDialogQueue && $.app._modalDialogQueue.length > 0) {
            $.app._modalDialogQueue.pop().dialog("close");
        }
    }
    ,
    alert : function(options) {
        if(!options) {
            options = {};
        }
        var defaults = {
            title : "警告",
            message : "非法的操作",
            okTitle : "关闭",
            ok : $.noop
        };
        options.alert = true;
        options = $.extend({}, defaults, options);
        this.confirm(options);
    }
    ,
    /**
     * 格式
     * @param options
     */
    confirm : function(options) {
        var defaults = {
            title : "确认执行操作",
            message : "确认执行操作吗？",
            cancelTitle : '取消',
            okTitle : '确定',
            cancel : $.noop,
            ok : $.noop,
            alert : false
        };

        if(!options) {
            options = {};
        }
        options = $.extend({}, defaults, options);

        var template =
            '<div class="modal hide fade confirm">' +
                '<div class="modal-header">' +
                '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
                '<h3 class="title">{title}</h3>' +
                '</div>' +
                '<div class="modal-body">' +
                '<div>{message}</div>' +
                '</div>' +
                '<div class="modal-footer">' +
                '<a href="#" class="btn btn-ok btn-danger" data-dismiss="modal">{okTitle}</a>' +
                '<a href="#" class="btn btn-cancel" data-dismiss="modal">{cancelTitle}</a>'+
                '</div>' +
                '</div>';

        var modalDom =
            $(template
                .replace("{title}", options.title)
                .replace("{message}", options.message)
                .replace("{cancelTitle}", options.cancelTitle)
                .replace("{okTitle}", options.okTitle));


        var hasBtnClick = false;
        if(options.alert) {
            modalDom.find(".modal-footer > .btn-cancel").remove();
        } else {
            modalDom.find(".modal-footer > .btn-cancel").click(function() {
                hasBtnClick = true;
                options.cancel();
            });
        }
        modalDom.find(".modal-footer > .btn-ok").click(function() {
            hasBtnClick = true;
            options.ok();
        });

        var modal = modalDom.modal();

        modal.on("hidden", function() {
            modal.remove();//移除掉 要不然 只是hidden
            if(hasBtnClick) {
                return true;
            }
            if(options.alert) {
                options.ok();
            } else {
                options.cancel();
            }
        });

        return modal;
    }
    ,
    isImage : function(filename) {
        return /gif|jpe?g|png|bmp$/i.test(filename);
    }
    ,
    
    removeContextPath : function(url) {
        if(url.indexOf(ctx) == 0) {
            return url.substr(ctx.length);
        }
        return url;
    }
    ,
    /**
     * 异步化form表单或a标签
     * @param $form
     * @param containerId
     */
    asyncLoad : function($tag, containerId) {
        if($tag.is("form")) {
            $tag.submit(function() {
                if($tag.prop("method").toLowerCase() == 'post') {
                    $.post($tag.prop("action"), $tag.serialize(), function(data) {
                        $("#" + containerId).replaceWith(data);
                    });
                } else {
                    $.get($tag.prop("action"), $tag.serialize(), function(data) {
                        $("#" + containerId).replaceWith(data);
                    });
                }
                return false;
            });
        } else if($tag.is("a")) {
            $tag.click(function() {
                $.get($tag.prop("href"), function(data) {
                    $("#" + containerId).replaceWith(data);
                });
                return false;
            });
        } else {
            $.app.alert({message : "该标签不支持异步加载，支持的标签有form、a"});
        }

    },
    /**
     * 只读化表单
     * @param form
     */
    readonlyForm : function(form, removeButton) {
        var inputs = $(form).find(":input");
        inputs.not(":submit,:button").prop("readonly", true);
        if(removeButton) {
            inputs.remove(":button,:submit");
        }
    }
    ,

    /**
     * 将$("N").val() ----> [1,2,3]
     */
    joinVar : function(elem, separator) {
        if(!separator) {
            separator = ",";
        }
        var array = new Array();
        $(elem).each(function() {
            array.push($(this).val());
        });

        return array.join(separator);
    },

    /**
     * 异步加载table子内容(父子表格)
     * @param toggleEle
     * @param tableEle
     * @param asyncLoadURL
     */
    toggleLoadTable : function(tableEle, asyncLoadURL) {
        var openIcon = "icon-plus-sign";
        var closeIcon = "icon-minus-sign";
        $(tableEle).find("tr .toggle-child").click(function() {
            var $a = $(this);
            //只显示当前的 其余的都隐藏
            $a.closest("table")
                .find(".toggle-child." + closeIcon).not($a).removeClass(closeIcon).addClass(openIcon)
                .end().end()
                .find(".child-data").not($a.closest("tr").next("tr")).hide();

            //如果是ie7
            if($(this).closest("html").hasClass("ie7")) {
                var $aClone = $(this).clone(true);
                if($aClone.hasClass(closeIcon)) {
                    $aClone.addClass(openIcon).removeClass(closeIcon);
                } else {
                    $aClone.addClass(closeIcon).removeClass(openIcon);
                }
                $(this).after($aClone);
                $(this).remove();
                $a = $aClone;
            } else {
                $a.toggleClass(openIcon);
                $a.toggleClass(closeIcon);
            }

            var $currentTr = $a.closest("tr");
            var $dataTr = $currentTr.next("tr");
            if(!$dataTr.hasClass("child-data")) {
                $.app.waiting();
                $dataTr = $("<tr class='child-data' style='display: none;'></tr>");
                var $dataTd = $("<td colspan='" + $currentTr.find("td").size() + "'></td>");
                $dataTr.append($dataTd);
                $currentTr.after($dataTr);
                $dataTd.load(asyncLoadURL.replace("{parentId}", $a.data("id")),function() {
                    $.app.waitingOver();
                });
            }
            $dataTr.toggle();

            return false;
        });

    },

    initAutocomplete : function(config) {

        var defaultConfig = {
            minLength : 1,
            enterSearch : false,
            focus: function( event, ui ) {
                $(config.input).val( ui.item.label );
                return false;
            },
            renderItem : function( ul, item ) {
                return $( "<li>" )
                    .data( "ui-autocomplete-item", item )
                    .append( "<a>" + item.label + "</a>" )
                    .appendTo( ul );
            }
        };

        config = $.extend(true, defaultConfig, config);

        $(config.input)
            .on( "keydown", function( event ) {
                //回车查询
                if(config.enterSearch && event.keyCode === $.ui.keyCode.ENTER) {
                    config.select(event, {item:{value:$(this).val()}});
                }
            })
            .autocomplete({
                source : config.source,
                minLength : config.minLength,
                focus : config.focus,
                select : config.select
            }).data( "ui-autocomplete" )._renderItem = config.renderItem;
    }

};

$.layouts = {
    layout: null,
    /**初始化布局*/
    initLayout: function () {
        function resizePanel(panelName, panelElement, panelState, panelOptions, layoutName) {
            //var tabul = $(".tabs-fix-top");
            if (panelName == 'north') {
                var top = 0;
                if($("html").hasClass("ie")) {
                    top = panelElement.height() - 33;

                } else {
                    top = panelElement.height() - 30;
                }
                if (panelState.isClosed) {
                    top = -58;
                }
                //tabul.css("top", top);
            }

            if(panelName == "center") {
                //tabul.find(".ul-wrapper").andSelf().width(panelState.layoutWidth);
                //$.tabs.initTabScrollHideOrShowMoveBtn();
            }


        }

        this.layout = $('.index-panel').layout({
            west__size:  150
            ,   south__size: 30
            ,	west__spacing_closed:		20
            ,	west__togglerLength_closed:	100
            ,	west__togglerContent_closed:"菜<BR>单"
            ,	togglerTip_closed:	"打开"
            ,	togglerTip_open:	"关闭"
            ,	sliderTip:			"滑动打开"
            ,   resizerTip:         "调整大小"
            ,	spacing_open: 0
            ,	west__resizable: false
            ,   onhide: resizePanel
            ,   onshow: resizePanel
            ,   onopen: resizePanel
            ,   onclose: resizePanel
            ,   onresize: resizePanel
            ,	center__maskContents:true // IMPORTANT - enable iframe masking
            ,   north : {
                togglerLength_open : 0
                ,  resizable : false
                ,  size: 70
            },
            south: {
                resizable:false
            }
        });
    }
}

$.parentchild = {
    /**
     * 初始化父子操作中的子表单
     * options
     *     {
                form : 表单【默认$("childForm")】,
                tableId : "表格Id"【默认"childTable"】,
                excludeInputSelector : "[name='_show']"【排除的selector 默认无】,
                trId : "修改的哪行数据的tr id， 如果没有表示是新增的",
                validationEngine : null 验证引擎,
                modalSettings:{//模态窗口设置
                    width:800,
                    height:500,
                    buttons:{}
                },
                updateUrl : "${ctx}/showcase/parentchild/parent/child/{id}/update" 修改时url模板 {id} 表示修改时的id,
                deleteUrl : "${ctx}/showcase/parentchild/parent/child/{id}/delete  删除时url模板 {id} 表示删除时的id,
            }
     * @param options
     * @return {boolean}
     */
    initChildForm : function(options) {
        var defaults = {
            form : $("#childForm"),
            tableId : "childTable",
            excludeInputSelector : "",
            trId : "",
            validationEngine : null
        };

        if(!options) {
            options = {};
        }
        options = $.extend({}, defaults, options);

        //如果有trId则用trId中的数据更新当前表单
        if(options.trId) {
            var $tr = $("#" + options.trId);
            if($tr.length && $tr.find(":input").length) {
                //因为是按顺序保存的 所以按照顺序获取  第一个是checkbox 跳过
                var index = 1;
                $(":input", options.form).not(options.excludeInputSelector).each(function() {
                    var $input = $(this);
                    var $trInput = $tr.find(":input").eq(index++);
                    if(!$trInput.length) {
                        return;
                    }
                    var $trInputClone = $trInput.clone(true).show();
                    //saveModalFormToTable 为了防止重名问题，添加了tr id前缀，修改时去掉
                    $trInputClone.prop("name", $trInputClone.prop("name").replace(options.trId, ""));
                    $trInputClone.prop("id", $trInputClone.prop("id").replace(options.trId, ""));

                    //克隆后 select的选择丢失了 TODO 提交给jquery bug?
                    if($trInput.is("select")) {
                        $trInput.find("option").each(function(i) {
                            $trInputClone.find("option").eq(i).prop("selected", $(this).prop("selected"));
                        });
                    }
                    if($trInput.is(":radio,:checkbox")) {
                        $trInputClone.prop("checked", $trInput.prop("checked"));
                    }

                    $trInputClone.replaceAll($input);
                });
            }
        }

        //格式化子表单的 input label
        $(":input,label", options.form).each(function() {
            var prefix = "child_";
            if($(this).is(":input")) {
                var id = $(this).prop("id");
                if(id && id.indexOf(prefix) != 0) {
                    $(this).prop("id", prefix + id);
                }
            } else {
                var _for = $(this).prop("for");
                if(_for && _for.indexOf(prefix) != 0) {
                    $(this).prop("for", prefix + _for);
                }
            }
        });

        options.form.submit(function() {
            if(options.validationEngine && !options.validationEngine.validationEngine("validate")) {
                return false;
            }
            return $.parentchild.saveModalFormToTable(options);
        });
    }
    ,
    //保存打开的模态窗口到打开者的表格中
    /**
     * options
     *     {
                form : 表单【默认$("childForm")】,
                tableId : "表格Id"【默认"childTable"】,
                excludeInputSelector : "[name='_show']"【排除的selector 默认无】,
                updateCallback : 【修改时的回调  默认 updateChild】,
                deleteCallback : 【删除时的回调默认 deleteChild】,
                trId : "修改的哪行数据的tr id， 如果没有表示是新增的"
            }
     * @param options
     * @return {boolean}
     */
    saveModalFormToTable :function(options) {
        var $childTable =  $("#" + options.tableId);
        var $childTbody = $childTable.children("tbody");

        if(!options.trId || options.alwaysNew) {
            var counter = $childTbody.data("counter");
            if(!counter) {
                counter = 0;
            }
            options.trId = "new_" + counter++;
            $childTbody.data("counter", counter);
        }
        var $lastTr = $("#" + options.trId, $childTbody);

        var $tr = $("<tr></tr>");
        $tr.prop("id", options.trId);
        if(!$lastTr.length || options.alwaysNew) {
            $childTbody.append($tr);
        } else {
            $lastTr.replaceWith($tr);
        }

        var $td = $("<td></td>");

        //checkbox
        $tr.append($td.clone(true).addClass("check").append("<input type='checkbox'>"));

        var $inputs = $(":input", options.form).not(":button,:submit,:reset", options.form);
        if(options.excludeInputSelector) {
            $inputs = $inputs.not(options.excludeInputSelector);
        }
        $inputs = $inputs.filter(function() {
            return $inputs.filter("[name='" + $(this).prop("name") + "']").index($(this)) == 0;
        });
        $inputs.each(function() {
            var $input = $("[name='" + $(this).prop("name") + "']", options.form);

            var val = $input.val();
            //使用文本在父页显示，而不是值
            //如果是单选按钮/复选框 （在写的过程中，必须在输入框后跟着一个label）
            if($input.is(":radio,:checkbox")) {
                val = "";
                $input.filter(":checked").each(function() {
                    if(val != "") {
                        val = val + ",";
                    }
                    val = val + $("label[for='" + $(this).prop("id") + "']").text();
                });
            }
            //下拉列表
            if($input.is("select")) {
                val = "";
                $input.find("option:selected").each(function() {
                    if(val != "") {
                        val = val + ",";
                    }
                    val = val + $(this).text();
                });
            }

            //因为有多个孩子 防止重名造成数据丢失
            $input.each(function() {
                if($(this).is("[id]")) {
                    $(this).prop("id", options.trId + $(this).prop("id"));
                }
                $(this).prop("name", options.trId + $(this).prop("name"));
            });
            $tr.append($td.clone(true).append(val).append($input.hide()));

        });

        $.table.initCheckbox($childTable);

        $.app.cancelModelDialog();
        return false;
    }
    ,
    /**
     * 更新子
     * @param $a 当前按钮
     * @param updateUrl  更新地址
     */
    updateChild : function($tr, updateUrl, modalSettings) {
        if(updateUrl.indexOf("?") > 0) {
            updateUrl = updateUrl + "&";
        } else {
            updateUrl = updateUrl + "?";
        }
        updateUrl = updateUrl + "trId={trId}";

        //表示已经在数据库中了
        if($tr.is("[id^='old']")) {
            updateUrl = updateUrl.replace("{id}", $tr.prop("id").replace("old_", ""));
        } else {
            //表示刚刚新增的还没有保存到数据库
            updateUrl = updateUrl.replace("{id}", 0);
        }
        updateUrl = updateUrl.replace("{trId}", $tr.prop("id"));
        $.app.modalDialog("修改", updateUrl, modalSettings);
    }
    ,
    /**
     * 以当前行复制一份
     * @param $a 当前按钮
     * @param updateUrl  更新地址
     */
    copyChild : function($tr, updateUrl, modalSettings) {
        if(updateUrl.indexOf("?") > 0) {
            updateUrl = updateUrl + "&";
        } else {
            updateUrl = updateUrl + "?";
        }
        updateUrl = updateUrl + "trId={trId}";
        updateUrl = updateUrl + "&copy=true";

        //表示已经在数据库中了
        if($tr.is("[id^='old']")) {
            updateUrl = updateUrl.replace("{id}", $tr.prop("id").replace("old_", ""));
        } else {
            //表示刚刚新增的还没有保存到数据库
            updateUrl = updateUrl.replace("{id}", 0);
        }
        updateUrl = updateUrl.replace("{trId}", $tr.prop("id"));
        $.app.modalDialog("复制", updateUrl, modalSettings);
    }
    ,
    /**
     * 删除子
     * @param $a 当前按钮
     * @param deleteUrl 删除地址
     */
    deleteChild : function($a, deleteUrl) {
    	
    	if(confirm("确认删除吗？")){
    		var $tr = $a.closest("tr");
            //如果数据库中存在
            if($tr.prop("id").indexOf("old_") == 0) {
                deleteUrl = deleteUrl.replace("{id}", $tr.prop("id").replace("old_", ""));
                $.post(deleteUrl, function(data) {
                    $tr.remove();
                });
            } else {
                $tr.remove();
            }
    	}
    	/*
        $.app.confirm({
            message : "确认删除吗？",
            ok : function() {
                var $tr = $a.closest("tr");
                //如果数据库中存在
                if($tr.prop("id").indexOf("old_") == 0) {
                    deleteUrl = deleteUrl.replace("{id}", $tr.prop("id").replace("old_", ""));
                    $.post(deleteUrl, function(data) {alert(data);
                        $tr.remove();
                    });
                } else {
                    $tr.remove();
                }

            }
        });*/
    }
    ,
    /**
     * 初始化父子表单中的父表单
     * {
     *     form: $form 父表单,
     *     tableId : tableId 子表格id,
     *     prefixParamName : "" 子表单 参数前缀,
     *     modalSettings:{} 打开的模态窗口设置
     *     createUrl : "${ctx}/showcase/parentchild/parent/child/create",
     *     updateUrl : "${ctx}/showcase/parentchild/parent/child/{id}/update" 修改时url模板 {id} 表示修改时的id,
     *     deleteUrl : "${ctx}/showcase/parentchild/parent/child/{id}/delete  删除时url模板 {id} 表示删除时的id,
     * }
     */
    initParentForm : function(options) {


        var $childTable = $("#" + options.tableId);
        $.table.initCheckbox($childTable);
        //绑定在切换页面时的事件 防止误前进/后退 造成数据丢失
        $(window).on('beforeunload',function(){
            if($childTable.find(":input").length) {
                return "确定离开当前编辑页面吗？";
            }
        });
        $(".btn-create-child").click(function() {
            $.app.modalDialog("新增", options.createUrl, options.modalSettings);
        });
        $(".btn-update-child").click(function() {
            var $trs = $childTable.find("tbody tr").has(".check :checkbox:checked:first");
            if(!$trs.length) {
                $.app.alert({message : "请先选择要修改的数据！"});
                return;
            }
            $.parentchild.updateChild($trs, options.updateUrl, options.modalSettings);
        });

        $(".btn-copy-child").click(function() {
            var $trs = $childTable.find("tbody tr").has(".check :checkbox:checked:first");
            if(!$trs.length) {
                $.app.alert({message : "请先选择要复制的数据！"});
                return;
            }
            $.parentchild.copyChild($trs, options.updateUrl, options.modalSettings);
        });


        $(".btn-delete-child").click(function() {
            var $trs = $childTable.find("tbody tr").has(".check :checkbox:checked");
            if(!$trs.length) {
                $.app.alert({message : "请先选择要删除的数据！"});
                return;
            }
            $.app.confirm({
                message: "确定删除选择的数据吗？",
                ok : function() {
                    var ids = new Array();
                    $trs.each(function() {
                        var id = $(this).prop("id");
                        if(id.indexOf("old_") == 0) {
                            id = id.replace("old_", "");
                            ids.push({name : "ids", value : id});
                        }
                    });

                    $.post(options.batchDeleteUrl, ids, function() {
                        $trs.remove();
                        $.table.changeBtnState($childTable);
                    });

                }
            });
        });

        options.form.submit(function() {
            //如果是提交 不需要执行beforeunload
            $(window).unbind("beforeunload");
            $childTable.find("tbody tr").each(function(index) {
                var tr = $(this);
                tr.find(".check > :checkbox").attr("checked", false);
                tr.find(":input").each(function() {
                    if($(this).prop("name").indexOf(options.prefixParamName) != 0) {
                        $(this).prop("name", options.prefixParamName + "[" + index + "]." + $(this).prop("name").replace(tr.prop("id"), ""));
                    }
                });
            });
        });
    }

}

$.movable = {
    /**
     * urlPrefix：指定移动URL的前缀，
     * 如/sample，生成的URL格式为/sample/{fromId}/{toId}/{direction:方向(up|down)}
     * @param table
     * @param urlPrefix
     */
    initMoveableTable : function(table) {
        if(!table.length) {
            return;
        }
        var urlPrefix = table.data("move-url-prefix");
        if(!urlPrefix) {
            $.app.alert({message : "请添加移动地址URL，如&lt;table move-url-prefix='/sample'&gt;<br/>自动生成：/sample/{fromId}/{toId}/{direction:方向(up|down)}"});
        }
        var fixHelper = function (e, tr) {
            var $originals = tr.children();
            var $helper = tr.clone();
            $helper.children().each(function (index) {
                // Set helper cell sizes to match the original sizes
                $(this).width($originals.eq(index).width())
            });
            return $helper;
        };

        //事表格可拖拽排序
        table.find("tbody")
            .sortable({
                helper: fixHelper,
                opacity: 0.5,
                cursor: "move",
                placeholder: "sortable-placeholder",
                update: function (even, ui) {
                    even.stopPropagation();
                    prepareMove(ui.item.find(".moveable").closest("td"));
                }
            });

        //弹出移动框
        table.find("a.pop-movable[rel=popover]")
            .mouseenter(function (e) {
                var a = $(this);
                a.popover("show");
                var idInput = a.closest("tr").find(".id");
                idInput.focus();
                a.next(".popover").find(".popover-up-btn,.popover-down-btn").click(function() {
                    var fromId = $(this).closest("tr").prop("id");
                    var toId = idInput.val();

                    if(!/\d+/.test(toId)) {
                        $.app.alert({message : "请输入数字!"});
                        return;
                    }

                    var fromTD = $(this).closest("td");

                    if($(this).hasClass("popover-up-btn")) {
                        move(fromTD, fromId, toId, "up");
                    } else {
                        move(fromTD, fromId, toId, "down");
                    }
                });
                a.parent().mouseleave(function() {
                    a.popover("hide");
                });
            });

        table.find(".up-btn,.down-btn").click(function() {
            var fromTR = $(this).closest("tr");
            if($(this).hasClass("up-btn")) {
                fromTR.prev("tr").before(fromTR);
            } else {
                fromTR.next("tr").after(fromTR);
            }
            prepareMove($(this).closest("td"));
        });

        /**
         *
         * @param fromTD
         */
        function prepareMove(fromTD) {
            var fromTR = fromTD.closest("tr");
            var fromId = fromTR.prop("id");
            var nextTR = fromTR.next("tr");
            if(nextTR.length) {
                move(fromTD, fromId, nextTR.prop("id"), "down");
            } else {
                var preTR = fromTR.prev("tr");
                move(fromTD, fromId, preTR.prop("id"), "up");
            }

        }
        function move(fromTD, fromId, toId, direction) {
            if(!(fromId && toId)) {
                return;
            }
            var order = $.movable.tdOrder(fromTD);
            if (!order) {
                $.app.alert({message: "请首先排序要移动的字段！"});
                return;
            }
            //如果升序排列 需要反转direction
            if(order == "desc") {
                if(direction == "up") {
                    direction = "down";
                } else {
                    direction = "up";
                }
            }
            $.app.waiting("正在移动");
            var url = urlPrefix + "/" + fromId + "/" + toId + "/" + direction;
            $.getJSON(url, function(data) {
                $.app.waitingOver();
                if(data.success) {
                    $.table.reloadTable(fromTD.closest("table"));
                } else {
                    $.app.alert({message : data.message});
                }

            });
        }
    }
    ,
    initMovableReweight : function($btn, url) {
        $btn.click(function () {
            $.app.confirm({
                message: "确定优化权重吗？<br/><strong>注意：</strong>优化权重执行效率比较低，请在本系统使用人员较少时执行（如下班时间）",
                ok: function () {
                    $.app.waiting("优化权重执行中。。");
                    $.getJSON(url, function(data) {
                        $.app.waitingOver();
                        if(!data.success) {
                            $.app.alert({message : data.message});
                        } else {
                            location.reload();
                        }
                    });
                }
            });
        });
    },

    tdOrder : function(td) {
        var tdIndex = td.closest("tr").children("td").index(td);
        return td.closest("table").find("thead > tr > th").eq(tdIndex).prop("order");
    }
};

$.btn = {
    initChangeStatus : function(urlPrefix, tableId, config) {
        $(config.btns.join(",")).each(function(i) {
            $(this).off("click").on("click", function() {
                var $table = $("#" + tableId);
                var checkbox = $.table.getAllSelectedCheckbox($table);
                if(checkbox.size() == 0) {
                    return;
                }
                var title = config.titles[i];
                var message = config.messages[i];
                var status = config.status[i];
                var url = urlPrefix + "/" + status + "?" + checkbox.serialize();
                $.app.confirm({
                    title : title,
                    message : message,
                    ok : function() {
                        window.location.href = url;
                    }
                });
            });
        });
    },
    /**
     * 初始化改变显示隐藏的btn
     */
    initChangeShowStatus : function(urlPrefix, tableId) {
        $.btn.initChangeStatus(urlPrefix, tableId, {
            btns : [".status-show", ".status-hide"],
            titles : ['显示数据', '隐藏数据'],
            messages : ['确认显示数据吗？', '确认隐藏数据吗？'],
            status : ['true', 'false']
        });
    }
};

$.array = {
    remove : function(array, data) {
        if(array.length == 0) {
            return;
        }
        for(var i = array.length - 1; i >= 0; i--) {
            if(array[i] == data) {
                array.splice(i, 1);
            }
        }
    },
    contains : function(array, data) {
        if(array.length == 0) {
            return false;
        }
        for(var i = array.length - 1; i >= 0; i--) {
            if(array[i] == data) {
                return true;
            }
        }
        return false;
    },
    indexOf : function(array, data) {
        if(array.length == 0) {
            return -1;
        }
        for(var i = array.length - 1; i >= 0; i--) {
            if(array[i] == data) {
                return i;
            }
        }
        return -1;
    },
    clear : function(array) {
        if(array.length == 0) {
            return;
        }
        array.splice(0, array.length);
    },
    trim : function(array) {
        for(var i = array.length - 1; i >= 0; i--) {
            if(array[i] == "" || array[i] == null) {
                array.splice(i, 1);
            }
        }
        return array;
    }

};

/*
 * Project: Twitter Bootstrap Hover Dropdown
 * Author: Cameron Spear
 * Contributors: Mattia Larentis
 *
 * Dependencies?: Twitter Bootstrap's Dropdown plugin
 *
 * A simple plugin to enable twitter bootstrap dropdowns to active on hover and provide a nice user experience.
 *
 * No license, do what you want. I'd love credit or a shoutout, though.
 *
 * http://cameronspear.com/blog/twitter-bootstrap-dropdown-on-hover-plugin/
 */
;(function($, window, undefined) {
    // outside the scope of the jQuery plugin to
    // keep track of all dropdowns
    var $allDropdowns = $();

    // if instantlyCloseOthers is true, then it will instantly
    // shut other nav items when a new one is hovered over
    $.fn.dropdownHover = function(options) {

        // the element we really care about
        // is the dropdown-toggle's parent
        $allDropdowns = $allDropdowns.add(this.parent());

        return this.each(function() {
            var $this = $(this).parent(),
                defaults = {
                    delay: 100,
                    instantlyCloseOthers: true
                },
                data = {
                    delay: $(this).data('delay'),
                    instantlyCloseOthers: $(this).data('close-others')
                },
                settings = $.extend(true, {}, defaults, options, data),
                timeout;

            $this.hover(function() {
                if(settings.instantlyCloseOthers === true)
                    $allDropdowns.removeClass('open');

                window.clearTimeout(timeout);
                $(this).addClass('open');
            }, function() {
                timeout = window.setTimeout(function() {
                    $this.removeClass('open');
                }, settings.delay);
            });
        });
    };

    // apply dropdownHover to all elements with the data-hover="dropdown" attribute
    $(document).ready(function() {
        $('[data-hover="dropdown"]').dropdownHover();
    });
})(jQuery, this);


$(function () {
    //global disable ajax cache
    $.ajaxSetup({ cache: true });

    $("[data-toggle='tooltip']").each(function() {

        $(this).tooltip({delay:300});
    });

    $(document).ajaxError(function(event, request, settings) {

        $.app.waitingOver();
        if(request.status == 0) {// 中断的不处理
            return;
        }
        top.$.app.alert({
            title : "网络故障/系统故障",
            //<refresh>中间的按钮在ajax方式中删除不显示
            message : request.responseText.replace(/(<refresh>.*<\/refresh>)/g, "")
        });
    });
});

