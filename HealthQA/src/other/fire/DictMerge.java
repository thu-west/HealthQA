package other.fire;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import platform.util.log.Trace;

public class DictMerge {
	
	public static void merge_2 () throws Exception {
		String f = "io/1-merge.txt";
		String fo = "io/full-med.dic";
		String fo_df = "io/full-med-with-df.dic";
		BufferedReader br = new BufferedReader( new FileReader( f ));
		String line = null;
		Map<String, Integer> map = new HashMap<String, Integer>();
		while ((line = br.readLine()) != null) {
			String key = line.split("\t")[0];
			int value = Integer.parseInt( line.split("\t")[1] );
			if( map.containsKey(key) )
				map.put(key, map.get(key)+value);
			else
				map.put(key, value);
		}
		br.close();
		
		ArrayList<Entry<String,Integer>> l = new ArrayList<Entry<String,Integer>>(map.entrySet());    
        
        Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {    
            @Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {    
                return o2.getValue().compareTo(o1.getValue());
            }    
        });
		
		BufferedWriter bw = new BufferedWriter( new FileWriter( fo ));
		BufferedWriter bw2 = new BufferedWriter( new FileWriter( fo_df ));
		Iterator<Entry<String,Integer>> it = l.iterator();
		while( it.hasNext() ) {
			Entry<String,Integer> entry = it.next();
			bw2.write(entry.getKey() + "\t" + entry.getValue());
			bw2.newLine();
			bw.write(entry.getKey());
			bw.newLine();
		}
		bw.close();
		bw2.close();
	}
	
	
	
	public static void merge_1 () throws Exception{
//		String[] fs = new String[]{
//				"res/1.txt"
//				, "res/2.txt"
//				, "res/3.txt"
//				, "res/4.txt"
//				, "res/5.txt"
//				, "res/6.txt"
//				, "res/7.txt"
//		};
		String[] fs = new String[]{
				"D:\\中药.txt"
				, "D:\\药.txt"
		};
		
		Set<String> set = new TreeSet<String>();
		String line = null;
		for( String f : fs ) {
			BufferedReader br = new BufferedReader( new FileReader( f ));
			while ((line = br.readLine()) != null) {
				set.add(line);
			}
			br.close();
		}
		BufferedWriter bw = new BufferedWriter( new FileWriter( "D:\\merge.dic"));
		for( String s : set ){
			bw.write(s);
			bw.newLine();
		}
		bw.close();
	}
	
	
	public static void merge_baike( ) throws IOException {
		String f = "io/ext.dic";
		String f2 = "io/baike2-4.dic";
		String fo1 = "io/ext包含百科的词汇-1.dic";
		String fo = "io/ext包含百科的词汇.dic";
		BufferedReader br = new BufferedReader( new FileReader( f ));
		BufferedReader br2 = new BufferedReader( new FileReader( f2 ));
		
		Set<String> a = new HashSet<String>();
		String line = null;
		while ( (line=br.readLine())!=null) {
			a.add(line);
		}
		br.close();
		
		Set<String> b = new HashSet<String>();
		while ( (line=br2.readLine()) != null ) {
			b.add(line);
		}
		br2.close();
		
		Trace t1 =new Trace( b.size(), 1 );
		BufferedWriter bw1 = new BufferedWriter( new FileWriter( fo1 ));
		BufferedWriter bw = new BufferedWriter( new FileWriter( fo ));
		int count = 0;
		for( String bs : b ){
//			if((count++)>10000)
//				break;
			t1.debug("detect "+count+" words", true);
			for( String as : a ){
//				String temp = Algorithm.longestCommmonSubSquence(as, bs);
//				if( temp.length()>=2 && (temp.length()>as.length()-2 || temp.length()>bs.length()-2 ) ) {
//					bw.write(bs +" -> "+as);
//					bw.newLine();
//				}
				if(as.contains(bs) && !as.equals(bs)) {
					bw1.write(bs+" -> "+as);
					bw.write(bs);
					bw1.newLine();
					bw.newLine();
					count++;
					break;
				}
			}
		}
		bw.close();
		bw1.close();
	}
	
	public static void main(String[] args) throws Exception {
//		merge_2();
//		merge_baike();
		merge_1();
	}

}
