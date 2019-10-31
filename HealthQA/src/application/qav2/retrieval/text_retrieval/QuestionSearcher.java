package application.qav2.retrieval.text_retrieval;

import java.io.*;
import java.util.*;

import knowledge.DictGallary;
import knowledge.DictReadWrite;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import application.qav2.ao.Question;
import application.qav2.dao.Entity;
import application.qav2.dao.HealthqaDB;
import platform.GlobalSettings;
import platform.Platform;
import platform.nlp.ao.Word;
import platform.util.log.Trace;

public class QuestionSearcher {

	static final Trace trace = new Trace().setValid(true, true);

	static final boolean boost = true;
	static final String QuestionIndexFile = GlobalSettings
			.contextDir("index/question.index");

	static Set<String> MedDict = null;
	static public IndexSearcher indexSearcher = null;
	static public IndexReader indexReader = null;
	static public int doc_num;

	static {
		try {
			open();
			MedDict = DictReadWrite.loadDictInStringSet(DictGallary.med_all);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void open() throws IOException {
		indexReader = null;
		indexSearcher = null;
		indexReader = DirectoryReader.open(FSDirectory.open(new File(
				QuestionIndexFile)));
		indexSearcher = new IndexSearcher(indexReader);
		doc_num = indexReader.numDocs();
	}

	public static void close() throws IOException {
		indexReader.close();
	}

	public static void displayToken(String str, Analyzer analyzer)
			throws IOException {
		// 将一个字符串创建成Token流
		TokenStream stream = analyzer.tokenStream("", new StringReader(str));
		// 保存相应词汇
		CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
		while (stream.incrementToken()) {
			System.out.println("[" + cta + "]");
		}
	}

	public static String splitWord(String text) throws IOException {
		IKSegmenter seg = new IKSegmenter(new StringReader(text), false);
		String result = "";
		Lexeme l = null;
		while ((l = seg.next()) != null) {
			result = result + l.getLexemeText() + "/";
		}
		return result;
	}

	public static float dictDF2Boost(int df) {
		if (!boost)
			return 1;
		return 6 + 100 / df > 10 ? 10 : (int) (6 + 100 / df);
	}

	public static float pos2Boost(float idf) {
		float f = (float) (idf / 100.0 * 3.0);
		f = (float) (Math.log10(idf) * 3.0);
		if (!boost)
			return 1;
		return f > 10 ? 10 : f;
	}

	public static ScoreDoc[] search(String keyword, int maxSearchResultCount)
			throws Exception {
		Sort sort = new Sort(new SortField[] { new SortField("content",
				SortField.Type.SCORE, false) });
		Lexeme l = null;
		BooleanQuery query = new BooleanQuery();
		Set<String> set = Platform.findNounVerb(keyword);
		IKSegmenter seg = new IKSegmenter(new StringReader(keyword), false);

		
		while ((l = seg.next()) != null) {
			String s = l.getLexemeText();
			Term t = new Term("content", s);
			TermQuery tq = new TermQuery(t);
			boolean in_dict = MedDict.contains(s);
			boolean is_nv = set.contains(s);
			if (in_dict && is_nv)
				// tq.setBoost( Math.max( 5, pos2Boost(
				// doc_num/(float)(indexReader.docFreq(t)+1.0)) )); //
				// dictDF2Boost(LTPSettings.ext_dict_df.get(s))
				tq.setBoost(Math.max(5, pos2Boost(doc_num
						/ (float) (indexReader.docFreq(t) + 1.0))));
			else if (in_dict)
				tq.setBoost(5); // dictDF2Boost(LTPSettings.ext_dict_df.get(s))
			else if (is_nv)
				tq.setBoost(pos2Boost(doc_num
						/ (float) (indexReader.docFreq(t) + 1.0)));
			query.add(tq, BooleanClause.Occur.SHOULD);
		}

		BooleanQuery likeThisQuery = new BooleanQuery();
		likeThisQuery.add(query, BooleanClause.Occur.SHOULD);
		// System.out.println(likeThisQuery);

		trace.debug("Query: " + likeThisQuery);

		TopFieldCollector tfc = TopFieldCollector.create(sort,
				maxSearchResultCount, true, true, true, false);
		indexSearcher.search(likeThisQuery, tfc);

		return tfc.topDocs().scoreDocs;
	}

	public static List<Question> searchQuestion(String keyword,
			int maxSearchResultCount) throws Exception {
		List<Question> qList = new ArrayList<Question>();
		ScoreDoc[] scoreDoc = search(keyword, maxSearchResultCount);
		for (int i = 0; i < scoreDoc.length; i++) {
			int doc_id = scoreDoc[i].doc;
			float score = scoreDoc[i].score;
			Document mydoc = indexSearcher.doc(doc_id);
			String id = mydoc.get("id");
			Question q = HealthqaDB.getQuestion(id);
			q.setScore(score);
			qList.add(q);
		}
		return qList;
	}

	public static List<Question> searchQuestion(String keyword,
			int maxSearchResultCount, double minimal_score) throws Exception {
		List<Question> qList = new ArrayList<Question>();
		
		ScoreDoc[] scoreDoc = search(keyword, maxSearchResultCount);
		for (int i = 0; i < scoreDoc.length; i++) {
			int doc_id = scoreDoc[i].doc;
			float score = scoreDoc[i].score;
			if (score <= minimal_score)
				continue;
			Document mydoc = indexSearcher.doc(doc_id);
			String id = mydoc.get("id");
			Question q = HealthqaDB.getQuestion(id);
			q.findKeywords();
			q.findEntities();
			q.setScore(score);
			qList.add(q);	
		}
		//liu 2015.12.15
		List<Question> quesList = new ArrayList<Question>();
		Question input_question = new Question(null, keyword, null, null, null,
				null, null, null, null);
		input_question.findKeywords();
		input_question.findEntities();
		for(Entity e1:input_question.entities)
			for (Question question : qList)
			  for (Entity e2:question.entities) {
				if (e2.name.equals(e1.name)) {
					if(quesList.contains(question))
						continue;
					quesList.add(question);
				}
			}

		
//		return qList;
		return quesList;
	}


	public static void main(String[] args) throws Exception {
		String keyword = null;
		// keyword = new String("糖尿病的病因");
		keyword = new String("糖尿病的诊断标准？");
		// keyword = new String("糖尿病患者有哪些症状？");
		// keyword = new String("糖尿病有哪些危害？");
		// keyword = new String("糖尿病需要做哪些检查？");
		// keyword = new String("糖尿病的治疗方法");

		System.out.println(Word.toSimpleString(Platform.segment(keyword)));
		// List<Question> qList = QuestionSearcher.searchQuestion(keyword,
		// 10,0.75);
		List<Question> quesList = QuestionSearcher
				.searchQuestion(keyword, 15, 0.5);

		for (Question q : quesList) {
			System.out.println(q.raw_content);
		}

		// for (Question q : qList) {
		// System.out.println(q.raw_content);
		// }

	}

}