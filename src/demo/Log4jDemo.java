package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import tools.LogUtil;

public class Log4jDemo {

    public static void main(String[] args) {
        
        //Logger logger = Logger.getLogger(Log4jDemo.class);
        List<String> tmp_list = new ArrayList<String>();
        tmp_list.add("123");
        tmp_list.add("333");
        Map<String, Integer> tmp_map = new HashMap<String, Integer>();
        tmp_map.put("asdfa", 123);
        // 记录debug级别的信息  
        //logger.debug("This is debug message.");  
        // 记录info级别的信息  
        //logger.info("This is info message.");  
        // 记录error级别的信息  
        //logger.error("This is error message."); 
        //logger.error(tmp_list);
        LogUtil.error(tmp_list);
        LogUtil.info(tmp_map);
        LogUtil.debug("I'm debug");
    }

}

