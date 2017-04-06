package tools;

/**
 * 常用工具
 * @author chenhuan001
 *
 */
public class ComUtil {
	final static char []NEED_TRIM_CHAR = {
		'\r',
		'\n',
		' ',
		' ',
	};
	
	public static String str_trim_beg(String bsd) {
		 
		if (bsd == null) {
			return null;
		}
		String ret_str = "";
		for (int i = 0; i < bsd.length(); i++) {
			char temp_c = bsd.charAt(i);
			if (isCharInArray(temp_c, NEED_TRIM_CHAR)) {
				continue;
			} else {
				ret_str = bsd.substring(i);
				break;
			}
		}
		return ret_str;
	}
	
	public static boolean isCharInArray(char c, char[] array_c) {
		for (int i = 0; i < array_c.length; i++) {
			if (array_c[i] == c) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isStringInArray(String str, String[] array_str) {
		if (null == array_str || null == str) {
			return false;
		}
		for (int i = 0; i < array_str.length && array_str[i] != null; i++) {
			if (array_str[i].equals(str)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean charIsNumber(char c) {
		return (c >= '0' && c <= '9');
	}
	
	public static void change_white_space(String path) {
		String content = FileUtil.readLogByString(path);
		int a = ' ';//网页空格 160  
		System.out.println(a);
		int b = ' ';
		System.out.println(b);
		content = content.replaceAll(" ", " ");
		FileUtil.deleteEveryThing(path);
		FileUtil.writeLog(path, content);
	}
		
	/**
	 * 返回[0-x) 的随机数
	 * @param x
	 * @return
	 */
	public static int getRand(int x) {
		return (int)(Math.random() * x);
	}
	public static void main(String[] args) {
		//change_white_space("Data/EpgTvInfo.txt");
	}
	
}
