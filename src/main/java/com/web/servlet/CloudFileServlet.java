package com.web.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.ParentNotDirectoryException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.security.AccessControlException;

import com.web.bean.HdfsFile;
import com.web.bean.JsonModel;
import com.web.biz.HdfsBizImpl;

//@WebServlet("/cloudFile.do")
public class CloudFileServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private HdfsBizImpl hbi = new HdfsBizImpl();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String op = request.getParameter("op");
		try {
			if ("showRoot".equals(op)) {
				showRootOp(request, response);
			} else if ("goUp".equals(op)) {
				goUpOp(request, response);
			} else if ("rename".equals(op)) {
				renameOp(request, response);
			} else if ("delFile".equals(op)) {
				delFileOp(request, response);
			} else if ("mkdir".equals(op)) {
				mkdirOp(request, response);
			} else if ("moveTo".equals(op)) {
				moveToOp(request, response);
			} else if ("copyTo".equals(op)) {
				copyToOp(request, response);
			} else if ("selectFile".equals(op)) {
				selectFileOp(request, response);
			} else if ("downloadFile".equals("op")) {
				downloadFileOp(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadFileOp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String currentPath = request.getParameter("currentPath");
		if (!currentPath.endsWith("/")) {
			currentPath += "/";
		}
		String fileName = request.getParameter("fileName");
		//System.out.println(currentPath+" "+fileName);
		// 获取临时目录
		String folder = System.getProperty("java.io.tmpdir");
		
		String localPath;
		try {
			localPath = hbi.downloadFile(currentPath, fileName, folder);
			// 下载的文件的类型
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);// http的响应协议，以附件形式下载
			String mimeType = this.getServletContext().getMimeType(fileName); // 获取文件的mimetype
			response.setContentType(mimeType); // 设置响应头，指定文件类型
			ServletOutputStream oos = response.getOutputStream(); // 获取输出流，以输出文件到浏览器
			InputStream fis = new BufferedInputStream(new FileInputStream(localPath));
			byte[] buffer = new byte[10 * 1024];
			int len = 0;
			while ((len = fis.read(buffer, 0, buffer.length)) != -1) {
				oos.write(buffer, 0, len);
				oos.flush();
			}
			oos.flush();
			fis.close();
			oos.close();
			
			File f = new File(localPath);
			f.delete();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}

	}

	private void selectFileOp(HttpServletRequest request, HttpServletResponse response) throws AccessControlException,
			FileNotFoundException, UnsupportedFileSystemException, IllegalArgumentException, IOException {
		String currentPath = request.getParameter("currentPath");
		if (!currentPath.endsWith("/")) {
			currentPath += "/";
		}
		String type = request.getParameter("type");
		JsonModel jm = new JsonModel<>();
		Map<String, Set<String>> map = (Map<String, Set<String>>) request.getServletContext().getAttribute("map");
		List<HdfsFile> list = hbi.selectFile(currentPath, type, map);
		jm.setCode(1);
		jm.setT(list);
		super.writeJson(jm, response);
	}

	private void copyToOp(HttpServletRequest request, HttpServletResponse response)
			throws AccessControlException, FileAlreadyExistsException, FileNotFoundException,
			ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
		String currentPath = request.getParameter("currentPath");
		if (!currentPath.endsWith("/")) {
			currentPath += "/";
		}
		String oldName = request.getParameter("oldName");
		String newPath = request.getParameter("newPath");
		if (!newPath.endsWith("/")) {
			newPath += "/";
		}

		boolean result = hbi.copyTo(oldName, newPath, currentPath);
		JsonModel jm = new JsonModel<>();

		if (result) {
			jm.setCode(1);
			List<HdfsFile> list = hbi.getFileList(newPath);
			jm.setT(list);
		} else {
			jm.setCode(0);
			jm.setT("复制失败！");
		}
		super.writeJson(jm, response);
	}

	private void moveToOp(HttpServletRequest request, HttpServletResponse response)
			throws AccessControlException, FileAlreadyExistsException, FileNotFoundException,
			ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
		String currentPath = request.getParameter("currentPath");
		if (!currentPath.endsWith("/")) {
			currentPath += "/";
		}
		String oldName = request.getParameter("oldName");
		String newPath = request.getParameter("newPath");
		if (!newPath.endsWith("/")) {
			newPath += "/";
		}
		// System.out.println(currentPath + " " + newPath + " " + oldName);
		boolean result = hbi.moveTo(oldName, newPath, currentPath);
		JsonModel jm = new JsonModel<>();

		if (result) {
			jm.setCode(1);
			List<HdfsFile> list = hbi.getFileList(newPath);
			jm.setT(list);
		} else {
			jm.setCode(0);
			jm.setT("移动失败！");
		}
		super.writeJson(jm, response);

	}

	private void mkdirOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String currentPath = request.getParameter("currentPath");
		if (!currentPath.endsWith("/")) {
			currentPath += "/";
		}
		String newName = request.getParameter("newName");
		boolean result = hbi.mkdir(newName, currentPath);
		JsonModel jm = new JsonModel<>();
		if (result) {
			List<HdfsFile> list = hbi.getFileList(currentPath);
			jm.setCode(1);
			jm.setT(list);
		} else {
			jm.setCode(0);
			jm.setT("创建目录失败！");
		}
		super.writeJson(jm, response);
	}

	private void delFileOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String currentPath = request.getParameter("currentPath");
		String oldName = request.getParameter("oldName");
		if (!currentPath.endsWith("/")) {
			currentPath += "/";
		}

		JsonModel jm = new JsonModel();
		boolean result = hbi.delFile(oldName, currentPath);
		if (result) {
			List<HdfsFile> list = hbi.getFileList(currentPath);
			jm.setCode(1);
			jm.setT(list);
		} else {
			jm.setCode(0);
			jm.setT("删除失败！");
		}
		super.writeJson(jm, response);
	}

	private void renameOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String newName = request.getParameter("newName");
		String currentPath = request.getParameter("currentPath");
		if (!currentPath.endsWith("/")) {
			currentPath += "/";
		}
		String oldName = request.getParameter("oldName");
		// System.out.println(newName+""+currentPath+""+oldName);
		boolean r = hbi.rename(oldName, newName, currentPath);
		JsonModel jm = new JsonModel<>();
		if (r) {
			jm.setCode(1);
			List<HdfsFile> list = hbi.getFileList(currentPath);
			jm.setT(list);
		} else {
			jm.setCode(0);
			jm.setT("修改失败！");
		}
		super.writeJson(jm, response);

	}

	private void goUpOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String path = request.getParameter("path");
		Path root = new Path(path);
		Path parent = root.getParent();

		JsonModel jm = new JsonModel();
		if (parent == null) {
			jm.setCode(0);
			jm.setT("/");
		} else {
			jm.setCode(1);
			String parentPath = parent.toString();
			List<HdfsFile> list = hbi.getFileList(parentPath);
			Map<String, Object> map = new HashMap<>();
			map.put("parentPath", parentPath);
			map.put("sonFileList", list);
			jm.setT(map);
		}

		super.writeJson(jm, response);
	}

	private void showRootOp(HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException, IOException {
		String path = request.getParameter("path");

		List<HdfsFile> list = hbi.getFileList(path);

		super.writeJson(list, response);

	}

	private String formatSize(long len) {
		String result = "";
		if (len / 1024 / 1024 / 1024 > 0) {
			result = len / 1024 / 1024 / 1024 + "G";
		} else if (len / 1024 / 1024 > 0) {
			result = len / 1024 / 1024 + "M";
		} else if (len / 1024 > 0) {
			result = len / 1024 + "K";
		} else {
			result = len + "B";
		}
		return result;
	}
}
