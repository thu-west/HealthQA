package platform.nlp.special;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import platform.GlobalSettings;
import platform.db.database.ISHCDBConfig;
import platform.db.database.ISHCDataOperator;
import platform.nlp.ao.Word;
import platform.util.log.Trace;

public class OfflineSegmenter {
	static HashMap<String, Word[][]> question_dict;
	static HashMap<String, Word[][]> answer_dict;
	static HashMap<String, String> answer_reply;
	
	static Trace t = new Trace();
	
	static Word parseWord(String word_str, int word_id) {
		String[] e = word_str.split("<>");
		return new Word( word_id, e[0], e[1], e[2], null, null);
	}
	
	static Word[][] parse( String text ) {
		String[] sent_array = text.split("\n");
		Word[][] result = new Word[sent_array.length][];
		int i=-1;
		for( String sent : sent_array ){
			if( sent.isEmpty() )
				continue;
			String[] word_array = sent.split("\t");
			result[++i] = new Word[word_array.length];
			int j=-1;
			for( String w : word_array ){
				if(w.isEmpty())
					continue;
				result[i][++j] = parseWord(w, j);
			}
		}
		return result;
	}
	
	static void loadAnswer() throws SQLException {
		if( answer_dict != null )
			return;
		answer_dict = new HashMap<String, Word[][]>();
		answer_reply = new HashMap<String, String>();
		ISHCDBConfig config = new ISHCDBConfig(
				GlobalSettings.database_url,
				GlobalSettings.database_username, GlobalSettings.database_password);
		ISHCDataOperator op = new ISHCDataOperator(config);
		
		ResultSet rs =  op.query("select * from `answer_after_seg`");
		
		rs.last();
		int total = rs.getRow();
		rs.beforeFirst();
		
		Trace t1 = new Trace(total, total/5);
		while( rs.next() ) {
			t1.debug("loading answer", true);
			answer_dict.put(rs.getString(1), parse(rs.getString(3)));
			answer_reply.put(rs.getString(1), rs.getString(2));
			
		}
	}
	
	static void loadQuestion() throws SQLException {
		if( question_dict != null )
			return;
		question_dict = new HashMap<String, Word[][]>();
		ISHCDBConfig config = new ISHCDBConfig(
				GlobalSettings.database_url,
				GlobalSettings.database_username, GlobalSettings.database_password);
		ISHCDataOperator op = new ISHCDataOperator(config);
		
		ResultSet rs =  op.query("select * from `question_after_seg`");
		
		rs.last();
		int total = rs.getRow();
		rs.beforeFirst();
		
		Trace t1 = new Trace(total, total/5);
		while( rs.next() ) {
			t1.debug("loading question", true);
			question_dict.put(rs.getString(1), parse(rs.getString(2)));
		}
	}
	
	public static Word[][] segmentQuestion(String question_id) {
		if( question_dict == null ){
			try {
				loadQuestion();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return question_dict.get(question_id); 
	}
	
	public static Word[][] segmentAnswer( String answer_id ) {
		if( answer_dict == null ){
			try {
				loadAnswer();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return answer_dict.get(answer_id);
	}
	
	public static void main(String[] args) {
		Word[][] w = segmentQuestion("40100000000000001");
		long s = System.currentTimeMillis();
		w = segmentQuestion("40100000000000001");
		System.out.println("query : "+(System.currentTimeMillis()-s));
		System.out.println(Word.toSimpleString(w));
	}
}
