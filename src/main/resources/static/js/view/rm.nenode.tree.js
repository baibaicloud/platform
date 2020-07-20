var rmnenodetree = {
	init : function() {

		var topheight = $(window).height() - 120;
		$("#rm-new-tree").css("height", topheight + "px");
		$("#rm-new-tree").niceScroll({
			cursorcolor : "#cccccc",
			cursorwidth : "10px",
			cursoropacitymax : 0.9
		});
		
		$.httpSend({
            url: '/nenode/list/get',
            type: 'post',
            success: function(resp){
            	if(!resp.success){ M.toast({html: resp.message}); return; }
            	$("#rm-resource-group-load-panel").addClass("hide");
            	if(resp.content.length == 0){
            		$("#rm-new-tree").addClass("hide");
            		$("#rm-tree-empty").removeClass("hide");
            		$("#rm-list-panel").addClass("hide");
            		$("#rm-list-empty-panel").removeClass("hide");
            		$("#rm-list-loading-panel").addClass("hide");
            		return;
            	}
            	
            	rmnenodetree.loadTree(resp.content);
            }
        })
	},
	loadTree: function(data){
		var plugins = [ "dnd", "wholerow"];
		if(indexObj.rightInfo.authType == "SOLE"){
			plugins = [ "contextmenu", "dnd", "wholerow" ,"changed"];   
		}else{
			$.each(indexObj.rightInfo.rights, function(i, item){
	            if(item == "nenode_manage"){
	            	plugins = [ "contextmenu", "dnd", "wholerow" ,"changed"];                      
	            }
	        });
		}

		$('#rm-new-tree').jstree({
			"plugins" : plugins,
			'core' : {
				"themes" : {
					"variant" : "large",
					"stripes" : true
				},
				"check_callback" : true,
				"multiple" : false,
				"animation" : 0,
				'data' : data
			},
			"contextmenu" : {
				"items" : {
					"newNode" : {
						"label": "添加",
						"action": function (data) {
								var inst = $.jstree.reference(data.reference),
								obj = inst.get_node(data.reference);
								inst.create_node(obj, {}, "first", function (new_node) {
								try {
									inst.edit(new_node);
									rmnenodetree.addNode(new_node);
								} catch (ex) {
									setTimeout(function () { inst.edit(new_node); },0);
								}
							});
						}
					},
					"renameNode" : {
						"label": "重名",
						"action": function (data) {
							var inst = $.jstree.reference(data.reference),
							obj = inst.get_node(data.reference);
							inst.edit(obj);
							rmnenodetree.renameNode(obj);
						}
					},
					"delNode" : {
						"label": "删除",
						"action": function (data) {
							var inst = $.jstree.reference(data.reference),
							obj = inst.get_node(data.reference);
							if(obj.parent == "#"){
								M.toast({html: "此节点无法删除"});
								return;
							}
							
							if(obj.children.length != 0){
								M.toast({html: "请先删除子节点"});
								return;
							}
							
							if(inst.is_selected(obj)) {
								inst.delete_node(inst.get_selected());
							}else {
								inst.delete_node(obj);
							}
							rmnenodetree.delNode(obj);
						}
					}
				}
			}
		}).on('loaded.jstree', function(e, data) {
			var inst = data.instance;
			var obj = inst.get_node(e.target.firstChild.firstChild.lastChild);
			inst.select_node(obj);
			inst.open_node(obj);
		}).on('changed.jstree', function(e, data) {
			var inst = data.instance;
			if(data.action == "select_node"){
				$("#rm-use-last-ne-btn").removeClass("gback");
	            $("#rm-use-last-ne-btn").css("color","#909090");
	            resourceManageObj.treeItemChildClick(data.node);
			}
		}).on('create_node.jstree', function(e, data) {
		}).on('rename_node.jstree', function(e, data){
			rmnenodetree.renameNode(data.node);
		}).on('move_node.jstree', function(e, data){
			var inst = data.instance;
			obj = inst.get_node(data.node);
			var children = inst.get_node(inst.get_parent(obj)).children;
			rmnenodetree.moveNode(data.node, children);
		});
	},
	addNode: function(node){
		
		if(isNaN(node.parent)){
			$('#rm-new-tree').jstree(true).refresh();
			return;
		}
		
		$.httpSend({
            url: '/nenode/create',
            type: 'post',
            data: JSON.stringify({
            	title: node.text,
                pid: node.parent
            }),
            success: function(resp){
                if(!resp.success){
                    M.toast({html: resp.message});
                    return;
                }
                node.sid = resp.content.id;
                $('#rm-new-tree').jstree(true).set_id(node, resp.content.id);
            }
        })
	},
	getNodeId: function(node){
		if(isNaN(node.id) && !node.sid){
			return null;
		}
		
		var id = null;
		if(!isNaN(node.id)){
			id = node.id;
		}else{
			id = node.sid;
		}
		
		return id;
	},
	renameNode: function(node){
		var id = rmnenodetree.getNodeId(node);
		if(id == null){
			return;
		}
		
		$.httpSend({
            url: '/nenode/update',
            type: 'post',
            data: JSON.stringify({
            	id: id,
                title: node.text,
            }),
            success: function(resp){
                if(!resp.success){
                    M.toast({html: resp.message});
                    return;
                }
            }
        })
	},
	delNode: function(node){
		var id = rmnenodetree.getNodeId(node);
		if(id == null){
			return;
		}
		$.httpSend({
            url: '/nenode/del',
            type: 'post',
            data: JSON.stringify({
                id: id
            }),
            success: function(resp){
                if(!resp.success){
                    M.toast({html: resp.message});
                    return;
                }
            }
        })
	},
	moveNode: function(node, children){
		
		if(node.parent == "#"){
			$('#rm-new-tree').jstree(true).refresh();
			return;
		}
		
		var id = rmnenodetree.getNodeId(node);
		if(id == null){
			return;
		}
		
		$.httpSend({
            url: '/nenode/move',
            type: 'post',
            data: JSON.stringify({
                id: id,
                pid: node.parent,
                cids: children.join(",")
            }),
            success: function(resp){
                if(!resp.success){
                    M.toast({html: resp.message});
                    return;
                }
            }
        })
	}
}