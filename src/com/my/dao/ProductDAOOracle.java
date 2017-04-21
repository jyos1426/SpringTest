package com.my.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.my.sql.MyConnection;
import com.my.vo.Product;
//@Component("productDAO")
@Repository("productDAO")
public class ProductDAOOracle implements ProductDAO {
//	@Autowired
	@Resource(name="dataSource1")
	private DataSource dataSource;
	
	@Override
	public void insert(Product p) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		// 2. DB와의 연결 - 매번 메소드 호출시
		con = dataSource.getConnection();
		
		// 3. SQL 구문을 송신
		String insertSQL = "INSERT INTO product VALUES(?,?,?)";
		try{
			pstmt = con.prepareStatement(insertSQL);
			pstmt.setString(1, p.getNo());
			pstmt.setString(2, p.getName());
			pstmt.setInt(3, p.getPrice());		
			pstmt.executeUpdate();
		}catch(SQLException e){
			int errorCode = e.getErrorCode();
			if(errorCode == 1){		//오라클에서 PK중복인 경우 오류 1번을 의미
				throw new Exception("이미 존재하는 제품번호입니다.");
			}else{
				throw e;
			}
		}			
		// 4. 송신결과 수신(생략)		
		// 5. 연결닫기				
		MyConnection.close(con,pstmt);
	}

	@Override
	public List<Product> selectAll() throws Exception {
		Connection con = null;
		Statement stmt = null;		
		ResultSet rs = null;
		
		con = dataSource.getConnection();
		String selectallSQL = "SELECT * FROM product ORDER BY prod_no";
		
		stmt = con.createStatement();
		rs = stmt.executeQuery(selectallSQL);		
		ArrayList<Product> list = new ArrayList<>();
	
		while( rs.next() ){		
			list.add(new Product(rs.getString("prod_no"),
									rs.getString("prod_name"),
									rs.getInt("prod_price")
									));
		}
		MyConnection.close(con,stmt,rs);
		return list;
	}

	@Override
	public Product selectByNo(String no) throws Exception {
		Connection con = null;
		Statement stmt = null;		
		ResultSet rs = null;
		
		con = dataSource.getConnection();	
		String selectIDSQL = "SELECT * FROM product WHERE prod_no ='"+no+"'";
		stmt = con.createStatement();
		rs = stmt.executeQuery(selectIDSQL);	
		
		Product p = null;
		if (rs.next()){
			p = new Product(rs.getString("prod_no"),rs.getString("prod_name"),rs.getInt("prod_price"));
		}		
		MyConnection.close(con,stmt,rs);
		
		return p;
	}

	@Override
	public List<Product> selectByName(String word) throws Exception {
		Connection con = null;
		Statement stmt = null;		
		ResultSet rs = null;
		
		con = dataSource.getConnection();	
		String selectNameSQL = "SELECT * FROM product WHERE prod_name LIKE '%"+word+"%'";
		stmt = con.createStatement();
		rs = stmt.executeQuery(selectNameSQL);	
		
		ArrayList<Product> list = new ArrayList<>();
		while( rs.next() ){		
			list.add(new Product(rs.getString("prod_no"),
									rs.getString("prod_name"),
									rs.getInt("prod_price")
									));
		}
		MyConnection.close(con,stmt,rs);
		return list;
	}
}
