(function ($)
{
    //全局系统对象
    window['common'] = {};
	//修改ajax默认设置
		$.ajaxSetup({
			type : 'POST',
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				$.messager.progress('close');
				var str = "<div style='height:300px;overflow:auto;'>";
				str += XMLHttpRequest.responseText;
				str += "</div>"
				$.messager.alert('错误', str);
			}
		});
		
		var easyuiErrorFunction = function(XMLHttpRequest) {
			$.messager.progress('close');
			var str = "<div style='height:300px;overflow:auto;'>";
			str += XMLHttpRequest.responseText;
			str += "</div>"
			$.messager.alert('错误', str);
		};
		$.fn.datagrid.defaults.onLoadError = easyuiErrorFunction;
		$.fn.treegrid.defaults.onLoadError = easyuiErrorFunction;
		$.fn.tree.defaults.onLoadError = easyuiErrorFunction;
		$.fn.combogrid.defaults.onLoadError = easyuiErrorFunction;
		$.fn.combobox.defaults.onLoadError = easyuiErrorFunction;
		$.fn.form.defaults.onLoadError = easyuiErrorFunction;
		/**
		 * 取消easyui默认开启的parser
		 * 在页面加载之前，先开启一个进度条
		 * 然后在页面所有easyui组件渲染完毕后，关闭进度条
		 * @requires jQuery,EasyUI
		 */
		$.parser.auto = false;
		$(function() {
			$.messager.progress({
				text : '加载中....',
				interval : 100
			});
			$.parser.parse(window.document);
			window.setTimeout(function() {
				$.messager.progress('close');
				if (self != parent) {
					window.setTimeout(function() {
						try {
							parent.$.messager.progress('close');
						} catch (e) {
						}
					}, 500);
				}
			}, 1);
			$.parser.auto = true;
		});
		//IE检测
			common.isLessThanIe8 = function() {
				return ($.browser.msie && $.browser.version < 8);
			};
		/**
		 * 使panel和datagrid在加载时提示
		 * @requires jQuery,EasyUI
		 */
		$.fn.panel.defaults.loadingMessage = '加载中....';
		$.fn.datagrid.defaults.loadMsg = '加载中....';
		
		
		/**
		 * @requires jQuery,EasyUI
		 * 防止panel/window/dialog组件超出浏览器边界
		 * @param left
		 * @param top
		 */
		var easyuiPanelOnMove = function(left, top) {
			var l = left;
			var t = top;
			if (l < 1) {
				l = 1;
			}
			if (t < 1) {
				t = 1;
			}
			var width = parseInt($(this).parent().css('width')) + 14;
			var height = parseInt($(this).parent().css('height')) + 14;
			var right = l + width;
			var buttom = t + height;
			var browserWidth = $(window).width();
			var browserHeight = $(window).height();
			if (right > browserWidth) {
				l = browserWidth - width;
			}
			if (buttom > browserHeight) {
				t = browserHeight - height;
			}
			$(this).parent().css({/* 修正面板位置 */
				left : l,
				top : t
			});
		};
		$.fn.dialog.defaults.onMove = easyuiPanelOnMove;
		$.fn.window.defaults.onMove = easyuiPanelOnMove;
		$.fn.panel.defaults.onMove = easyuiPanelOnMove;
		
		/**
		 * @requires jQuery,EasyUI
		 * panel关闭时回收内存
		 */
		$.fn.panel.defaults.onBeforeDestroy = function() {
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
			} catch (e) {
			}
		};

		 
		//序列化表单到对象
		common.serializeObject = function(form) {
			//console.dir(form.serializeArray());
			var o = {};
			$.each(form.serializeArray(), function(index) {
				if (o[this['name']]) {
					o[this['name']] = o[this['name']] + "," + (this['value']==''?' ':this['value']);
				} else {
					o[this['name']] = this['value']==''?' ':this['value'];
				}
			});
			//console.dir(o);
			return o;
		};
		/**
		 * 扩展树表格级联选择（点击checkbox才生效）：
		 * 		自定义两个属性：
		 * 		cascadeCheck ：普通级联（不包括未加载的子节点）
		 * 		deepCascadeCheck ：深度级联（包括未加载的子节点）
		 */
		$.extend($.fn.treegrid.defaults,{
			onLoadSuccess : function() {
				var target = $(this);
				var opts = $.data(this, "treegrid").options;
				var panel = $(this).datagrid("getPanel");
				var gridBody = panel.find("div.datagrid-body");
				var idField = opts.idField;//这里的idField其实就是API里方法的id参数
				gridBody.find("div.datagrid-cell-check input[type=checkbox]").unbind(".treegrid").click(function(e){
					if(opts.singleSelect) return;//单选不管
					if(opts.cascadeCheck||opts.deepCascadeCheck){
						var id=$(this).parent().parent().parent().attr("node-id");
						var status = false;
						if($(this).attr("checked")){
							target.treegrid('select',id);
							status = true;
						}else{
							target.treegrid('unselect',id);
						}
						//级联选择父节点
						selectParent(target,id,idField,status);
						selectChildren(target,id,idField,opts.deepCascadeCheck,status);
					}
					e.stopPropagation();//停止事件传播
				});
			}
		});
		
		/**
		 * 扩展树表格级联勾选方法：
		 * @param {Object} container
		 * @param {Object} options
		 * @return {TypeName} 
		 */
		$.extend($.fn.treegrid.methods,{
			/**
			 * 级联选择
		     * @param {Object} target
		     * @param {Object} param 
			 *		param包括两个参数:
		     *			id:勾选的节点ID
		     *			deepCascade:是否深度级联
		     * @return {TypeName} 
			 */
			cascadeCheck : function(target,param){
				var opts = $.data(target[0], "treegrid").options;
				if(opts.singleSelect)
					return;
				var idField = opts.idField;//这里的idField其实就是API里方法的id参数
				var status = false;//用来标记当前节点的状态，true:勾选，false:未勾选
				var selectNodes = $(target).treegrid('getSelections');//获取当前选中项
				for(var i=0;i<selectNodes.length;i++){
					if(selectNodes[i][idField]==param.id)
						status = true;
				}
				//级联选择父节点
				selectParent(target,param.id,idField,status);
				selectChildren(target,param.id,idField,param.deepCascade,status);
			}
		});
		
		
		
		/**
		 * 级联选择父节点
		 * @param {Object} target
		 * @param {Object} id 节点ID
		 * @param {Object} status 节点状态，true:勾选，false:未勾选
		 * @return {TypeName} 
		 */
		function selectParent(target,id,idField,status){
			var parent = target.treegrid('getParent',id);
			if(parent){
				var parentId = parent[idField];
				if(status)
					target.treegrid('select',parentId);
				else
					target.treegrid('unselect',id);
				selectParent(target,parentId,idField,status);
			}
		}
		/**
		 * 级联选择子节点
		 * @param {Object} target
		 * @param {Object} id 节点ID
		 * @param {Object} deepCascade 是否深度级联
		 * @param {Object} status 节点状态，true:勾选，false:未勾选
		 * @return {TypeName} 
		 */
		function selectChildren(target,id,idField,deepCascade,status){
			//深度级联时先展开节点
			if(status&&deepCascade)
				target.treegrid('expand',id);
			//根据ID获取下层孩子节点
			var children = target.treegrid('getChildren',id);
			for(var i=0;i<children.length;i++){
				var childId = children[i][idField];
				if(status)
					target.treegrid('select',childId);
				else
					target.treegrid('unselect',childId);
				selectChildren(target,childId,idField,deepCascade,status);//递归选择子节点
			}
		}

		/**
		 * @author xxx
		 * 
		 * @requires jQuery,EasyUI
		 * 
		 * 创建一个模式化的dialog
		 * 
		 * @returns $.modalDialog.handler 这个handler代表弹出的dialog句柄
		 * 
		 * @returns $.modalDialog.xxx 这个xxx是可以自己定义名称，主要用在弹窗关闭时，刷新某些对象的操作，可以将xxx这个对象预定义好
		 */
		$.modalDialog = function(options) {
			if ($.modalDialog.handler == undefined) {// 避免重复弹出
				var opts = $.extend({
					title : '',
					width : 840,
					height : 680,
					modal : true,
					onClose : function() {
						$.modalDialog.handler = undefined;
						$(this).dialog('destroy');
					}
					/*onOpen : function() {
						parent.$.messager.progress({
							title : '提示',
							text : '数据处理中，请稍后....'
						});
					}*/
				}, options);
				opts.modal = true;// 强制此dialog为模式化，无视传递过来的modal参数
				return $.modalDialog.handler = $('<div/>').dialog(opts);
			}
		};
		
		
		/**  
		 * datagrid鼠标经过提示单元格内容
		 * 扩展两个方法  
		 * 
		 * * onLoadSuccess:function(data){   
		 *	    $('#test').datagrid('doCellTip',{cls:{'background-color':'red'},delay:1000});   
		 *	} 
		 * 
		 */  
		$.extend($.fn.datagrid.methods, {   
		    /**
		     * 开打提示功能  
		     * @param {} jq  
		     * @param {} params 提示消息框的样式  
		     * @return {}  
		     */  
		    doCellTip : function(jq, params) {   
		        function showTip(data, td, e) {   
		            if ($(td).text() == "")   
		                return;   
		            data.tooltip.text($(td).text()).css({   
		                        top : (e.pageY + 10) + 'px',   
		                        left : (e.pageX + 20) + 'px',   
		                        'z-index' : $.fn.window.defaults.zIndex,   
		                        display : 'block'   
		                    });   
		        };   
		        return jq.each(function() {   
		            var grid = $(this);   
		            var options = $(this).data('datagrid');   
		            if (!options.tooltip) {   
		                var panel = grid.datagrid('getPanel').panel('panel');   
		                var defaultCls = {   
		                    'border' : '1px solid #333',   
		                    'padding' : '1px',   
		                    'color' : '#333',   
		                    'background' : '#f7f5d1',   
		                    'position' : 'absolute',   
		                    'max-width' : '200px',   
		                    'border-radius' : '4px',   
		                    '-moz-border-radius' : '4px',   
		                    '-webkit-border-radius' : '4px',   
		                    'display' : 'none'   
		                }   
		                var tooltip = $("<div id='celltip'></div>").appendTo('body');   
		                tooltip.css($.extend({}, defaultCls, params.cls));   
		                options.tooltip = tooltip;   
		                panel.find('.datagrid-body').each(function() {   
		                    var delegateEle = $(this).find('> div.datagrid-body-inner').length   
		                            ? $(this).find('> div.datagrid-body-inner')[0]   
		                            : this;   
		                    $(delegateEle).undelegate('td', 'mouseover').undelegate(   
		                            'td', 'mouseout').undelegate('td', 'mousemove')   
		                            .delegate('td', {   
		                                'mouseover' : function(e) {   
		                                    if (params.delay) {   
		                                        if (options.tipDelayTime)   
		                                            clearTimeout(options.tipDelayTime);   
		                                        var that = this;   
		                                        options.tipDelayTime = setTimeout(   
		                                                function() {   
		                                                    showTip(options, that, e);   
		                                                }, params.delay);   
		                                    } else {   
		                                        showTip(options, this, e);   
		                                    }   
		  
		                                },   
		                                'mouseout' : function(e) {   
		                                    if (options.tipDelayTime)   
		                                        clearTimeout(options.tipDelayTime);   
		                                    options.tooltip.css({   
		                                                'display' : 'none'   
		                                            });   
		                                },   
		                                'mousemove' : function(e) {   
		                                    var that = this;   
		                                    if (options.tipDelayTime) {   
		                                        clearTimeout(options.tipDelayTime);   
		                                        options.tipDelayTime = setTimeout(   
		                                                function() {   
		                                                    showTip(options, that, e);   
		                                                }, params.delay);   
		                                    } else {   
		                                        showTip(options, that, e);   
		                                    }   
		                                }   
		                            });   
		                });   
		  
		            }   
		  
		        });   
		    },   
		    /**
		     * 关闭消息提示功能  
		     * @param {} jq  
		     * @return {}  
		     */  
		    cancelCellTip : function(jq) {   
		        return jq.each(function() {   
		                    var data = $(this).data('datagrid');   
		                    if (data.tooltip) {   
		                        data.tooltip.remove();   
		                        data.tooltip = null;   
		                        var panel = $(this).datagrid('getPanel').panel('panel');   
		                        panel.find('.datagrid-body').undelegate('td',   
		                                'mouseover').undelegate('td', 'mouseout')   
		                                .undelegate('td', 'mousemove')   
		                    }   
		                    if (data.tipDelayTime) {   
		                        clearTimeout(data.tipDelayTime);   
		                        data.tipDelayTime = null;   
		                    }   
		                });   
		    }   
		});

})(jQuery);

