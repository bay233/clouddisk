package com.web.dao;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

public class HdfsDao {
	private static Configuration conf;
	static{
		System.setProperty("HADOOP_USER_NAME", "root");
		conf = new Configuration();
	}
	
	public static FileSystem getFileSystem() throws IOException{
		return FileSystem.get(conf);
	}
}
