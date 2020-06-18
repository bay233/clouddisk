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

	private long maxFile = 2*1024*1024*1024; //�����ļ��������
	//private String allowedFilesList = "jpg,png,gif,jpeg,bmp";   //�����ϴ����ļ�����
	//private String DeniedFilesList = "jsp,asp,php,aspx,sh,js,exe,bat,html";  //��ֹ�ϴ����ļ�����
	
	

	public Map<String, String> fUpload(PageContext pageContext) throws ServletException, IOException, SQLException, SmartUploadException{
		Map<String, String> result = new HashMap<String, String>();
		
		SmartUpload su = new SmartUpload();
		su.initialize(pageContext);
		
		//���������ϴ����ļ�����
		//su.setAllowedFilesList(allowedFilesList);
		//���岻�����ϴ����ļ�����
		//su.setDeniedFilesList(DeniedFilesList);
		//���õ����ļ����������
		su.setMaxFileSize(maxFile);
		//���������ϴ��ļ�������������
		su.setTotalMaxFileSize(100*maxFile);
		
		su.setCharset("utf-8");
		su.upload();
		
		//�����request����HttpServletRequest����smartupload��request����
		Request request = su.getRequest();
		//��hdfs�ϱ�������ļ���λ��
		String currentPath = request.getParameter("currentPath");
		
		result.put("currentPath", currentPath);
		//��ȡ�ַ�����
		//String title = request.getParameter("title");
		//result.put("title", title);
		//ȡ�ļ�
		String filepath = "img\\";
		Files files = su.getFiles(); //��ȡ�����ϴ����ļ� ������ļ�ͬʱ�ϴ�ʱ��
		//��ȡ�����ļ�·�� F:\Tomcat\apache-tomcat-8.0.51\wtpwebapps\jiepai
		String tomcatroot = pageContext.getRequest().getRealPath("/");
		java.io.File tomcatFile = new java.io.File(tomcatroot);
		tomcatroot = tomcatFile.getParent(); // ����·��
		tomcatFile = new java.io.File(tomcatroot);
		tomcatroot = tomcatFile.getParent();
		
		if(files != null && files.getCount()>0){
			for (int i = 0; i < files.getCount(); i++){
				File file = files.getFile(i); //��ȡ�ļ�
				
				String fieldName = file.getFieldName();
				System.out.println("***************"+fieldName);
				//ȡfile�ĺ�׺��
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
