package application.qav2.process_question.structual;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import platform.util.log.Trace;

public class QuestionParser {
	//                                                           1   2         3        4   5   6          7        8
	public static final Pattern organ_os = Pattern.compile("(?:(tt)|(f)|O| )*(o|s) (?:(tt)|(l)|(f)|O| )*(os|ss) (?:(l)|O )*");
	//                                                               1   2        3       4   5   6         7        8
	public static final Pattern indicator_is = Pattern.compile("(?:(tt)|(f)|O| )*(i) (?:(tt)|(l)|(f)|O| )*(is|g) (?:(l)|O )*");
	//                                                         1    2        3      4
	public static final Pattern disease = Pattern.compile("(?:(tt)|(f)|O| )*(d) (?:(l)|O| )*");
	//                                                       1    2    3   4         5       6    7        8
	public static final Pattern drug = Pattern.compile("(?:(gmt)|(gm)|(f)|(tt)|O| )*(m) (?:(gmt)|(gm)|D|O|(l)| )*");
	//                                                       1    2        3      4
	public static final Pattern treat = Pattern.compile("(?:(tt)|(f)|O| )*[^t](t) (?:(l)|O| )*");
	//                                                       1    2        3      
	public static final Pattern check = Pattern.compile("(?:(tt)|(f)|O| )*(c) (?:(O)| )*");
//	public static final Pattern check = Pattern.compile("(?:(tt)|(f)|O| )*(c) (?:(l)|O| )*");
	
	static Trace t = new Trace().setValid(false, true);
	
	static JSONObject qinfo = new JSONObject();
	static String[] tag_status = null;
	
