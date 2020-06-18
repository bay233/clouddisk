package com.web.Listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.dom4j.DocumentException;

import com.web.dao.FileExtXmlParser;

public class InitListener implements ServletContextListener {

    public InitListener() {
    }

    public void contextDestroyed(ServletContextEvent sce)  { 
    }

    public void contextInitialized(ServletContextEvent sce)  { 
    	FileExtXmlParser fep = new FileExtXmlParser();
    	
    	try {
			Map<String,Set<String>> map = fep.parse("filesExt.xml");
			
			ServletContext application = sce.getServletContext();
			application.setAttribute("map", map);
			
			System.out.println("服务器tomcat启动时，自动加载filesExt.xml文件："+map);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    }
	
}
