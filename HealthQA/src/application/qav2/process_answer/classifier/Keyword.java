package application.qav2.process_answer.classifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Keyword {
	
	static class Value{
		String belong_category;
		Double tfidf;
		public Value(String _belong_category, double _tfidf) { belong_category=_belong_category; tfidf=_tfidf; }
		public double getFactor() {
			return Parameter.TFIDF_FACTOR*Math.pow(tfidf, 2);
		}
	}
	
	public Map<String, Integer> ktoi = new HashMap<String, Integer>();
	public Map<Integer, String> itok = new HashMap<Integer, String>();
	int index = -1;
	
	public String distinguish_categories;
	public Map<String, Keyword.Value> word;
	public Map<String, Keyword.Value> charactor;
	public Map<String, Keyword.Value> all;
	public Keyword() {
		word = new HashMap<String, Keyword.Value>();
		charactor = new HashMap<String, Keyword.Value>();
		all = new HashMap<String, Keyword.Value>();
		distinguish_categories = "";
	}
	
	public void write( String filename ) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filename), "utf8"));
		for(Entry<String, Integer> e: ktoi.entrySet()) {
			String k = e.getKey();
			int i = e.getValue();
			Value v = all.get(k);
			bw.write(i+"\t"+k+"\t"+v.belong_category+"\t"+v.tfidf);
			bw.newLine();
		}
		bw.close();
	}
	
	public static Keyword read( String filename ) throws IOException {
		Keyword kw = new Keyword();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename), "utf8"));
		String line = null;
		while( (line=br.readLine()) != null ) {
			String[] temp = line.split("\t");
			int i = Integer.parseInt(temp[0]);
			String k = temp[1];
			String cate = temp[2];
			double tfidf = Double.parseDouble(temp[3]);
			kw.ktoi.put(k, i);
			kw.itok.put(i, k);
			kw.all.put(k, new Value(cate, tfidf));
		}
		br.close();
		return kw;
	}
	
	public void addWord(String key, Value value) {
		String cate = value.belong_category;
		double v = value.tfidf;
		Value sd = word.get(key);
		if(sd==null) {
			word.put(key, new Keyword.Value(cate, v));
		} else {
			word.put(key, new Keyword.Value(sd.belong_category+"|"+cate, sd.tfidf>v?v:sd.tfidf));
		}
		sd = all.get(key);
		if(sd==null) {
			all.put(key, new Keyword.Value(cate, v));
			ktoi.put(key, ++index);
			itok.put(index, key);
		} else {
			all.put(key, new Keyword.Value(sd.belong_category+"|"+cate, sd.tfidf>v?v:sd.tfidf));
		}
	}
	
	public void addChar(String key, Value value) {
		String cate = value.belong_category;
		double v = value.tfidf;
		Value sd = charactor.get(key);
		if(sd==null) {
			charactor.put(key, new Keyword.Value(cate, v));
		} else {
			charactor.put(key, new Keyword.Value(sd.belong_category+"|"+cate, sd.tfidf>v?v:sd.tfidf));
		}
		sd = all.get(key);
		if(sd==null) {
			all.put(key, new Keyword.Value(cate, v));
			ktoi.put(key, ++index);
			itok.put(index, key);
		} else {
			all.put(key, new Keyword.Value(sd.belong_category+"|"+cate, sd.tfidf>v?v:sd.tfidf));
		}
	}
}