/**
 * obj是否为空
 */
function isempty(obj){
	if(obj !=null && obj!=""){
		return false;
	}
	return true;
}

/**
 * 
 * @param id
 */
function initYearCombobox(id){
	var year_datas = new Array();
	var myDate = new Date();
	var year = myDate.getFullYear();
	var k = 0;
	for(var i=year; i>year-9; i--){
		var year_data = new Object();
		year_data.year = i;
		year_data.name = i+"年";
		year_datas[k] = year_data;
		k++;
	}
	$('#'+id).combobox({
		data: year_datas,
		valueField: 'year',
		textField: 'name',
		width:80,
		value:year,
		editable: false
	});
}

/**
 * 
 * @param id
 */
function initMonthCombobox(id){
	var month_datas = new Array();
	var myDate = new Date();
	var month = myDate.getMonth()+1;
	month = month<10?"0"+month:month;
	for(var i=0; i<12; i++){
		var month_data = new Object();
		month_data.month = i<9?"0"+(i+1):i+1;
		month_data.name = i+1+"月";
		month_datas[i] = month_data;
	}
	$('#'+id).combobox({
		data: month_datas,
		valueField: 'month',
		textField: 'name',
		width:60,
		value:month,
		editable: false
	});
}

var m9k = {};
m9k.getLength = function(str) {
	// /<summary>获得字符串实际长度，中文2，英文1</summary>
	// /<param name="str">要获得长度的字符串</param>
	var realLength = 0, len = str.length, charCode = -1;
	for (var i = 0; i < len; i++) {
		charCode = str.charCodeAt(i);
		if (charCode >= 0 && charCode <= 128)
			realLength += 1;
		else
			realLength += 2;
	}
	return realLength;
};

//金额格式化
var common = {};
common.fmoney = function(v){
	if(isNaN(v)){  
        return v;  
    }  
    v = (Math.round((v - 0) * 100)) / 100;  
    v = (v == Math.floor(v)) ? v + ".00" : ((v * 10 == Math.floor(v * 10)) ? v  
            + "0" : v);  
    v = String(v);  
    var ps = v.split('.');  
    var whole = ps[0];  
    var sub = ps[1] ? '.' + ps[1] : '.00';  
    var r = /(\d+)(\d{3})/;  
    while (r.test(whole)) {  
        whole = whole.replace(r, '$1' + ',' + '$2');  
    }  
    v = whole + sub;  
      
    return v;  
} 

//下拉选单默认选第一笔
$.extend($.fn.combobox.methods, {
    selectedIndex: function (jq, index) {
        if (!index) {
            index = 0;
        }
        $(jq).combobox({
            onLoadSuccess: function () {
                var opt = $(jq).combobox('options');
                var data = $(jq).combobox('getData');
 
                for (var i = 0; i < data.length; i++) {
                    if (i == index) {
                        $(jq).combobox('setValue', eval('data[index].' + opt.valueField));
                        break;
                    }
                }
            }
        });
    }
});