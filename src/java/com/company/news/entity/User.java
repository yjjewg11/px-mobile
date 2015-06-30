package com.company.news.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 用户基本信息表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "px_user")
public class User extends IdEntity {

	@Column
	private String loginname;// 登录名。手机号码或邮箱
	@Column
	private String name;// 昵称
	@Column
	private String password;// 密码，md5加密。（UTF-8）
	@Column
	private String tel;// 电话号码。
	@Column
	private Integer tel_verify;// 电话号码，验证。默认0，0:没验证。1:验证，2：提交验证，3.验证失败
	@Column
	private Integer sex;// 0:男,1:女
	@Column
	private Integer disable;// 是否被管理员封号。0：不封。1：封号，不允许登录。
	@Column
	private Timestamp login_time;// 最后一次登录时间。
	@Column
	private Timestamp create_time;
	@Column
	private String office;// 职位。
	@Column
	private Timestamp last_login_time;// 上一次登录时间
	public Timestamp getLast_login_time() {
		return last_login_time;
	}

	public void setLast_login_time(Timestamp last_login_time) {
		this.last_login_time = last_login_time;
	}

	@Column
	private Integer type;// 用户类型。0:普通用户(默认)；1:组织管理员

	@Column
	private String email;// email

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}



	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Integer getTel_verify() {
		return tel_verify;
	}

	public void setTel_verify(Integer tel_verify) {
		this.tel_verify = tel_verify;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getDisable() {
		return disable;
	}

	public void setDisable(Integer disable) {
		this.disable = disable;
	}



	public Timestamp getLogin_time() {
		return login_time;
	}

	public void setLogin_time(Timestamp loginTime) {
		login_time = loginTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp createTime) {
		create_time = createTime;
	}

}
