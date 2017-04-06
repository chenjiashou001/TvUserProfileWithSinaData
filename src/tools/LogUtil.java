package tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogUtil {
	private static final String LOG4J_PROPERITES_PATH = "log4j.properties";
	
	public static void debug(Object str_debug) {
		StackTraceElement stack[] = (new Throwable()).getStackTrace();

	    Logger logger = Logger.getLogger(stack[1].getClassName());
	    logger.log(LogUtil.class.getName(), Level.DEBUG, str_debug, null);
		//logger.debug(str_debug);
	}
	
	public static void info(Object str_info) {
		StackTraceElement stack[] = (new Throwable()).getStackTrace();

	    Logger logger = Logger.getLogger(stack[1].getClassName());
	    logger.log(LogUtil.class.getName(), Level.INFO, str_info, null);
		//logger.info(str_info);
	}
	
	public static void error(Object str_err) {
		StackTraceElement stack[] = (new Throwable()).getStackTrace();

	    Logger logger = Logger.getLogger(stack[1].getClassName());
	    logger.log(LogUtil.class.getName(), Level.ERROR, str_err, null);
		//logger.error(str_err);
	}
	
	@SuppressWarnings("unused")
	private static void logClean(String... clean_types) {
		if (clean_types.length == 0) {
			return ;
		}
		InputStream in = ClassLoader.getSystemResourceAsStream(LOG4J_PROPERITES_PATH);  		  
        Properties p = new Properties();  
        try {
			p.load(in);
		} catch (IOException e) {
			error("Properties 加载错误");
		}
        
		for (int i = 0; i < clean_types.length; i++) {
			String clean_type = clean_types[i];
			String file_path = null;
			switch(clean_type) {
			case "debug" :
				file_path = p.getProperty("log4j.appender.D.File");
				break;
			case "info" :
				file_path = p.getProperty("log4j.appender.info.File");
				break;
			case "error" :
				file_path = p.getProperty("log4j.appender.E.File");
				break;
			}
			FileUtil.deleteEveryThing(file_path);
		}		
	}
	
	public static void main(String[] args) {
		//logClean("debug", "info", "error");
	}

}
