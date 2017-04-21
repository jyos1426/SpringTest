package com.my.vo;

import java.io.FileWriter;
import java.io.IOException;

public class FilePrinter implements Printable {

	private String fileName = "a.txt";
	public FilePrinter(String fileName){
		this.fileName=fileName;
	}
	@Override
	public void print(String msg) {
		//msg값을 a.txt에 출력
		FileWriter fw = null;
		try{
			fw = new FileWriter(fileName);
			fw.write(msg);
		}catch(Exception e){
			
		}finally{
			if(fw != null){
				try{
					fw.close();
				}catch(IOException e){
					
				}
			}
		}
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
