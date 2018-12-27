/**
 * EasyUI DataGrid根据字段动态合并单元格
* 参数 tableID 要合并table的id
 * 参数 colList 要合并的列,用逗号分隔(例如："name,department,office");
 */
 function mergeCellsByField(tableID, colList) {
     var ColArray = colList.split(",");
     var tTable = $("#" + tableID);
     var TableRowCnts = tTable.datagrid("getRows").length;
     var tmpA;
     var tmpB;
     var PerTxt = "";
     var CurTxt = "";
     var alertStr = "";
     for (j = ColArray.length - 1; j >= 0; j--) {
         PerTxt = "";
         tmpA = 1;
         tmpB = 0;

         for (i = 0; i <= TableRowCnts; i++) {
             if (i == TableRowCnts) {
                 CurTxt = "";
             }
             else {
                 CurTxt = tTable.datagrid("getRows")[i][ColArray[j]];
             }
             if (PerTxt == CurTxt) {
                 tmpA += 1;
             }
             else {
                 tmpB += tmpA;
                 
                 tTable.datagrid("mergeCells", {
                     index: i - tmpA,
                     field: ColArray[j],//合并字段
                    rowspan: tmpA,
                     colspan: null
                 });
                 tTable.datagrid("mergeCells", { //根据ColArray[j]进行合并
                    index: i - tmpA,
                    field: "Ideparture",
                    rowspan: tmpA,
                    colspan: null
                });
                
                 tmpA = 1;
             }
             PerTxt = CurTxt;
         }
     }
 }
 
 /**
	 * @requires jQuery,EasyUI
	 * 
	 * 扩展treegrid，使其支持平滑数据格式
	 */
	$.fn.treegrid.defaults.loadFilter = function(data, parentId) {
		var opt = $(this).data().treegrid.options;
		var idFiled, textFiled,iconCls, parentField;
		
		if (opt.parentField) {
			idFiled = opt.idFiled ||opt.idField || 'id';
			textFiled = opt.textFiled ||opt.textField ||opt.treeField || 'text';
			iconCls = opt.iconCls || 'iconCls';
			parentField = opt.parentField;
			var i, l, treeData = [], tmpMap = [];
			for (i = 0, l = data.length; i < l; i++) {
				tmpMap[data[i][idFiled]] = data[i];
			}
			for (i = 0, l = data.length; i < l; i++) {
				
				if (tmpMap[data[i][parentField]] && data[i][idFiled] != data[i][parentField]) {
					if (!tmpMap[data[i][parentField]]['children'])
						tmpMap[data[i][parentField]]['children'] = [];
					data[i]['text'] = data[i][textFiled];
					data[i]['iconCls'] = data[i][iconCls];
					tmpMap[data[i][parentField]]['children'].push(data[i]);
				} else {
					data[i]['text'] = data[i][textFiled];
					data[i]['iconCls'] = data[i][iconCls];
					treeData.push(data[i]);
				}
			}
			return treeData;
		}
		return data;
	};
	
	/**
	 * @requires jQuery,EasyUI
	 * 
	 * 扩展tree，使其支持平滑数据格式
	 */
	$.fn.tree.defaults.loadFilter = function(data, parent) {
		var opt = $(this).data().tree.options;
		var idFiled, textFiled,iconCls, parentField;
		if (opt.parentField) {
			idFiled = opt.idFiled ||opt.idField || 'id';
			textFiled = opt.textFiled ||opt.textField ||opt.treeField || 'text';
			iconCls = opt.iconCls || 'iconCls';
			parentField = opt.parentField;
			var i, l, treeData = [], tmpMap = [];
			for (i = 0, l = data.length; i < l; i++) {
				tmpMap[data[i][idFiled]] = data[i];
			}
			for (i = 0, l = data.length; i < l; i++) {
				if (tmpMap[data[i][parentField]] && data[i][idFiled] != data[i][parentField]) {
					if (!tmpMap[data[i][parentField]]['children'])
						tmpMap[data[i][parentField]]['children'] = [];
					data[i]['text'] = data[i][textFiled];
					data[i]['iconCls'] = data[i][iconCls];
					tmpMap[data[i][parentField]]['children'].push(data[i]);
				} else {
					data[i]['text'] = data[i][textFiled];
					data[i]['iconCls'] = data[i][iconCls];
					treeData.push(data[i]);
				}
			}
			return treeData;
		}
		return data;
	};
	/**
	 * @requires jQuery,EasyUI
	 * 
	 * 扩展combotree，使其支持平滑数据格式
	 */
	$.fn.combotree.defaults.loadFilter = function(data, parent) {
		var opt = $(this).data("tree").options;
		var idFiled, textFiled, parentField,iconCls;
		if (opt.parentField) {
			idFiled = opt.idFiled ||opt.idField|| 'id';
			textFiled = opt.textFiled ||opt.textField||opt.treeField || 'text';
			parentField = opt.parentField;
			iconCls = opt.iconCls || 'iconCls';
			var i, l, treeData = [], tmpMap = [];
			for (i = 0, l = data.length; i < l; i++) {
				tmpMap[data[i][idFiled]] = data[i];
			}
			for (i = 0, l = data.length; i < l; i++) {
				if (tmpMap[data[i][parentField]] && data[i][idFiled] != data[i][parentField]) {
					if (!tmpMap[data[i][parentField]]['children'])
						tmpMap[data[i][parentField]]['children'] = [];
					data[i]['text'] = data[i][textFiled];
					data[i]['iconCls'] = data[i][iconCls];
					tmpMap[data[i][parentField]]['children'].push(data[i]);
				} else {
					data[i]['text'] = data[i][textFiled];
					data[i]['iconCls'] = data[i][iconCls];
					treeData.push(data[i]);
				}
			}
			return treeData;
		}
		return data;
	};
 
 
 /**
  * 没有权限的弹出窗口
  */
 function auth(){
	 $.messager.alert("提示","您没有权限执行操作");
 }
 
 
 /**
  * 判断row的点击动作 是选中 or 取消选中
  */
 function isSelected(row,obj){
	var isSelected = 0;//判断被点击行 是选中 （1） 或 取消选中（0）
	var selectedNodes = obj.treegrid('getSelections');
	$.each(selectedNodes,function(index){
		if(this.id &&this.id == row.id){
			isSelected = 1;
			return false;
		}
		if(this.ID &&this.ID == row.ID){
			isSelected = 1;
			return false;
		}
	})
	return isSelected;
 }
 
