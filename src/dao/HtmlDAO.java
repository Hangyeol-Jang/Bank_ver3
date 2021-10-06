package dao;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HtmlDAO {
	private static HtmlDAO singletone = new HtmlDAO();
	private HtmlDAO() {}
	public static HtmlDAO getInstance() {return singletone;}

	public String getStockHistory(int invenCode, int simulCode) {
		String str = "";
		boolean first = true;
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select stock from bank.simul_inventory where inventory_code=? and simul_code=?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, invenCode);
			pstmt.setInt(2, simulCode);
			rs = pstmt.executeQuery();
			
			if(rs!=null) {
				while(rs.next()) {
					if(!first) str+=",";
					str += rs.getString("stock");
					first=false;
				}}
			
		}catch(Exception e) {e.printStackTrace();
		}finally {
			try {if(rs!=null)rs.close();}catch(Exception e) {}
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
		
		return str;
	}
	
	public void genDataSet(int index, int facilCode, int simulCode, PrintWriter pw) {
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select time_step, operation from bank.simul_facility where facility_code=? and simul_code=?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, facilCode);
			pstmt.setInt(2, simulCode);
			rs = pstmt.executeQuery();
			
			if(rs!=null) {
				while(rs.next()) {
					if(rs.getInt("operation")==1) {
						pw.println("{x:"+rs.getInt("time_step")+",y:"+index+"},");
					}
				}}
			
		}catch(Exception e) {e.printStackTrace();
		}finally {
			try {if(rs!=null)rs.close();}catch(Exception e) {}
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
	}
}
