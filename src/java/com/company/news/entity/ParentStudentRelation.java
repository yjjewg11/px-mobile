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
@Table(name = "px_parentstudentrelation")
public class ParentStudentRelation extends IdEntity {

	@Column
	private String parentuuid;//班级标示
	@Column
	private String studentuuid;// 昵称
	@Column
	private Integer type;// '1:妈妈' 2:爸爸 3：爷爷 4：奶奶 5：外公 6：外婆

	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getParentuuid() {
		return parentuuid;
	}
	public void setParentuuid(String parentuuid) {
		this.parentuuid = parentuuid;
	}
	public String getStudentuuid() {
		return studentuuid;
	}
	public void setStudentuuid(String studentuuid) {
		this.studentuuid = studentuuid;
	}

	



}
