package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CompanyDAO {
	private static CompanyDAO singletone = new CompanyDAO();
	private CompanyDAO() {}
	public static CompanyDAO getInstance() {
		return singletone;
	}
	
	public ResultSet login(String id, String pw) {
		Connection conn=BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from bank.company where id=? and pw=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			rs = pstmt.executeQuery();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	public ResultSet getData(String id) {
		Connection conn=BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from bank.company where id=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			rs.next();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	public int updateName(String name, String id) {
		Connection conn=BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = "update bank.company set name=? where id=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, id);
			result = pstmt.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
		return result;
	}
	public int updatePw(String pw, String id) {
		Connection conn=BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = "update bank.company set pw=? where id=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pw);
			pstmt.setString(2, id);
			result = pstmt.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
		return result;
	}
}
