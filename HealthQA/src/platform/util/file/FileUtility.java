package platform.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtility {
	public static BufferedWriter newBufferedWriter(String filepath) throws IOException
	{
		String path = filepath.substring(0,filepath.lastIndexOf("\\"));
		File folder = new File(path);
		if(!folder.exists())
			folder.mkdirs();
		return new BufferedWriter( new FileWriter(filepath) );
	}
	
	public static void compare(String f1, String f2, String o) throws IOException, FileNotFoundException {
		BufferedReader br1 = new BufferedReader(new InputStreamReader( new FileInputStream(f1), "utf8"));
		BufferedReader br2 = new BufferedReader(new InputStreamReader( new FileInputStream(f2), "utf8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(o), "utf8"));
		String line1 = null, line2=null;
		while( (line1=br1.readLine())!= null ) {
			line2 = br2.readLine();
			if(line2.equals(line1))
				continue;
			bw.write(line1);
			bw.newLine();
			bw.write(line2);
			bw.newLine();
			bw.newLine();
		}
		bw.close();
		br1.close();
		br2.close();
	}
}
