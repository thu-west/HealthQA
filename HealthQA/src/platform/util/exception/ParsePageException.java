package platform.util.exception;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;

public class ParsePageException extends Exception{
	public String url;
	public String type;
	static HashSet<String> list;
	static {
		list = new HashSet<String>();
	}
	
	public ParsePageException(String _url, String _type) {
		super(_type+" ["+_url+"]");
		url = _url;
		type = _type;
		list.add(url+" ["+type+"]");
	}
	
	public static void save(String filename){
		File f = new File(filename);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat s = new SimpleDateFormat("MM_dd_hh_mm");
		File nf = new File(filename+"-"+s.format(c.getTime())+".backup");
		if(f.exists()){
			f.renameTo(nf);
			System.out.println("ParsePageException: old file back up in "+filename+"-"+s.format(c.getTime())+".backup");
		}
		System.out.println("ParsePageException: saving list in "+filename);
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
