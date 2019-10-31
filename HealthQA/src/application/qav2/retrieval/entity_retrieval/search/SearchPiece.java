package application.qav2.retrieval.entity_retrieval.search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.qav2.dao.HealthqaDBMgr;


public class SearchPiece {
	
	static Connection con= null;
	static {
		try {
			con = HealthqaDBMgr.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//search post from question table
public static String questionSearch(String id) throws SQLException
{
	String question_content = null;
	String  sql ="SELECT * from question WHERE id="+"'"+id+"'";
	Statement stmt = con.createStatement();
	ResultSet rs = stmt.executeQuery(sql);
	while (rs.next()) {
		question_content = rs.getString("content");
	}
	rs.close();
	stmt.close();
	return question_content;
	
}
//search reply_post from answer table
public static String answerSearch(String id) throws SQLException
{
	String answer_content = null;
	String sql = "SELECT * from answer WHERE id ="+"'"+id+"'";
	Statement stmt = con.createStatement();
	ResultSet rs = stmt.executeQuery(sql);
	while (rs.next()) {
		answer_content = rs.getString("content");
	}
	rs.close();
	stmt.close();
	return answer_content;
}

	public static String piece(String key) throws Exception {
		String content = null;
//		String sql = "SELECT * FROM piece WHERE question='80100000000019761' and id='898513'";
//		String sql = "select * from piece where id =" + "'"
//				+ key + "'"+"and question="+id;
//		
		
		String sql = "select * from piece where id =" + "'"
				+ key + "'";
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			 content = rs.getString("anno_content");
			 
			String question_id = rs.getString("question");
			String question = questionSearch(question_id);
			
			String answer_id = rs.getString("answer");
			String answer = answerSearch(answer_id);
			
			
			System.out.println("piece:"+content);
			System.out.println("question:"+question);
			System.out.println("answer:"+answer);
		}
		rs.close();
		stmt.close();
		return content;
	}
}
