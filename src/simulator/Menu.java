package simulator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Menu {
	private String[] items = null;
	private Map<String,Method> menuMap = null;
	BufferedReader br = null;
	boolean flag = false;
	
	public Menu(String[] items) throws Exception {
		this.items = items;
		this._generateMenuMap(items);
		
		InputStream is = System.in;
		InputStreamReader isr = new InputStreamReader(is);
		this.br = new BufferedReader(isr);
	}
	public Menu(String item) throws Exception {
		this.items = new String[] {item};
		this.flag = true;
		
		InputStream is = System.in;
		InputStreamReader isr = new InputStreamReader(is);
		this.br = new BufferedReader(isr);
	}
	
	/**
	 * @summary 매뉴 번호를 입력 받고, 해당 매뉴로 이동하거나 메시지를 출력하는 메서드
	 * @return 정상종료:1  상위메뉴로 이동:0  프로그램 강제종료:-1
	 * @throws Exception
	 */
	public int input() throws Exception {
		int result = 1;
		System.out.print(" 메뉴를 선택해주세요\t: ");
		String input = br.readLine();
		if(input == null) {
			System.out.println("\n 프로그램을 종료합니다.");
			result = -1;
		}else if(input.equals("//")) {
			System.out.println(" 상위 메뉴로 이동합니다.");
			result = 0;
		}else if(this.menuMap.containsKey(input)) {
			menuMap.get(input).invoke(SimulManager.getInstance());
		}else {
			System.out.println(" 잘못된 입력입니다.");
		}
		
		return result;	// 정상종료:1 상위메뉴로이동:0 프로그램강제종료:-1
	}
	
	/**
	 * @summary 배열을 입력받아서 Map을 만드는 메서드
	 * @param menu
	 * @throws Exception
	 */
	private void _generateMenuMap(String[] menu) throws Exception {
		this.menuMap = new HashMap<>();
		Class<?> cls = Class.forName(SimulManager.class.getTypeName());
		String[] str= new String[2]; // 0: 메뉴번호   1: 메서드이름
		int cnt = 1;
		for(String s : menu) {
			str = s.split("\\.");
			menuMap.put(""+cnt++, cls.getMethod(str[1]));
		}
	}
	
	/**
	 * @summary 주어진 배열을 매뉴로 출력하는 메서드
	 * @param menu
	 */
	public void print() {
		int num_menu = 3;
		System.out.println();
		System.out.print("┌");
		for (int i = 0; i < num_menu; i++) {
			if (i == 0)	System.out.print("───────────────");
			else		System.out.print("────────────────");
		} System.out.println("┐");
		
		int cycle = items.length / num_menu;
		if (items.length % num_menu > 0)
			cycle = cycle + 1;
		String temp = "";
		int cnt = 0;
		for (int i = 0; i < cycle; i++) {
			for (int j = 0; j < num_menu; j++) {
				temp = "";
				if (j == 0)	temp += "| ";
				else		temp += "  ";
				if (cnt < items.length) {
					if(!flag) temp+=((cnt + 1) + ".");
					else temp+="◎ ";
					temp += ((items[cnt]).split("\\."))[0];
				}	
				System.out.print(temp + "\t");
				if (temp.length() < 7)	System.out.print("\t");
				cnt++;
			}
			System.out.println("|");
		}
		System.out.println("|\t\t\t\t\t\t|");
		System.out.println("| 상위매뉴로 이동(//)  프로그램종료(ctrl+Z)\t\t|");
		System.out.print("└");
		for (int i = 0; i < num_menu; i++) {
			if (i == 0) System.out.print("───────────────");
			else System.out.print("────────────────");
		} System.out.println("┘");
	}
}
