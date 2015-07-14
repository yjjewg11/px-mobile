package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.news.form.UserLoginForm;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.SmsService;
import com.company.news.service.UserinfoService;
import com.company.news.vo.ResponseMessage;

/**
 * 短信验证接口
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/sms")
public class SmsController extends AbstractRESTController{
  @Autowired
  private SmsService smsService;
  //@RequestParam("md5") String md5,@PathVariable("uuid") String uuid
  @RequestMapping(value = "/sendCode", method = RequestMethod.GET)
  public String sendCode(ModelMap model, HttpServletRequest request,@RequestParam("tel") String tel,@RequestParam("type") Integer type) {

      // 清除原输入参数MAP
      model.clear();
      smsService.sendCode(model, request,tel,type);
      return "";
  }
}
