var AdminVo={
		/**
		 * 类型'0:普通通知 1:内部通知 2：班级通知',
		 * Type为2时,必须传班级UUID
		 * @param t
		 * @returns
		 */
	map:{
		enable_0:"禁用",
		enable_1:"启用",
		
		type_0:"云平台",
		type_1:"幼儿园",
		type_2:"培训"
	},
	get:function(t){
		return this.map[t];
	},
	type:function(t){
		return this.map["type_"+t];
	}
}