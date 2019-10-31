package application.qav2.retrieval.text_retrieval.util;

import platform.nlp.ao.Word;

public class SimilarityUtil {
	
	/*
	 * 给定两个String，比较其相似性。
	 */
	public static double similarityBetweenString(String str1, String str2) throws Exception {
		return Similarity.computeQuesSimilarity(str1, str2);
	}
	
	/*
	 * 给定两个Word[]，比较其相似性
	 */
	public static double similarityBetweenWordarray( Word[][] w1, Word[][] w2 ) {
		return Similarity.pieceSimilarity(w1, w2, false);
	}
	
}
