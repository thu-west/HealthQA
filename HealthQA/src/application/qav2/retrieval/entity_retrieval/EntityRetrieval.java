package application.qav2.retrieval.entity_retrieval;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import application.qav2.Ask;
import application.qav2.ao.Question;
import application.qav2.ao.ScoredAnswer;
import application.qav2.ao.ScoredPiece;
import application.qav2.ao.ScoredQuestion;
import application.qav2.dao.Entity;
import application.qav2.dao.HealthqaDB;
import application.qav2.process_data.Transform;
import application.qav2.process_question.entity.QuestionEntity;
import application.qav2.summarization.Summarization;
import platform.GlobalSettings;
import platform.util.log.Trace;

public class EntityRetrieval {

	// String EntityDir = GlobalSettings.contextDir("index/entity");
	static final Trace t = new Trace().setValid(true, true);

	public static Connection sqlite = null;

	public static Connection getSQLiteConnection() throws SQLException {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"
					+ GlobalSettings.contextDir("index/entitymap.db"));
			c.setAutoCommit(false);
			t.remind("Opened database successfully");
		} catch (Exception e) {
			t.error(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return c;
	}

	static {
		try {
			sqlite = getSQLiteConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	

	 public String search(String question_str) throws Exception
	 {
	    String allPiece ="";
	    Ask ask = new Ask();
	    // clean question
	    question_str = ask.cleanQuestionStr(question_str);
	 // create Question object
	    Question question =ask.createQuestionFrom(question_str);
	    String question_id =null;

		List<ScoredQuestion> top_questions = ask.findTopQuestions( question_str, 10, 0.75 );
		List<ScoredAnswer> answers = Summarization.findTopAnswers(
				question, top_questions);
		List<ScoredPiece> sents = HealthqaDB.getScoredPieceList(answers);
		for (ScoredPiece sp : sents) {
			String sent = sp.piece.entity_content;
			//System.out.println(sent);
//		}
//		for (ScoredAnswer scoredAnswer : answers) {
//			String entity_answer = scoredAnswer.answer.entity_content;
//			System.out.println(entity_answer);
		
//		for (ScoredQuestion scoredQuestion : top_questions) {
////			System.out.println(scoredQuestion.target_question);
//			question_str = scoredQuestion.target_question;
//			question_id = scoredQuestion.question.id;
		
		String anno_ques = QuestionEntity.annotate(question_str);
//		System.out.println(anno_ques);
//		System.out.println("-----------------------------");

		Set<Entity> entities_set_q = Transform.extractEntities(anno_ques);
		// Entity[] entities = new Entity[entities_set.size()];
		Entity[] entities = entities_set_q
				.toArray(new Entity[entities_set_q.size()]);
		
		Set<Entity> entities_set_p = Transform.extractEntities(sent);
		Entity[] entities_p = entities_set_p
				.toArray(new Entity[entities_set_p.size()]);

//		StringBuffer sBuffer = new StringBuffer();
		int count=0;
		for (Entity entity : entities) {
			for (Entity entity_p : entities_p) {
				if (entity_p.name.contains(entity.name)) {
					System.out.println(sp.piece.entity_content);
					System.out.println("-------------------------------");
				}
			}
//			count++;
//			String entity_name = entity.name;
//
//			Statement stmt = sqlite.createStatement();
//			String sql = "select name,piece from entity where name =" + "'"
//					+ entity_name + "'";
//			ResultSet rs = stmt.executeQuery(sql);
//			while (rs.next()) {
//				String piece = rs.getString("piece");
//
//				sBuffer.append(piece + ";;");
//			}
//			rs.close();
//			stmt.close();
//			System.out.println(entity_name);
//		}
//		String result = sBuffer.toString();
//
//		String[] piece_id = result.split(";;");
//		Map<String, Integer> map = new HashMap<String, Integer>();
//		for (int i = 0; i < piece_id.length; i++) {
//			if (null != map.get(piece_id[i])) {
//				map.put(piece_id[i], map.get(piece_id[i - 1]) + 1); // value+1
//			} else {
//				map.put(piece_id[i], 1);
//			}       
//		}
//		Iterator it = map.entrySet().iterator();
//		while (it.hasNext()) {
//			Map.Entry entry = (Map.Entry) it.next();
//			String key = entry.getKey().toString();
//			int value = Integer.parseInt(entry.getValue().toString());
//			if (value >=   count) {
////				System.out.println("key is :" + key + "---value :" + value);
//		  SearchPiece.piece(key);
////				SearchPiece.piece(key,question_id);
//				System.out.println("key is :" + key + "---value :" + value);
//			  System.out.println("-----------------------------------------");
//			}


		}
		}
//	 }
	    return allPiece;
	
	        
	 }
	public static void main(String[] args) throws Exception {

		String question_str = "糖尿病饮食       ";
//		String question_str = "糖尿病吃什么菜可以控制血糖?";
		// String input = "糖尿病需要注意什么？";
		EntityRetrieval er = new EntityRetrieval();
		er.search(question_str);

	}

}
