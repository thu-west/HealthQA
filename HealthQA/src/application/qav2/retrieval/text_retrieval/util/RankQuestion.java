package application.qav2.retrieval.text_retrieval.util;

import java.util.ArrayList;
import java.util.List;

import application.qav2.ao.Question;
import application.qav2.ao.ScoredQuestion;

public class RankQuestion {
	
	/*
	 * 问题重新排序：
	 * target: 给定要查询的问题
	 * rawQs: 粗查询的问题列表
	 * N: 指定精确查询返回的问题个数
	 * 返回：N个最相似的问题
	 */
	
	public static List<ScoredQuestion> rankQuestions (String target, List<Question> rawQs, double sim_threshold ) throws Exception {
//			int size = rawQs.size();
//			double[] s = new double[size];

//			Word[][] targetTitle = Platform.segment(target);

			List<ScoredQuestion> topQs = new ArrayList<ScoredQuestion>();
			
			for( Question q : rawQs ) {
				// 先比较title
//				double score = SimilarityUtil.similarityBetweenWordarray( targetTitle, q.seg_content );
				double score = q.score;
				if( score <= sim_threshold ) continue;
				ScoredQuestion qs = new ScoredQuestion(q, target);
				qs.score = score;
				topQs.add(qs);
			}
			
			return topQs;
			
//			for(int j = 0; j < size; j++) {
//				//改进算法？
//				int index = 0;
//				for(int k = 1; k < size - j; k++) index = s[k]> s[index] ? k:index;
//				if( s[index] < sim_threshold )
//					break;
//				topQs.add(rawQs.get(index));
//				pieceSimilarity(targetTitle, rawQs.get(index).title, true);
//				System.out.println("相似度: " + s[index]);
//				s[index] = -1;
//			}

			
	}
}
