/**
 *
 * @author
 *
 * @requires jQuery,EasyUI
 *
 */
/* debug information if something error in IE, pls check if there is one point '.' following with'$'
 * some error was caused by wrongly use {} or ()synax expresion"
*/
var gb = $.extend({}, gb); /*define global object to prevent repeat var name*/

$.parser.auto = false;

$(function () {
    var win = $.messager.progress({
        msg: '加载中....',
        interval: 100
    });
    $.parser.parse(window.document);
    window.setTimeout(function () {
        $.messager.progress('close');
        if (self != parent) {
            window.setTimeout(function () {
                try {
                    parent.$.messager.progress('close');
                } catch (e) {}
            }, 500);
        }
    }, 1);
    $.parser.auto = true;
});

gb.fs = function (str) {
    for (var i = 0; i < arguments.length - 1; i++) {
        str = str.replace("{" + i + "}", arguments[i + 1]);
    }
    return str;
};

$.fn.panel.defaults.loadingMessage = '加载中....';

$.fn.datagrid.defaults.loadMsg = '加载中....';

$.fn.layout.paneldefaults.loadingMessage = '加载中...';

$.fn.panel.defaults.onBeforeDestroy = function () {
    var frame = $('iframe', this);
    try {
        if (frame.length > 0) {
            frame[0].contentWindow.document.write('');
            frame[0].contentWindow.close();
            frame.remove();
            if ($.browser.msie) {
                CollectGarbage();
            }
        }
    } catch (e) {}
};

gb.serializeObject = function (form) { /*turn form elements vaule serialized to object*/
    var o = {};
    $.each(form.serializeArray(), function (index) {
        if (o[this.name]) {
            o[this.name] = o[this.name] + "," + this.value;
        } else {
            o[this.name] = this.value;
        }
    });
    return o;
};

$.extend($.fn.combo.methods, {
    /**
 *      * ..combo...
 *           * @param {Object} jq
 *                * @param {Object} param stopArrowFocus:...........foucs...
 *                     * stoptype:.......disable.readOnly....
 *                          */
    disableTextbox: function (jq, param) {
        return jq.each(function () {
            param = param || {};
            var textbox = $(this).combo("textbox");
            var that = this;
            var panel = $(this).combo("panel");
            var data = $(this).data('combo');
            if (param.stopArrowFocus) {
                data.stopArrowFocus = param.stopArrowFocus;
                var arrowbox = $.data(this, 'combo').combo.find('span.combo-arrow');
                arrowbox.unbind('click.combo').bind('click.combo', function () {
                    if (panel.is(":visible")) {
                        $(that).combo('hidePanel');
                    } else {
                        $("div.combo-panel").panel("close");
                        $(that).combo('showPanel');
                    }
                });
                textbox.unbind('mousedown.mycombo').bind('mousedown.mycombo', function (e) {
                    e.preventDefault();
                });
            }
            textbox.prop(param.stoptype ? param.stoptype : 'disabled', true);
            data.stoptype = param.stoptype ? param.stoptype : 'disabled';
        });
    },
    /**
 * .....
 * @param {Object} jq
 */
    enableTextbox: function (jq) {
        return jq.each(function () {
            var textbox = $(this).combo("textbox");
            var data = $(this).data('combo');
            if (data.stopArrowFocus) {
                var that = this;
                var panel = $(this).combo("panel");
                var arrowbox = $.data(this, 'combo').combo.find('span.combo-arrow');
                arrowbox.unbind('click.combo').bind('click.combo', function () {
                    if (panel.is(":visible")) {
                        $(that).combo('hidePanel');
                    } else {
                        $("div.combo-panel").panel("close");
                        $(that).combo('showPanel');
                    }
                    textbox.focus();
                });
                textbox.unbind('mousedown.mycombo');
                data.stopArrowFocus = null;
            }
            textbox.prop(data.stoptype, false);
            data.stoptype = null;
        });
    }
});

