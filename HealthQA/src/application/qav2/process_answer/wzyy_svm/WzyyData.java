package application.qav2.process_answer.wzyy_svm;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class WzyyData {
	
	public static final String work_dir = "res/data/complete-semantic-split-svm/";
	public static String workDir(String filename) {
		return work_dir + filename;
	}
	
	public static class Sample {
		int label;
		String front;
		String before;
		String after;
		public Sample(int _label, String _front, String _before, String _after) {
			label = _label;
			front = _front;
			before = _before;
			after = _after;
		}
	}
	
	public static class Keyword {
		public int size;
		public Map<String, Integer> ktoi = new HashMap<String, Integer>();
		public Map<Integer, String> itok = new HashMap<Integer, String>();
		public void write( String filename ) throws IOException {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename), "utf8"));
			bw.write(""+size);
			bw.newLine();
			for(Entry<String, Integer> e: ktoi.entrySet()) {
				String k = e.getKey();
				int i = e.getValue();
				bw.write(k+"\t"+i);
				bw.newLine();
			}
			bw.close();
		}
		public Keyword read( String filename ) throws IOException {
			ktoi.clear();
			itok.clear();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "utf8"));
			size = Integer.parseInt(br.readLine());
			String line = null;
			while( (line=br.readLine()) != null ) {
				String[] temp = line.split("\t");
				String k = temp[0];
				int i = Integer.parseInt(temp[1]);
				ktoi.put(k, i);
				itok.put(i, k);
			}
			br.close();
			return this;
		}
	}
	
	public Keyword front_keyword = new Keyword();
	public Keyword before_keyword = new Keyword();
	public Keyword after_keyword = new Keyword();
	public static final int gram = 3;
	public ArrayList<Sample> samples = new ArrayList<Sample>();
	
	Integer[] puncs;
	String[] sents;
	ArrayList<String> l_sents = new ArrayList<String>();
	ArrayList<Integer> l_puncs = new ArrayList<Integer>();
	
	void read() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(workDir("wzyy.origin")), "utf8"));
		String line = null;
		while((line=br.readLine())!=null) {
			line = line.replace("，", "， ").replace("。", "。 ");
			String[] pieces = line.split(" ");
			for(String p: pieces) {
				if(p.isEmpty()) continue;
				int len = p.length();
				l_sents.add(p.substring(0, len-1));
				l_puncs.add(p.charAt(len-1)=='。'?1:-1);
			}
			l_puncs.add(0);
			l_sents.add("");
		}
		puncs = new Integer[l_puncs.size()];
		sents = new String[l_sents.size()];
		l_puncs.toArray(puncs);
		l_sents.toArray(sents);
		br.close();
	}
	
	void getSamples() {
		for( int i=0; i<puncs.length-1; i++ ) {
			if(puncs[i]==0) continue;
			String s = sents[i];
			String front = null;
			String before = null;
			String after = null;
			if(s.length()<=gram) {
				front = before = s;
			} else {
				front = s.substring(0, gram);
				before = s.substring(s.length()-gram, s.length());
			}
			if(puncs[i+1]!=0) {
				if(sents[i+1].length()<=gram) {
					after = sents[i+1];
				} else {
					after = sents[i+1].substring(0, gram);
				}
			}
			samples.add(new Sample(puncs[i], front, before, after));
		}
	}

	void generatekeywords() throws IOException {
		
		for(Sample s: samples ) {
			String ts  =s.front;
			Keyword tf = front_keyword;
			for(int k=0; k<ts.length(); k++) {
				String key = ts.charAt(k)+"";
				if(!tf.ktoi.containsKey(key)) {
					tf.ktoi.put(key, tf.size);
					tf.itok.put(tf.size, key);
					tf.size++;
				}
			}
			
			ts  =s.before;
			tf = before_keyword;
			for(int k=0; k<ts.length(); k++) {
				String key = ts.charAt(k)+"";
				if(!tf.ktoi.containsKey(key)) {
					tf.ktoi.put(key, tf.size);
					tf.itok.put(tf.size, key);
					tf.size++;
				}
			}
			
			if(s.after==null) continue;
			
			ts  =s.after;
			tf = after_keyword;
			for(int k=0; k<ts.length(); k++) {
				String key = ts.charAt(k)+"";
				if(!tf.ktoi.containsKey(key)) {
					tf.ktoi.put(key, tf.size);
					tf.itok.put(tf.size, key);
					tf.size++;
				}
			}
		}
		front_keyword.write(workDir("keyword/front.keyword"));
		before_keyword.write(workDir("keyword/before.keyword"));
		after_keyword.write(workDir("keyword/after.keyword"));
	}
	
	public void loadKeywords() throws IOException {
		front_keyword.read(workDir("keyword/front.keyword"));
		before_keyword.read(workDir("keyword/before.keyword"));
		after_keyword.read(workDir("keyword/after.keyword"));
	}
	
	public static void main(String[] args) throws IOException {
		WzyyData wd = new WzyyData();
		wd.read();
		wd.getSamples();
		wd.generatekeywords();
		WzyyData wd2 = new WzyyData();
		wd2.loadKeywords();
		
		for(Sample s: wd.samples) {
			System.out.println(s.label+"\t"+s.front+"\t"+s.before+"\t"+s.after);
		}
	}
}
