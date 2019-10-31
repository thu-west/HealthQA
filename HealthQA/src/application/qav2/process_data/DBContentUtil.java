package application.qav2.process_data;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import platform.nlp.ltp.LTPSettings;
import platform.util.general.JavaRegex;

public class DBContentUtil {
	
	static Pattern pattern_dot_ch = Pattern.compile("\\.["+JavaRegex.chinese_character+"]");
	static Pattern pattern_decimals = Pattern.compile("[^.0-9][0-9]+([,，、·•])[0-9]+(?!(.[0-9]+))");
	
	static String[] stopwords = new String[]{
			"您好","你好","谢谢您", "谢谢你", "谢谢大夫", "谢谢医师", "谢谢"
			};
	
	static String preprocess_common ( String str ) {
		
		str = str.replace("#", "").replace("\\", "");
		
		// delete stop words
		for(String w : stopwords ){
			str = str.replace(w, "");
		}
		
		// 行首空格和回车
		str = str.replaceAll("　", "");
		str += "。";
		str = str.replaceAll("^[\r\n\t "+JavaRegex.punctuation+"]+", "");
		str = str.replaceAll("[^"+JavaRegex.chinese_character+"]{15,}", "");
		str = str.replaceAll(JavaRegex.url_http, "");
		str = str.replaceAll(JavaRegex.url_www, "");
		str = str.replaceAll("[\t ]+", "");
		str = str.replaceAll("[？！；。?;!\r\n]+", "。");
		str = str.replaceAll("[【{\\(\\[]", "(");
		str = str.replaceAll("[】}\\)\\]]", ")");
		str = str.replaceAll("[,，]+", "，");
		str = str.replaceAll("[、]+", "、");
		str = str.replaceAll("[：:]+", "：");
		str = str.replaceAll("[.]+", ".");
		str = str.replaceAll("[，、.：:,（\\-]+。", "。");
		str = str.replaceAll("。[，、.：:,（\\-]+", "。");
		
		// . join a chinese character ===>  。 join a chinese character
		StringBuffer buf_str = new StringBuffer(str);
		Matcher m = pattern_dot_ch.matcher(str);
		while( m.find() ){
			m.start();
			buf_str.setCharAt(m.start(), '。');
		}
		str = buf_str.toString();
		
		// num、num num，num num,num  haha5.9,5.9,  5,9。
		Matcher m2 = pattern_decimals.matcher(str);
		buf_str = new StringBuffer(str);
		while( m2.find() ){
			m2.start(1);
			buf_str.setCharAt(m2.start(1), '.');
		}
		str = buf_str.toString();
		
		String[] ss = str.split("。");
		String new_str = "";
		for( String temp : ss ){
			if( temp.length() >= LTPSettings.piece_valid_length ) {
				new_str = new_str + temp + "。";
			}
		}
		return new_str.replaceAll("\\s", "").replace("\\0", "");
	}
	
	static String preprocess_specified_questions( String question, String root ) {
		
		if( root.equals("ask.39.net") ) {
			if( question.contains("这种病已困扰我很长时间了，真的很痛苦，请问究竟该如何治疗才能痊愈。"))
				return null;
			if( Pattern.matches(".*咨询下["+JavaRegex.chinese_character+"。]+要怎么办才好.*", question))
				return null;
			if( Pattern.matches(".*请问["+JavaRegex.chinese_character+"。]+这个问题好烦人.*", question))
				return null;
			if( Pattern.matches(".*咨询下["+JavaRegex.chinese_character+"。]+请问["+JavaRegex.chinese_character+"]+怎么样.*", question))
				return null;
//			question = question.replace("性别：", " ");
//			question = question.replace("年龄：", " ");
			question = question.replace("病情描述：", " ");
			// 曾经的治疗及用药情况
			question = question.replaceAll("发病时间", " ");
			question = question.replaceAll("补充问题", " ");
			question = question.replaceAll("想得到怎样的帮助。", " ");
			question = question.replaceAll("想得到怎样的帮助：", " ");
			question = question.replaceAll("想得到怎样的帮助", " ");
			question = question.replaceAll("曾经的治疗及用药情况", " ");
			question = question.replaceAll("曾经治疗情况及是否有过敏、遗传病史", " ");
//			question = question.replaceAll("\\([0-9]+-[0-9]+-[0-9]+", " ");
			// help content reserve
//			String help = "";
//			if( question.contains(" ") )
//				help = question.substring(question.indexOf(" ")+1);
//			if( question.contains("\t"))
//				question = question.substring(0, question.indexOf("\t")) + "。"+help;
			return question;
		}
		
		if (root.equals("club.xywy.com")) {
			
			question = question.replaceAll("病情描述（发病时间、主要症状、症状变化等）。", " ");
			question = question.replaceAll("病情描述（发病时间、主要症状、症状变化等）：", " ");
			question = question.replaceAll("病情描述（发病时间、主要症状、症状变化等）", " ");
			question = question.replaceAll("曾经治疗情况和效果", " ");
			question = question.replaceAll("想得到怎样的帮助。", " ");
			question = question.replaceAll("想得到怎样的帮助：", " ");
			question = question.replaceAll("想得到怎样的帮助", " ");
//			// remove dumplicate:
//			question = question.substring(question.indexOf("\t")+1);
//			String help = "";
//			// help content reserve
//			if( question.contains(" ") )
//				help = question.substring(question.indexOf(" ")+1);
//			// remove aboundon
//			if( question.contains("\n") )
//				question = question.substring(0, question.indexOf("\n")) + "。" + help; 
			question.replaceAll(" ", "。");
			return question;
		}
		
		if (root.equals("120ask.com")) {
			question = question.replaceAll("健康咨询描述。", "");
			question = question.replaceAll("健康咨询描述：", "");
			question = question.replaceAll("健康咨询描述", "");
			question = question.replaceAll("感谢医生为", "\t");
			question = question.replaceAll("我快速解答", ";;;");
			question = question.replaceAll("该如何治疗和预防", " ");
			question = question.replaceAll("曾经的治疗情况和效果。", " ");
			question = question.replaceAll("曾经的治疗情况和效果：", " ");
			question = question.replaceAll("曾经的治疗情况和效果", " ");
			question = question.replaceAll("想得到怎样的帮助。", " ");
			question = question.replaceAll("想得到怎样的帮助：", " ");
			question = question.replaceAll("想得到怎样的帮助", " ");
			// help content reserve
//			String help = "";
//			if (question.contains(" ")) 
//				help = question.substring(question.indexOf(" ") + 1);
//			if (question.contains("\n")) 
//				question = question.substring(0, question.indexOf("\n"))+ "。" + help;
//			// remove aboundon
//			if( question.contains("!!!")) 
//				question = question.substring(0, question.indexOf("!!!")-4)+"。";
//			if( question.contains(";;;")) 
//				question = question.substring(0, question.indexOf(";;;")-1);
			if( question.contains("\t")) 
				question = question.substring(0, question.indexOf("\t"));
			
			question.replaceAll(" ", "。");
			return question;
		}
		return null;
	}
	
