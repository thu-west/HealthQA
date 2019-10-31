package application.qav2.ao;

import java.util.List;

public class ScoredAnswer implements Comparable<ScoredAnswer>{
	
	public Answer answer;
	public Double score = 0.0;
	public Keyword[] kws_scored;
	
	public double AKW = 0;
	public double QKW = 0;
	public double QUES_SIM = 0;
	public int SN = 0;
	public int WN = 0;
	public int CN = 0;
	
	//liu
	public int EC = 0;
	
	private double STAT = 0;
	
	private double total = 28;
	private double alpha_QKW = 10/total;
	private double alpha_QUES_SIM = 8/total;
	private double alpha_AKW = 6/total;
	private double alpha_STAT = 4/total;
	//
	private double alpha_EC = 9/total;
	
	public ScoredAnswer ( Answer answer ) {
		this.answer = answer;
	}
	
	public String printKeywords() {
		String line = "";
		for( Keyword kw : kws_scored ) {
			line = line + kw.word + "("+kw.weight+")";
		}
		return line;
	}
	
	public static List<ScoredAnswer> calculateFinalScores( List<ScoredAnswer> as_list ) {
		
		int total_answer = as_list.size();
		
		// calculate STAT
		double aver_SN=0, aver_WN=0, aver_CN=0;
		for( ScoredAnswer as : as_list ) {
			aver_SN += as.SN;
			aver_WN += as.WN;
			aver_CN += as.CN;
		}        
		aver_SN /= total_answer;
		aver_WN /= total_answer;
		aver_CN /= total_answer;
		for( ScoredAnswer as : as_list ) {
			as.STAT = Math.pow(0.5, aver_SN/as.SN) + Math.pow(0.5, aver_WN/as.WN) + Math.pow(0.5, aver_CN/as.CN); 
		}
		
		// normalize
		double aver_AKW=0, aver_QUES_SIM=0, aver_STAT=0, aver_QKW=0;
		for( ScoredAnswer as : as_list ) {
			aver_AKW += as.AKW;
			aver_QUES_SIM += as.QUES_SIM;
			aver_STAT += as.STAT;
			aver_QKW += as.QKW;
		}
		aver_AKW /= total_answer;
		aver_QUES_SIM /= total_answer;
		aver_STAT /= total_answer;
		aver_QKW /= total_answer;
		for( ScoredAnswer as : as_list ) {
			as.AKW /= aver_AKW;
			as.STAT /= aver_STAT;
			as.QUES_SIM /= aver_QUES_SIM;
			as.QKW /= aver_QKW;
			as.score = as.alpha_AKW * as.AKW + as.alpha_QUES_SIM * as.QUES_SIM + as.alpha_STAT * as.STAT + as.alpha_QKW * as.QKW;
		}
		
		return as_list;
	}
	
	@Override
	public String toString() {
		return String.format("answer: %s\n\tscore: %.2f <== QKW: %.2f, AKW: %.2f, QUES_SIM: %.2f, STAT: %.2f (SN-%d,WN-%d,CN-%d)", 
				answer.raw_content, score, QKW, AKW, QUES_SIM, STAT, SN, WN, CN);
	}
	
	@Override
	public int compareTo(ScoredAnswer arg0) {
		return -score.compareTo(arg0.score);
	}
}
