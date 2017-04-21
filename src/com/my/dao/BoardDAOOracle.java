package com.my.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.my.sql.MyConnection;
import com.my.vo.RepBoard;

@Component
public class BoardDAOOracle{
	@Autowired
	private DataSource dataSource;
	
	public List<RepBoard> selectAll(){
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<RepBoard> list = new ArrayList<>();
		String selectALLSQL = "SELECT level, rownum, a.*" 
//								+" FROM (SELECT * from repboard ORDER BY no DESC) a"
								+" FROM repboard a"
								+" START WITH parent_no = 0"
								+" CONNECT BY prior no = parent_no"
								+" ORDER siblings by no desc";
				
		try {
			con =  dataSource.getConnection();
			pstmt = con.prepareStatement(selectALLSQL);
			rs = pstmt.executeQuery();
			while(rs.next()){
				int level = rs.getInt("level");
				int no = rs.getInt("no");
				int parentNo = rs.getInt("parent_no");
				String subject = rs.getString("SUBJECT");
				String content = rs.getString("CONTENT");
				String password = rs.getString("PASSWORD");
				list.add( new RepBoard(level,no,parentNo,subject,content,password) );		
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{	
			MyConnection.close(con,pstmt,rs);
		}
	}
	
	public List<RepBoard> selectByNo(int num){
		Connection con = null;
		PreparedStatement pstmt = null;
		String findByNumSQL = 
					"SELECT level, rownum, a.*"
					+ " FROM repboard a"
					+ " WHERE level <= 2"
					+ " START WITH no = ?"
					+ " CONNECT BY prior no = parent_no"
					+ " ORDER siblings BY no DESC";
		ResultSet rs = null;
		ArrayList<RepBoard> list = new ArrayList<>();
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(findByNumSQL);
			pstmt.setInt(1,num);
			rs = pstmt.executeQuery();

			while(rs.next()){
				int level = rs.getInt("level");
				int no = rs.getInt("no");
				int parentNo = rs.getInt("parent_no");
				String subject = rs.getString("SUBJECT");
				String content = rs.getString("CONTENT");
				String password = rs.getString("PASSWORD");
				list.add( new RepBoard(level,no,parentNo,subject,content,password) );		
		}
				return list;
		}catch (Exception e) {	
			e.printStackTrace();
			return null;
		}finally{	
			
			MyConnection.close(con,pstmt,rs);
		}
	}
	
	public List<RepBoard> selectByCont(String cont){
		Connection con = null;
		PreparedStatement pstmt = null;
		String findByContSQL = 
				"SELECT b.* FROM "+
				"(SELECT level, rownum, a.* FROM repboard a "+
				"START WITH parent_no = 0 "+
				"CONNECT BY prior no = parent_no "+
				"ORDER siblings BY no DESC)b "+
				"WHERE subject LIKE '%"+cont+"%' ";
		
		ResultSet rs = null;
		ArrayList<RepBoard> list = new ArrayList<>();
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(findByContSQL);
			rs = pstmt.executeQuery();

			while(rs.next()){
				int level = rs.getInt("level");
				int no = rs.getInt("no");
				int parentNo = rs.getInt("parent_no");
				String subject = rs.getString("SUBJECT");
				String content = rs.getString("CONTENT");
				String password = rs.getString("PASSWORD");
				list.add( new RepBoard(level,no,parentNo,subject,content,password) );		
		}
				return list;
		}catch (Exception e) {	
			e.printStackTrace();
			return null;
		}finally{	
			
			MyConnection.close(con,pstmt,rs);
		}
	}
	
	public void insert(RepBoard board){
		Connection con = null;
		PreparedStatement pstmt = null;
		String insertSQL = "INSERT INTO repboard (no, parent_no, subject, content, password)"
						+ " VALUES (repboard_seq.NEXTVAL,?,?,?,?)";	
	
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(insertSQL);
	
			pstmt.setInt(1, board.getParent_no());
			pstmt.setString(2, board.getSubject());
			pstmt.setString(3, board.getContent());
			pstmt.setString(4, board.getPassword());
			pstmt.executeUpdate();
			
		} catch (Exception e) {
		e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	public void update(RepBoard board){
		Connection con = null;
		PreparedStatement pstmt = null;
		int no = board.getNo(); 
		String subject = board.getSubject(); 
		String content = board.getContent(); 
		String password = board.getPassword();
		
		try {
			con = dataSource.getConnection();
			String updateSQL = "";
			if(!subject.equals("")){			
				updateSQL = 
					"UPDATE repboard SET subject = ? WHERE no = ?";	
					pstmt = con.prepareStatement(updateSQL);
					pstmt.setString(1,subject);
					pstmt.setInt(2,no);
					pstmt.executeUpdate();		
			}
			if(!content.equals("")){			
				updateSQL = 
					"UPDATE repboard SET content = ? WHERE no = ?";	
					pstmt = con.prepareStatement(updateSQL);
					pstmt.setString(1,content);
					pstmt.setInt(2,no);
					pstmt.executeUpdate();		
			}
			if(!password.equals("")){			
				updateSQL = 
					"UPDATE repboard SET password = ? WHERE no = ?";	
					pstmt = con.prepareStatement(updateSQL);
					pstmt.setString(1,password);
					pstmt.setInt(2,no);
					pstmt.executeUpdate();		
			}								
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			MyConnection.close(con,pstmt);		
		}
	}
	
	public void delete(int no){
		Connection con = null;
		PreparedStatement pstmt = null;
		String deleteSQL = "DELETE FROM repboard WHERE no = ?";	
	
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(deleteSQL);

			pstmt.setInt(1, no);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
		e.printStackTrace();
		}
	}
	
	public boolean chkPassword(int no, String pwd){
		Connection con = null;
		PreparedStatement pstmt = null;
		String selectPasswordSQL = 
					"SELECT * FROM repboard WHERE no= ? AND password =?";
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(selectPasswordSQL);
			pstmt.setInt(1,no);
			pstmt.setString(2,pwd);
			rs = pstmt.executeQuery();			
			return rs.next();
		}catch (Exception e) {
			return false;
		}finally{
			MyConnection.close(con,pstmt,rs);			
		}
		

	}
	
	public List<RepBoard> selectParents(){
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<RepBoard> list = new ArrayList<>();
		String selectALLSQL = "SELECT * FROM repboard WHERE parent_no = 0 ORDER BY no DESC";
				
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(selectALLSQL);
			rs = pstmt.executeQuery();
			while(rs.next()){
				int no = rs.getInt("no");
				int parentNo = rs.getInt("parent_no");
				String subject = rs.getString("SUBJECT");
				String content = rs.getString("CONTENT");
				String password = rs.getString("PASSWORD");
				list.add( new RepBoard(no,parentNo,subject,content,password) );		
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{	
			MyConnection.close(con,pstmt,rs);
		}
	}

}