$.extend($.messager.defaults, {
    ok: "确定",
    cancel: "取消"
});
$.extend($.fn.datagrid.methods, {
    editCell: function(jq,param){
        return jq.each(function(){
            var opts = $(this).datagrid('options');
            var fields = $(this).datagrid('getColumnFields',true).concat($(this).datagrid('getColumnFields'));
            for(var i=0; i<fields.length; i++){
                var col = $(this).datagrid('getColumnOption', fields[i]);
                col.editor1 = col.editor;
                if (fields[i] != param.field){
                    col.editor = null;
               }
            }
            $(this).datagrid('beginEdit', param.index);
            for(var i=0; i<fields.length; i++){
                var col = $(this).datagrid('getColumnOption', fields[i]);
                col.editor = col.editor1;
            }
        });
    }
});
$.extend($.fn.validatebox.defaults.rules, {
    eqPassword: {
        validator: function (value, param) {
            return value == $(param[0]).val();
        },
        message: '输入的两次密码不一致'
    },
    lessThanSpecifiedValue: {
        validator: function (value, spec) {
            return value <= spec;
        },
        message: '数值超出范围'
    },
    floatOrInt: {
        validator: function (value) {
            return (/^(\d{1,8}(,\d\d\d)*(\.\d{1,8}(,\d\d\d)*)?|\d+(\.\d+))?$/i).test(value);
        },
        message: '整数或者小数'
    },
    Float: {// 验证整数或小数
        validator: function (value) {
            return /^(0\.\d+)?$/i.test(value);
        },
        message: '请输入小数，并确保格式正确'
    },
    Float_has_zero: {// 验证整数或小数
        validator: function (value) {
            return /^(0\.\d+|0)?$/i.test(value);
        },
        message: '请输入小数或者零，并确保格式正确'
    },        
    complexityCheck: {
        validator: function (value) {
            if (value.length > 6 && value.match(/[\d]/) && value.match(/[A-Z]/) && value.match(/[a-z]/) && value.match(/[,.~!@#$%^*]/) && value.match(/[^\da-zA-Z]/)) {
                return true;
            } else {
                return false;
            }
        },
        message: '密码长度大于6,必须包含大写A-Z 小写a-z 数字0-9 英文符号,.~!@#$%^*'
    },
    integer: {
        validator: function (value) {
            return (/^[+]?[1-9]+\d*$/i).test(value);
        },
        message: '输入大于零的整数'
    },
    integer_has_zero: {
        validator: function (value) {
            return (/^[+]?[0-9]+\d*$/i).test(value);
        },
        message: '输入大于等于零的整数'
    },
    chinese: { // 验证中文
        validator: function (value) {
            return /^[\u0391-\uFFE5]+$/i.test(value);
        },
        message: '请输入中文'
    },
    no_chinese: { // 验证非中文
        validator: function (value) {
	        if (value.match(/^[\u0391-\uFFE5]+$/))
	        {
		        return false;
	        }
            return true;
        },
        message: '不能输入中文及符号'
    },
    mobile: { //value值为文本框中的值
        validator: function (value) {
            var reg = /^1[3|4|5|8|9]\d{9}$/;
            return reg.test(value);
        },
        message: '输入手机号码格式不准确.'
    },
    phone: { //
        validator: function (value) {
            return /^[0-9]{4,16}$/.test(value);
            //#return /^[A-Z][A-Z0-9_]{3,15}$/.test(value);
        },
        message: '请输入数字，长度4-16'
    },

    upCaseAndNum: { //
        validator: function (value) {
            return /^[A-Z0-9]+$/.test(value);
            //#return /^[A-Z][A-Z0-9_]{3,15}$/.test(value);
        },
        message: '请输入大写字母或者数字'
    },
    upCase_Num_Underscore: { //
        validator: function (value) {
            //return /^[A-Z][A-Z]*{3,16}$/i.test(value);
            return /^[A-Z][A-Z0-9_]+$/.test(value);
            //return !(/^(([A-Z]*|/d*|[-_/~!@#/$%/^&/*/./(/)/[/]/{/}<>/?/'"]*)|.{3,15})$|/s/.test(value));

        },
        message: '请输入大写字母或者数字或者下划线，以字母开头'
    },
    upCase_Num_Underscore_Gerneral: { //
        validator: function (value) {
            //return /^[A-Z][A-Z]*{3,16}$/i.test(value);
            return /^[A-Z][A-Z0-9_-]+$/.test(value);
            //return !(/^(([A-Z]*|/d*|[-_/~!@#/$%/^&/*/./(/)/[/]/{/}<>/?/'"]*)|.{3,15})$|/s/.test(value));

        },
        message: '请输入大写字母或者数字,中划线,下划线，以字母开头 '
    },
     upCase_Num_Underscore_Gerneral_In_Any_Position: { //
        validator: function (value) {
            //return /^[A-Z][A-Z]*{3,16}$/i.test(value);
            return /[A-Z0-9_-]+$/.test(value);
            //return !(/^(([A-Z]*|/d*|[-_/~!@#/$%/^&/*/./(/)/[/]/{/}<>/?/'"]*)|.{3,15})$|/s/.test(value));

        },
        message: '请输入大写字母或者数字,中划线,下划线 '
    },
    Letter_Num_Dash_Underscore: { //
        validator: function (value) {
            //return /^[A-Z][A-Z]*{3,16}$/i.test(value);
            return /^[A-Za-z0-9][A-Za-z0-9_-]+$/.test(value);
            //return !(/^(([A-Z]*|/d*|[-_/~!@#/$%/^&/*/./(/)/[/]/{/}<>/?/'"]*)|.{3,15})$|/s/.test(value));

        },
        message: '字母或者数字开头，随后可字母,数字,中划线,下划线 '
    },
    Spec_Key_Word: { //
        validator: function (value) {
            //return /^[A-Z][A-Z]*{3,16}$/i.test(value);
            return !(/[,;=<>]+$/.test(value));
            //return !(/^(([A-Z]*|/d*|[-_/~!@#/$%/^&/*/./(/)/[/]/{/}<>/?/'"]*)|.{3,15})$|/s/.test(value));

        },
        message: '不能输入英文逗号,分号,等号,大于小于符号'
    },
    ip: {
        validator : function(value) {
            //return /^(\d+)\.(\d+)\.(\d+)\.(\d+)$/.test(value); 
            //return /^[0-9]{7,16}$/.test(value);
            if (value.match(/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/) && value.split(".")[0] < 224 && value.split(".")[0] > 0 && value.split(".")[1] < 256 && value.split(".")[2] < 256 && value.split(".")[3] < 256){
                return true;
            } else { 
                return false; 
            }
        },
        message : 'IP地址格式不正确'
    },
    ipMask: {
        validator : function(value) {
            if (value.match(/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/) && value.split(".")[0] < 256 && value.split(".")[0] > 0 && value.split(".")[1] < 256 && value.split(".")[2] < 256 && value.split(".")[3] < 256){
                return true;
            } else { 
                return false; 
            }
        },
        message : 'IP掩码格式不正确'
    },
    intInterval: {
        validator: function (value, param) {
            return value >= param[0] && param[1] >= value;
        },
        message: '请输入介于{0}-{1}之间的数字.'

    },

    length: {
        validator: function (value, param) {
            return value.length >= param[0] && param[1] >= value.length;
        },
        message: '请输入{0}-{1}位字符.'
    },
    passDate: {
        validator: function (value, param) {
            var myDate = new Date();
            var now_year = parseInt(myDate.getFullYear());
            var now_month = parseInt(myDate.getMonth());
            var now_day = parseInt(myDate.getDate());
            var tmp_split = value.split("-");
            var tmp_year =  parseInt(tmp_split[0]);
            var tmp_month =  parseInt(tmp_split[1]);
            var tmp_day = parseInt(tmp_split[2]);
            var flag = true;
            if (tmp_year == now_year) 
            {
                if (tmp_month == now_month)
                {
                    if (tmp_day > now_day)
                    {
                        flag = false;
                    }
                } else if (tmp_month > now_month)
                {
	                flag = false;
                }
            } else if (tmp_year > now_year)  {
	            flag = false;
            }
            return flag;
        },
        message: '输入未来日期'
    },
    futureDate: {
        validator: function (value, param) {
            var myDate = new Date();
            var now_year = parseInt(myDate.getFullYear());
            var now_month = parseInt(myDate.getMonth());
            var now_day = parseInt(myDate.getDate());
            var tmp_split = value.split("-");
            var tmp_year =  parseInt(tmp_split[0]);
            var tmp_month =  parseInt(tmp_split[1]);
            var tmp_day = parseInt(tmp_split[2]);
            var flag = true;
            if (tmp_year == now_year) 
            {
                if (tmp_month == now_month)
                {
                    if (tmp_day < now_day)
                    {
                        flag = false;
                    }
                } else if (tmp_month < now_month)
                {
	                flag = false;
                }
            } else if (tmp_year < now_year)  {
	            flag = false;
            }
            return flag;
        },
        message: '输入未来日期'
    }
});


$.extend($.fn.datagrid.defaults.editors, {
    datetimebox: {
        init: function (container, options) {
            var editor = $('<input />').appendTo(container);
            options.editable = false;
            editor.datetimebox(options);
            return editor;
        },
        getValue: function (target) {
            return $(target).datetimebox('getValue');
        },
        setValue: function (target, value) {
            $(target).datetimebox('setValue', value);
        },
        resize: function (target, width) {
            $(target).datetimebox('resize', width);

        },
        destroy: function (target) {
            $(target).datetimebox('destroy');
        }
    }
});

$.extend($.fn.datagrid.methods, {
    addEditor: function (jq, param) {
        if (param instanceof Array) {
            $.each(param, function (index, item) {
                var e = $(jq).datagrid('getColumnOption', item.field);
                e.editor = item.editor;
            });
        } else {
            var e = $(jq).datagrid('getColumnOption', param.field);
            e.editor = item.editor;
        }
    },
    removeEditor: function (jq, param) {
        if (param instanceof Array) {
            $.each(param, function (index, item) {
                var e = $(jq).datagrid('getColumnOption', item);
                e.editor = {};
            });
        } else {
            var e = $(jq).datagrid('getColumnOption', param);
            e.editor = {};
        }
    }
});

var easyuiErrorFunction = function (XMLHttpRequest) {
        $.messager.progress('close');
        $.messager.alert("Error", XMLHttpRequest.responseText);
    };

$.fn.datagrid.defaults.onLoadError = easyuiErrorFunction;
$.fn.treegrid.defaults.onLoadError = easyuiErrorFunction;
$.fn.tree.defaults.onLoadError = easyuiErrorFunction;
$.fn.combogrid.defaults.onLoadError = easyuiErrorFunction;
$.fn.combo.defaults.onLoadError = easyuiErrorFunction;
$.fn.form.defaults.onLoadError = easyuiErrorFunction;

gb.ns = function () {
    var o = {},
        d;
    for (var i = 0; i < arguments.length; i++) {
        d = arguments[i].split(".");
        o = window[d[0]] = window[d[0]] || {};
        for (var k = 0; k < d.slice(1).length; k++) {
            o = o[d[k + 1]] = o[d[k + 1]] || {};
        }
    }
    return o;
};

gb.random4 = function () {
    return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
};

gb.UUID = function () {
    return (gb.random4() + gb.random4() + "-" + gb.random4() + "-" + gb.random4() + "-" + gb.random4() + "-" + gb.random4() + gb.random4() + gb.random4());
};

gb.getErrorMessage = function (message, errorEvent) {
    // Generate error message for easyui messager
    message = message + "<br/>" + errorEvent.status + '&nbsp;' + errorEvent.statusText;
    return message;
};

// make sure easyui-dialog (top, left) do not extend available screen
// usage:
// $('#dlgID').dialog({
//      onMove: gb.restrictDialog(left, top)
// });
gb.restrictDialog = function (left, top) {
    if (top < 0) {
        $(this).dialog('move', {top:0, left:left});
    }
    if (top > (window.screen.availHeight-250)) {
        $(this).dialog('move', {top:window.screen.availHeight-250, left:left});
    }
    if (left < 0) {
        $(this).dialog('move', {top:top, left:0});
    }
    if (left > (window.screen.availWidth-480)) {
        $(this).dialog('move', {top:top, left:window.screen.availWidth-480});
    }
};

gb.clear_easyui_invalid = function clear_easyui_invalid() {
    $(".validatebox-invalid").each(function(index, elem) {
        $(elem).removeClass("validatebox-invalid");
    });
    $(".textbox-invalid").each(function(index, elem) {
        $(elem).removeClass("textbox-invalid");
    });
};

// if current url not valid, do the disableFunction (e.g hide buttons etc)
gb.checkUIAccess = function (disableFunction) {
    var valid_frames = $.cookie('ui_access').split(',');
    var current_url = '';
    var location = window.location.toString();
    var i = location.lastIndexOf('/');
    if (i !== -1) {
        current_url = location.slice(i+1, location.length);
        //console.debug(current_url);
        if (valid_frames.indexOf(current_url) === -1) {
            disableFunction();
        }
    } else {
        disableFunction();
        $.messager.alert('错误', '权限检查错误，当前URL<'+current_url+'>不合法');
    }
};
gb.are_cookies_enabled = function() {
    var cookieEnabled = (navigator.cookieEnabled) ? true : false;
    if (typeof navigator.cookieEnabled == "undefined" && !cookieEnabled)
    {
        document.cookie="testcookie";
        cookieEnabled = (document.cookie.indexOf("testcookie") != -1) ? true : false;
    }
    return (cookieEnabled);
};
