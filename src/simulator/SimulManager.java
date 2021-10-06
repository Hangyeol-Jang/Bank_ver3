package simulator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import dao.CompanyDAO;
import dao.InventoryDAO;
import tool.Input;
import tool.out;
import process.Process;

public class SimulManager {
	private static SimulManager singletone = new SimulManager(); 
	private boolean termination = false;	// 프로그램 종료에서 사용되는 플래그
	private String id;		// 로그인 상태를 저장하기 위한 아이디
	private SimulManager() {}
	public static SimulManager getInstance() {return singletone;}
	
	/**
	 * @summary 로그인을 요구하는 매뉴
	 */
	public void adminLogin() {
		LogIn login = new LogIn();
		int result = -1;
		String items ="관리자 로그인";
		Menu menu = null;
		
		try {
			menu = new Menu(items);
			
			while(!termination) {
				id = null;
				menu.print();
				
				try {
					result = login.input();
					if(result==-1) termination=true;
					else if(result==1) {
						id = login.getId();
						this.mainMenu();
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				System.out.println();
			}
		}catch(Exception e) { e.printStackTrace(); }
		
	}
	
	/**
	 * @summary 로그인 이후 "관리자 설정, 시뮬레이션"을 선택하는 창
	 */
	public void mainMenu() {
		String[] items = {"관리자설정.adminSetting", "시뮬레이션.productSimulation"};
		Menu menu = null;
		int result = 1;
		
		try {
			menu = new Menu(items);
			
			while(!termination) {
				menu.print();
				result = menu.input();
				if(result== -1) termination=true;
				else if(result==0) break;
			}
		}catch(Exception e) { e.printStackTrace(); }
	}
	/**
	 * @summary 관리자 설정 매뉴. "이름 변경, 비밀번호 변경"을 선택할 수 있다.
	 */
	public void adminSetting() {
		String[] items = {"이름변경.alterName", "비밀번호변경.alterPw"};
		Menu menu = null;
		int result = 1;
		if(this.id != null){
			try {
				menu = new Menu(items);
				while(!termination) {
					menu.print();
					result = menu.input();
					if(result== -1) termination=true;
					else if(result==0) break;
				}
			}catch(Exception e) { e.printStackTrace(); }
		}else {
			System.out.println(" 로그인 상태가 아닙니다.");
		}
	}
	/**
	 * @summary 관리자의 이름을 변경하는 메뉴. 하위 매뉴는 없음.
	 */
	public void alterName() {
		ResultSet rs = null;
		try {
			if(id!=null){
				rs = CompanyDAO.getInstance().getData(id);
				String name = rs.getString("name");
				System.out.println(" 현재 관리자 이름 : "+name);
				System.out.println(" 새로운 이름을 입력해주세요.");
				name = Input.input();
				CompanyDAO.getInstance().updateName(name,id);
			}else {
				System.out.println(" 로그인 상태가 아닙니다.");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {if(rs!=null)rs.close();}catch(Exception e) {}
		}
	}
	/**
	 * @summary 관리자의 비밀번호를 변경하는 매뉴. 하위 매뉴 없음.
	 */
	public void alterPw() {
		ResultSet rs = null;
		try {
			if(id!=null){
				rs = CompanyDAO.getInstance().getData(id);
				String name = rs.getString("pw");
				System.out.println(" 현재 비밀번호 : "+name);
				System.out.println(" 새로운 비밀번호를 입력해주세요.");
				name = Input.input();
				CompanyDAO.getInstance().updatePw(name,id);
			}else {
				System.out.println(" 로그인 상태가 아닙니다.");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {if(rs!=null)rs.close();}catch(Exception e) {}
		}
	}

	public void productSimulation() {
		Map<String,String> map = new HashMap<String,String>();
		ResultSet rs = null;
		String code = "";
		String name = "";
		while(true) {
			rs = InventoryDAO.getInstance().selectMerchandise();
			if(rs!=null) {
				System.out.println();
				out.upper();
				out.middle("code    product name");
				out.seperator();
				try {
					while(rs.next()) {
						code = rs.getString("code");
						name = rs.getString("name");
						out.middle((code.length()<2? "  ":" ")+code+"     "+name);
						map.put(code, name);
					}
					out.lower();
					// 코드 선택
					int invenCode = this.selectByCode(map);
					if(invenCode==0) return;
					else if(invenCode==-1) {
						this.termination = true;
						return;
					}else if(invenCode == -2) continue;
					// 생산량 설정
					int amount= this.productAmount();
					if(amount==0) return;
					else if(amount==-1) {
						this.termination = true;
						return;
					}else if(amount < 1) {
						System.out.println(" 생산량은 양수만 가능합니다.");
						continue;
					}
					// 시뮬 준비
					Process process = new Process(invenCode, amount);
					// 실행 여부를 확인
					if(this.yesORno()) {
						System.out.println(" 시뮬레이션 진행 중...\n");
						while(!process.execute()) {}
						System.out.println("<<시뮬레이션 완료>>");
						process.resultHTML();
					}
					
				}catch(Exception e) {
					e.printStackTrace();
					return;
				}finally {
					try {if(rs!=null) rs.close();}catch(Exception e) {}
				}
			}else {
				System.out.println(" 제품이 존재하지 않습니다.");
				return;
			}
		}
	}
	private boolean yesORno() throws Exception {
		boolean result = false;
		
		InputStream is = System.in;
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		while(true) {
			System.out.print(" 시뮬레이션을 진행하시겠습니까?(y,n)\t: ");
			String input = br.readLine();
			if(input==null) {
				termination = true;
				break;
			}else if(input.equals("n")  || input.equals("N")) {
				System.out.println(" 시뮬레이션을 취소했습니다.");
				break;
			}else if(input.equals("y") || input.equals("Y")) {
				System.out.println(" 시뮬레이션을 실행합니다.");
				result = true;
				break;
			}else {
				System.out.println(" 잘못된 입력입니다.");
			}
		}
		return result;
	}
	private int productAmount(){
		int result = -2;
		InputStream is = System.in;
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		System.out.print(" 시뮬레이션할 생산량을 입력해주세요\t: ");
		try {
			String input = br.readLine();
			if(input == null) {
				System.out.println("\n 프로그램을 종료합니다.");
				result = -1;
			}else if(input.equals("//")) {
				System.out.println(" 상위 메뉴로 이동합니다.");
				result = 0;
			}else {
				result = Integer.parseInt(input);
			}
		}catch(Exception e){
			System.out.println(" 잘못된 입력입니다.");
		}
		return result;
	}
	private int selectByCode(Map<String,String> map) throws Exception {
		InputStream is = System.in;
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		int result = -2;
		
		System.out.print(" 코드를 선택해주세요\t: ");
		String input = br.readLine();
		if(input == null) {
			System.out.println("\n 프로그램을 종료합니다.");
			result = -1;
		}else if(input.equals("//")) {
			System.out.println(" 상위 메뉴로 이동합니다.");
			result = 0;
		}else if(map.containsKey(input)) {
			result = Integer.parseInt(input);
		}else {
			System.out.println(" 잘못된 입력입니다.");
		}
		
		return result;
	}
}
