package com.company.news.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="px_rolerightrelation") 
public class RoleRightRelation extends IdEntity{
	

	  @Column
	  private String roleuuid;//角色ID
	  @Column
	  private String rightuuid;//权限ID
	  @Column
	  private String rightname;//权限名
	public String getRoleuuid() {
		return roleuuid;
	}
	public void setRoleuuid(String roleuuid) {
		this.roleuuid = roleuuid;
	}
	public String getRightuuid() {
		return rightuuid;
	}
	public void setRightuuid(String rightuuid) {
		this.rightuuid = rightuuid;
	}
	public String getRightname() {
		return rightname;
	}
	public void setRightname(String rightname) {
		this.rightname = rightname;
	}
	  



}
