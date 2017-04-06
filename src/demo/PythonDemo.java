package demo;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class PythonDemo {
	
	public static void main(String args[]) {
		try {
			Process proc = Runtime.getRuntime().exec(""
					+ "python /Users/chenhuan001/PycharmProjects/First"
					+ "/main.py"
					);
			//会等python脚本跑完。。。
			
			InputStreamReader stdin = new InputStreamReader(proc.getInputStream());
			LineNumberReader input = new LineNumberReader(stdin);
			String line;
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println(1112);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
