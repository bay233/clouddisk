package com.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

import com.web.bean.HdfsFile;
import com.web.bean.JsonModel;
import com.web.biz.HdfsBizImpl;
import com.web.utils.FileUploadUtil;

//@WebServlet("/upload.do")
public class UploadServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	private HdfsBizImpl hbi = new HdfsBizImpl();
	@Override 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FileUploadUtil fuu = new FileUploadUtil();
		
		PageContext pageContext = JspFactory.getDefaultFactory().getPageContext(this, request, response, null, true, 8192, true);
		
		JsonModel jm = new JsonModel();
		try {
			Map<String, String> map = fuu.fUpload(pageContext);
			boolean b = hbi.uploadFile(map);
			
			//response.sendRedirect("index.jsp");
			
			jm.setCode(1);
			List<HdfsFile> list = hbi.getFileList(map.get("currentPath"));
			jm.setT(list);
		} catch (Exception e) {
			e.printStackTrace();
			jm.setCode(0);
			jm.setT(e.getMessage());
		}
		
		super.writeJson(jm, response);
		
	}

    

}
