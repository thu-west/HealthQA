package knowledge.dbdict;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import platform.util.database.DBConfig;
import platform.util.database.MysqlConnection;
import platform.util.database.ValueTransfer;
import platform.util.log.Trace;

public class BaiduBaikeToDB {
	
	static final Trace t = new Trace().setValid(true, true);
	
	static MysqlConnection con;
	
	public BaiduBaikeToDB () {
		con = new MysqlConnection(new DBConfig("jdbc:mysql://127.0.0.1:3308/MedDict", "root", "123qwe"));
		con.connect();
	}
	
	void insert(Map<String, String> values ) {
		if(values.isEmpty()) return;
		String tables = "";
		String vs = "";
		for(Entry<String, String> e: values.entrySet()) {
			tables = tables + "`"+e.getKey()+"`,";
			vs = vs + ValueTransfer.SqlValueFor(e.getValue())+",";
		}
		tables = tables.substring(0, tables.length()-1);
		vs = vs.substring(0, vs.length()-1);
//		vs = vs.replace("　"," ").replaceAll("[\\s]+", " ").replaceAll("^[\\s]+", "").replaceAll("[\\s]+$", "");
		String cmd = "insert into baidubaike ("+tables+") values ("+vs+")";
//		System.out.println(cmd);
		con.update(cmd);
	}
	
	void toDB( ) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("C:/Users/Fire/Desktop/baidu-dump.dat"), "utf8"));
		String line = null;
		Trace tt = new Trace(34569999, 500);
		Map<String, String> values = new HashMap<String, String>();
		int i = 0;
		while( (line=br.readLine()) != null ) {
			i++;
			if(line.isEmpty()) {
				// write into database
				i--;
				insert(values);
				values = new HashMap<String, String>();
				continue;
			}
			tt.debug("processing ", true);
			if(i<33600500) continue;
//			System.out.println(i);
			 
			try{
				
				int k = line.indexOf(":");
				String key = line.substring(0, k);
				String value = line.substring(k+1, line.length());
				values.put(key.toLowerCase(), value);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(!values.isEmpty()) 
			insert(values);
		br.close();
	}
	
	static void statFile( ) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("C:/Users/Fire/Desktop/baidu-dump.dat"), "utf8"));
		String line = null;
		Map<String, Integer> set = new HashMap<String, Integer>();
		Trace tt = new Trace(34569999, 5000);
		
		while( (line=br.readLine()) != null ) {
			if(line.isEmpty())  continue;
			try{
				tt.debug("processing ", true);
				int k = line.indexOf(":");
				String key = line.substring(0, k);
				String value = line.substring(k+1, line.length());
				if(set.containsKey(key)) {
					int temp  = set.get(key);
					set.put(key, temp>value.length()?temp:value.length());
				} else {
					set.put(key, value.length());
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		for(Entry<String, Integer> s : set.entrySet()) {
			System.out.println(s.getKey()+": "+s.getValue());
		}
		br.close();
	}
	
	public static void main(String[] args) throws IOException {
//		new BaiduBaikeToDB().toDB();
//		System.out.println("　 ；fdsajfl 　fksa".replace("　"," ").replaceAll("[\\s]+", " ").replaceAll("^[\\s]+", "").replaceAll("[\\s]+$", ""));
	}

}