	public static String preprocess_specified_answer (String answer, String reply_ID ){
		
		if( Pattern.matches("^000401.*", reply_ID) ) {
			if( answer.contains("审核") || answer.contains("投诉")) {
				return null;
			}
			answer = answer.replaceAll("建议：", "。");
			answer = answer.replaceAll("病情分析：", "。");
			answer = answer.replaceAll("分析：根据您的描述，", "。");
			answer = answer.replaceAll("分析：", "。");
			answer = answer.replaceAll("提问者对于答案的评价", "\t");
			int cut = answer.indexOf('\t');
			if( cut >= 0 )
				answer = answer.substring(0, answer.indexOf('\t'));
			return answer;
		}
		
		if (Pattern.matches("^000501.*", reply_ID)) {
			answer = answer.replaceAll("病情分析：", "。");
			answer = answer.replaceAll("根据你的叙述，", "");
			answer = answer.replaceAll("根据你的叙述！", "");
			answer = answer.replaceAll("指导意见：", "。");
			answer = answer.replaceAll("意见建议：", "。");
			return answer;
		}
		
		if (Pattern.matches("^000801.*", reply_ID)) {
			if ( answer.contains("系统提示") && answer.contains("语音") )
				return null;
			answer = answer.replaceAll("病情分析：", "。");
			answer = answer.replaceAll("根据你的叙述，", "");
			answer = answer.replaceAll("根据你的叙述！", "");
			answer = answer.replaceAll("指导意见：", "。");
			answer = answer.replaceAll("意见建议：", "。");
			return answer;
		}
		
		return answer;
//		
//		String[] splitwords = new String[] {
//			"建议:","建议："
//			,"分析：","分析:"
//			,"病情分析：","病情分析:"
//			,"病情：","病情:"
//			,"指导意见：","指导意见:"
//			};
//		
//		for(String w : splitwords ){
//			answer = answer.replace(w, "\n");
//		}
//		return answer;
	}
	
	public static String json_to_plaintext( String content_in_json ){
		JSONObject json = new JSONObject( content_in_json );
		Iterator<String> it_key = json.keys();
		String content = "";
		while( it_key.hasNext() ){
			String key = it_key.next();
			// 不需要性别年龄字段
			if( key.equals("性别")|| key.equals("年龄"))
				continue;
			content = content + json.get( key )+"\n";
		}
		return content;
	}
	
	public static void main(String[] args) {
//		String s = "请问治疗糖尿病那家医院好。治疗糖尿病那家医院好这个问题好烦人：去年因为心血管动脉硬化和脑血管各住一次院，因为胸�有积液，抽了以后才好。今年医生说是心衰，心外抽出大量";
//		System.out.println(preprocess_specified_questions(s, "ask.39.net"));
		String str = "{}()【】[]";
				str = str.replaceAll("[【{\\(\\[]", "(");
				str = str.replaceAll("[】}\\)\\]]", ")");
				System.out.println(str);
				
	}
	
}
