package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InventoryDAO {
	private static InventoryDAO singletone = new InventoryDAO();
	private InventoryDAO() {}
	private Connection conn = BankDAO.getInstance().getConnection();
	public static InventoryDAO getInstance() {
		return singletone;
	}
	
	public ResultSet selectMerchandise() {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String sql = "select * from bank.inventory where merchandise='y'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
}
