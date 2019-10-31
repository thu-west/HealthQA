package knowledge;

import java.util.*;
import java.util.Map.Entry;

import platform.Platform;
import platform.nlp.ao.Word;
import platform.util.MyString;
import platform.util.general.*;

public class DictTree {
	
	public static class Node {
		
		public String cont;
		public String type;
		public int words;
		public boolean is_word;
		public Node parent;
		public Map<String, Node> children;
		
		public Node(String _cont, Node _parent) {
			cont = _cont;
			is_word = false;
			type = null;
			words = 0;
			parent = _parent;
			children = null;
		}
	}
	
	Node root = null;
	static Set<String> dictionary;
	
	private void insert( String word, Node root ) {
		root.words++;
		if(word.length()<1) {
			root.is_word = true;
			return;
		}
		
		String curr = word.substring(0, 1);
		if(root.children==null)
			root.children = new HashMap<String, Node>();
		Node n = root.children.get(curr);
		if( n==null ) {
			n = new Node(curr, root);
			root.children.put(curr, n);
		}
		insert( word.substring(1), n);
	}
	
	
	public static void insertWithType( String word, Node root, String type ) {
		root.words++;
		if(word.length()<1) {
			root.is_word = true;
			if(root.type!=null) {
				Node tt = root;
				StringBuffer sb = new StringBuffer();
				while(tt.parent!=null) { sb.insert(0, tt.cont); tt=tt.parent; }
				type = root.type + "-" + type;
			}
			root.type = type;
			return;
		}
		
		String curr = word.substring(0, 1);
		if(root.children==null)
			root.children = new HashMap<String, Node>();
		Node n = root.children.get(curr);
		if( n==null ) {
			n = new Node(curr, root);
			root.children.put(curr, n);
		}
		insertWithType( word.substring(1), n, type);
	}
	
	private void insertWord( Word[] word, int index, Node root ) {
		root.words++;
		if(index == word.length) {
			root.is_word = true;
			return;
		}
		
		Word curr = word[index];
		if(root.children==null)
			root.children = new HashMap<String, Node>();
		Node n = root.children.get(curr.cont);
		if( n==null ) {
			n = new Node(curr.cont, root);
			root.children.put(curr.cont, n);
		}
		insertWord( word, ++index, n);
	}
	
	public Node buildPrefixTree( Set<String> set, boolean seg_word ) throws Exception {
		Iterator<String> it = set.iterator();
		root = new Node("", null);
		while( it.hasNext() ) {
			if( seg_word ) {
				Word[] word = Platform.segment(it.next())[0];
				insertWord(word, 0, root);
			} else {
				String word = it.next();
				insert(word, root);
			}
		}
		return root;
	}
	
	public Node buildSuffixTree( Set<String> set, boolean seg_word) throws Exception {
		Iterator<String> it = set.iterator();
		Node root = new Node("", null);
		while( it.hasNext() ) {
			if(seg_word) {
				Word[] word = StringUtil.reverseWord(Platform.segment(it.next())[0]);
				insertWord(word, 0, root);
			} else {
				String word = MyString.reverse(it.next());
				insert(word, root);
			}
		}
		return root;
	}
	
	public static void printTree( Node root, String prefix ) {
		if( root.is_word ) {
			System.out.println( prefix + root.cont );
			return;
		}
		Set<Entry<String, Node>> ts = root.children.entrySet();
		Iterator<Entry<String, Node>> it = ts.iterator();
		while (it.hasNext()) {
			Entry<String, Node> e = it.next();
			printTree(e.getValue(), prefix + root.cont);
		}
	}
	
	public static void printQianxuTree( Node root, String prefix ) {
		System.out.println("["+root.cont+"-"+root.words+"-"+root.is_word+"-"+root.type+"]");
		if( root.is_word ) {
			return;
		}
		Set<Entry<String, Node>> ts = root.children.entrySet();
		Iterator<Entry<String, Node>> it = ts.iterator();
		while (it.hasNext()) {
			Entry<String, Node> e = it.next();
			printQianxuTree(e.getValue(), prefix + root.cont);
		}
	}
	
