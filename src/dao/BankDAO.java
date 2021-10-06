package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class BankDAO {
	private static BankDAO singleton = new BankDAO();
	private Connection conn;
	
	public static BankDAO getInstance() { return singleton; }
	private BankDAO() {
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/bank?characterEncoding=UTF-8&serverTimezone=UTC";
		conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, "root", "java");
		}catch(Exception e) { e.printStackTrace(); }
	}
	public Connection getConnection() { return conn; }
}
