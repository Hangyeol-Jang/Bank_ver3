package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import process.SimulModule;

public class SimulDAO {
	private static SimulDAO singletone = new SimulDAO();
	private SimulDAO() {}
	public static SimulDAO getInstance() {return singletone;}
	
	public ArrayList<Integer> getChild(int code){
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select process from inventory where code=?";
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, code);
			rs = pstmt.executeQuery();
			
			if(rs!=null) {
				rs.next();
				String process = rs.getString("process");
				if(process!=null) {
					rs.close();
					pstmt.close();
					
					pstmt=conn.prepareStatement("select inventory_code from input where Facility_code=?");
					pstmt.setInt(1, Integer.parseInt(process));
					rs=pstmt.executeQuery();
					if(rs!=null) {
						while(rs.next()) {
							list.add(rs.getInt("inventory_code"));
						}
					}
					
				}
			}
			
		}catch(Exception e) {e.printStackTrace();
		}finally {
			try {if(rs!=null)rs.close();}catch(Exception e) {}
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
		return list;
	}
	/**
	 * @summary ��ǰ�ڵ�(int)�� �Է¹޾Ƽ� �����ڵ�(int)�� ��ȯ
	 * @param productCode
	 * @return �������� ���� ��� -1, ������ ��� ���� �ڵ� ��ȯ
	 */
	public int getFromInventory(int productCode){
		int result = -1;
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from bank.inventory where code=?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, productCode);
			rs = pstmt.executeQuery();
			
			if(rs!=null) {
				rs.next();
				String str = rs.getString("process");
				if(str!=null) result=Integer.parseInt(str);
				else result = 0;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {if(rs!=null)rs.close();}catch(Exception e) {}
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
		
		return result;
	}
	public int getFromFacility(int facilityCode) {
		int result = -1;
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from bank.facility where code=?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, facilityCode);
			rs = pstmt.executeQuery();
			
			if(rs!=null) {
				rs.next();
				String str = rs.getString("OUTPUT_QUANTITY");
				if(str!=null) result=Integer.parseInt(str);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {if(rs!=null)rs.close();}catch(Exception e) {}
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
		
		return result;
	}
	public void getFromInput(int facilityCode,ArrayList<Integer> inputCode,ArrayList<Integer> inputQuant ) {
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from bank.input where FACILITY_CODE=?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, facilityCode);
			rs = pstmt.executeQuery();
			
			if(rs!=null) {
				String str;
				while(rs.next()) {
					str = rs.getString("INVENTORY_CODE");
					if(str!=null)	inputCode.add(Integer.parseInt(str));
					else			inputCode.add(0);
					str = rs.getString("INPUT_QUANTITY");
					if(str!=null)	inputQuant.add(Integer.parseInt(str));
					else			inputQuant.add(0);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {if(rs!=null)rs.close();}catch(Exception e) {}
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
	}
	
	public int getMaxSimulCode() {
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select max(code) as max from bank.simulinfo";
		int simulCode = 0;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs!=null) {
				rs.next();
				simulCode = rs.getInt("max") + 1;
			}else simulCode = 1;
		}catch(Exception e) {e.printStackTrace();
		}finally {
			try {if(rs!=null)rs.close();}catch(Exception e) {}
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
		return simulCode;
	}
	
	public int insertSimulInfo(int simulCode, int productCode, int quota) {
		int result = -1;
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		String sql = "insert into bank.simulinfo(code, target_product, quota) values(?, ?, ?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, simulCode);
			pstmt.setInt(2, productCode);
			pstmt.setInt(3, quota);
			
			result = pstmt.executeUpdate();
			
		}catch(Exception e) {e.printStackTrace();
		}finally {
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
		return result;
	}
	// ��ǰcode�� key��, ��� value�� ���� Map�� ��ȯ (������ �������� �ʴ´�)
	public Map<Integer, Integer> getInventoryDict(ArrayList<Integer> inputCode) {
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String codes = "";
		Map<Integer, Integer> inputStockMap = new HashMap<Integer, Integer>();
		for(int i=0;i<inputCode.size();i++) {
			if(i>0) codes+=",";
			codes+=inputCode.get(i);
		}
		String sql = "select * from bank.inventory where code in ("+codes+")";
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs!=null) {
				while(rs.next()) {
					if(rs.getString("process")!=null) {
						// �����(process�� ���� ���)�� map�� ���� �ʴ´�.
						inputStockMap.put(rs.getInt("code"), rs.getInt("stock"));
					}}}
		}catch(Exception e) {e.printStackTrace();
		}finally {
			try {if(rs!=null)rs.close();}catch(Exception e) {}
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
		return inputStockMap;
	}
	public void resetStock(ArrayList<SimulModule> simulList) {
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		String codes = "";
		for(int i=0; i<simulList.size();i++) {
			if(i>0) codes+=",";
			codes+=simulList.get(i).getProductID();
		}
		String sql = "update bank.inventory set stock=0 where code in ("+codes+")";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		}catch(Exception e) {e.printStackTrace();
		}finally {
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
	}
	// INVENTORY�� ��� ����
	public void updateStock(int code, int variance) {
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		String var = "";
		if(variance<0) 	var += variance;
		else			var = "+"+variance;
		String sql = "update bank.inventory set stock=stock"+var
				+" where code=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, code);
			pstmt.executeUpdate();
		}catch(Exception e) {e.printStackTrace();
		}finally {
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
	}
	// ��� ��ȭ���� ���
	public void insertSimulInventory(int inventoryCode, int simulCode,int timeStep) {
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		String sql = "insert into bank.simul_inventory(inventory_code,simul_code,time_step,stock) values (?,?,?, (select stock from inventory where code = ?) )";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, inventoryCode);
			pstmt.setInt(2, simulCode);
			pstmt.setInt(3, timeStep);
			pstmt.setInt(4, inventoryCode);
			pstmt.executeUpdate();
		}catch(Exception e) {e.printStackTrace();
		}finally {
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
	}
	// ���� ���� ������ ���
	public void insertSimulFacility(int facilCode, int simulCode, int timeStep, int oper) {
		Connection conn = BankDAO.getInstance().getConnection();
		PreparedStatement pstmt = null;
		String sql = "insert into bank.simul_facility(FACILITY_CODE, SIMUL_CODE, TIME_STEP, OPERATION) values (?, ?, ?, ?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, facilCode);
			pstmt.setInt(2, simulCode);
			pstmt.setInt(3, timeStep);
			pstmt.setInt(4, oper);
			pstmt.executeUpdate();
		}catch(Exception e) {e.printStackTrace();
		}finally {
			try {if(pstmt!=null)pstmt.close();}catch(Exception e) {}
		}
	}
}
