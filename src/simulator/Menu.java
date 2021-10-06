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
	 * @summary �Ŵ� ��ȣ�� �Է� �ް�, �ش� �Ŵ��� �̵��ϰų� �޽����� ����ϴ� �޼���
	 * @return ��������:1  �����޴��� �̵�:0  ���α׷� ��������:-1
	 * @throws Exception
	 */
	public int input() throws Exception {
		int result = 1;
		System.out.print(" �޴��� �������ּ���\t: ");
		String input = br.readLine();
		if(input == null) {
			System.out.println("\n ���α׷��� �����մϴ�.");
			result = -1;
		}else if(input.equals("//")) {
			System.out.println(" ���� �޴��� �̵��մϴ�.");
			result = 0;
		}else if(this.menuMap.containsKey(input)) {
			menuMap.get(input).invoke(SimulManager.getInstance());
		}else {
			System.out.println(" �߸��� �Է��Դϴ�.");
		}
		
		return result;	// ��������:1 �����޴����̵�:0 ���α׷���������:-1
	}
	
	/**
	 * @summary �迭�� �Է¹޾Ƽ� Map�� ����� �޼���
	 * @param menu
	 * @throws Exception
	 */
	private void _generateMenuMap(String[] menu) throws Exception {
		this.menuMap = new HashMap<>();
		Class<?> cls = Class.forName(SimulManager.class.getTypeName());
		String[] str= new String[2]; // 0: �޴���ȣ   1: �޼����̸�
		int cnt = 1;
		for(String s : menu) {
			str = s.split("\\.");
			menuMap.put(""+cnt++, cls.getMethod(str[1]));
		}
	}
	
	/**
	 * @summary �־��� �迭�� �Ŵ��� ����ϴ� �޼���
	 * @param menu
	 */
	public void print() {
		int num_menu = 3;
		System.out.println();
		System.out.print("��");
		for (int i = 0; i < num_menu; i++) {
			if (i == 0)	System.out.print("������������������������������");
			else		System.out.print("��������������������������������");
		} System.out.println("��");
		
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
					else temp+="�� ";
					temp += ((items[cnt]).split("\\."))[0];
				}	
				System.out.print(temp + "\t");
				if (temp.length() < 7)	System.out.print("\t");
				cnt++;
			}
			System.out.println("|");
		}
		System.out.println("|\t\t\t\t\t\t|");
		System.out.println("| �����Ŵ��� �̵�(//)  ���α׷�����(ctrl+Z)\t\t|");
		System.out.print("��");
		for (int i = 0; i < num_menu; i++) {
			if (i == 0) System.out.print("������������������������������");
			else System.out.print("��������������������������������");
		} System.out.println("��");
	}
}
