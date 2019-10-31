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
import platform.GlobalSettings;
import platform.Platform;
import platform.nlp.ao.Word;
import platform.util.log.Trace;


public class AnswerSearcher {
	
	static final Trace trace = new Trace().setValid(true, true);
	
	static final boolean boost=true;
    static final String IndexFile = GlobalSettings.contextDir("index/answer.index");
    
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
		indexReader = DirectoryReader.open(FSDirectory.open(new File( IndexFile)));
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
  
    public static float dictDF2Boost( int df ) {
    	if( !boost )
    		return 1;
    	return 6+100/df > 10 ? 10 : (int)(6+100/df);
    }
    
    public static float pos2Boost( float idf ) {
    	float f = (float)(idf/100.0*3.0);
    	f = (float)( Math.log10(idf)*3.0 );
    	if(!boost)
    		return 1;
    	return f > 10 ? 10 : f;
    }
    
    public static ScoreDoc[] search( String keyword, int maxSearchResultCount ) throws Exception {
    	Sort sort = new Sort(new SortField[] { new SortField("content", SortField.Type.SCORE, false) });
        Lexeme l = null;
		BooleanQuery query = new BooleanQuery();
		Set<String> set = Platform.findNounVerb( keyword );
		IKSegmenter seg = new IKSegmenter(new StringReader(keyword), false);
		
		while ((l = seg.next()) != null) {
			String s = l.getLexemeText();
			Term t = new Term("content", s );
			TermQuery tq = new TermQuery( t );
			boolean in_dict = MedDict.contains(s);
			boolean is_nv = set.contains(s);
			if( in_dict && is_nv )
				tq.setBoost( Math.max( 5, pos2Boost( doc_num/(float)(indexReader.docFreq(t)+1.0)) ));  // dictDF2Boost(LTPSettings.ext_dict_df.get(s))
			else if( in_dict )
				tq.setBoost( 5 ); // dictDF2Boost(DictGallary.ext_dict_df.get(s))
			else if ( is_nv )
				tq.setBoost( pos2Boost( doc_num/(float)(indexReader.docFreq(t)+1.0)) );
			query.add(tq, BooleanClause.Occur.SHOULD);
		}
		
		BooleanQuery likeThisQuery = new BooleanQuery();
		likeThisQuery.add(query, BooleanClause.Occur.SHOULD);
		
		trace.debug("Query: "+likeThisQuery);
		
		TopFieldCollector tfc = TopFieldCollector.create(sort, maxSearchResultCount, true, true, true, false);
		indexSearcher.search(likeThisQuery, tfc);
		
		return tfc.topDocs().scoreDocs;
    }
    
    public static List<Question> searchAnswer(String keyword, int maxSearchResultCount) throws Exception {
    	List<Question> qList = new ArrayList<Question>();
    	ScoreDoc[] scoreDoc = search(keyword, maxSearchResultCount);
		for (int i = 0; i < scoreDoc.length; i++) {
			int doc_id = scoreDoc[i].doc;
			float score = scoreDoc[i].score;
			Document mydoc = indexSearcher.doc(doc_id);
			String id = mydoc.get("id");
			String title = mydoc.get("content");
			trace.debug(score+" : "+title);
			qList.add(new Question(id, title, title));
		}
        return qList;
    }
    
    public static List<Question> searchAnswer(String keyword, int maxSearchResultCount, double minimal_score) throws Exception {
    	List<Question> qList = new ArrayList<Question>();
		ScoreDoc[] scoreDoc = search(keyword, maxSearchResultCount);
		for (int i = 0; i < scoreDoc.length; i++) {
			int doc_id = scoreDoc[i].doc;
			float score = scoreDoc[i].score;
			if( score <= minimal_score )
				continue;
			Document mydoc = indexSearcher.doc(doc_id);
			String id = mydoc.get("id");
			String title = mydoc.get("content");
			trace.debug(score+" : "+title);
			qList.add(new Question(id, title, title, score));
		}
        return qList;
    }
    
    public static void main(String[] args) throws Exception {
    	String keyword = new String("得了糖尿病，能吃米饭吗？");
//    	String keyword = new String("糖尿病能够根除吗？");
//    	String keyword = new String("糖尿病怎么治？");
//    	String keyword = new String("白矾泡脚有用吗，青少年可以用吗。");
//    	keyword = "6.7叫糖尿病吗。能吃西瓜，米饭，桃子。";
//    	keyword = "糖尿病怎么治。全身疼，还吐，拉肚子，怎么治。";
//    	keyword = "我妈妈有糖尿病最近频繁拉肚子要怎么治。发病有两个星期了而且之前也有过这种状况患有糖尿病。";
//    	keyword = "青少年血糖低会得糖尿病吗。";
    	keyword = "2型糖尿病可以喝酒吗";
    	System.out.println(Word.toSimpleString( Platform.segment(keyword) ));;
    	List<Question> qList =  searchAnswer(keyword, 50,0);
    	
        for (Question q : qList) {
        	System.out.println(q.raw_content);
        }
	}

}