<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<script type="text/javascript" charset="utf-8">
var centerTabs;
var title_now;
$(function() {
	centerTabs = $('#centerTabs').tabs({
		
	});
	centerTabs.tabs('add', {
		iconCls:'icon-business',
		title:'我的工作台',
		closable : false,
		content : '<iframe src="${pageContext.request.contextPath }/home.htmlx" frameborder="0" style="border:0;width:100%;height:99%;margin-top:2px;"></iframe>',
		tools : [ {
			iconCls : 'icon-reload12',
			handler : function() {
				refreshTab('我的工作台');
			}
		} ]
	});
	
	//tab右键触发时候所触发的函数  
    centerTabs.tabs({  
        onContextMenu:function(e, title) {  
            //在每个菜单选项中添加title值  
            var $divMenu = $("#tab_rightmenu div[id]");  
            $divMenu.each(function() {  
                $(this).attr("id", title);  
            });  
              
            //显示menu菜单  
            $('#tab_rightmenu').menu('show', {     
                left: e.pageX,    
                top: e.pageY     
            });   
            e.preventDefault();  
        }  
    });  
    //实例化menu点击触发事件  
    $('#tab_rightmenu').menu({  
        "onClick":function(item) {  
            closeTab(item.id,item.text);  
        }  
    });  
    
});

function addTab(title, url, icon, uncheckEq) {
	selectTreeNode(url);
	//if (centerTabs.tabs('exists', title_now)) {
	if (centerTabs.tabs('exists', title)) {
		//centerTabs.tabs('select', title_now);
		centerTabs.tabs('select', title);
		//if (!uncheckEq && title_now == title) return;
		if(!uncheckEq) return;//重复点击是否刷新
		centerTabs.tabs('update', {
			tab : centerTabs.tabs('getSelected'),
			options : {
				title : title,
				content : '<iframe src="${pageContext.request.contextPath }/sys/redirect.htmlx?url='+encodeURIComponent(url)+'" frameborder="0" style="border:0;width:100%;height:99%;margin-top:2px;"></iframe>',
				cache:true,
				closable : true,
				iconCls:icon
				,tools : [ {
					iconCls : 'icon-reload12',
					handler : function() {
						refreshTab(title);
					} 
				} ]
			} 
		});
	} else {
			centerTabs.tabs('add', {
				iconCls:icon,
				title : title,
				closable : true,
				//border:false,
				//href : 'error/error.jsp',
				content : '<iframe src="${pageContext.request.contextPath }/sys/redirect.htmlx?url='+encodeURIComponent(url)+'" frameborder="0" style="border:0;width:100%;height:99%;margin-top:2px;"></iframe>',
				tools : [ {
					iconCls : 'icon-reload12',
					handler : function() {
						refreshTab(title);
					}
				} ]
			});
		
	}
	//title_now = title;
	if ($.support.msie) {
		CollectGarbage();
	}
}
function refreshTab(title) {
	var tab = centerTabs.tabs('getTab', title);
	centerTabs.tabs('update', {
		tab : tab,
		options : tab.panel('options')
	});
}

//关闭  
function closeTab(title, text) {  
    if(text == '关闭全部标签') {  
        $(".tabs li").each(function(index, obj) {  
              //获取所有可关闭的选项卡  
              var tabTitle = $(".tabs-closable", this).text();  
              $("#centerTabs").tabs("close", tabTitle);  
         });  
    }  
      
    if(text == '关闭其他标签') {  
        $(".tabs li").each(function(index, obj) {  
              //获取所有可关闭的选项卡  
              var tabTitle = $(".tabs-closable", this).text();  
              if(tabTitle != title) {  
                $("#centerTabs").tabs("close", tabTitle);  
              }  
         });  
    }  
      
    if(text == '关闭右侧标签') {  
         var $tabs = $(".tabs li");  
         for(var i = $tabs.length - 1; i >= 0; i--) {  
            //获取所有可关闭的选项卡  
            var tabTitle = $(".tabs-closable", $tabs[i]).text();  
            if(tabTitle != title) {  
                $("#centerTabs").tabs("close", tabTitle);  
            } else {  
                break;  
            }  
         }  
    }  
      
    if(text == '关闭左侧标签') {  
         var $tabs = $(".tabs li");  
         for(var i = 0; i < $tabs.length; i++) {  
            //获取所有可关闭的选项卡  
            var tabTitle = $(".tabs-closable", $tabs[i]).text();  
            if(tabTitle != title) {  
                $("#centerTabs").tabs("close", tabTitle);  
            } else {  
                break;  
            }  
         }  
    }  
}

</script>
<div id="centerTabs"  data-options="fit:true,border:false">
    
</div>
<!--关闭tab选项菜单-->  
<div id="tab_rightmenu" class="easyui-menu" style="width:120px; display:none">   
    <div name="tab_menu-tabcloseall" id="closeAll"  >  
        关闭全部标签  
    </div>   
  
    <div name="tab_menu-tabcloseother" id="closeOthor" data-options="iconCls:'icon-no'" >  
        关闭其他标签  
    </div>   
  
    <div class="menu-sep"></div>   
  
    <div name="tab_menu-tabcloseright" id="closeRight">  
        关闭右侧标签  
    </div>      
    <div name="tab_menu-tabcloseleft" id="closeLeft">  
        关闭左侧标签  
    </div>   
</div>  