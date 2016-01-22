package com.company.news.service;

import org.springframework.stereotype.Service;

import com.company.news.entity.FPReply;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class FPReplyService extends AbstractReplyService {
	public static final int USER_type_default = 1;// 0:老师

	@Override
	public Class getEntityClass() {
		return FPReply.class;
	}

	

}
