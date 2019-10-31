package platform.mltools;

import java.util.*;
import java.util.Map.Entry;

public class TFIDFProcessor {
	/*
	 * input: store the documents to be processed, String is a term, String[] form a piece, String[][] form a document
	 */
	String[][] document_collection;
	/*
	 * input : default value is null, if you want to map the 1st dimension index(Integer) of document_collection to a set of String value.
	 */
	HashMap<String, Integer> docid_index_map;
	
	/*
	 * output : after processing, the variable stores all the different term in the collection
	 */
	public ArrayList<String> term_dictionary;
	/*
	 * output : after processing, the variable stores the tf-idf values of each word in each documents
	 */
	public HashMap<String, Double>[] documents_tfidf;

	HashMap<String, Double>[] tf;
	HashMap<String, Integer> df;
	HashMap<String, Double> idf;

	// if docid_index_map is not null, the function will be used to get the corresponding tfidf of a document
	public HashMap<String, Double> getTFIDFOfDocument(String id) {
		if (docid_index_map == null)
			return null;
		return documents_tfidf[docid_index_map.get(id)];
	}
	
	public HashMap<String, Double> getTFIDFOfDocument(int index) {
		return documents_tfidf[index];
	}

	public TFIDFProcessor(String[][] collection, HashMap<String, Integer> name_index_map) throws Exception {
		document_collection = collection;
		this.docid_index_map = name_index_map;
		calTFIDF();
	}

	private void calTFIDF() throws Exception {

		int total_doc = document_collection.length;
		if (total_doc == 0)
			throw new Exception("No document in collection!");

		tf = new HashMap[total_doc];
		df = new HashMap<String, Integer>();
		idf = new HashMap<String, Double>();
		documents_tfidf = new HashMap[total_doc];

		int i = -1;
		for (String[] line : document_collection) {
			tf[++i] = new HashMap<String, Double>();
			double max_tf = 1.0;
			for (String term : line) {
				if (tf[i].containsKey(term)) {
					double ctf = tf[i].get(term) + 1.0;
					max_tf = ctf>max_tf ? ctf : max_tf;
					tf[i].put(term, ctf);
				} else {
					tf[i].put(term, 1.0);
					if (df.containsKey(term))
						df.put(term, df.get(term) + 1);
					else
						df.put(term, 1);
				}
			}
//			System.out.println(max_tf);
			// normalize with the max raw term frequency, ref: http://en.wikipedia.org/wiki/Tf%E2%80%93idf
			for (Entry<String, Double> e: tf[i].entrySet()) {
				tf[i].put(e.getKey(), 0.5+0.5*e.getValue()/max_tf);
			}
		}

		for (Entry<String, Integer> e: df.entrySet()) {
			idf.put(e.getKey(), Math.log( (1.0 + total_doc) / (1.0 + e.getValue())));
		}
		term_dictionary = new ArrayList<String>(df.keySet());

		int j = -1;
		for (HashMap<String, Double> doc_tf : tf) {
			documents_tfidf[++j] = new HashMap<String, Double>();
			Iterator<String> it = doc_tf.keySet().iterator();
			while (it.hasNext()) {
				String term = it.next();
				documents_tfidf[j].put( term, doc_tf.get(term) * idf.get(term));
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String[][] s = new String[3][];
		s[0] = new String[] { "w", "e", "e","tt", "w","w"};
		s[1] = new String[] {"w"};
		s[2] = new String[] { "w", "r", "s", "tt", "sdfsa" };
		HashMap<String, Integer> docid_index_map = new HashMap<String, Integer>();
		docid_index_map.put("1", 0);
		docid_index_map.put("2", 1);
		TFIDFProcessor tip = new TFIDFProcessor(s, docid_index_map);
		System.out.println(tip.documents_tfidf[0]);
		System.out.println(tip.getTFIDFOfDocument(2));
		System.out.println(tip.term_dictionary.toString());
	}
}
