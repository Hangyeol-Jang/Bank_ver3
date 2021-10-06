package tool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Input {
	
	public static String input() throws Exception {
		String input = null;
		InputStream is = System.in;
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		System.out.print(" ют╥б : ");
		input = br.readLine();
		
		return input;
	}
}
