package com.company.news.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.weixin4j.WeixinException;

import com.company.common.HttpRequestUtils;
import com.company.news.ProjectProperties;
import com.company.news.cache.CacheConstants;
import com.company.news.cache.PxRedisCache;
import com.company.news.entity.EZCameraInfo;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.RestConstants;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;
import com.hikvision.HikvisionClient;
import com.hikvision.HikvisionConstants;
import com.hikvision.PublicControllerTest;
import com.hikvision.entity.AccessToken;
import com.hikvision.entity.AccessTokenResult;
import com.hikvision.entity.AccessTokenResultRespone;
import com.hikvision.entity.CameraInfo;
import com.hikvision.entity.CameraInfoResult;
import com.hikvision.entity.CameraInfoResultRespone;
import com.hikvision.entity.Page;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public  class EZCameraService extends AbstractService {
	//String url="https://graph.qq.com/user/get_simple_userinfo?oauth_consumer_key=100330589&access_token=BF796FA297CC548136E8819D9BC1EA92&openid=0020D2823810BC79EAA0E228E14F1688&format=json";
	
	static public String hikvision_key= ProjectProperties.getProperty(
				"hikvision_key", "c279ded87d3f4fdca7658f95fb5f1d9e");
	
	static public String hikvision_secret= ProjectProperties.getProperty(
			"hikvision_secret", "b097e53bb9627e7e32b7a8001c701151");
	
	
	
	
	public HikvisionClient hikvisionClient=new HikvisionClient();
	
	
	public boolean update_cameraListToDB(ModelMap model, HttpServletRequest request,ResponseMessage responseMessage) throws WeixinException,Exception {
		//oauth_consumer_key=100330589&access_token=BF796FA297CC548136E8819D9BC1EA92&openid=0020D2823810BC79EAA0E228E14F1688&format=json
		 String dateString=TimeUtils.getCurrentTime(TimeUtils.YYYY_MM_DD_FORMAT);
			
		
		AccessTokenResultRespone accessTokenResultRespone =hikvisionClient.getAccessToken() ;
		AccessToken accessToken=(AccessToken)PxRedisCache.getObject(CacheConstants.Key_YS7_accessToken,AccessToken.class);
		
		if(accessToken==null){//存在
			if(!dateString.equals(accessToken.getUpdateDate())){//当天有效
				AccessTokenResult accessTokenResult=accessTokenResultRespone.getResult();

				if(!HikvisionConstants.Result_Code_Succes.equals(accessTokenResult.getCode())){
					responseMessage.setMessage(accessTokenResult.getMsg());
					return false;
				}
				accessToken=accessTokenResult.getData();
			}
		}
		
	
		CameraInfoResultRespone cameraInfoResultRespone= hikvisionClient.cameraList(accessToken.getAccessToken());
		 
		CameraInfoResult cameraInfoResult= cameraInfoResultRespone.getResult();
		if(!HikvisionConstants.Result_Code_Succes.equals(cameraInfoResult.getCode())){
			responseMessage.setMessage(cameraInfoResult.getMsg());
			return false;
		}
		Page page=cameraInfoResult.getPage();
		if(page.getTotal()<1){
			responseMessage.setMessage("无数据");
			return false;
		}
		List<CameraInfo> data=cameraInfoResult.getData();
		int create_count=0;
		int update_count=0;
		for(CameraInfo cameraInfo:data){
			EZCameraInfo eZCameraInfo=(EZCameraInfo)this.nSimpleHibernateDao.getObjectByAttribute(EZCameraInfo.class, "cameraId", cameraInfo.getCameraId());
			if(eZCameraInfo==null){
				eZCameraInfo=new EZCameraInfo();
				BeanUtils.copyProperties(eZCameraInfo, data);
				eZCameraInfo.setClass_uuid("-1");
				eZCameraInfo.setGroup_uuid("-1");
				eZCameraInfo.setCreate_time(TimeUtils.getCurrentTimestamp());
				create_count++;
			}else{
				BeanUtils.copyProperties(eZCameraInfo, data);
				update_count++;
			}
			this.nSimpleHibernateDao.save(eZCameraInfo);
		}
		responseMessage.setMessage("同步成功,总数据:"+page.getTotal()+"!新加数据:"+create_count+",更新数据:"+update_count);
		return true;
		
	}
	
	public boolean getAccessToken(ModelMap model, HttpServletRequest request,ResponseMessage responseMessage) throws WeixinException,Exception {
		//oauth_consumer_key=100330589&access_token=BF796FA297CC548136E8819D9BC1EA92&openid=0020D2823810BC79EAA0E228E14F1688&format=json
		 String dateString=TimeUtils.getCurrentTime(TimeUtils.YYYY_MM_DD_FORMAT);
			
		
	
		AccessToken accessToken=(AccessToken)PxRedisCache.getObject(CacheConstants.Key_YS7_accessToken,AccessToken.class);
		
		if(accessToken!=null){//存在
			if(dateString.equals(accessToken.getUpdateDate())){//当天有效
				model.put(RestConstants.Return_YS7_accessToken,accessToken.getAccessToken());
				model.put(RestConstants.Return_YS7_AppKey, PublicControllerTest.key);
				
				return true;
			}
		}
		
		AccessTokenResultRespone accessTokenResultRespone =hikvisionClient.getAccessToken() ;
		AccessTokenResult accessTokenResult=accessTokenResultRespone.getResult();
		if(!HikvisionConstants.Result_Code_Succes.equals(accessTokenResult.getCode())){
			responseMessage.setMessage(accessTokenResult.getMsg());
			return false;
		}
		  accessToken= accessTokenResult.getData();
		 accessToken.setUpdateDate(dateString);
		PxRedisCache.setObject(CacheConstants.Key_YS7_accessToken,accessToken );
		
		model.put(RestConstants.Return_YS7_accessToken,accessToken.getAccessToken());
		model.put(RestConstants.Return_YS7_AppKey, PublicControllerTest.key);
		
		return true;
	}
	
	

	String Selectsql=" SELECT t1.camera_id as cameraId,t1.camera_name as cameraName,t1.channel_no as channelNo,t1.device_serial as deviceSerial,t1.is_encrypt as isEncrypt,t1.is_shared as isShared,t1.pic_url as picUrl,t1.is_online as isOnline,t1.device_id as deviceId ,t1.camera_no as cameraNo  ";
	String SqlFrom=" FROM ez_camera_info t1 ";

	/**
	 * 查询相册
	 * @param user
	 * @param pData
	 * @param model
	 * @return
	 */
	public PageQueryResult getCameraList(SessionUserInfoInterface user,String group_uuid,String class_uuid,
			PaginationData pData, ModelMap model) {
		String selectsql=Selectsql;
		String sqlFrom=SqlFrom;
		sqlFrom += " where   t1.group_uuid in("+DBUtil.stringsToWhereInValue(group_uuid)+")";
		sqlFrom += " and   t1.class_uuid in("+DBUtil.stringsToWhereInValue(class_uuid)+")";
		
		String sql=sqlFrom;
		pData.setPageSize(50);
		
		  sql += " order by t1.group_uuid,CONVERT( t1.camera_name USING gbk)";
		 
		Query  query =this.nSimpleHibernateDao.createSqlQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		String countsql="select count(*) "+sql;
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,countsql, pData);

		return pageQueryResult;
	}
	
	
	
	
	public static void main(String[] s)throws Exception{
//		HttpClient dd=new HttpClient();
//		dd.setOpenID("0020D2823810BC79EAA0E228E14F1688");
//		dd.setToken("BF796FA297CC548136E8819D9BC1EA92");
//		PostParameter[] par=new PostParameter[4];
//		//oauth_consumer_key=100330589&access_token=BF796FA297CC548136E8819D9BC1EA92&openid=0020D2823810BC79EAA0E228E14F1688&format=json
////		par[0]=new PostParameter("grant_type","authorization_code");
////		par[0]=new PostParameter("client_id",appid);
////		par[0]=new PostParameter("client_secret",QQ_appkey);
////		par[0]=new PostParameter("code",code);
////		par[0]=new PostParameter("grant_type","authorization_code");
//		
//		par[0]=new PostParameter("oauth_consumer_key","100330589");
//		par[1]=new PostParameter("access_token","BF796FA297CC548136E8819D9BC1EA92");
//		par[2]=new PostParameter("openid","0020D2823810BC79EAA0E228E14F1688");
//		par[3]=new PostParameter("format","json");
//		Response response=dd.get("https://graph.qq.com/user/get_simple_userinfo", par);
//		String responseStr=response.asString();
//		
//		System.out.println(responseStr);
		
		EZCameraService dd=new EZCameraService();
		String url="https://graph.qq.com/user/get_simple_userinfo?oauth_consumer_key=100330589&access_token=BF796FA297CC548136E8819D9BC1EA92&openid=0020D2823810BC79EAA0E228E14F1688&format=json";
		Object responseStr=HttpRequestUtils.httpGet(url);
		System.out.print(responseStr);
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}


}
