package simulator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;

import dao.CompanyDAO;

public class LogIn {
	private BufferedReader br;
	private String id;
	public String getId() {
		return id;
	}
	public LogIn() {
		id = null;
		InputStream is = System.in;
		InputStreamReader isr = new InputStreamReader(is);
		this.br = new BufferedReader(isr);
	}
	
	/**
	 * @summary 입력을 받아서 로그인 실행
	 * @throws Exception
	 */
	public int input() throws Exception {
		String id=null, pw=null;
		int vID, vPW;
		int result = -1;
		
		while(true) {
			System.out.print(" ID\t: ");
			id = br.readLine();
			vID = this._validity(id,"[0-9a-zA-Z]");
			System.out.print(" PW\t: ");
			pw = br.readLine();
			vPW = this._validity(pw,"[0-9a-zA-Z!@#$%^&*()_+~<>?]");
			if(vID==-1 || vPW==-1) {
				this._termProMsg();
				return result;
			}
			else if(vID==0 || vPW==0) {
				this._endInputMsg();
				return 0;
			}
			else if(vID==1 && vPW==1) break;
			else{
				this._wrongInpuMsg();
				return 2;
			}
		}
		ResultSet rs = CompanyDAO.getInstance().login(id, pw); 
		if(rs!=null && rs.next()) {
			this._successMsg();
			this.id = rs.getString("ID");
			result=1; 
		}
		else{
			this._unkownAdminMsg();
			result=2;
		}
		try {if(rs!=null)rs.close();}catch(Exception e) {}
		return result; //-1:프로그램종료 0:입력종료요청 1:정상입력 2:잘못된입력
	}
	private void _endInputMsg() {
		System.out.println(" 입력을 초기화합니다.");
	}
	private void _termProMsg() {
		System.out.println("\n 프로그램을 종료합니다.");
	}
	private void _successMsg() {
		System.out.println(" 로그인 성공");
	}
	private void _wrongInpuMsg() {
		System.out.println(" 잘못된 입력입니다.");
	}
	private void _unkownAdminMsg() {
		System.out.println(" 존재하지 않는 계정입니다.");
	}
	
	private int _validity(String input, String restriction) {
		int result = -1;
		if(input==null) { }
		else if(input.length()<1) {result=2; }
		else if(input.equals("//")) {result = 0;}
		else if(input.replaceAll(restriction,"")!="") { result=2; }
		else {result = 1;}
		
		return result; // -1:프로그램종료 0:입력종료요청 1:정상입력 2:재입력필요
	}
}