	/**
	 * 输入一个问题，使用model进行实体抽取，输出已经标记好的问题
	 * 
	 * 输入标记好的问题，通过标记，分析出问题中的
	 * 		疾病，
	 * 		药物，
	 * 		器官及其描述，
	 * 		指标及其描述，
	 * 		治疗方法，
	 * 		年龄，身高，体重，性别等信息，
	 * 输出这些信息条目（JSON）。
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	
	static void extractSymptom ( String tags, String[] pos2word ) {
		Matcher m = organ_os.matcher(tags);
		JSONArray symptoms  = new JSONArray();
		while(m.find()) {
			t.debug("extract symptom");
			JSONObject o = new JSONObject();
			o.put("o", pos2word[m.start(3)]); tag_status[m.start(3)] = null;
			o.put("os", pos2word[m.start(7)]); tag_status[m.start(7)] = null;
			int i = 0;
			if ((i = m.start(1)) >= 0) { o.put("tt", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(4)) >= 0) { o.put("tt", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(2)) >= 0) { o.put("f", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(6)) >= 0) { o.put("f", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(5)) >= 0) { o.put("l", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(8)) >= 0) { o.put("l", pos2word[i]); tag_status[i] = null; }
			symptoms.put(o);
		}
		if(symptoms.length()>0)
			qinfo.put("symptom", symptoms);
	}
	
	static void extractIndicator ( String tags, String[] pos2word ) {
		Matcher m = indicator_is.matcher(tags);
		JSONArray indicators = new JSONArray();
		while(m.find()) {
			t.debug("extract indictor");
			JSONObject o  = new JSONObject();
			o.put("i", pos2word[m.start(3)]); tag_status[m.start(3)] = null;
			o.put("is", pos2word[m.start(7)]); tag_status[m.start(7)] = null;
			int i = 0;
			if ((i = m.start(1)) >= 0) { o.put("tt", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(4)) >= 0) { o.put("tt", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(2)) >= 0) { o.put("f", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(6)) >= 0) { o.put("f", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(5)) >= 0) { o.put("l", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(8)) >= 0) { o.put("l", pos2word[i]); tag_status[i] = null; }
			
			indicators.put(o);
		}
		if(indicators.length()>0)
			qinfo.put("indicator", indicators);
	}
	
	static void extractCheck ( String tags, String[] pos2word ) {
		Matcher m = check.matcher(tags);
		JSONArray checks = new JSONArray();
		while(m.find()) {
			t.debug("extract check");
			JSONObject o  = new JSONObject();
			o.put("c", pos2word[m.start(3)]);
			tag_status[m.start(3)] = null;
			int i = 0;
			if ((i = m.start(1)) >= 0) { o.put("tt", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(2)) >= 0) { o.put("f", pos2word[i]); tag_status[i] = null; }
//			if ((i = m.start(4)) >= 0) { o.put("l", pos2word[i]); tag_status[i] = null; }
			checks.put(o);
		}
		if(checks.length()>0)
			qinfo.put("check", checks);
	}
	
	static void extractTreat ( String tags, String[] pos2word ) {
		Matcher m = treat.matcher(tags);
		JSONArray treats = new JSONArray();
		while(m.find()) {
			t.debug("extract treat: "+m.group(0));
			JSONObject o  = new JSONObject();
			o.put("t", pos2word[m.start(3)]);
			tag_status[m.start(3)] = null;
			int i = 0;
			if ((i = m.start(1)) >= 0) { o.put("tt", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(2)) >= 0) { o.put("f", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(4)) >= 0) { o.put("l", pos2word[i]); tag_status[i] = null; }
			treats.put(o);
		}
		if(treats.length()>0)
			qinfo.put("treat", treats);
	}
	
	static void extractDisease ( String tags, String[] pos2word ) {
		Matcher m = disease.matcher(tags);
		JSONArray diseases = new JSONArray();
		while(m.find()) {
			t.debug("extract disease");
			JSONObject o  = new JSONObject();
			o.put("d", pos2word[m.start(3)]);
			tag_status[m.start(3)] = null;
			int i = 0;
			if ((i = m.start(1)) >= 0) { o.put("tt", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(2)) >= 0) { o.put("f", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(4)) >= 0) { o.put("l", pos2word[i]); tag_status[i] = null; }
			diseases.put(o);
		}
		if(diseases.length()>0)
			qinfo.put("disease", diseases);
	}
	
	static void extractDrug ( String tags, String[] pos2word ) {
		Matcher m = drug.matcher(tags);
		JSONArray drugs = new JSONArray();
		while(m.find()) {
			t.debug("extract drug");
			JSONObject o  = new JSONObject();
			o.put("d", pos2word[m.start(5)]);
			tag_status[m.start(5)] = null;
			int i = 0;
			if ((i = m.start(4)) >= 0) { o.put("tt", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(3)) >= 0) { o.put("f", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(6)) >= 0) { o.put("gmt", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(7)) >= 0) { o.put("gm", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(1)) >= 0) { o.put("gmt", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(2)) >= 0) { o.put("gm", pos2word[i]); tag_status[i] = null; }
			if ((i = m.start(8)) >= 0) { o.put("l", pos2word[i]); tag_status[i] = null; }
			drugs.put(o);
		}
		if(drugs.length()>0)
			qinfo.put("drug", drugs);
	}
	
	static void extractRemain( String tags, String[] pos2word ) {
		JSONArray os = new JSONArray();
		JSONArray o = new JSONArray();
		JSONArray s = new JSONArray();
		JSONArray ss = new JSONArray();
		JSONArray i = new JSONArray();
		for( int j=0; j<tag_status.length; j++ ) {
			if(tag_status[j] != null) {
				String tag = tag_status[j];
				if(tag.equals("os") || tag.equals("ss")) os.put(pos2word[j]);
				else if(tag.equals("o") || tag.equals("s")) o.put(pos2word[j]);
				else if(tag.equals("i")) i.put(pos2word[j]);
			}
		}
		JSONObject remain = new JSONObject();
		if(os.length()>0)
			remain.put("os", os);
		if(s.length()>0)
			remain.put("s", s);
		if(o.length()>0)
			remain.put("o", o);
		if(ss.length()>0)
			remain.put("ss", ss);
		if(i.length()>0)
			remain.put("i", i);
		if(remain.length()>0)
			qinfo.put("remain", remain);
	}
	
	static void dealRelation( String rel ) {
		qinfo.put("relation", rel);
		if(qinfo.has("gender") ) 
			return;
		if(rel.contains("婆")
				|| rel.contains("妻")
				|| rel.contains("妹")
				|| rel.contains("女")
				|| rel.contains("姐")
				|| rel.contains("妈")
				|| rel.contains("奶")
				|| rel.contains("娘"))
			qinfo.put("gender", "女");
		else if(rel.contains("爷")
				|| rel.contains("爸")
				|| rel.contains("舅")
				|| rel.contains("哥")
				|| rel.contains("弟")
				|| rel.contains("儿子"))
			qinfo.put("gender", "男");
	}
	
	public static  JSONObject structualRepresent ( String ques_anno ) throws Exception {
		// 标出：逗号\D，句号\J
		StringBuffer sb = new StringBuffer();
		for( int i=0; i<ques_anno.length(); i++) {
			if(ques_anno.charAt(i) == '，')
				sb.append(" ，\\D ");
			else if(ques_anno.charAt(i) == '。')
				sb.append(" 。\\J ");
			else
				sb.append(ques_anno.charAt(i));
		}
		ques_anno = sb.toString().replaceAll("^[ ]+", "").replaceAll("[ ]+$", "").replaceAll("[ ]+", " ");
		
		// 抽取年龄，性别，关系等信息，同时统计标签长度以便进行下一步处理
		StringBuffer sb_orig_ques = new StringBuffer();
		String[] ss = ques_anno.split(" ");
		int total_tag_len = 0;
		Pattern p = Pattern.compile("^(.*)\\\\(i|is|o|os|s|ss|m|gm|gmt|d|c|t|g|f|l|tt|D|J|a|x|r)$");
		for(String s : ss ) {
			Matcher m = p.matcher(s);
			String word = s;
			String tag = "O";
			if(m.find()) {
				word = m.group(1);
				sb_orig_ques.append(word);
				tag = m.group(2);
				total_tag_len += tag.length();
				// 顺表抽取年龄，性别，关系
				if(tag.equals("a"))
					qinfo.put("age", word);
				else if(tag.equals("x")) 
					qinfo.put("gender", word);
				else if(tag.equals("r")) 
					dealRelation(word);
			} else {
				total_tag_len++;
			}
		}
		
		// 创建标签序列tags，以空格分隔各个tag，同时建立tag位置和对应词语的映射
		String[] pos2word = new String[total_tag_len+ss.length];
		tag_status = new String[total_tag_len+ss.length];
		String tags = null;
		int i=0;
		StringBuffer sb_tags = new StringBuffer();
		for(String s : ss) {
			Matcher m = p.matcher(s);
			String word = s;
			String tag = "O";
			if(m.find()) {
				word = m.group(1);
				tag = m.group(2);
			}
			pos2word[i] = word;
			tag_status[i] = tag;
			sb_tags.append(tag);
			sb_tags.append(" ");
			i += ( 1+tag.length());
		}
		tags = sb_tags.toString();
		t.debug(ques_anno);
		t.debug(tags);
		
		extractSymptom(tags, pos2word);
		extractIndicator(tags, pos2word);
		extractDisease(tags, pos2word);
		extractDrug(tags, pos2word);
		extractCheck(tags, pos2word);
		extractTreat(tags, pos2word);
		extractRemain(tags, pos2word);
		
		return qinfo;
	}
	
	static void test2 () {
		Pattern p = Pattern.compile("(?:(tt)|(f)|O| )*(i) (?:(tt)|(l)|(f)|O| )*(is) (?:(l)|O )*");
		
		Matcher m = p.matcher("O r O d D tt O i O is O d D O tt O J O o os O J O J ");
		if( m.find()) {
			System.out.println(m.start(1)+": "+m.group(1));
			System.out.println(m.start(2)+": "+m.group(2));
			System.out.println(m.start(3)+": "+m.group(3));
			System.out.println(m.start(4)+": "+m.group(4));
			System.out.println(m.start(5)+": "+m.group(5));
			System.out.println(m.start(6)+": "+m.group(6));
			System.out.println(m.start(7)+": "+m.group(7));
			System.out.println(m.start(8)+": "+m.group(8));
		}
		
		if( m.find()) {
			System.out.println(m.start(1)+": "+m.group(1));
			System.out.println(m.start(2)+": "+m.group(2));
			System.out.println(m.start(3)+": "+m.group(3));
			System.out.println(m.start(4)+": "+m.group(4));
			System.out.println(m.start(5)+": "+m.group(5));
			System.out.println(m.start(6)+": "+m.group(6));
			System.out.println(m.start(7)+": "+m.group(7));
			System.out.println(m.start(8)+": "+m.group(8));
		}
	}
		
	static void test3 () throws Exception {
		JSONObject o = structualRepresent(
				"我 婆婆\\r 有 糖尿病\\d ， 7月\\tt 突发 心肌\\o 梗塞\\os ，做了2个 血管支架\\t ， 今天\\tt 突然 饭后血糖\\i 3.5\\g ，出 虚汗\\d ， 腿\\o 软不能走路\\os ，躺着稍微好点，需要去医院检查吗。");
		System.out.println(o.toString(2));
	}
	
	public static void main(String[] args) throws Exception {
//		test1();
//		test2();
		test3();
		
	}
}
