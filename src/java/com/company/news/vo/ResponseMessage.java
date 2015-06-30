package com.company.news.vo;

import com.company.news.rest.RestConstants;

public class ResponseMessage implements java.io.Serializable{

  private String status;
  public ResponseMessage() {//默认初始化为失败状态
    status=RestConstants.Return_ResponseMessage_failed;
  }
  private String message;
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
}
