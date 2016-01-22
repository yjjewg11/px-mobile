package com.company.news.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.company.news.service.FPReplyService;

@Controller
@RequestMapping(value = "/fPreply")
public class FPReplyController extends AbstractRESTController {
	
	@Autowired
	private FPReplyService fPReplyService;



	public FPReplyService getfPReplyService() {
		return fPReplyService;
	}


	public void setfPReplyService(FPReplyService fPReplyService) {
		this.fPReplyService = fPReplyService;
		this.setfPReplyService(fPReplyService);
	}
	

}
