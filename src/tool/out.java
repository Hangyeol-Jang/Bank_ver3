package tool;

public class out {
	public static void upper() {
		System.out.println("��������������������������������������������������������������������������������������������������");
	}
	public static void middle(String str) {
		int len = "����������������������������������������������������������������������������������������������".length();
		System.out.print("| ");
		System.out.print(str);
		for(int i=0;i<len-str.length()-1;i++) {
			System.out.print(" ");
		}
		System.out.println("|");
	}
	public static void lower() {
		System.out.println("��������������������������������������������������������������������������������������������������");
	}
	public static void seperator() {
		System.out.println("��������������������������������������������������������������������������������������������������");
	}
}
