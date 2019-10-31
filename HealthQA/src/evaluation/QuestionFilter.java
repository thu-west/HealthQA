package evaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.util.*;

import application.qav2.ao.Question;
import application.qav2.ao.ScoredAnswer;
import application.qav2.ao.ScoredQuestion;
import application.qav2.retrieval.text_retrieval.QuestionSearcher;
import application.qav2.retrieval.text_retrieval.util.RankQuestion;
import application.qav2.summarization.Summarization;
import platform.GlobalSettings;
import platform.db.database.ISHCDBConfig;
import platform.db.database.ISHCDataOperator;

public class QuestionFilter {
	static ISHCDataOperator operator = new ISHCDataOperator(new ISHCDBConfig(GlobalSettings.database_url, GlobalSettings.database_username, GlobalSettings.database_password));

	public static void generate_question_set() throws Exception {  //select questions randomly
		int count = 0;
		int num = 400; //  the number of question to select
		int min_len = 500; // the minimal length of answer
		Set<String> searched_id = new TreeSet<String>(); // restore the searched id
		Map<Integer, Integer> len_list = new HashMap<Integer, Integer>(); // restore the
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"./res/selected_question" + ".txt"));
		int max;
	    int min=1;
	    String q="";
	    Random random1 = new Random();
	    Random random2 = new Random();
	    String regEx="[`\\~!@#$%^&*\\(\\)+=|{}':;'\\\\,\\[\\].<>≥/?~！[\"\']\\-@#￥%……&*\\（\\）——+|{}【】‘；：”“’。，、？]";   
		while (count < num) {
			int sample1=random1.nextInt(3);
			String id;
			if(sample1==0){
				id="401000000000";
				max=73767;
			}
			else if(sample1==1){
				id="501000000000";
				max=32564;
			}
			else{
				id="801000000000";
				max=33257;
			}
			int sample = random2.nextInt(max)%(max-min+1) + min;   // to_do generate id randomly
			String x=String.format("%05d", sample);
			id=id+x;
			ArrayList<String> qs = new ArrayList<String>();
			if (!searched_id.contains(id)) {
				searched_id.add(id);
				String sql1 = "select `ID`, `content` from question where `ID`="
						+ id + ";"; // fetch question of certain id
				ResultSet rs = operator.query(sql1);
				try {
					while (rs.next()) {
						q=rs.getString(2);	
						q=q.replaceAll(regEx," ");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("id: "+id);
				// find similar question
				if (!q.isEmpty()) {
					List<Question> rawQs = QuestionSearcher.searchQuestion(q,
							100, 0.75);
					List<ScoredQuestion> sqs = RankQuestion.rankQuestions(q, rawQs, 0);
					Question question = new Question("0", q, q);
					question.findKeywords();
					List<ScoredAnswer> sa = Summarization.findTopAnswers(
							question, sqs);
					int temp = 0;
					if(sa!=null){
						for (ScoredAnswer s : sa) {
							temp += s.answer.raw_content.length();
							searched_id.add(s.answer.question_id);
						}
					}
					if (temp >= min_len) {
						len_list.put(++count, temp);
						if(!qs.contains(q)){
							qs.add(q);	
							bw.write(count + ": " + q);
							bw.newLine();
							for (int i = 0; i < sqs.size(); i++) {
								searched_id.add(sqs.get(i).question.id);
								bw.write(count + ": "
										+ sqs.get(i).question.raw_content);
								bw.newLine();
							}
							System.out.println("Count:");
							System.out.print(count);
							System.out.println("");
						}
					}
				}

			}
		}
		bw.close();
		for (int key : len_list.keySet()) {
			System.out.println(key + " : " + len_list.get(key));
		}
		System.out.println("End");
	}
	
	
	public static void main(String[] args) throws Exception {	
		generate_question_set();	
	}
}