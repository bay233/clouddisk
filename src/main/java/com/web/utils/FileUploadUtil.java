package com.web.utils;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

import com.jspsmart.upload.File;
import com.jspsmart.upload.Files;
import com.jspsmart.upload.Request;
import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;

public class FileUploadUtil {

	private long maxFile = 2*1024*1024*1024; //单个文件最大限制
	//private String allowedFilesList = "jpg,png,gif,jpeg,bmp";   //允许上传的文件类型
	//private String DeniedFilesList = "jsp,asp,php,aspx,sh,js,exe,bat,html";  //禁止上传的文件类型
	
	

	public Map<String, String> fUpload(PageContext pageContext) throws ServletException, IOException, SQLException, SmartUploadException{
		Map<String, String> result = new HashMap<String, String>();
		
		SmartUpload su = new SmartUpload();
		su.initialize(pageContext);
		
		//定义允许上传的文件类型
		//su.setAllowedFilesList(allowedFilesList);
		//定义不允许上传的文件类型
		//su.setDeniedFilesList(DeniedFilesList);
		//设置单个文件的最大限制
		su.setMaxFileSize(maxFile);
		//设置所有上传文件的总容量限制
		su.setTotalMaxFileSize(100*maxFile);
		
		su.setCharset("utf-8");
		su.upload();
		
		//这里的request不是HttpServletRequest而是smartupload的request对象
		Request request = su.getRequest();
		//在hdfs上保存这个文件的位置
		String currentPath = request.getParameter("currentPath");
		
		result.put("currentPath", currentPath);
		//获取字符数据
		//String title = request.getParameter("title");
		//result.put("title", title);
		//取文件
		String filepath = "img\\";
		Files files = su.getFiles(); //获取所有上传的文件 （多个文件同时上传时）
		//获取工程文件路径 F:\Tomcat\apache-tomcat-8.0.51\wtpwebapps\jiepai
		String tomcatroot = pageContext.getRequest().getRealPath("/");
		java.io.File tomcatFile = new java.io.File(tomcatroot);
		tomcatroot = tomcatFile.getParent(); // 工程路径
		tomcatFile = new java.io.File(tomcatroot);
		tomcatroot = tomcatFile.getParent();
		
		if(files != null && files.getCount()>0){
			for (int i = 0; i < files.getCount(); i++){
				File file = files.getFile(i); //获取文件
				
				String fieldName = file.getFieldName();
				System.out.println("***************"+fieldName);
				//取file的后缀名
				String extName = file.getFileExt();
				String newFileName = filepath + file.getFileName();
				//System.out.println(tomcatroot + "\\webapps\\" + newFileName);
				file.saveAs(tomcatroot + "\\webapps\\" + newFileName, SmartUpload.SAVE_PHYSICAL);
				result.put(fieldName, newFileName);
				result.put("realPath", tomcatroot + "\\webapps\\" + newFileName);
				result.put("fileName", file.getFileName());
			}
		}
		
		return result;
	}
	
}
