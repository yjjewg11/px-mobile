/**
 * 公布接口,给手机调用
 * 调用选择头像图片,裁剪和调整方向,回调方法.
 * G_jsCallBack.selectHeadPic_callback(base64);
 * 
 * 选择上图图片,回调方法,只压缩和调整方向.
 * G_jsCallBack.ajax_uploadByphone(base64);
 */
var G_jsCallBack={
	
		/**
		 * 调用选择头像图片,裁剪和调整方向,回调方法.
		 * @param base64
		 */
	selectPic_callback:function(base64){
		w_img_upload_nocut.ajax_uploadByphone(results.base64);
	},
		/**
		 * 选择上图图片,回调方法,只压缩和调整方向.
		 * @param base64
		 */
	selectHeadPic_callback:function(base64){
		w_uploadImg.base64.ajax_uploadByphone(base64);
	}
}

/**
 * 调用手机方法
 * 调用选择头像图片.剪切图片为198*198,并可以调整方向
 * G_CallPhoneFN.selectHeadPic();
 * 调用上传图片,要求不剪切,只压缩在800k以内,并可以调整方向
 * G_CallPhoneFN.selectImgPic();
 * 登录后,将sessionid传给手机
 * G_CallPhoneFN.jsessionToPhone(JSESSIONID);
 
window.JavaScriptCall={
		selectImgPic:function(){alert("ddd");},
		jsessionToPhone:function(){}
};*/
var G_CallPhoneFN={
	
		/**
		 * 调用选择上传图片的回调
		 * @returns {Boolean}
		 */
		selectImgPic:function(){
			try{
				if(window.JavaScriptCall){
					JavaScriptCall.selectImgPic();
					return true;
				}
			}catch(e){
				  console.log('Exception:JavaScriptCall.selectImgPic()=', e.message);
			}
			console.log('window.selectImgPic==false');
			return false;
		},
	/**
	 * 调用选择头像图片
	 * @returns {Boolean}
	 */
	selectHeadPic:function(){
		try{
			if(window.JavaScriptCall){
				JavaScriptCall.selectHeadPic();
				return true;
			}
		}catch(e){
			  console.log('Exception:JavaScriptCall.selectHeadPic()=', e.message);
		}
		console.log('window.JavaScriptCall==false');
		return false;
	},
	/**
	 * JSESSIONID=C483CC4E6FECB6F6267591D624704A86
	 */
	jsessionToPhone:function(sessionid){
		try{
			if(window.JavaScriptCall){
				JavaScriptCall.jsessionToPhone(sessionid);
				return true;
			}
		}catch(e){
			  console.log('G_CallPhoneFN：', e.message);
		}
		console.log('G_CallPhoneFN：', "false");
		return false;
	}
}