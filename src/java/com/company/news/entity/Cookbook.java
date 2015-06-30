package com.company.news.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="px_cookbook") 
public class Cookbook extends IdEntity{
	
	  @Column
	  private String name;//品牌名称
	  @Column
	  private String img;//公司全称
	  @Column
	  private Integer type;//类型：1:主食 2:汤&粥 3:炒菜 4:水果 5:奶制品&其他
	  @Column
	  private String groupuuid;//机构ID
	public String getGroupuuid() {
		return groupuuid;
	}
	public void setGroupuuid(String groupuuid) {
		this.groupuuid = groupuuid;
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}


}
