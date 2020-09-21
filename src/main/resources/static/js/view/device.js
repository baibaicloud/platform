var deviceObj = {
		lastSelectDevice: null,
		deviceId: null,

		tunnelAckClick: function(cur){
			var tunneltype = "TCP";
			if(!$('#rm-tunnel-tunneltype-tcp').is(':checked') && !$('#rm-tunnel-tunneltype-udp').is(':checked')) {
				M.toast({html: "请选择一个协议类型"});
				return;
			}
			
			if($('#rm-tunnel-tunneltype-udp').is(':checked')){
				tunneltype = "UDP";
			}
			
			if($("#rm-tunnel-local-ip").val() == "" || $("#rm-tunnel-local-port").val() == "" || $("#rm-tunnel-remote-port").val() == ""){
				M.toast({html: "参数填写未完整"});
				return;
			}
			
			if($("#rm-tunnel-remark").val().length > 120){
				M.toast({html: "备注内容太长"});
				return;
			}
			
			$(cur).addClass("disabled");
			$.httpSend({
                url: '/tunnelweb/tunnel/add',
                type: 'post',
                data: JSON.stringify({
                	deviceId: deviceObj.deviceId,
                	tunneltype: tunneltype,
                	localIp: $("#rm-tunnel-local-ip").val(),
                	localPort: $("#rm-tunnel-local-port").val(),
                	remotePort: $("#rm-tunnel-remote-port").val(),
                	remark: $("#rm-tunnel-remark").val()
                }),
                success: function(resp){
                	$("#rm-tunnel-add-btn").removeClass("disabled");
                	if(!resp.success){
                        M.toast({html: resp.message});
                        return;
                    }
                	
                	M.toast({html: "添加成功"});
                	$("#rm-nenode-tunnel-modal").modal("close");
                }
            })
		},
		tunnelDevice: function(cur){
			deviceObj.deviceId = $(cur).parent().parent().parent().parent().attr("id");
			$("#rm-tunnel-add-btn").removeClass("disabled");
			$("#rm-nenode-tunnel-modal").modal("open");
			$("#rm-tunnel-tunneltype-tcp").removeAttr("checked"); 
			$("#rm-tunnel-tunneltype-udp").removeAttr("checked"); 
			$("#rm-tunnel-local-port").val("");
			$("#rm-tunnel-remote-port").val("");
			$("#rm-tunnel-remark").val("");
			$("#rm-tunnel-local-ip").val("");
		},
		moveAckClick: function(cur){
			if(deviceObj.lastSelectDevice == null){
				return;
			}
			
			$.httpSend({
                url: '/device/nenode/move',
                type: 'post',
                data: JSON.stringify({
                	deviceId: deviceObj.deviceId,
                	nenodeId: deviceObj.lastSelectDevice.id
                }),
                success: function(resp){
                	if(!resp.success){
                        M.toast({html: resp.message});
                        return;
                    }
                	
                	resourceManageObj.loadDeviceList();
                }
            })
			
		},
		moveDevice: function(cur){
			deviceObj.deviceId = $(cur).parent().parent().parent().parent().attr("id");
			$("#rm-nenode-modal").modal("open");
			$("#rm-nenode-modal-tree").html("");
			$("#rm-nenode-modal-move-to-name").html("无");
			deviceObj.lastSelectDevice = null;
			
			$.httpSend({
                url: '/nenode/list/get',
                type: 'post',
                success: function(resp){
                	if(!resp.success){
                        M.toast({html: resp.message});
                        return;
                    }
                	
                	var list = new Array();
                	resp.content.forEach(function(item, index){
                		list.push({
                			id: item.id,
                			name: item.text,
                			pId: item.parent
                		});
                	});
                	
                	list[0].open= true;
                	deviceObj.loadTree(list);
                }
            })
		},
		delDevice: function(cur){
			deviceObj.deviceId = $(cur).parent().parent().parent().parent().attr("id");
			var name = $(cur).parent().parent().parent().parent().find("span[tag=devicename]").html();
			$.conf({
				title: "删除提醒",
				content: "是否确定删除【"+ name +"】网元?",
				okfun: function(){
					deviceObj.ackDelDevice();
				}
			});
		},
		ackDelDevice: function(){
			$.httpSend({
                url: '/device/delete',
                type: 'post',
                data: JSON.stringify({
                	deviceId: deviceObj.deviceId,
                }),
                success: function(resp){
                	if(!resp.success){
                        M.toast({html: resp.message});
                        return;
                    }
                	
                	resourceManageObj.loadDeviceList();
                }
            })
		},
		loadTree: function(list){
			var setting = {
	            data: {
	                simpleData: {
	                    enable: true
	                }
	            },
	            callback: {
	            	onClick: function(event, treeId, treeNode){
	            		deviceObj.lastSelectDevice = treeNode;
	            		$("#rm-nenode-modal-move-to-name").html(treeNode.name);
	            	}
	            }
	        };
	        
	        $.fn.zTree.init($("#rm-nenode-modal-tree"), setting, list);
		}
}
