package application.qav2.process_question.intention;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import platform.util.log.Trace;

public class QuestionIntention {

	static final Trace t = new Trace().setValid(false, true);

	public static List<QuestionBlock> extractBody(String plain_question)
			throws Exception {
//		String[] q_a = question.split("(，\\\\D|。\\\\J)");
		String[] q_a = plain_question.split("[，。]+");
		ArrayList<QuestionBlock> bodys = new ArrayList<QuestionBlock>();
		for (String str : q_a) {
			Pattern p = Pattern.compile("(.*?(?:吗|呢|$))");
			Pattern p1 = Pattern.compile("((?:请问|问一下|不知道).*$)");
			Matcher m = p.matcher(str);
			boolean exist = false;
			while (m.find()) {
				exist = true;
				String sent = m.group(1);
				Matcher m1 = p1.matcher(sent);
				if (m1.find()) {
					QuestionBlock qb = new QuestionBlock(m.group(1));
					if( qb.isValid() )
						bodys.add(qb);
				} else { 
					QuestionBlock qb = new QuestionBlock(sent);
					if( qb.isValid() )
						bodys.add(qb);
				}
			}
			if(!exist) {
				QuestionBlock qb = new QuestionBlock(str);
				if(qb.isValid()) {
					bodys.add(qb);
				}
			}
		}
		return bodys;
	}
	
	public static void main(String[] args) throws Exception {
//		String question = "学他多少";
		String question = "应该吃什么东西？";
		List<QuestionBlock> qbs = extractBody(question);
		for(QuestionBlock qb : qbs) {
			System.out.println(qb.question);
			System.out.println(qb.interrogative);
			System.out.println(qb.type);
			System.out.println(qb.target_type);
			System.out.println();
		}
	}

}
