package com.my.vo;

public class DotPrinter implements Printable {
	@Override
	public void print(String msg) {
		System.out.println("......");
		System.out.println(msg);
		System.out.println("......");
	}
}
