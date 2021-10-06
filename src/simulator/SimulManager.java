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
	private boolean termination = false;	// ���α׷� ���ῡ�� ���Ǵ� �÷���
	private String id;		// �α��� ���¸� �����ϱ� ���� ���̵�
	private SimulManager() {}
	public static SimulManager getInstance() {return singletone;}
	
	/**
	 * @summary �α����� �䱸�ϴ� �Ŵ�
	 */
	public void adminLogin() {
		LogIn login = new LogIn();
		int result = -1;
		String items ="������ �α���";
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
	 * @summary �α��� ���� "������ ����, �ùķ��̼�"�� �����ϴ� â
	 */
	public void mainMenu() {
		String[] items = {"�����ڼ���.adminSetting", "�ùķ��̼�.productSimulation"};
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
	 * @summary ������ ���� �Ŵ�. "�̸� ����, ��й�ȣ ����"�� ������ �� �ִ�.
	 */
	public void adminSetting() {
		String[] items = {"�̸�����.alterName", "��й�ȣ����.alterPw"};
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
			System.out.println(" �α��� ���°� �ƴմϴ�.");
		}
	}
	/**
	 * @summary �������� �̸��� �����ϴ� �޴�. ���� �Ŵ��� ����.
	 */
	public void alterName() {
		ResultSet rs = null;
		try {
			if(id!=null){
				rs = CompanyDAO.getInstance().getData(id);
				String name = rs.getString("name");
				System.out.println(" ���� ������ �̸� : "+name);
				System.out.println(" ���ο� �̸��� �Է����ּ���.");
				name = Input.input();
				CompanyDAO.getInstance().updateName(name,id);
			}else {
				System.out.println(" �α��� ���°� �ƴմϴ�.");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {if(rs!=null)rs.close();}catch(Exception e) {}
		}
	}
	/**
	 * @summary �������� ��й�ȣ�� �����ϴ� �Ŵ�. ���� �Ŵ� ����.
	 */
	public void alterPw() {
		ResultSet rs = null;
		try {
			if(id!=null){
				rs = CompanyDAO.getInstance().getData(id);
				String name = rs.getString("pw");
				System.out.println(" ���� ��й�ȣ : "+name);
				System.out.println(" ���ο� ��й�ȣ�� �Է����ּ���.");
				name = Input.input();
				CompanyDAO.getInstance().updatePw(name,id);
			}else {
				System.out.println(" �α��� ���°� �ƴմϴ�.");
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
					// �ڵ� ����
					int invenCode = this.selectByCode(map);
					if(invenCode==0) return;
					else if(invenCode==-1) {
						this.termination = true;
						return;
					}else if(invenCode == -2) continue;
					// ���귮 ����
					int amount= this.productAmount();
					if(amount==0) return;
					else if(amount==-1) {
						this.termination = true;
						return;
					}else if(amount < 1) {
						System.out.println(" ���귮�� ����� �����մϴ�.");
						continue;
					}
					// �ù� �غ�
					Process process = new Process(invenCode, amount);
					// ���� ���θ� Ȯ��
					if(this.yesORno()) {
						System.out.println(" �ùķ��̼� ���� ��...\n");
						while(!process.execute()) {}
						System.out.println("<<�ùķ��̼� �Ϸ�>>");
						process.resultHTML();
					}
					
				}catch(Exception e) {
					e.printStackTrace();
					return;
				}finally {
					try {if(rs!=null) rs.close();}catch(Exception e) {}
				}
			}else {
				System.out.println(" ��ǰ�� �������� �ʽ��ϴ�.");
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
			System.out.print(" �ùķ��̼��� �����Ͻðڽ��ϱ�?(y,n)\t: ");
			String input = br.readLine();
			if(input==null) {
				termination = true;
				break;
			}else if(input.equals("n")  || input.equals("N")) {
				System.out.println(" �ùķ��̼��� ����߽��ϴ�.");
				break;
			}else if(input.equals("y") || input.equals("Y")) {
				System.out.println(" �ùķ��̼��� �����մϴ�.");
				result = true;
				break;
			}else {
				System.out.println(" �߸��� �Է��Դϴ�.");
			}
		}
		return result;
	}
	private int productAmount(){
		int result = -2;
		InputStream is = System.in;
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		System.out.print(" �ùķ��̼��� ���귮�� �Է����ּ���\t: ");
		try {
			String input = br.readLine();
			if(input == null) {
				System.out.println("\n ���α׷��� �����մϴ�.");
				result = -1;
			}else if(input.equals("//")) {
				System.out.println(" ���� �޴��� �̵��մϴ�.");
				result = 0;
			}else {
				result = Integer.parseInt(input);
			}
		}catch(Exception e){
			System.out.println(" �߸��� �Է��Դϴ�.");
		}
		return result;
	}
	private int selectByCode(Map<String,String> map) throws Exception {
		InputStream is = System.in;
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		int result = -2;
		
		System.out.print(" �ڵ带 �������ּ���\t: ");
		String input = br.readLine();
		if(input == null) {
			System.out.println("\n ���α׷��� �����մϴ�.");
			result = -1;
		}else if(input.equals("//")) {
			System.out.println(" ���� �޴��� �̵��մϴ�.");
			result = 0;
		}else if(map.containsKey(input)) {
			result = Integer.parseInt(input);
		}else {
			System.out.println(" �߸��� �Է��Դϴ�.");
		}
		
		return result;
	}
}
