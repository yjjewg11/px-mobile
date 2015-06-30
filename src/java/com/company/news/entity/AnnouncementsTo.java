package com.company.news.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "px_announcementsto")
public class AnnouncementsTo extends IdEntity {

	@Column
	private String classuuid;// 班级UUID
	@Column
	private String announcementsuuid;// 通知UUID

	public String getClassuuid() {
		return classuuid;
	}

	public void setClassuuid(String classuuid) {
		this.classuuid = classuuid;
	}

	public String getAnnouncementsuuid() {
		return announcementsuuid;
	}

	public void setAnnouncementsuuid(String announcementsuuid) {
		this.announcementsuuid = announcementsuuid;
	}

}
