package application.qav2.ao;

public class ScoredQuestion implements Comparable<ScoredQuestion> {
	
	public String target_question;
	public Question question;
	public Double score; // similarity

	public ScoredQuestion(Question question, String target_question) {
		this.target_question = target_question;
		this.question = question;
		score = 0.0;
	}
	
	@Override
	public String toString() {
		return String.format("question: %s\n\tscore: %.2f", 
				question.raw_content, score );
	}
	
	@Override
	public int compareTo(ScoredQuestion arg0) {
		return -score.compareTo(arg0.score);
	}
}
