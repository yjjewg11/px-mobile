package com.company.news.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="px_basedatalist") 
public class BaseDataList extends IdEntity{
	

	  @Column
	  private String typeuuid;//
	  @Column
	  private String datavalue;//
	  @Column
	  private Integer datakey;//
	  @Column
	  private String description;//
	  @Column
	  private Integer enable;//
	  
	public String getTypeuuid() {
		return typeuuid;
	}
	public void setTypeuuid(String typeuuid) {
		this.typeuuid = typeuuid;
	}

	public String getDatavalue() {
		return datavalue;
	}
	public void setDatavalue(String datavalue) {
		this.datavalue = datavalue;
	}
	public Integer getDatakey() {
		return datakey;
	}
	public void setDatakey(Integer datakey) {
		this.datakey = datakey;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getEnable() {
		return enable;
	}
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
	  


}
