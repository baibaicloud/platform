(function($) {
	
	$.httpSend = function(param) {
		var option = {
			url: "",
			type: "get",
			dataType: "json",
			cache: false,
			timeout: 15000,
			success: null,
			error : null
		}
		option = $.extend(option, param);
		if (!option.success) {
			option.success = function(data){};
		}
		if (!option.error) {
			option.error = function(e){};
		}
		
		// 增加错误处理
		if($.aop){
			$.aop.before({target: option, method: 'error'}, errorHandler);
		}
		
		$.ajax(option);
	};
	
	$.httpSendForm = function(param) {
		var id = param.formId;
		if(!id){
			M.toast({html: '请求失败', text: '请求参数[formId]缺少表单的id'});
			return false;
		}
		if(id.indexOf("#") != 0){
			id = "#"+id;
		}
		var option = {
				type:"POST",
				dataType:"json",
				contentType : "multipart/form-data",
				cache : false,
				timeout : 10000,
				beforeSubmit : function(){
					return true;
				},
				success:null,
				error : null,
				resetSession:true
		}
		option = $.extend(option, param);
		if (!option.success) {
			option.success = function(data){};
		}
		if (!option.error) {
			option.error = function(e){};
		}
		
		// 增加错误处理
		if($.aop){
			$.aop.before({target: option, method: 'error'}, errorHandler);
		}
		
		$(id).ajaxSubmit(option);
		return true;
	}
	
	var errorHandler = function(e) {
		M.toast({html: '请求失败，无法连接到服务端. code=[ '+ e[0].status +' ]'});
	}
	
	$.getUrlParam = function (name) {
	     var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");//构造一个含有目标参数的正则表达式对象
	     var r = window.location.search.substr(1).match(reg);//匹配目标参数
	     if (r != null) return unescape(r[2]); return null;//返回参数值
	}
	
})(jQuery);