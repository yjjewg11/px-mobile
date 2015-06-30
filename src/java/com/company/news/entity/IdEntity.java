package com.company.news.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
/**
 * 
 * @ClassName: IdEntity
 * @Description: 基础实体类  注：此类注解在方法上
 * @author wangchuan
 * @date 2013-3-11 上午10:01:31
 *
 */
@MappedSuperclass
public class IdEntity implements Serializable {
	private static final long serialVersionUID = 6030326789124757879L;
	/**
	 * 主键
	 */
	private String uuid;
	
	@Id
	@Column(name = "uuid", length = 36)
	//@GeneratedValue(generator = "system-uuid")
	//@GenericGenerator(name = "system-uuid", strategy = "uuid")	
	@GeneratedValue(generator = "uuid")   //指定生成器名称  
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")  //生成器名称
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	
	
}
