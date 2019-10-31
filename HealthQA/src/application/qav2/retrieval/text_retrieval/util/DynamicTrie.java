package application.qav2.retrieval.text_retrieval.util;

import java.io.*;
import java.util.*;

import knowledge.DictGallary;

public class DynamicTrie {
	private TrieNode root;

	DynamicTrie() {
		root = new TrieNode();
	}

	class TrieNode {
		private char letter;
		private ArrayList<TrieNode> child;
		private Hashtable<Character, Integer> list;
		private ArrayList<String> types; //存储所有的分类编号
		private boolean isEnd; //是否为字符串的最后一个字
		
		TrieNode() {
			letter = 'a';
			child = new ArrayList<TrieNode>();
			list = new Hashtable<Character, Integer>();
			types = new ArrayList<String>();
			isEnd = false;
		}
	}

	public void insertWord(String word, String type) {
		if(word == null || word.length() == 0) return;

		TrieNode node = root;

		for(int i = 0; i < word.length(); i++){
			//中文字符Unicode: U+4E00（一）..U+9FA5 （龥），共20901个字。
			//只是“中日韩统一表意文字”这个区间，不包括偏旁和标点符号;
			int pos;
			if(node.list.get(word.charAt(i)) == null) {
				pos = node.list.size();
				node.list.put(word.charAt(i), pos);
				node.child.add(new TrieNode());
				node.child.get(pos).letter = word.charAt(i);
			} else pos = node.list.get(word.charAt(i));
			node = node.child.get(pos);
		}
		node.isEnd = true;
		node.types.add(type);
	}

	//找词语
	public boolean has(String word) {
		if (word == null || word.length() == 0) return false;

		TrieNode node = root;
		for (int i = 0, len = word.length(); i < len; i++) {
			int pos = node.list.get(word.charAt(i));
			if (node.child.get(pos) != null) node = node.child.get(pos); 
			else return false;
		}
		return node.isEnd;
	}
	
	ArrayList<String> getTypes(String word) {
		if (word == null || word.length() == 0) return null;

		TrieNode node = root;
		for (int i = 0, len = word.length(); i < len; i++) {
			if (node.list.get(word.charAt(i)) != null) {
				int pos = node.list.get(word.charAt(i));
				if (node.child.get(pos) != null) node = node.child.get(pos); else return null;
			}
			else return null;
		}
		return node.types;
	}

	public TrieNode getRoot(){
		return this.root;
	}
	
	protected static void loadCilin(DynamicTrie cilin) throws IOException {
		String line;
		BufferedReader fi = new BufferedReader(new FileReader(DictGallary.cilin));
		line = fi.readLine();
		//int j = 1;
		while (line != null) {
			String[] strs = line.split("[=@# ]");
			for (int i = 2; i < strs.length; i++) cilin.insertWord(strs[i], strs[0]);
			//System.out.println(j + "th line has been loaded.");
			//cilin.printAllWords();
			//j++;
			line = fi.readLine();
		}
		fi.close();
		System.out.println("Cilin has been loaded successfully.");
		
	}

	protected static void main(String[] args) throws IOException {
		System.out.println("Running Trie.java");
		DynamicTrie cilin = new DynamicTrie();
		long stime = System.currentTimeMillis();
		loadCilin(cilin);
		System.out.println(System.currentTimeMillis() - stime);
		System.out.println(cilin.getRoot().child.size());
		TrieNode a = cilin.getRoot().child.get(cilin.getRoot().list.get('生')).child.get(0);
		System.out.println(a.letter);
		for(String b:a.types) System.out.println(b);
		System.out.println(cilin.has("糖尿病"));
		System.out.println(cilin.has("糖尿"));
		//System.out.println("Constructed a trie: tree");
	}
}