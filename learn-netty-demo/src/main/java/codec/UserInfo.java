package codec;

import java.io.Serializable;

/**
 * @description
 * @author: ts
 * @create:2021-04-09 09:41
 */
public class UserInfo implements Serializable {
	
	private  Integer id;
	private String name;
	private Integer age;
	private String gender;
	private String address;
	
	public UserInfo() {
	}
	
	public UserInfo(Integer id, String name, Integer age, String gender, String address) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.gender = gender;
		this.address = address;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getAge() {
		return age;
	}
	
	public void setAge(Integer age) {
		this.age = age;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return "UserInfo{" +
				"id=" + id +
				", name='" + name + '\'' +
				", age=" + age +
				", gender='" + gender + '\'' +
				", address='" + address + '\'' +
				'}';
	}
}
