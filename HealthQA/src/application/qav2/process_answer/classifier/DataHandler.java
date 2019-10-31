package application.qav2.process_answer.classifier;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import platform.Platform;
import platform.mltools.TFIDFProcessor;
import platform.nlp.ao.Word;

public class DataHandler {
	
	static class Item {
		public String plain_text;
		public Word[][] seg_word;
		public Map<String, Integer> tag_appearance;
		public Item () {
			plain_text = "";
			tag_appearance = new HashMap<String, Integer>();
		}
		public void addTag(String tag) {
			Integer v = tag_appearance.get(tag);
			if(v==null) tag_appearance.put(tag, 1);
			else tag_appearance.put(tag, v+1);
		}
	}
	
	public static Map<String, HashSet<Item>> readRichText (String filename, Map<String, Integer> label_ktoi) throws Exception {
		HashMap<String, HashSet<Item>> collection = new HashMap<String, HashSet<Item>>();
		BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream(filename)));
		String line = null;
		Pattern p = Pattern.compile("\\\\([a-z]+)");
		while( (line=br.readLine()) != null ) {
			Item item = new Item();
			String content = line.split("\t")[1];
			String cate = line.split("\t")[0];
			if(cate.split("[ ]+").length>1) {
				continue;
			}
			if(!label_ktoi.containsKey(cate)) {
				continue;
			}
			Matcher m = p.matcher(content);
			while(m.find()) {
				String temp_tag = m.group(1);
				item.addTag(temp_tag);
			}
			item.plain_text = content.replaceAll("\\\\[a-z]+", "").replace(" ", "");
			item.seg_word = Platform.segment(content);
			if(collection.containsKey(cate)) {
				collection.get(cate).add(item);
			} else {
				HashSet<Item> set = new HashSet<Item>();
				set.add(item);
				collection.put(cate, set);
			}
		}
		br.close();
		return collection;
	}
	
	public static Keyword getKeywordWithHighTfidf(Map<String, HashSet<Item>> collection, String[][] label_set, double tfidf_min_threshold) throws Exception {
		
		Keyword keyword = new Keyword();
		
		String[][] document_in_word = new String[label_set.length][];
		String[][] document_in_char = new String[label_set.length][];
		
		for( int i=0; i<label_set.length; i++) {
			List<String> wd_list = new ArrayList<String>();
			List<String> ch_list = new ArrayList<String>();
			for(String category: label_set[i]) {
				for( Item item: collection.get(category) ) {
					String content = item.plain_text;
					for( Word[] sent : item.seg_word) {
						for( Word w : sent) {
							wd_list.add(w.cont);
						}
					}
					for(int j=0; j<content.length(); j++) {
						ch_list.add(content.charAt(j)+"");
					}
				}
			}
			document_in_word[i] = new String[wd_list.size()];
			document_in_char[i] = new String[ch_list.size()];
			wd_list.toArray(document_in_word[i]);
			ch_list.toArray(document_in_char[i]);
		}
		
		TFIDFProcessor pword = new TFIDFProcessor(document_in_word, null);
		TFIDFProcessor pchar = new TFIDFProcessor(document_in_char, null);
		
		for(int i=0; i<label_set.length; i++) {
			String cate = StringUtils.join(label_set[i], ",");
			keyword.distinguish_categories = keyword.distinguish_categories + "("+cate+") ";
			
			for( Entry<String, Double> e: pword.getTFIDFOfDocument(i).entrySet() ) {
				String k = e.getKey();
				double v = e.getValue();
				if(v<tfidf_min_threshold) continue;
				keyword.addWord(k, new Keyword.Value(cate, v));
			}
			for( Entry<String, Double> e: pchar.getTFIDFOfDocument(i).entrySet()) {
				String k = e.getKey();
				double v = e.getValue();
				if(v<tfidf_min_threshold) continue;
				keyword.addChar(k, new Keyword.Value(cate, v));
			}
		}
		
		return keyword;
	}
	
	
	public static void main(String[] args) throws Exception {
	}

}
