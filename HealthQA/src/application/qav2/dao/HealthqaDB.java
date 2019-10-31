package application.qav2.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

import application.qav2.ao.*;
import platform.util.log.Trace;

public class HealthqaDB {
	
	static final Trace trace = new Trace().setValid(false, false);
	
	static Connection con = null;
	
	static Connection sqlite = null;
	
	static {
		try {
			con = HealthqaDBMgr.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static Set<String> set = new TreeSet<String>();
	
	public static List<Answer> getAnswerList( List<Question> question_list ) throws SQLException {
		ArrayList<Answer> as = new ArrayList<Answer>();
		Iterator<Question> it = question_list.iterator();
		Trace t1 = new Trace(question_list.size(), 1).setValid(false, false);
		t1.remind("total " + question_list.size() + " answers");
		while( it.hasNext() ) {
			t1.debug("Fetching answers for questions", true);
			Question q = it.next();
			String s = q.id;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * " +
					" from answer where `question`=" + s);
			while( rs.next() ){
				Answer a = new Answer(rs.getString("id"), rs.getString("question"), rs.getString("content"), Integer.parseInt(rs.getString("plength")), rs.getString("anno_content"));
				a.setQuestion(q);
				as.add(a);
			}
			rs.close();
			st.close();
		}
		return as;
	}
	
	public static ArrayList<Piece> getPieceList( List<Answer> answer_list ) throws SQLException {
		ArrayList<Piece> piece_list = new ArrayList<Piece>();
		Iterator<Answer> it = answer_list.iterator();
		Trace t1 = new Trace(answer_list.size(), 1).setValid(false, false);
		t1.remind("total " + answer_list.size() + " answers");
		while( it.hasNext() ) {
			t1.debug("Fetching pieces for answers", true);
			Answer a = it.next();
			String id = a.id;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * " +
					" from piece where `answer`=" + id);
			while( rs.next() ){
				Piece apiece = new Piece(rs.getString("id"), rs.getString("question"), rs.getString("answer"), rs.getInt("pos"), rs.getString("category"), rs.getString("category_2"), rs.getBoolean("context"), rs.getString("anno_content"), rs.getString("content"));
				apiece.answer = a;
				apiece.question = a.question;
				piece_list.add(apiece);
			}
			rs.close();
			st.close();
		}
		return piece_list;
	}
	
	public static ArrayList<ScoredPiece> getScoredPieceList( List<ScoredAnswer> answer_list ) throws SQLException {
		ArrayList<ScoredPiece> piece_list = new ArrayList<ScoredPiece>();
		Iterator<ScoredAnswer> it = answer_list.iterator();
		Trace t1 = new Trace(answer_list.size(), 1).setValid(false, false);
		t1.remind("total " + answer_list.size() + " answers");
		while( it.hasNext() ) {
			t1.debug("Fetching pieces for answers", true);
			ScoredAnswer sa = it.next();
			Answer a = sa.answer;
			String id = a.id;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * " +
					" from piece where `answer`=" + id);
			while( rs.next() ){
				Piece apiece = new Piece(rs.getString("id"), rs.getString("question"), rs.getString("answer"), rs.getInt("pos"), rs.getString("category"), rs.getString("category_2"), rs.getBoolean("context"), rs.getString("anno_content"), rs.getString("content"));
				apiece.answer = a;
				apiece.question = a.question;
				ScoredPiece spiece =  new ScoredPiece(apiece, sa);
				piece_list.add(spiece);
			}
			rs.close();
			st.close();
		}
		return piece_list;
	}
	
	public static ArrayList<Question> getQuestionList( List<String> question_id_list ) throws SQLException  {
		ArrayList<Question> qs = new ArrayList<Question>();
		Iterator<String> it = question_id_list.iterator();
		while( it.hasNext() ) {
			String s = it.next();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * " +
					" from question where `id`=" + s );
			if(rs.next()) {
				Question q = new Question(rs.getString("id"), rs.getString("content"), rs.getString("title"), rs.getString("anno_content"), new JSONObject(rs.getString("structure")), rs.getString("interrogative"), rs.getString("interrogative_word"), rs.getString("type"), rs.getString("answer_type"));
				qs.add(q);
			}
			rs.close();
			st.close();
		}
		return qs;
	}
	
	public static Question getQuestion( String id ) throws SQLException  {
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("select * "
				+ " from question where `id`=" + id);
		Question q = null;
		if( rs.next() ) {
			q = new Question(rs.getString("id"), rs.getString("content"),
					rs.getString("title"), rs.getString("anno_content"),
					new JSONObject(rs.getString("structure")),
					rs.getString("interrogative"),
					rs.getString("interrogative_word"), rs.getString("type"),
					rs.getString("answer_type"));
		}
		rs.close();
		st.close();
		return q;
	}
	
	public static Answer getAnswer( String id ) throws SQLException {
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("select * " + " from answer where `id`="
				+ id);
		Answer a = null;
		if( rs.next() ) {
			a = new Answer(rs.getString("id"), rs.getString("question"),
					rs.getString("content"), Integer.parseInt(rs
							.getString("plength")), rs.getString("anno_content"));
		}
		rs.close();
		st.close();
		return a;
	}
	
	public static Piece getPiece( String id ) throws SQLException {
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("select * " + " from piece where `id`="
				+ id);
		Piece apiece = null;
		if( rs.next() ) {
			apiece = new Piece(rs.getString("id"), rs.getString("question"), rs.getString("answer"), rs.getInt("pos"), rs.getString("category"), rs.getString("category_2"), rs.getBoolean("context"), rs.getString("anno_content"), rs.getString("content"));
		}
		rs.close();
		st.close();
		return apiece;
	}
	
	public static String[] getAllQuestion() throws SQLException {
//		ResultSet rs = operator.query("select `ID`, `content`" +
//				" from TEMP_processed_question" );
//		rs.last();
//		System.out.println(rs.getRow());
//		String[] a = new String[rs.getRow()];
//		rs.beforeFirst();
//		int i=0;
//		Trace t = new Trace(a.length, a.length/100);
//		try{
//			while( rs.next() ){
//				a[i++] = rs.getString("content");
//				t.debug(i+" reading database");
//			}
//		}catch( Exception e ){
//			e.printStackTrace();
//		}
//		return a;
		return null;
	}
	
	public static String[] getAllAnswer() throws SQLException {
//		ResultSet rs = operator.query("select `ID`, `content`" +
//				" from TEMP_processed_answer" );
//		rs.last();
//		String[] a = new String[rs.getRow()];
//		rs.beforeFirst();
//		int i=0;
//		try{
//			while( rs.next() ){
//				a[i++] = rs.getString("content");
//			}
//		}catch( Exception e ){
//			e.printStackTrace();
//		}
//		return a;
		return null;
	}
	
	public static void main(String[] args) throws JSONException, SQLException {
		
	}
}
