
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

public class test1 {
	private static FileSystem fs;
	private static Configuration conf;
	
	static {
		System.setProperty("HADOOP_USER_NAME", "root");
		conf = new Configuration();
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();		}
	}

	@Test
	public void TestMkdir() throws IOException{
		Path p = new Path("/yc3");
		boolean b = fs.mkdirs(p);
		if (b){
			System.out.println(p.getName() + "创建成功");
		}
	}

}
