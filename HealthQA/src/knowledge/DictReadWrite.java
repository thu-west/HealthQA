package knowledge;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import platform.util.log.Trace;

public class DictReadWrite {
	
	static Trace t = new Trace().setValid(false, false);
	
	public static Set<String> loadDictInStringSet( String dictfile ) throws Exception {
		Set<String> set = new HashSet<String>();
//		BufferedReader br = new BufferedReader( new FileReader( dictfile ));
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(dictfile), "utf8"));
		String line = null;
		t.debug("Loading dict: "+dictfile);
		while( (line=br.readLine()) != null ) {
			set.add(line);
		}
		br.close();
		return set;
	}
	
	public static void saveDictInStringIntegerMap( Map<String, Integer> dict, String filename ) throws Exception {
		List<Map.Entry<String,Integer>> list=new ArrayList<Map.Entry<String,Integer>>();  
        list.addAll(dict.entrySet());  
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> arg0,
					Entry<String, Integer> arg1) {
				return arg1.getValue().compareTo(arg0.getValue());
			}
		});
        BufferedWriter bw = new BufferedWriter( new FileWriter( filename ));
        for(Iterator<Map.Entry<String,Integer>> it=list.iterator(); it.hasNext();)  
        {
        	Map.Entry<String, Integer> en = it.next();
            bw.write(en.getKey());
            bw.newLine();
        }
        bw.close();
	}
	
	public static void saveStatisticInStringStatMap( Map<String, Stat> dict, String filename ) throws Exception {
		List<Map.Entry<String,Stat>> list=new ArrayList<Map.Entry<String,Stat>>();  
        list.addAll(dict.entrySet());  
        ValueComparator vc=new ValueComparator();
        Collections.sort(list,vc);
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(filename), "GBK"));
        for(Iterator<Map.Entry<String,Stat>> it=list.iterator();it.hasNext();)  
        {
        	Map.Entry<String, Stat> en = it.next();
            bw.write(en.getKey() + "\t" + en.getValue());
            bw.newLine();
        }
        bw.close();
	}
	
	public static void saveStatisticInStringIntegerMap( Map<String, Integer> dict, String filename ) throws Exception {
		List<Map.Entry<String,Integer>> list=new ArrayList<Map.Entry<String,Integer>>();  
        list.addAll(dict.entrySet());  
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> arg0,
					Entry<String, Integer> arg1) {
				return arg1.getValue().compareTo(arg0.getValue());
			}
		});
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(filename), "GBK"));
//        BufferedWriter bw = new BufferedWriter( new FileWriter( filename ));
        for(Iterator<Map.Entry<String,Integer>> it=list.iterator(); it.hasNext();)  
        {
        	Map.Entry<String, Integer> en = it.next();
            bw.write(en.getKey() + "\t" + en.getValue());
            bw.newLine();
        }
        bw.close();
	}
	
	public static void saveDictInStringSet( Set<String> dict, String filename ) throws Exception {
		Iterator<String> it = dict.iterator();
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		while (it.hasNext()) {
			bw.write(it.next());
			bw.newLine();
		}
        bw.close();
	}
	
	public static Map<String, Stat> loadStatisticInStringStatMap( String filename ) throws Exception {
		t.debug("Loading dict: "+filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename), "utf8"));
		HashMap<String, Stat> dict = new HashMap<String, Stat>(10000, 0.1f);
		String line = null;
		while( (line=br.readLine()) != null ) {
			dict.put(line, new Stat());
		}
		br.close();
		return dict;
	}

}