	public void shrinkPrefixTree() {
		if(dictionary == null ) {
			try {
				dictionary = DictReadWrite.loadDictInStringSet(DictGallary.baike);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		shrinkPrefixTreeHelper(root);
	}
	
	public static void shrinkPrefixTreeHelper( Node root ) {
		if( root.children == null )
			return;
		Iterator<Entry<String, Node>> it = root.children.entrySet().iterator();
		while( it.hasNext() ) {
			Node n = it.next().getValue();
			shrinkPrefixTreeHelper(n);
			String cont = root.cont + n.cont;
			if( n.words == root.words && dictionary.contains(cont) ) {
				root.cont = cont;
				root.is_word = n.is_word;
				root.children = n.children;
				break;
			}
		}
	}
	
	public void shrinkSuffixTree() {
		if(dictionary == null ) {
			try {
				dictionary = DictReadWrite.loadDictInStringSet(DictGallary.baike);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		shrinkSuffixTreeHelper(root);
	}
	
	private void shrinkSuffixTreeHelper( Node root ) {
		if( root.children == null )
			return;
		Iterator<Entry<String, Node>> it = root.children.entrySet().iterator();
		while( it.hasNext() ) {
			Node n = it.next().getValue();
			shrinkSuffixTreeHelper(n);
			String cont = n.cont + root.cont;
			if( n.words == root.words && dictionary.contains(cont) ) {
				root.cont = cont;
				root.is_word = n.is_word;
				root.children = n.children;
				break;
			}
		}
	}
	
	private void generatePossiblePrefix( Node root, String prefix, Map<String, Integer> candidate) {
		if( dictionary.contains(prefix) ) {
			candidate.put(prefix, root.parent.words);
		}
		if(root.words<2)
			return;
		Iterator<Entry<String, Node>> it = root.children.entrySet().iterator();
		while( it.hasNext() ) {
			Node n = it.next().getValue();
			generatePossiblePrefix(n, prefix + root.cont, candidate);
		}
	}
	
	private void generatePossibleSuffix( Node root, String suffix, Map<String, Integer> candidate) {
		if( dictionary.contains(suffix) ) {
			candidate.put(suffix, root.parent.words);
		}
		if(root.words<2)
			return;
		Iterator<Entry<String, Node>> it = root.children.entrySet().iterator();
		while( it.hasNext() ) {
			Node n = it.next().getValue();
			generatePossibleSuffix(n, root.cont + suffix, candidate);
		}
	}
	
	void extractLeafWords( Node root, Set<String> set ) {
		if( root.children == null ) {
			set.add(root.cont);
			return;
		}
		Iterator<Entry<String, Node>> it = root.children.entrySet().iterator();
		while( it.hasNext() ) {
			Node n = it.next().getValue();
			extractLeafWords(n, set);
		}
	}
	
	public static void main(String[] args) throws Exception {
//		DictTree dt = new DictTree();
//		String path = DictPersistence.final_path+"med.dict";
//		Set<String> set = DictPersistence.loadDictInStringSet(path);
//		
//		{
//			Node root = dt.buildSuffixTree(set);
////			dt.shrinkSuffixTree(root);
//			dt.printQianxuTree(root, "");
//			Map<String, Integer> map = new HashMap<String, Integer>();
//			dt.generatePossibleSuffix(root, "", map);
//			Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
//			while( it.hasNext() ) {
//				Entry<String, Integer> e = it.next();
//				System.out.println(e.getKey() + ": " + e.getValue());
//			}
//			DictPersistence.saveStatisticInStringIntegerMap(map, DictPersistence.stat_path+"suffix.xls");
//			DictPersistence.saveDictInStringIntegerMap(map, DictPersistence.temp_path+"suffix.dict");
//		}
//		
//		{
//			Node root = dt.buildPrefixTree(set);
//			dt.shrinkPrefixTree(root);
////			Set<String> lws = new HashSet<String>();
////			dt.extractLeafWords(root, lws);
////			DictPersistence.saveDictInStringSet(lws, DictPersistence.temp_path+"tail.dict");
////			DictProcess.reDeOrder(DictPersistence.temp_path+"tail.dict");
//			Map<String, Integer> map = new HashMap<String, Integer>();
//			dt.generatePossiblePrefix(root, "", map);
//			System.out.println("Possible prefix");
//			Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
//			while( it.hasNext() ) {
//				Entry<String, Integer> e = it.next();
//				System.out.println(e.getKey() + ": " + e.getValue());
//			}
//			DictPersistence.saveStatisticInStringIntegerMap(map, DictPersistence.stat_path+"prefix.xls");
//			DictPersistence.saveDictInStringIntegerMap(map, DictPersistence.temp_path+"prefix.dict");
//		}
		
		DictTree dt = new DictTree();
		Set<String> set = DictReadWrite.loadDictInStringSet(DictGallary.final_path+"器官.dict");
		dt.buildPrefixTree(set, false);
		dt.shrinkPrefixTree();
		System.out.println(dt.root);
		DictTree.printQianxuTree(dt.root, "");
		
	}
}
