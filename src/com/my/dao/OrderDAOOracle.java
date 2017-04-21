package com.my.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import com.my.sql.MyConnection;
import com.my.vo.Customer;
import com.my.vo.OrderInfo;
import com.my.vo.OrderLine;
import com.my.vo.Product;

public class OrderDAOOracle {
	private DataSource dataSource;
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}	
	
	public void insert(OrderInfo info) {
		Connection con = null;
		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			insertInfo(info, con);
			insertLine(info, con);
			con.commit();
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			MyConnection.close(con);
		} // 이렇게해야 같은 세션을 사용
	}

	public void insertInfo(OrderInfo info, Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		String insertInfoSQL = "INSERT INTO order_info (INFO_NO, INFO_DATE, INFO_ID)"
				+ " VALUES ( order_info_seq.NEXTVAL,SYSDATE, ?)";
		pstmt = con.prepareStatement(insertInfoSQL);
		pstmt.setString(1, info.getInfo_c().getId());
		pstmt.addBatch();

		pstmt.executeBatch();
	}

	public void insertLine(OrderInfo info, Connection con) throws SQLException {

		PreparedStatement pstmt = null;
		String insertLineSQL = "INSERT INTO order_line (LINE_INFO_NO, LINE_PROD_NO, LINE_QUANTITY)"
				+ " VALUES (order_info_seq.CURRVAL, ?, ?)";
		List<OrderLine> lines = info.getLines();

		pstmt = con.prepareStatement(insertLineSQL);
		for (OrderLine line : lines) {
			pstmt.setString(1, line.getLine_p().getNo());
			pstmt.setInt(2, line.getLine_quantity());
			pstmt.addBatch();
			pstmt.clearParameters();
		}
		pstmt.executeBatch();
		MyConnection.close(null, pstmt);
		
	}

	/**
	 * 전체 주문목록을 반환
	 * 
	 * @return
	 */
	public List<OrderInfo> selectAll() {
		List<OrderInfo> list_info = new ArrayList<>();
		ArrayList<OrderLine> list_line = null;
		Product product = null;
		Customer customer = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String selectAllSQL = 
				"SELECT info_no,info_date, info_id, id, password, name, line_info_no , line_prod_no, line_quantity, prod_no, prod_name, prod_price "
						+"FROM order_info info LEFT OUTER JOIN order_line line ON info_no = line_info_no "
						+"JOIN customer c ON id = info_id "
						+"JOIN product p ON prod_no = line_prod_no "
						+"ORDER BY info_no"; //정렬
	
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(selectAllSQL);
			rs = pstmt.executeQuery();
			int info_no = 0;
			
			while(rs.next()){		
				if(rs.getRow()==1){
					list_line = new ArrayList<>();	// line 그룹 새로
					int line_info_no = rs.getInt("line_info_no");	
					list_line.add( new OrderLine(line_info_no,product,rs.getInt("line_quantity")) ); 	//라인 저장 
					info_no = rs.getInt("info_no");	
					customer = new Customer( rs.getString("id"),rs.getString("password"),rs.getString("name") );
					list_info.add( new OrderInfo(info_no,rs.getDate("info_date"),customer,list_line) );
					
				}else if( info_no != rs.getInt("info_no")){//?						
					info_no = rs.getInt("info_no");	
					customer = new Customer( rs.getString("id"),rs.getString("password"),rs.getString("name") );
					list_info.add( new OrderInfo(info_no,rs.getDate("info_date"),customer,list_line) );
										
					list_line = new ArrayList<>();	// line 그룹 새로
					int line_info_no = rs.getInt("line_info_no");	
					list_line.add( new OrderLine(line_info_no,product,rs.getInt("line_quantity")) ); 	//라인 저장 
					
				}else{			
					int line_info_no = rs.getInt("line_info_no");
					product = new Product(rs.getString("prod_no"),rs.getString("prod_name"),rs.getInt("prod_price"));					
					list_line.add( new OrderLine(line_info_no,product,rs.getInt("line_quantity")) );	//이어서 라인저장
				}
			}			
			
			return list_info;
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			MyConnection.close(con);
		} // 이렇게해야 같은 세션을 사용
		
		return list_info;		
	}

	/**
	 * 주문자에 해당 주문목록 반환
	 * 
	 * @param id(주문자)
	 * @return
	 */
	public List<OrderInfo> selectById(String id) {		
		List<OrderInfo> list_info = new ArrayList<>();	
		Connection con = null;
		
		String selectIdSQL =  
				"SELECT info_no,info_date, line_quantity, prod_no, prod_name, prod_price "
						+"FROM order_info info LEFT OUTER JOIN order_line line ON info_no = line_info_no  "
						+"JOIN product p ON prod_no = line_prod_no "
						+"WHERE info_id = ?"
						+"ORDER BY info_no"; //info_no, info_date, line_quantity, prod_no, prod_name, prod_price;
		
		try {
			Customer customer = null;	
			CustomerDAO dao = new CustomerDAOOracle();
			customer = dao.selectById(id);	
			
			con = dataSource.getConnection();
			PreparedStatement pstmt = con.prepareStatement(selectIdSQL);
			pstmt.setString(1, id);			
			ResultSet rs = pstmt.executeQuery();	//쿼리문 생성 및 전송
			
	
			ArrayList<OrderLine> list_line = null;
			//받아올 정보들
			
			int tmp = -1;	//비교용 값
			
			while( rs.next() ){
				int info_no = rs.getInt("info_no");			
				Date date = rs.getDate("info_date");
				int line_quantity = rs.getInt("line_quantity");
				String prod_name = rs.getString("prod_name");
				String prod_no = rs.getString("prod_no");
				int prod_price = rs.getInt("prod_price");
				
				if(tmp != info_no){			//새로운 info
					tmp = info_no;									
					
					OrderInfo info = new OrderInfo();
		            list_line = new ArrayList<>();
		            info.setLines(list_line);   
		            list_info.add(info);
		            
		            //info setting
		            info.setInfo_no(info_no);
		            info.setInfo_c(customer);
		            info.setInfo_date(date);
				}
					Product product = new Product(prod_no,prod_name,prod_price);	
					list_line.add(new OrderLine(info_no,product,line_quantity));
			}			
			
		}catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
			}
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MyConnection.close(con);
		} // 이렇게해야 같은 세션을 사용
	
		return list_info;
	}

	/**
	 * 주문번호로 해당 주문정보를 반환
	 * 
	 * @param info_no
	 * @return
	 */
	public OrderInfo selectByNo(int info_no) {
		return null;
	}

	/**
	 * 상품번호에 해당하는 주문목록을 반환
	 * 
	 * @param prod_no
	 * @return
	 */
	public List<OrderInfo> selectByProdNo(String prod_no) {
		return null;
	}

	public List<OrderInfo> selectByDate(String frDate, String toDate) {
		return null;
	}

}// 일처리할 때 주문 기본과 상세 정보는 한 트랜젝션에서 관리
