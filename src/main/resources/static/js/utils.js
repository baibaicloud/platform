(function($) {
	$.msgbox = function(param) {
		var option = {
			msg: "提示内容",
			data: null,
			oktxt: "确定",
			ok: null,
			no : null
		}
		option = $.extend(option, param);
		
		var id = "toast-" + new Date().getTime();
		var toastHTML = '<span tag="msgbox">'+ option.msg +'</span><button id="'+ id +'" class="btn-flat toast-action">'+ option.oktxt +'</button>';
		M.toast({html: toastHTML});
		$("#" + id).click(function(){
			if(option.ok == null){
				return;
			}
			$("span[tag=msgbox]").parent().remove();
			option.ok(option.data);
		});
	};
	
	$.conf = function(param){
		var option = {
				title: "提示",
				content: "",
				oktxt: "确定",
				notxt: "取消",
				draggable: true,
				boxWidth: '50%',
				okfun: null,
			};
		option = $.extend(option, param);
		
		$.confirm({
		    title: option.title,
		    content: option.content,
		    theme: 'material',
		    draggable:  option.draggable,
		    boxWidth:  option.boxWidth,
		    animationSpeed: 0,
		    useBootstrap: false,
		    buttons: {
		        confirm: {
		        	text: option.oktxt,
		        	btnClass: 'btn-blue gback',
		        	action: option.okfun
		        },
		        cancel: {
		        	text: option.notxt
		        }
		    }
		});
	}
})(jQuery);