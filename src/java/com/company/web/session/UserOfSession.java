package com.company.web.session;

import com.company.news.interfaces.SessionUserInfoInterface;

public class UserOfSession implements SessionUserInfoInterface{
	private String uuid;
	private String name;
	private String img;
	private String loginname;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getLoginname() {
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

}
