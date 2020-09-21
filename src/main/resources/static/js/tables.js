var tableObj = {
	table : function(id, _size) {
		var amtable =  new Object({
			tid : id,
			page: "1",
			pages: "1",
			pageSize: 3,
			size: _size + "",
			load: null,
			init : function() {
				
				$("#" + this.tid).find("li[tag=btn-left]").click(function(){
					if(amtable.page == "1"){
						return;
					}
					
					amtable.page = eval(amtable.page + "-1") + "";
					amtable.loadPage(amtable);
				});
				
				$("#" + this.tid).find("li[tag=btn-right]").click(function(){
					if(amtable.page == amtable.pages){
						return;
					}
					
					amtable.page = eval(amtable.page + "+1") + "";
					amtable.loadPage(amtable);
				});
				
				this.loadPage(this);
			},
			refresh: function(gotoFist){
				if(gotoFist){
					amtable.page = "1";
				}
				this.loadPage(this);
			},
			nextPage: function(curPage){
				$("#" + amtable.tid).find(".progress").removeClass("hide");
				this.page = curPage;
				this.load(this);
			},
			loadCallback: function(content){
				
				if(parseInt(amtable.page) > content.pages){
					amtable.page = eval(amtable.page + "-1") + "";
					amtable.loadPage(amtable);
					return;
				}
				
				amtable.pages = content.pages;
				$("#" + this.tid).find("a[tag=page-count]").html(content.pages);
				$("#" + this.tid).find("a[tag=total-count]").html(content.total);
				$("#" + this.tid).find("a[tag=cur-page-count]").html(content.current);
				
				if(content.current <= 1){
					$("#" + this.tid).find("li[tag=btn-left]").removeClass("waves-effect").addClass("disabled");
				}else{
					$("#" + this.tid).find("li[tag=btn-left]").removeClass("disabled").addClass("waves-effect");
				}
				
				if(content.current == content.pages){
					$("#" + this.tid).find("li[tag=btn-right]").removeClass("waves-effect").addClass("disabled");
				}else{
					$("#" + this.tid).find("li[tag=btn-right]").removeClass("disabled").addClass("waves-effect");
				}
				
//				$("#" + this.tid).find("span[tag=page-btn-penal]").html("");
//				for(var i=letfpagesize; i< (letfpagesize + this.pageSize); i++){
//					if(i == content.current){
//						$("#" + this.tid).find("span[tag=page-btn-penal]").append('<li class="active"><a href="#!">'+ i +'</a></li>');
//					}else{
//						$("#" + this.tid).find("span[tag=page-btn-penal]").append('<li class="waves-effect"><a tag="page-btn" href="#!">'+ i +'</a></li>');
//					}
//				}
				
				$("#" + this.tid).find("tbody").html("");
				$("#" + this.tid).find(".progress").addClass("hide");
				var column = $("#" + this.tid).find("th[column]");
				for(var index in content.records){
					var html = $("#" + this.tid).find("div[tag=tr]").html();
					html = html.replace(new RegExp("{id}","gm"), content.records[index].id);
					html = html.replace(new RegExp("trr","gm"), "tr");
					html = html.replace(new RegExp("tdd","gm"), "td");
					
					column.each(function(one,two){
						var tempname = $(two).attr("column");
						html = html.replace(new RegExp("{"+ tempname +"}","gm"), content.records[index][tempname]);
					});
					
					$("#" + this.tid).find("tbody").append(html);
				}
				
				$("#" + id).find("a[tag=page-btn]").click(function(event){
					amtable.nextPage($(event.target).html());
		        });
			},
			loadPage: function(curObj){
				if(curObj.load == null){
					return;
				}
				
				$("#" + curObj.tid).find(".progress").removeClass("hide");
				curObj.load(curObj);
			}
		});
		
		return amtable;
	}
}
