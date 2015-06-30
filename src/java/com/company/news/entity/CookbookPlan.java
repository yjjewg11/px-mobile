package com.company.news.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="px_cookbookplan") 
public class CookbookPlan extends IdEntity{
	

	  @Column
	  private String groupuuid;//机构ID
	  @Column
	  private String time_1;//早餐
	  @Column
	  private String time_2;//早餐
	  @Column
	  private String time_3;//早餐
	  @Column
	  private String time_4;//早餐
	  @Column
	  private String time_5;//早餐
	  @Column
	  private Date plandate;
	  
	  


	public Date getPlandate() {
		return plandate;
	}
	public void setPlandate(Date plandate) {
		this.plandate = plandate;
	}
	public String getTime_1() {
		return time_1;
	}
	public void setTime_1(String time_1) {
		this.time_1 = time_1;
	}
	public String getTime_2() {
		return time_2;
	}
	public void setTime_2(String time_2) {
		this.time_2 = time_2;
	}
	public String getTime_3() {
		return time_3;
	}
	public void setTime_3(String time_3) {
		this.time_3 = time_3;
	}
	public String getTime_4() {
		return time_4;
	}
	public void setTime_4(String time_4) {
		this.time_4 = time_4;
	}
	public String getTime_5() {
		return time_5;
	}
	public void setTime_5(String time_5) {
		this.time_5 = time_5;
	}
	public String getGroupuuid() {
		return groupuuid;
	}
	public void setGroupuuid(String groupuuid) {
		this.groupuuid = groupuuid;
	}


}
