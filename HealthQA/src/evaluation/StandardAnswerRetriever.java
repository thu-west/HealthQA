package evaluation;

import java.io.*;
import java.util.*;

import application.qav2.ao.Question;
import application.qav2.ao.ScoredAnswer;
import application.qav2.ao.ScoredQuestion;
import application.qav2.retrieval.text_retrieval.QuestionSearcher;
import application.qav2.retrieval.text_retrieval.util.RankQuestion;
import application.qav2.summarization.Summarization;

public class StandardAnswerRetriever {

	public static void generate_standard_answer_set() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				"./res/question sample.txt"));

		String q = "";
		int i = 0;
		Map<Integer, Integer> len_list = new HashMap<Integer, Integer>();
		Set<Integer> len_set = new TreeSet<Integer>();
		int total = 0;
		while ((q = br.readLine()) != null) {
			List<Question> rawQs = QuestionSearcher.searchQuestion(q,
					100, 0.75);
			List<ScoredQuestion> sqs = RankQuestion.rankQuestions(q, rawQs, 0);
			Question ques = new Question("0", q, q);
			ques.findKeywords();
			List<ScoredAnswer> sa = Summarization.findTopAnswers(ques, sqs);
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					"./res/standard_answer_0.75/" + (++i) + ".txt"));
			bw.write("QUESTION: " + q);
			bw.newLine();
			bw.newLine();
			int j = 0;
			int temp = 0;
			if (sa != null) {
				for (ScoredAnswer s : sa) {

					bw.write("----------------------------------------------");
					bw.newLine();
					bw.write("REPLY QUESTION " + (++j));
					bw.newLine();
					bw.write(s.answer.question.raw_content);
					bw.newLine();
					bw.newLine();
					bw.write("ANSWER " + (j));
					bw.newLine();
					bw.write(s.answer.raw_content);
					bw.newLine();
					bw.newLine();
					total += s.answer.raw_content.length();
					temp += s.answer.raw_content.length();

				}
			}
			len_list.put(i, temp);
			len_set.add(temp);
			bw.write("----------------------------------------------");
			bw.close();
		}
		for (int s : len_set)
			// print answer length of every selected question
			System.out.println(s);
		System.out.println();
		System.out.println(total / (float) i); // average length
		System.out.println();
		for (int key : len_list.keySet()) {
			System.out.println(key + " : " + len_list.get(key)); // id+length
		}
		br.close();
	}

	public static void main(String[] args) throws Exception {

		generate_standard_answer_set();

	}
}
