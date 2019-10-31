package application.qav2.summarization.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import application.qav2.ao.*;
import platform.nlp.ao.Word;

public class RankAnswer {

	public static List<ScoredAnswer> findTopAnswers( Question input_question, List<ScoredQuestion> questions, List<Answer> answers ) {
		
		answers = DumpRemove.AnsdupRemove(answers);
		
		List<ScoredAnswer> scored_answers = new ArrayList<ScoredAnswer>();
		for( Answer a : answers ) {
			scored_answers.add( new ScoredAnswer(a) );
		}
		
		HashMap<String, ScoredQuestion> id2question = new HashMap<String, ScoredQuestion>();
		for( ScoredQuestion qs : questions ) 
			id2question.put(qs.question.id, qs);

		HashMap<String, Double>[] tfidf = AnswerTFIDF.tfidf(scored_answers);
		
		for ( int i=0; i<scored_answers.size(); i++ ) {
			Answer answer = scored_answers.get(i).answer;
			HashMap<String, Double> a_words = tfidf[i];
			
			// calculate QKW
			scored_answers.get(i).QKW = 0;
			for( Keyword k1 : input_question.kws ){
				for( Keyword k2 : answer.kws) {
					if( k1.word.equals(k2.word) ) 
						scored_answers.get(i).QKW += k1.weight;
				}
			}
			
			// calculate ANSWER KEYWORD
			Iterator<String> it2 = a_words.keySet().iterator();
			while (it2.hasNext()) {
				scored_answers.get(i).AKW += a_words.get(it2.next());
			}
			
			// calculate SENTENCE NUMBER
			String[] temp = answer.raw_content.split("[。，]");
			for (String tt : temp) {
				if (tt.length() > 5) {
					scored_answers.get(i).SN++;
				}
			}
			
			// calculate WORD NUMBER
			for (Word[] sent : answer.seg_content) {
				for (Word word : sent) {
					if (word.cont.length() > 1)
						scored_answers.get(i).WN++;
				}
			}

			// calculate CHARACTOR NUMBER
			scored_answers.get(i).CN = answer.raw_content.length();

			// calculate QUESITON SIMILARITY
			scored_answers.get(i).QUES_SIM = id2question.get(answer.question.id).score;
			
			// calculate Entity count
//			scored_answers.get(i).QUES_SIM = 0;
//			for( Entity e1 : input_question.entities ){
//				for( Entity e2 : answer.entities) {
//					if( e1.name.equals(e2.name) ) 
//						scored_answers.get(i).QUES_SIM = 5;   //计算答案与问题的相似度
			
//			scored_answers.get(i).EC++;
//				}
//			}

		}
		
		//liu 2015.12.15,如果QKW值为0，则去除
		int count = scored_answers.size();
		 for (int i = count-1; i >= 0; i--) {
				if (scored_answers.get(i).QKW == 0.00) 
					//System.out.println(scored_answers.get(i).QKW);
					scored_answers.remove(i);
				
		 	}
		
		
		return ScoredAnswer.calculateFinalScores(scored_answers);
	}
}
