package com.web.biz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.ParentNotDirectoryException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.security.AccessControlException;

import com.web.bean.HdfsFile;
import com.web.dao.HdfsDao;

public class HdfsBizImpl {
	
	public boolean rename(String oldName, String newName, String currentPath) throws IOException{
		Path src = new Path(currentPath + oldName);
		Path dst = new Path(currentPath + newName);
		boolean result = HdfsDao.getFileSystem().rename(src, dst);
		return result;
	}
	
	
	/**
	 * 查询当前路径下得所有文件
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List<HdfsFile> getFileList(String path) throws FileNotFoundException, IOException{
		
		Path root = new Path(path);
		FileStatus[] fs = HdfsDao.getFileSystem().listStatus(root);
		
		List<HdfsFile> list = getFiles( fs);
		return list;
	}
	
	private List<HdfsFile> getFiles(FileStatus[] fs){
		List<HdfsFile> list = new ArrayList<>();
		for(FileStatus lfs : fs){
			HdfsFile hf = new HdfsFile();
			Path p = lfs.getPath();
			String fileName = p.getName();
			hf.setFileName(fileName);
			
			boolean b = lfs.isFile();
			hf.setFile(b);
			
			Long len = null;
			String sizeString = null;
			if ( b ){
				len = lfs.getLen();
				sizeString = formatSize(len);
				hf.setSizeString(sizeString);
			}
			long mt = lfs.getModificationTime();
			Date d = new Date(mt);
			DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			String dateString = df.format( d );
			hf.setDateString(dateString);
			hf.setFilePath(lfs.getPath().toString());
			list.add(hf);
		}
		return list;
	}
	
	private String formatSize(long len){
		String result = "";
		if (len/1024/1024/1024 > 0){
			result = len/1024/1024/1024 + "G";
		}else if (len/1024/1024 > 0){
			result = len/1024/1024 + "M";
		}else if (len/1024 > 0){
			result = len/1024 + "K";
		}else{
			result = len + "B";
		}
		return result;
	}


	public boolean delFile(String oldName, String currentPath) throws IOException {
		Path path = new Path(currentPath + oldName);
		boolean result = HdfsDao.getFileSystem().delete(path,true);
		
		return result;
	}


	public boolean mkdir(String newName, String currentPath) throws IOException {
		Path f = new Path(currentPath + newName);
		boolean result = HdfsDao.getFileSystem().mkdirs(f);
		return result;
	}


	public boolean uploadFile(Map<String, String> map) {
		String realPath = map.get("realPath");
		String currentPath = map.get("currentPath");
		String fileName = map.get("fileName");
		
		if (!currentPath.endsWith("/")){
			currentPath += "/";
		}
		
		Path src = new Path(realPath);
		Path dst = new Path(currentPath + fileName);
		
		try {
			HdfsDao.getFileSystem().copyFromLocalFile(src, dst);
			File f = new File(realPath);
			if (f.exists()){
				f.delete();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	public boolean moveTo(String oldName, String newPath, String currentPath) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
		Path src = new Path(currentPath + oldName);
		Path dst = new Path(newPath + oldName);
		boolean result = FileContext.getFileContext().util().copy(src, dst, true, false);
		return result;
	}


	public boolean copyTo(String oldName, String newPath, String currentPath) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
		Path src = new Path(currentPath + oldName);
		Path dst = new Path(newPath + oldName);
		boolean result = FileContext.getFileContext().util().copy(src, dst);
		return result;
		
	}


	public List<HdfsFile> selectFile(String currentPath, String type,Map<String,Set<String>> map) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IllegalArgumentException, IOException {
		RemoteIterator<LocatedFileStatus> ri = HdfsDao.getFileSystem().listFiles(new Path(currentPath), true);
		List<HdfsFile> list = new ArrayList<>();
		
		while(ri.hasNext()){
			LocatedFileStatus lfs = ri.next();
			String fileName = lfs.getPath().getName();
			if (map != null){
				if ("0".equals(type)){
					addList(list, lfs);
				}else{
					Set<String> set = map.get(type);
					for (String ext : set) {
						if (fileName.endsWith(ext)){
							addList(list, lfs);
						}
					}
				}
			}
			/*if("1".equals(type)){
				if(fileName.endsWith(".jpg") || fileName.endsWith(".JPG") || fileName.endsWith(".png") || fileName.endsWith(".PNG")){
					addList(list, lfs);
				}
			}else if("2".equals(type)){
				if(fileName.endsWith(".txt") || fileName.endsWith(".TXT") || fileName.endsWith(".doc") || fileName.endsWith(".DOC")){
					addList(list, lfs);
				}
			}*/
		}
		
		/*FileStatus[] fs = FileContext.getFileContext().util().listStatus(new Path(currentPath), new PathFilter() {
			@Override
			public boolean accept(Path path) {
				String fileName = path.getName();
				if("1".equals(type)){
					if(fileName.endsWith(".jpg") || fileName.endsWith(".JPG") || fileName.endsWith(".png") || fileName.endsWith(".PNG")){
						return true;
					}
				}
				return false;
			}
		});
		
		List<HdfsFile> list = getFiles(fs);*/
		
		return list;
	}


	private void addList(List<HdfsFile> list, LocatedFileStatus lfs) {
		HdfsFile hf = new HdfsFile();
		Path p = lfs.getPath();
		String fileName = p.getName();
		hf.setFileName(fileName);
		
		boolean b = lfs.isFile();
		hf.setFile(b);
		
		Long len = null;
		String sizeString = null;
		if ( b ){
			len = lfs.getLen();
			sizeString = formatSize(len);
			hf.setSizeString(sizeString);
		}
		long mt = lfs.getModificationTime();
		Date d = new Date(mt);
		DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		String dateString = df.format( d );
		hf.setDateString(dateString);
		hf.setFilePath(lfs.getPath().toString());
		list.add(hf);
	}


	public String downloadFile(String currentPath, String fileName, String local) throws Exception {
		String src = currentPath + fileName;
		String dest = local + fileName;
		
		Path p = new Path(src);
		if ( !HdfsDao.getFileSystem().isFile(p)){
			throw new Exception(src + " is not a valid file");
		}
		
		HdfsDao.getFileSystem().copyToLocalFile(p, new Path(dest));
		
		
		return dest;
	}



}
