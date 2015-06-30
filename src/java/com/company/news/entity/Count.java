package com.company.news.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "px_count")
public class Count extends IdEntity {

	@Column
	private String ext_uuid;// 权限名
	@Column
	private Integer type;// 权限类型
	@Column
	private Integer count;// 权限类型
	@Column
	private Timestamp update_time;// 权限类型	
	
	public String getExt_uuid() {
		return ext_uuid;
	}

	public void setExt_uuid(String ext_uuid) {
		this.ext_uuid = ext_uuid;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Timestamp getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Timestamp update_time) {
		this.update_time = update_time;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}


}
