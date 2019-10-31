package application.qav2;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import platform.nlp.ao.Word;
import platform.util.log.Trace;
import application.qav2.ao.Keyword;
import application.qav2.ao.Question;
import application.qav2.ao.ScoredAnswer;
import application.qav2.ao.ScoredQuestion;
import application.qav2.ao.ScoredPiece;
import application.qav2.dao.Entity;
import application.qav2.dao.HealthqaDBMgr;
import application.qav2.process_question.entity.QuestionEntity;
import application.qav2.retrieval.text_retrieval.QuestionSearcher;
import application.qav2.retrieval.text_retrieval.util.RankQuestion;
import application.qav2.summarization.Summarization;

public class Ask {
	
	public static Trace t = new Trace().setValid(true, true);
	
	static Connection con= null;
	static {
		try {
			con = HealthqaDBMgr.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static float getWeight( String word, Keyword[] kwlist) {
		float weight = 0;
		float highest = -1;
		for( Keyword kw : kwlist ) {
			if( kw.word.equals(word) )
				weight = kw.weight;
//			t.debug(""+weight);
			highest = highest > kw.weight ? highest : kw.weight;
		}
//		t.debug(weight+ "/" +highest + "");
		return highest <= 0 ? 0 : weight/highest;
	}
	
	public List<ScoredQuestion> findTopQuestions(String target, int maxSearchResultCount, double sim_threshold) throws Exception {
		List<Question> rawQs = QuestionSearcher.searchQuestion(target, maxSearchResultCount, sim_threshold);

		//2015.12.11 liu
		if (rawQs == null)
			return null;
		for(Question q : rawQs ){
			q.findKeywords();    // 计算每个question的关键词
			q.findEntities();		//计算每个question的实体
		}	
		
		List<ScoredQuestion> temp = RankQuestion.rankQuestions(target, rawQs, 0);
		for (ScoredQuestion sq : temp) {
			t.debug("\t" + sq.score + "\t" + sq.question.score + "\t: "
					+ sq.question.raw_content);
		}
		Collections.sort(temp);
		return temp;
	}
	
	public String cleanQuestionStr( String question_str ) {
		question_str+="。";
		question_str = question_str.replaceAll("[ ]+", "").replace("\\", "/");
		question_str = question_str.replaceAll("[，：；,;:]+", "，").replaceAll("[，]*[？！。?!]+", "。");
		return question_str;
	}
	
	public Question createQuestionFrom(String question_str) throws Exception {
		Question question = new Question(null, question_str, null, null, null, null, null, null, null);
		question.findKeywords();
		question.findEntities();
		question.findStructure();
		return question;
	}
	
	JSONArray parseQuestion(Question question) {
		// parse input question
		JSONArray represent_array = new JSONArray();
		String[] ss1 = question.entity_content.split(" ");
		Pattern p1 = Pattern.compile("^(.*)\\\\(i|is|o|os|s|ss|m|gm|gmt|d|c|t|g|f|l|tt|D|J|a|x|r)$");
		for(String s : ss1 ) {
			JSONObject o = new JSONObject();
			Matcher m = p1.matcher(s);
			if(m.find()) {
				o.put("c", s);
				o.put("w", 1);
			} else {
				o.put("c", s);
				o.put("w", 0);
			}
			represent_array.put(o);
		}
		return represent_array;
	}
	
	void transferScoredQuestionToArrayAndObject(List<ScoredQuestion> questions, JSONArray qrank, JSONObject qlist) throws Exception {
		for ( ScoredQuestion sq : questions ) {
			// record the rank of question
			qrank.put(sq.question.id);
			// represent the question with piece (with weight)
			JSONArray qq = new JSONArray();
			String anno_ques = QuestionEntity.annotate(sq.question.raw_content);
			String[] ss = anno_ques.split(" ");
			Pattern p = Pattern.compile("^(.*)\\\\(i|is|o|os|s|ss|m|gm|gmt|d|c|t|g|f|l|tt|D|J|a|x|r)$");
			for(String s : ss ) {
				JSONObject o = new JSONObject();
				Matcher m = p.matcher(s);
				if(m.find()) {
					o.put("c", s);
					o.put("w", 1);
				} else {
					o.put("c", s);
					o.put("w", 0);
				}
				qq.put(o);
			}
			qlist.put(sq.question.id, qq);
		}
	}
	//求问题和答案实体的交集
	public static String[] intersect(String[] arr1, String[] arr2) {   
        Map<String, Boolean> map = new HashMap<String, Boolean>();   
        LinkedList<String> list = new LinkedList<String>();   
        for (String str : arr1) {   
            if (!map.containsKey(str)) {   
                map.put(str, Boolean.FALSE);   
            }   
        }   
        for (String str : arr2) {   
            if (map.containsKey(str)) {   
                map.put(str, Boolean.TRUE);   
            }   
        }   
  
        for (Entry<String, Boolean> e : map.entrySet()) {   
            if (e.getValue().equals(Boolean.TRUE)) {   
                list.add(e.getKey());   
            }   
        }   
  
        String[] result = {};   
        return list.toArray(result);   
    }   
	
	public  String ask ( String id, String question_str, int max_length,String type) throws Exception {
		
		JSONObject RESULT = new JSONObject();
		
		// clean question
		question_str = cleanQuestionStr(question_str);
//		// create Question object
		Question question = createQuestionFrom(question_str);
		// create represent array
		JSONArray represent_array = parseQuestion(question);
		RESULT.put("input", represent_array);

		// fetch top questions
//		List<ScoredQuestion> top_questions = findTopQuestions( question_str, 10, 0.75 );
		List<ScoredQuestion> top_questions = findTopQuestions( question_str, 15, 0.5);
//		System.out.println(top_questions);
		if( top_questions.size() == 0 || top_questions == null ) {
			t.remind("No similar questions, quit!");
			System.out.println(-11);        //*********************
			return null;
		}
		// represent the question
		JSONArray qrank = new JSONArray();
		JSONObject qlist = new JSONObject();
		try {
			transferScoredQuestionToArrayAndObject(top_questions, qrank, qlist);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: when anno question");
			System.out.println(-2);        //*********************
			return null;
		}
		// fetch top answers
		if (type.equals("a")) {
		JSONArray arank = new JSONArray();
		JSONObject alist = new JSONObject();
		JSONArray aranklist = new JSONArray();
		t.debug("\n==================find top answers==================");
		List<ScoredAnswer> answers = Summarization.findTopAnswers(
				question, top_questions);
//		//为了页面展示时仅显示答案，获取的为得分最高的答案
		ScoredAnswer a = answers.get(0);
		t.debug(a.toString());
		t.debug("-->" + a.answer.question.raw_content);
		t.debug("\t" + "answer kw: " + a.answer.printKeywords());
		t.debug("\t" + "scored kw: " + a.printKeywords());
		t.debug("\t" + "aid: " + a.answer.id + " qid: " + a.answer.question_id);
		arank.put(a.answer.id);
		JSONObject singA = new JSONObject();
		JSONObject singB = new JSONObject();
		JSONArray as = new JSONArray();
		//记录答案实体
		StringBuffer sBuffer = new StringBuffer();
		for (Entity entity : a.answer.entities) {
			sBuffer.append(entity.name + " ");
//			System.out.println(entity.name);
		}
		String str = sBuffer.toString();
		System.out.println(str);
		String[] arr_a = str.split(" ");
		// for(String s: arr){
		// System.out.println(s);
		// }
		//记录问题实体
		Question qus= createQuestionFrom(question_str);
//		String qString =QuestionEntity.annotate("30-40。罗大夫我是糖尿病患者。");
//		System.out.println(qString);
		StringBuffer sBuffer2 = new StringBuffer();	
		for (Entity entity :qus.entities ) {
			sBuffer2.append(entity.name + " ");
		}
		String str2 = sBuffer2.toString();
		System.out.println(str2);
		String[] arr_q = str2.split(" ");
		
		//问题和答案实体的交集
		String[] result_insect = intersect(arr_a, arr_q);   
        System.out.println("求交集的结果如下：");   
        for (String result : result_insect) {   
            System.out.println(result);   
        }   
		for (Word[] ws : a.answer.seg_content) {
			for (Word w : ws) {
				JSONObject o = new JSONObject();
				if (Arrays.asList(result_insect).contains(w.cont)) {
					o.put("c", w.cont);
					o.put("w", 5);
				} else {
					o.put("c", w.cont);
					o.put("w", getWeight(w.cont, a.answer.kws));
				}
				as.put(o);
			}
		}
		singA.put("content", as);
		singA.put("qid", a.answer.question.id);
		alist.put(a.answer.id, singA);

		singB.put("content", as);
		singB.put("aid", a.answer.id);
		singB.put("qid", a.answer.question.id);
		aranklist.put(singB);
		
		if ( answers.size() ==0 ||answers == null ) {
			t.remind("Not find reasonable answer!");
			System.out.println(-12);         //*********************
			return null;
		}	
		
		/*
		 * fetch top pieces
		 */
		JSONArray sranklist = new JSONArray();
		t.debug("\n==================find top pieces==================");
		List<ScoredPiece> pieces = Summarization.findTopPieces(
				question, answers);	
		//显示"仅片段"使用
//		for ( ScoredPiece ss : pieces ) {
//			t.debug(ss.answer.answer.question.raw_content);
//			t.debug(ss.toString());
//			t.debug("\t"+ss.piece.printKeywords());
//			t.debug("\t"+"aid: "+ss.piece.answer.id+" qid: "+ss.piece.answer.question_id);
//			//获取片段的实体
//			String[] arr_p = entityOfanswer(ss);
//			//获取问题实体
//			String[] arr_q = entityOfquestion(question_str);			
//			//问题和答案实体的交集
//			String[] result_insect = intersect(arr_p, arr_q);   
//	        System.out.println("求交集的结果如下：");   
//	        for (String result : result_insect) {   
//	            System.out.println(result);   
//	        }   
//			JSONObject singS = new JSONObject();
//			JSONArray aa = new JSONArray();
//			for(Word w : ss.piece.seg_content) {
//					JSONObject ob=new JSONObject();
//					if (Arrays.asList(result_insect).contains(w.cont)) {
//						ob.put("c", w.cont);
//						ob.put("w", 5);
//					}
//					else {
//						ob.put("c", w.cont);
//						ob.put("w", getWeight(w.cont, ss.piece.kws));
//					}
//					aa.put(ob);
//			}
//			singS.put("content", aa);
//			singS.put("aid", ss.answer.answer.id);
//			singS.put("qid", ss.answer.answer.question.id);
//			sranklist.put(singS);
//		}
		
		//片段组合liu 2016.3.25  为了页面展示时使用
		StringBuffer sb = new StringBuffer();
		for (ScoredPiece ss : pieces) {
			t.debug(ss.answer.answer.question.raw_content);
			t.debug(ss.toString());
			t.debug("\t" + ss.piece.printKeywords());
			t.debug("\t" + "aid: " + ss.piece.answer.id + " qid: "
					+ ss.piece.answer.question_id);
			//获取片段的实体
			String[] arr_p1 = entityOfanswer(ss);
			//获取问题实体
			String[] arr_q1 = entityOfquestion(question_str);			
			//问题和答案实体的交集
			String[] result_insect1 = intersect(arr_p1, arr_q1);   
	        System.out.println("求交集的结果如下：");   
	        for (String result1 : result_insect1) {   
	            System.out.println(result1);   
	        }        
			JSONObject singS = new JSONObject();
			JSONArray aa = new JSONArray();
//			for (Word w : ss.piece.seg_content) {
//				JSONObject o = new JSONObject();
//				o.put("c", w.cont);
//				o.put("w", getWeight(w.cont, ss.piece.kws));
//				aa.put(o);
//			}
			for (Word w : ss.piece.seg_content) {
				JSONObject ob = new JSONObject();
				if (Arrays.asList(result_insect).contains(w.cont)) {
					ob.put("c", w.cont);
					ob.put("w", 5);
				} else {
					ob.put("c", w.cont);
					ob.put("w", getWeight(w.cont, ss.piece.kws));
				}
				aa.put(ob);
			}
			singS.put("content", aa);
			singS.put("aid", ss.answer.answer.id);
			singS.put("qid", ss.answer.answer.question.id);
			sranklist.put(singS);
			if (sb.length() < 250) {
				sb.append(ss);
			} else
				break;
		}
		
		// liu 2015.12.16
		if (pieces.size() == 0 || pieces == null) {
			t.remind("Not find reasonable piece!");
			System.out.println(-13); // *********************
			return null;
		}
					
//		t.debug(sranklist.toString(2));
		
		JSONObject final_result = new JSONObject();
		final_result.put("qlist", qlist);
		final_result.put("qrank", qrank);
		final_result.put("alist", alist);
		final_result.put("arank", arank);
		final_result.put("aranklist", aranklist);
		final_result.put("sranklist", sranklist);		
		try {
			BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("tmp/"+id+".asw"), "utf8"));
			bw.write(final_result.toString());
			bw.close();
			t.remind("Done!");
			System.out.println(0);
		} catch (Exception e) {
			e.printStackTrace();
			t.error("ERROR: when writing file");
			System.out.println(-2);        //*********************
			return null;
		}
		
		
		/*
		 * combine the top pieces
		 */
		String finalSent = "";
		for( ScoredPiece sent : pieces ) {
			if( finalSent.length()+sent.piece.raw_content.length() > max_length ) {
				if( finalSent.length() < 10)
					return finalSent+sent.piece.raw_content+"。";
				else
					return finalSent;
			}
			finalSent += sent.piece.raw_content+"。";
		}
		if ( finalSent.equals("") )
			return pieces.get(0).piece.raw_content+"。";
		return finalSent.replaceAll("[，]*[。]+", "。");
	 }
		else if (type.equals("p")) {
			JSONArray arank = new JSONArray();
			JSONObject alist = new JSONObject();
			JSONArray aranklist = new JSONArray();
			t.debug("\n==================find top answers==================");
			List<ScoredAnswer> answers = Summarization.findTopAnswers(
					question, top_questions);
			
			//显示所有答案结果	
			for(ScoredAnswer a : answers ) {
				t.debug(a.toString());
				t.debug("-->"+a.answer.question.raw_content);
				t.debug("\t"+"answer kw: "+a.answer.printKeywords());
				t.debug("\t"+"scored kw: "+a.printKeywords());
				t.debug("\t"+"aid: "+a.answer.id+" qid: "+a.answer.question_id);
				
//				alist.put("aid", a.answer.id);
//				alist.put("content", a.answer.raw_content);
//				arank.put(alist);
				arank.put(a.answer.id);
//				arank.put(a.answer.raw_content);
				JSONObject singA = new JSONObject();
				JSONObject singB = new JSONObject();
				JSONArray as = new JSONArray();  
				for(Word[] ws : a.answer.seg_content) {
					for(Word w : ws) {
						JSONObject o=new JSONObject();
						o.put("c", w.cont);
						o.put("w", getWeight(w.cont, a.answer.kws));
						as.put(o);
					}
				}
			singA.put("content", as);
			singA.put("qid", a.answer.question.id);
			alist.put(a.answer.id, singA);

			singB.put("content", as);
			singB.put("aid", a.answer.id);
			singB.put("qid", a.answer.question.id);
			aranklist.put(singB);
			 }			
			//liu 2015.12.16
			if ( answers.size() ==0 ||answers == null ) {
				t.remind("Not find reasonable answer!");
				System.out.println(-12);         //*********************
				return null;
			}	
			
			/*
			 * fetch top pieces
			 */
			JSONArray sranklist = new JSONArray();
			t.debug("\n==================find top pieces==================");
			List<ScoredPiece> pieces = Summarization.findTopPieces(
					question, answers);	
			//显示"仅片段"使用
			for ( ScoredPiece ss : pieces ) {
				t.debug(ss.answer.answer.question.raw_content);
				t.debug(ss.toString());
				t.debug("\t"+ss.piece.printKeywords());
				t.debug("\t"+"aid: "+ss.piece.answer.id+" qid: "+ss.piece.answer.question_id);
				//获取片段的实体
				String[] arr_p = entityOfanswer(ss);
				//获取问题实体
				String[] arr_q = entityOfquestion(question_str);			
				//问题和答案实体的交集
				String[] result_insect = intersect(arr_p, arr_q);   
		        System.out.println("求交集的结果如下：");   
		        for (String result : result_insect) {   
		            System.out.println(result);   
		        }   
				JSONObject singS = new JSONObject();
				JSONArray aa = new JSONArray();
				for(Word w : ss.piece.seg_content) {
						JSONObject ob=new JSONObject();
						if (Arrays.asList(result_insect).contains(w.cont)) {
							ob.put("c", w.cont);
							ob.put("w", 5);
						}
						else {
							ob.put("c", w.cont);
							ob.put("w", getWeight(w.cont, ss.piece.kws));
						}
						aa.put(ob);
				}
				singS.put("content", aa);
				singS.put("aid", ss.answer.answer.id);
				singS.put("qid", ss.answer.answer.question.id);
				sranklist.put(singS);
			}
			
			// liu 2015.12.16
			if (pieces.size() == 0 || pieces == null) {
				t.remind("Not find reasonable piece!");
				System.out.println(-13); // *********************
				return null;
			}
						
//			t.debug(sranklist.toString(2));
			
			JSONObject final_result = new JSONObject();
			final_result.put("qlist", qlist);
			final_result.put("qrank", qrank);
			final_result.put("alist", alist);
			final_result.put("arank", arank);
			final_result.put("aranklist", aranklist);
			final_result.put("sranklist", sranklist);		
			try {
				BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("tmp/"+id+".asw"), "utf8"));
				bw.write(final_result.toString());
				bw.close();
				t.remind("Done!");
				System.out.println(0);
			} catch (Exception e) {
				e.printStackTrace();
				t.error("ERROR: when writing file");
				System.out.println(-2);        //*********************
				return null;
			}
			
			
			/*
			 * combine the top pieces
			 */
			String finalSent = "";
			for( ScoredPiece sent : pieces ) {
				if( finalSent.length()+sent.piece.raw_content.length() > max_length ) {
					if( finalSent.length() < 10)
						return finalSent+sent.piece.raw_content+"。";
					else
						return finalSent;
				}
				finalSent += sent.piece.raw_content+"。";
			}
			if ( finalSent.equals("") )
				return pieces.get(0).piece.raw_content+"。";
			return finalSent.replaceAll("[，]*[。]+", "。");
		}
		
		else {
			JSONArray arank = new JSONArray();
			JSONObject alist = new JSONObject();
			JSONArray aranklist = new JSONArray();
			t.debug("\n==================find top answers==================");
			List<ScoredAnswer> answers = Summarization.findTopAnswers(
					question, top_questions);	
			for(ScoredAnswer a : answers ) {
				t.debug(a.toString());
				t.debug("-->"+a.answer.question.raw_content);
				t.debug("\t"+"answer kw: "+a.answer.printKeywords());
				t.debug("\t"+"scored kw: "+a.printKeywords());
				t.debug("\t"+"aid: "+a.answer.id+" qid: "+a.answer.question_id);
				
//				alist.put("aid", a.answer.id);
//				alist.put("content", a.answer.raw_content);
//				arank.put(alist);
				arank.put(a.answer.id);
//				arank.put(a.answer.raw_content);
				JSONObject singA = new JSONObject();
				JSONObject singB = new JSONObject();
				JSONArray as = new JSONArray();  
				for(Word[] ws : a.answer.seg_content) {
					for(Word w : ws) {
						JSONObject o=new JSONObject();
						o.put("c", w.cont);
						o.put("w", getWeight(w.cont, a.answer.kws));
						as.put(o);
					}
				}
			singA.put("content", as);
			singA.put("qid", a.answer.question.id);
			alist.put(a.answer.id, singA);

			singB.put("content", as);
			singB.put("aid", a.answer.id);
			singB.put("qid", a.answer.question.id);
			aranklist.put(singB);
			 }			
			//liu 2015.12.16
			if ( answers.size() ==0 ||answers == null ) {
				t.remind("Not find reasonable answer!");
				System.out.println(-12);         //*********************
				return null;
			}	
			
			/*
			 * fetch top pieces
			 */
			JSONArray sranklist = new JSONArray();
			t.debug("\n==================find top pieces==================");
			List<ScoredPiece> pieces = Summarization.findTopPieces(
					question, answers);	
			
			//片段组合liu 2016.3.25  为了页面展示时使用
			StringBuffer sb = new StringBuffer();
			for (ScoredPiece ss : pieces) {
				t.debug(ss.answer.answer.question.raw_content);
				t.debug(ss.toString());
				t.debug("\t" + ss.piece.printKeywords());
				t.debug("\t" + "aid: " + ss.piece.answer.id + " qid: "
						+ ss.piece.answer.question_id);
				//获取片段的实体
				String[] arr_p = entityOfanswer(ss);
				//获取问题实体
				String[] arr_q = entityOfquestion(question_str);			
				//问题和答案实体的交集
				String[] result_insect = intersect(arr_p, arr_q);   
		        System.out.println("求交集的结果如下：");   
		        for (String result : result_insect) {   
		            System.out.println(result);   
		        }        
				JSONObject singS = new JSONObject();
				JSONArray aa = new JSONArray();
//				for (Word w : ss.piece.seg_content) {
//					JSONObject o = new JSONObject();
//					o.put("c", w.cont);
//					o.put("w", getWeight(w.cont, ss.piece.kws));
//					aa.put(o);
//				}
				for (Word w : ss.piece.seg_content) {
					JSONObject ob = new JSONObject();
					if (Arrays.asList(result_insect).contains(w.cont)) {
						ob.put("c", w.cont);
						ob.put("w", 5);
					} else {
						ob.put("c", w.cont);
						ob.put("w", getWeight(w.cont, ss.piece.kws));
					}
					aa.put(ob);
				}
				singS.put("content", aa);
				singS.put("aid", ss.answer.answer.id);
				singS.put("qid", ss.answer.answer.question.id);
				sranklist.put(singS);
				if (sb.length() < 250) {
					sb.append(ss);
				} else
					break;
			}
			
			// liu 2015.12.16
			if (pieces.size() == 0 || pieces == null) {
				t.remind("Not find reasonable piece!");
				System.out.println(-13); // *********************
				return null;
			}
						
//			t.debug(sranklist.toString(2));
			
			JSONObject final_result = new JSONObject();
			final_result.put("qlist", qlist);
			final_result.put("qrank", qrank);
			final_result.put("alist", alist);
			final_result.put("arank", arank);
			final_result.put("aranklist", aranklist);
			final_result.put("sranklist", sranklist);		
			try {
				BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("tmp/"+id+".asw"), "utf8"));
				bw.write(final_result.toString());
				bw.close();
				t.remind("Done!");
				System.out.println(0);
			} catch (Exception e) {
				e.printStackTrace();
				t.error("ERROR: when writing file");
				System.out.println(-2);        //*********************
				return null;
			}
			
			
			/*
			 * combine the top pieces
			 */
			String finalSent = "";
			for( ScoredPiece sent : pieces ) {
				if( finalSent.length()+sent.piece.raw_content.length() > max_length ) {
					if( finalSent.length() < 10)
						return finalSent+sent.piece.raw_content+"。";
					else
						return finalSent;
				}
				finalSent += sent.piece.raw_content+"。";
			}
			if ( finalSent.equals("") )
				return pieces.get(0).piece.raw_content+"。";
			return finalSent.replaceAll("[，]*[。]+", "。");
		}		
}


	public String[] entityOfquestion(String question_str) throws Exception {
		Question qus = createQuestionFrom(question_str);
		StringBuffer sBuffer = new StringBuffer();
		for (Entity entity : qus.entities) {
			sBuffer.append(entity.name + " ");
		}
		String str4 = sBuffer.toString();
		System.out.println(str4);
		String[] arr_q = str4.split(" ");
		return arr_q;
	}

	public String[] entityOfanswer(ScoredPiece ss) {
		StringBuffer sBuffer = new StringBuffer();
		for (Entity entity : ss.piece.entities) {
			sBuffer.append(entity.name + " ");
//				System.out.println(entity.name);
		}
		String str3 = sBuffer.toString();
		System.out.println(str3);
		String[] arr_p = str3.split(" ");
		return arr_p;
	}


	
	public static void acceptQuery ( String question ) {
		
	}
	
	public static void main(String[] args) throws Exception  {
//		String id = "11213216";
//		String input_question = "糖尿病能跑步吗？";
//		String type = "pz";
		String id = null;
		String input_question = null;
		String type = null;
		if(args.length > 1) {
			id = args[0];
			input_question = args[1];
			type = args[2];	
		}
		
		Trace trace = new Trace();
		trace.remind("Task id: "+id);
		trace.remind("Question: "+input_question);
		trace.remind("type:" +type);

		new Ask().ask(id, input_question, 300,type);
	}
}
