/**
 * Vo.getTypeList("s");
 */
var Vo={
	md5:null,
		/**
		 * 类型'0:普通通知 1:内部通知 2：班级通知',
		 * Type为2时,必须传班级UUID
		 * @param t
		 * @returns
		 */
	map:{
		ann_type_0:"全校公告",
		ann_type_1:"老师通知",
		ann_type_2:"班级通知",
		ann_type_3:"招生计划",
		ann_type_4:"文章分享",
		type_0:"云平台",
		type_1:"幼儿园",
		type_2:"培训"
	},
	//TypeList[tmp.typeuuid].push({key:tmp.datakey,val:tmp.datavalue});
	
	isInit:false,
	init:function(){
		if(Vo.isInit)return;
		Vo_ajax_MyClass_toVo();
	},
	/**
	 * 用于生成select列表数据
	 * @param s
	 * @returns
	 */
	getTypeList:function(s){
		Vo.init();
		var tmp=Vo.map.TypeList[s];
		if(!tmp)tmp=[];
		return tmp;
	},
	get:function(t){
		Vo.init();
		return this.map[t];
	},
	type:function(t){
		Vo.init();
		return this.map["group_type_"+t];
	},
	announce_type:function(t){
		Vo.init();
		return Vo.map["KD_ann_type_"+t];
	}
};

function Vo_ajax_MyClass_toVo() {
	if(!Vo.md5){
		Vo.md5=Store.getVo_md5();
	}
	$.AMUI.progress.start();
	var url = hostUrl + "rest/basedatalist/getAllList.json?md5="+Vo.md5;
	$.ajax({
		type : "GET",
		url : url,
		async: false,
		dataType : "json",
		success : function(data) {
			$.AMUI.progress.done();
			if (data.ResMsg.status == "success") {
				if(data.md5==Vo.md5){
					Vo.map=Store.getVo_map();
					return;
				}
				Vo.md5=data.md5;
				Vo.map={};
				
				var TypeList={};
				if(!data.list)return;
				for(var i=0;i<data.list.length;i++){
					var tmp=data.list[i];
					Vo.map[tmp.typeuuid+"_"+tmp.datakey]=tmp.datavalue;
					if(!TypeList[tmp.typeuuid])TypeList[tmp.typeuuid]=[];
					TypeList[tmp.typeuuid].push({key:tmp.datakey,val:tmp.datavalue});
				}
				Vo.map.TypeList=TypeList;
				Store.setVo_map(Vo.map);
				
				Vo.isInit=true;
			} else {
				alert(data.ResMsg.message);
			}
		},
		error : function( obj, textStatus, errorThrown ){
			$.AMUI.progress.done();
			alert(url+","+textStatus+"="+errorThrown);
			 console.log(url+',error：', obj);
			 console.log(url+',error：', textStatus);
			 console.log(url+',error：', errorThrown);
		}
	});
};

