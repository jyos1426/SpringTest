package com.my.vo;

@SuppressWarnings("serial")
public class Customer extends Person{
	private String id;
	private transient String password; 
	//ransient 직렬화 대상에서 제외
//	private String name;
	
	
	//Ctrl + shift + S
	public Customer() {
		// TODO Auto-generated constructor stub
	}
	
	public Customer(String id, String password, String name) {
		this.id = id;
		this.password = password;
//		this.name = name;	//부모로부터 상속은 받았으나 private라 접근이 불가한 것
		this.setName(name);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return id + "," + password + "," + this.getName();
	}
	


}
