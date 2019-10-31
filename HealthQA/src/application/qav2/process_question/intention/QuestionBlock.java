package application.qav2.process_question.intention;

import java.util.regex.Pattern;

public class QuestionBlock {
	public String question;
	public String interrogative;
	public String type;   // open question or close question
	public String target_type; // M, Y, ..., 
	
//	JSONObject entity;
//	List<Word> keyword = new ArrayList<Word>();
	public QuestionBlock(String _question) {
		question = _question;
		parseTargetType();
		parseType();
	}
	
	@Override
	public String toString() {
		return "["+type+"|"+target_type+","+interrogative+": "+question+"]";
	}
	
	public boolean isValid() {
		return type!=null;
	}
	
	void parseTargetType() {
		QuestionBlock qb = this;
		String str = qb.question;
		for (String s : QuesDef.M) {
			if (str.contains(s))
				qb.target_type = "M";
		}
		for (String s : QuesDef.Y) {
			if (str.contains(s))
				qb.target_type = "Y";
		}
		for (String s : QuesDef.T) {
			if (str.contains(s))
				qb.target_type = "T";
		}
		for (String s : QuesDef.I) {
			if (str.contains(s))
				qb.target_type = "I";
		}
		for (String s : QuesDef.R) {
			if (str.contains(s))
				qb.target_type = "R";
		}
		for (String s : QuesDef.W) {
			if (str.contains(s))
				qb.target_type = "W";
		}
		for (String s : QuesDef.D) {
			if (str.contains(s))
				qb.target_type = "D";
		}
	}
	
	void parseType() {
		QuestionBlock qb = this;
		String str = qb.question;
		for (String s : QuesDef.END) {
			if (str.contains(s)) {
				qb.interrogative = s;
				qb.type = "CLOSE";
			}
		}
		for (String s :  QuesDef.nengbuneng) {
			if (str.contains(s)) {
				qb.interrogative = s;
				qb.type = "CLOSE";
			}
		}
		for (String s :  QuesDef.shibushi) {
			if (str.contains(s)) {
				qb.interrogative = s;
				qb.type = "CLOSE";
			}
		}
		for (String s :  QuesDef.huibuhui) {
			if (str.contains(s)) {
				qb.interrogative = s;
				qb.type = "CLOSE";
			}
		}
		Pattern p = Pattern.compile("有.+吗");
		if( p.matcher(str).find() ) {
			qb.interrogative = "吗";
			qb.type = "OPEN";
		}
		for (String s :  QuesDef.ruhe) {
			if (str.contains(s) && !str.contains("没有" + s)
					&& !str.contains("没" + s) && !str.contains("不" + s)) {
				qb.interrogative = s;
				qb.type = "OPEN";
			}
		}
		for (String s : QuesDef.duoshao) {
			if (str.contains(s) && !str.contains("没有" + s)
					&& !str.contains("没" + s) && !str.contains("不" + s)) {
				qb.interrogative = s;
				qb.type = "OPEN";
			}
		}
		for (String s : QuesDef.na) {
			if (str.contains(s) && !str.contains("没有" + s)
					&& !str.contains("没" + s) && !str.contains("不" + s)) {
				qb.interrogative = s;
				qb.type = "OPEN";
			}
		}
		for (String s : QuesDef.shenme) {
			if (str.contains(s) && !str.contains("没有" + s)
					&& !str.contains("没" + s) && !str.contains("不" + s)) {
				qb.interrogative = s;
				qb.type = "OPEN";
			}
		}
	}
}
