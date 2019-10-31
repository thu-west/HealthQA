package other.fire;

import java.io.*;
import java.util.*;

import application.qav2.dao.HealthqaDB;
import platform.util.log.Trace;

public class DictTest {
	
	public static void A() throws Exception {
		
		System.out.println("read file");
		BufferedReader br = new BufferedReader( new FileReader( "res/full-med.dic" ));
		String line = null;
		Map<String, Integer> dict = new HashMap<String,Integer>();
		while( (line=br.readLine()) !=null ){
			dict.put(line, 0);
		}
		br.close();
		
		System.out.println("read database");
		String[] qs = HealthqaDB.getAllAnswer();
		
		System.out.println("checking");
		Trace t = new Trace(qs.length, qs.length/1000);
		for( String q : qs ){
			t.debug("checking answer", true);
			Iterator<String> it = dict.keySet().iterator();
			while( it.hasNext() ){
				String key = it.next();
				if( q.contains(key) ){
					dict.put(key, dict.get(key)+1);
				}
			}
		}
		
		System.out.println("writing into file");
		BufferedWriter bw = new BufferedWriter( new FileWriter( "io/q_dict_stat.txt"));
		Iterator<String> it = dict.keySet().iterator();
		while( it.hasNext() ){
			String key = it.next();
			bw.write( key + "\t" + dict.get(key));
			bw.newLine();
		}
		bw.close();
		
	}
	
	public static void Q() throws Exception {
		
		System.out.println("read file");
		BufferedReader br = new BufferedReader( new FileReader( "io\\药.txt" ));
		String line = null;
		Map<String, Integer> dict = new HashMap<String,Integer>();
		while( (line=br.readLine()) !=null ){
			dict.put(line, 0);
		}
		br.close();
		
		System.out.println("read database");
		String[] qs = HealthqaDB.getAllQuestion();
		
		System.out.println("checking");
		Trace t = new Trace(qs.length, qs.length/1000);
		for( String q : qs ){
			t.debug("checking question", true);
			Iterator<String> it = dict.keySet().iterator();
			while( it.hasNext() ){
				String key = it.next();
				if( q.contains(key) ){
					dict.put(key, dict.get(key)+1);
				}
			}
		}
		
		System.out.println("writing into file");
		BufferedWriter bw = new BufferedWriter( new FileWriter( "io\\药-df.txt"));
		Iterator<String> it = dict.keySet().iterator();
		while( it.hasNext() ){
			String key = it.next();
			bw.write( key + "\t" + dict.get(key));
			bw.newLine();
		}
		bw.close();
		
	}
	
	
	public static void zijizuo () throws Exception {
		BufferedReader br = new BufferedReader( new FileReader( "io/question_dict_stat.txt" ));
		BufferedWriter bw = new BufferedWriter( new FileWriter( "io/question_dict_stat_1.txt"));
		String a = br.readLine();
		System.out.println(a.length());
		String[] str = a.split("\t");
		for( String line : str){
			int j=0;
			if(line.length()<=1) {
				bw.write(line.substring(0, j));
				continue;
			}
//			System.out.println(line.charAt(j));
			try{
			while( line.charAt(j) >= '0' && line.charAt(j) <= '9'){
				j++;
			}
			}catch(Exception e) {
				System.out.println(line + "     " +j);
				throw e;
			}
			bw.write(line.substring(0, j));
			bw.newLine();
			bw.write(line.substring(j) + "\t");
		}
		br.close();
		bw.close();
	}
	
	public static void main(String[] args) throws Exception {
//		Q();
		A();
	}

}
