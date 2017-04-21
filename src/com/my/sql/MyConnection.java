package com.my.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyConnection {	
	public static void close(Connection con){
		if(con!=null){	
			try {			
					con.close();
				}
			 catch (SQLException e1) {
				e1.printStackTrace();
			 }
		}
	}	
	public static void close(Connection con,Statement stmt){
		if(stmt !=null){
			try {
					stmt.close();
				}
			 catch (SQLException e) {
				e.printStackTrace();
			}		
		}	
		close(con);
	}		
	public static void close(Connection con,Statement stmt, ResultSet rs){
		if(rs!=null){	
			try {			
					rs.close();
				}
			 catch (SQLException e1) {
				e1.printStackTrace();
			 }
		}
		close(con,stmt);
	}	
}
