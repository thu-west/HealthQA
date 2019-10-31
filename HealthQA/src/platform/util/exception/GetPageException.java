package platform.util.exception;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;

public class GetPageException extends Exception {
	public String url;
	public String type;
	static HashSet<String> list;
	static {
		list = new HashSet<String>();
	}
	static public String savepath = "D:\\";
	
	public GetPageException(String _url, String _type) {
		super(_type+" ["+_url+"]");
		url = _url;
		type = _type;
		list.add(url+" ["+type+"]");
		try {
			log(_url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected static void log(String url) throws IOException {
		BufferedWriter bw = new BufferedWriter( new FileWriter( savepath+"load-failed-page.txt", true));
		bw.newLine();
		bw.write(url);
		bw.close();
	}
	
	public static void save(String filename){
		File f = new File(filename);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat s = new SimpleDateFormat("MM_dd_hh_mm");
		File nf = new File(filename+"-"+s.format(c.getTime())+".backup");
		if(f.exists()){
			f.renameTo(nf);
			System.out.println("GetPageException: old file back up in "+filename+"-"+s.format(c.getTime())+".backup");
		}
		System.out.println("GetPageException: saving list in "+filename);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			Iterator<String> i = list.iterator();
			while(i.hasNext()){
				bw.write(i.next());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