//初始化清除按钮  
 function initClear(target){  
     var jq = $(target);  
     var opts = jq.data('combo').options;
     if(opts.initClear)
    	 return;
     var combo = jq.data('combo').combo;  
     var arrow = combo.find('span .combo-arrow');  
     var clear = arrow.siblings("span.combo-clear");  
     if(clear.size()==0){  
         //创建清除按钮。  
         clear = $('<span class="combo-clear" style="height: 20px;"></span>');  
           
         //清除按钮添加悬停效果。  
         clear.unbind("mouseenter.combo mouseleave.combo").bind("mouseenter.combo mouseleave.combo",  
             function(event){  
                 var isEnter = event.type=="mouseenter";  
                 clear[isEnter ? 'addClass' : 'removeClass']("combo-clear-hover");  
             }  
         );  
         //清除按钮添加点击事件，清除当前选中值及隐藏选择面板。  
         clear.unbind("click.combo").bind("click.combo",function(){  
             jq.combo("setValue","").combo("setText","");  
             jq.combo('hidePanel');  
         });  
         arrow.before(clear);  
     };  
     var input = combo.find("input.combo-text");  
     input.outerWidth(input.outerWidth()-clear.outerWidth());  
       
     opts.initClear = true;//已进行清除按钮初始化。  
 }  
   
 //扩展easyui combo添加清除当前值。
 $.extend($.fn.combo.methods,{  
     initClear:function(jq){  
         return jq.each(function(){  
              initClear(this);  
         });  
     }
 });  

 //年月挑选框布丁
 function easyuiMonth(db){
		db.datebox({
	        onShowPanel: function () {//显示日趋选择对象后再触发弹出月份层的事件，初始化时没有生成月份层
	            span.trigger('click'); //触发click事件弹出月份层
	            //fix 1.3.x不选择日期点击其他地方隐藏在弹出日期框显示日期面板
	            if (p.find('div.calendar-menu').is(':hidden')) p.find('div.calendar-menu').show();
	            if (!tds) setTimeout(function () {//延时触发获取月份对象，因为上面的事件触发和对象生成有时间间隔
	                tds = p.find('div.calendar-menu-month-inner td');
	                tds.click(function (e) {
	                    e.stopPropagation(); //禁止冒泡执行easyui给月份绑定的事件
	                    var year = /\d{4}/.exec(span.html())[0]//得到年份
	                    , month = parseInt($(this).attr('abbr'), 10); //月份，这里不需要+1
	                    db.datebox('hidePanel')//隐藏日期对象
	                    .datebox('setValue', year + '-' + month); //设置日期的值
	                });
	            }, 0);
	            yearIpt.unbind();//解绑年份输入框中任何事件
	        },
	        parser: function (s) {
	            if (!s) return new Date();
	            var arr = s.split('-');
	            return new Date(parseInt(arr[0], 10), parseInt(arr[1], 10) - 1, 1);
	        },
	        formatter: function (d) { return d.getFullYear() + '-' + ((d.getMonth() + 1)<10?'0'+(d.getMonth() + 1):(d.getMonth() + 1));/*getMonth返回的是0开始的，忘记了。。已修正*/ }
	    });


	    var p = db.datebox('panel'), //日期选择对象
	        tds = false, //日期选择对象中月份
	        aToday = p.find('a.datebox-current'),
	        yearIpt = p.find('input.calendar-menu-year'),//年份输入框
	        //显示月份层的触发控件
	        span = aToday.length ? p.find('div.calendar-title span') ://1.3.x版本
	        p.find('span.calendar-text'); //1.4.x版本
	    if (aToday.length) {//1.3.x版本，取消Today按钮的click事件，重新绑定新事件设置日期框为今天，防止弹出日期选择面板
	       
	        aToday.unbind('click').click(function () {
	            var now=new Date();
	            db.datebox('hidePanel').datebox('setValue', now.getFullYear() + '-' + (now.getMonth() + 1));
	        });
	    }
		
	}