package tool;

public class out {
	public static void upper() {
		System.out.println("忙式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式忖");
	}
	public static void middle(String str) {
		int len = "式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式".length();
		System.out.print("| ");
		System.out.print(str);
		for(int i=0;i<len-str.length()-1;i++) {
			System.out.print(" ");
		}
		System.out.println("|");
	}
	public static void lower() {
		System.out.println("戌式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式戎");
	}
	public static void seperator() {
		System.out.println("戍式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式式扣");
	}
}
