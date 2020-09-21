var nenodetreedrag = {
	select: null,
    class_name : null,  //允许放置的容器
	permitDrag : false,	//是否允许移动标识
	tempitem: null,

	_x : 0,             //节点x坐标
    _y : 0,			    //节点y坐标
    _left : 0,          //光标与节点坐标的距离
    _top : 0,           //光标与节点坐标的距离

    old_elm : null,     //拖拽原节点
    tmp_elm : null,     //跟随光标移动的临时节点
    new_elm : null,     //拖拽完成后添加的新节点
    count: 0,

    //初始化
    init : function (className){

        //允许拖拽节点的父容器的classname(可按照需要，修改为id或其他)
        nenodetreedrag.class_name = className;

        //监听鼠标按下事件，动态绑定要拖拽的节点（因为节点可能是动态添加的）
        $('.' + nenodetreedrag.class_name).on('mousedown', 'a', function(event){
            //当在允许拖拽的节点上监听到点击事件，将标识设置为可以拖拽
            nenodetreedrag.permitDrag = true;
            //获取到拖拽的原节点对象
            nenodetreedrag.old_elm = $(this);
            //执行开始拖拽的操作
            nenodetreedrag.mousedown(event);
            return false;
        });

        //监听鼠标移动
        $('.' + nenodetreedrag.class_name).mousemove(function(event){
            //判断拖拽标识是否为允许，否则不进行操作
            if(!nenodetreedrag.permitDrag) return false;
            //执行移动的操作
            nenodetreedrag.mousemove(event);
            return false;
        });

        //监听鼠标放开
        $('.' + nenodetreedrag.class_name).mouseup(function(event){
            //判断拖拽标识是否为允许，否则不进行操作
        	nenodetreedrag.mouseup(event);
        	nenodetreedrag.count = 0;
        	nenodetreedrag.select = null;
            if(!nenodetreedrag.permitDrag) return false;
            //拖拽结束后恢复标识到初始状态
            nenodetreedrag.permitDrag = false;
            return false;
        });
        
        $('.' + nenodetreedrag.class_name).mousemove('a', function(event){
        	if(!nenodetreedrag.permitDrag) return false;
        });
        
        
        $('.' + nenodetreedrag.class_name).on('mousedown', 'a', function(event){
            //当在允许拖拽的节点上监听到点击事件，将标识设置为可以拖拽
            nenodetreedrag.permitDrag = true;
            //获取到拖拽的原节点对象
            nenodetreedrag.old_elm = $(this);
            //执行开始拖拽的操作
            nenodetreedrag.mousedown(event);
            return false;
        });
        
        $('.' + nenodetreedrag.class_name).on('mouseout', 'a[nenodepath]', function(event){
        	
        	if($(event.target).attr("nenodepath") == undefined){
        		return;
        	}
        	$("div[tag=nenodetemp]").remove();
        });
        
        $('.' + nenodetreedrag.class_name).on('mouseenter', 'a[nenodepath]', function(event){
        	var html = $("#rm-tree-temp-item-move-template").html();
        	$(event.target).before(html);
        });
        
        $('.' + nenodetreedrag.class_name).on('mouseenter', 'div[tag=nenodetemp]', function(event){
        	var html = $("#rm-tree-temp-item-move-template").html();
        	$(event.target).before(html);
        });
        
        $('.' + nenodetreedrag.class_name).on('mouseout', 'div[tag=nenodetemp]', function(event){
            //if($(event.target).attr("tag") == )
        });

    },
    getCurNodeItem: function(item){
    	if($(item).attr("nenodetitle") == undefined){
			if($(item).parent().attr("nenodetitle") == undefined){
				return null;
			}
			return $(item).parent();
		}else{
			return $(item);
		}
    },
    

	//按下鼠标 执行的操作
	mousedown : function (event){
		
		nenodetreedrag.maxL = $(document).width() - $(nenodetreedrag.old_elm).outerWidth();
		nenodetreedrag.maxT = $(document).height() - $(nenodetreedrag.old_elm).outerHeight();
        
		if($(event.target).attr("nenodetitle") == undefined){
			if($(event.target).parent().attr("nenodetitle") == undefined){
				return;
			}
			nenodetreedrag.select = $(event.target).parent();
		}else{
			nenodetreedrag.select = $(event.target);
		}
		
		var tempnenode = $("#rm-tree-item-move-template").html();
		tempnenode = tempnenode.replace("{title}", $(nenodetreedrag.select).attr("nenodetitle"));
		nenodetreedrag.tmp_elm = $(tempnenode);
        
        //2.计算 节点 和 光标 的坐标
        nenodetreedrag._x = $('.' + nenodetreedrag.class_name).offset().left;
        nenodetreedrag._y = $('.' + nenodetreedrag.class_name).offset().top;

        var e = event || window.event;
        nenodetreedrag._left = nenodetreedrag._x;
        nenodetreedrag._top = nenodetreedrag._y;

        //3.修改克隆节点的坐标，实现跟随鼠标进行移动的效果
        $(nenodetreedrag.tmp_elm).css({
            'position' : 'absolute',
            'width':'100%',
        });
        
        //4.添加临时节点
        tmp = $(nenodetreedrag.old_elm).parent().append(nenodetreedrag.tmp_elm);
        nenodetreedrag.tmp_elm = $(tmp).find(nenodetreedrag.tmp_elm);
        $(nenodetreedrag.tmp_elm).css('cursor', 'move');
	},
	showTempNode: function(item){
		if(nenodetreedrag.tempitem){
			return;
		}
	},
	//移动鼠标 执行的操作
	mousemove : function (event){
		
        //2.计算坐标
        var e = event;
        var x = e.pageX - nenodetreedrag._left;
        var y = e.pageY - nenodetreedrag._top - 20;
        
        //不允许超出浏览器范围
        x = x < 0 ? 0: x;
        x = x > nenodetreedrag.maxL ? nenodetreedrag.maxL: x;
        y = y < 0 ? 0: y;
        y = y > nenodetreedrag.maxT ? nenodetreedrag.maxT: y;

        //3.修改克隆节点的坐标
        $(nenodetreedrag.tmp_elm).css('top',y);
        
        if(nenodetreedrag.count > 10){
        	$(nenodetreedrag.tmp_elm).removeClass("hide");
        	nenodetreedrag.showTempNode(event.target);
        }else{
        	nenodetreedrag.count++;
        }
	},

    //放开鼠标 执行的操作
    mouseup : function (event){

        //移除临时节点
        $(nenodetreedrag.tmp_elm).remove();
        
        var curitem = null;
        if($(event.target).attr("nenodetitle") == undefined){
			if($(event.target).parent().attr("nenodetitle") == undefined){
				return;
			}
			curitem = $(event.target).parent();
		}else{
			curitem = $(event.target);
		}
        
        //console.log($(curitem).attr("nenodetitle"));
        
        //resourceManageObj.showNotifyUI("是否");
    },
};
