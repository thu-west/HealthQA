package application.qav2.process_question.intention;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import application.qav2.process_question.structual.QuestionParser;
import platform.Platform;
import platform.nlp.ao.Word;
import platform.util.log.Trace;

public class QuestionType {
	
	static class QT {
		String question;
		String interrogative;
		JSONObject entity;
		List<Word> keyword = new ArrayList<Word>();
		public QT(String _question, String _keyword) {
			question = _question;
			interrogative = _keyword;
		}
	}
	
	static class Q {
		int type;
		String entity;
		String entity_info;
		public Q(int _type, String _entity, String _entity_info) {
			type = _type;
			entity = _entity;
			entity_info = _entity_info;
		}
	}
	
	static Trace t = new Trace().setValid(true, true);
	
	static String[] END = new String[] {"吗", "呢"};
	static String[] BEGIN = new String[] {"请问", "不知道", "问一下"};     // 不作为判断问句的标准
	static String[] nengbuneng = new String[] {"能不能", "能否", "可否" };
	static String[] shibushi = new String[] {"是不是", "是否"};
	static String[] ruhe = new String[] {"如何", "怎么", "怎样"};
	static String[] duoshao = new String[] {"多少", "多高", "多大", "多长时间", "多久"};
	static String[] huibuhui = new String[] {"会不会"};
	static String[] na = new String[] {"哪"};
	static String[] shenme = new String[] {"什么"};
	static String[] other = new String[] {"算不算","怎么", "怎么办", "怎么样", "怎样", "如何", "怎么回事", "怎么会", "咋", "咋回事", "为什么", "为何", "什么回事", "什么原因", "因为什么"
		,"何", "啥", "哪些", "哪里", "哪种", "哪个", "哪方面"};
	
	static String getType( String str )  {
		
		for( String s : END ) {
			if( str.contains(s) )
				return s+"//end";
		}
		for( String s : nengbuneng ) {
			if( str.contains(s) )
				return s+"//nengbuneng";
		}
		for( String s : shibushi ) {
			if( str.contains(s) )
				return s+"//shibushi";
		}
		for( String s : huibuhui ) {
			if( str.contains(s) )
				return s;
		}
		for( String s : ruhe ) {
			if( str.contains(s) && !str.contains("没有"+s) && !str.contains("没"+s) && !str.contains("不"+s) )
				return s+"//ruhe";
		}
		for( String s : duoshao ) {
			if( str.contains(s) && !str.contains("没有"+s) && !str.contains("没"+s) && !str.contains("不"+s) )
				return s+"//duoshao";
		}
		for( String s : na ) {
			if( str.contains(s) && !str.contains("没有"+s) && !str.contains("没"+s) && !str.contains("不"+s) )
				return s+"//na";
		}
		for( String s : shenme ) {
			if( str.contains(s) && !str.contains("没有"+s) && !str.contains("没"+s) && !str.contains("不"+s) )
				return s+"//shenme";
		}
		
		return null;
	}
	
	static ArrayList<QT> extractBody(String question) throws Exception {
		String[] q_a = question.split("(，\\\\D|。\\\\J)");
		ArrayList<QT> bodys = new ArrayList<QT>();
		for(String str : q_a ) {
			t.debug("ii: "+str);
			Pattern p = Pattern.compile("(.*?(?:吗|呢|$))");
			Pattern p1 = Pattern.compile("((?:请问|问一下|不知道).*$)");
			Matcher m = p.matcher(str);
			while(m.find()) {
				String sent = m.group(1);
				 {
					Matcher m1 = p1.matcher(sent);
					if(m1.find()) {
						String type = getType(m.group(1));
						if(type!=null)
							bodys.add(new QT(m.group(1), type));
					} else {
						String type = getType(sent);
						if(type != null )
							bodys.add(new QT(sent, type));
					}
				}
			}
		}
		for (QT qt : bodys) {
			t.debug(qt.interrogative+": "+qt.question);
		}
		return bodys;
	}
	
	static void fenci ( String filename ) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename+".fenci"));
		String line = null;
		while( (line=br.readLine()) != null ) {
			if(line.length()<1) {
				bw.newLine();
				continue;
			}
//			bw.write(line);
//			bw.write("  ***  ");
			bw.write(Word.toSimpleString( Platform.segment(line)).replaceAll("^[ ]*\\{[ ]*", "").replaceAll("[ ]*\\}[ ]*$", "") );
			bw.newLine();
		}
		bw.close();
		br.close();
	}
	
	static void test () throws Exception {
//		extractQuestion();
		
		String shenme_filter = "( |^)[^/ ]+/(?!n|v|ry)[a-z0-9]*";
		String shenme_noun_behind = "什么/ry [^/ ]+/n";
		String shenme_verb_behind = "什么/ry [^/ ]+/v";
		String shenme_verb_ahead = "( |^)[^/ ]+/v[a-z0-9]* 什么/ry";
		
		String na_filter = "( |^)[^/ ]+/(?!n|v|ry|q)[a-z]+";
		String na_noun_behind = "( |^)[^/ ]+/ry ([^/ ]+/q ){0,1}[^/ ]+/n";
		String na_verb_behind = "( |^)[^/ ]+/ry ([^/ ]+/q ){0,1}[^/ ]+/v";
		String na_verb_ahead = "( |^)[^/ ]+/v[a-z0-9]* [^/ ]+/ry";
		
		String zenme_filter = "( |^)[^/ ]+/(?!n|v|ryv)[a-z0-9]*";      // 需要用实体抽取的方法进一步处理
		String zenme_verb_behind = "怎么/ryv [^/ ]+/v";
		
		String zenyang_filter = zenme_filter;
		String zenyang_verb_behind = "怎样/ryv [^/ ]+/v";
		
		String ruhe_filter = zenme_filter;
		String ruhe_verb_behind = "如何/ryv [^/ ]+/v";
		String ruhe_noun_ahead = "( |^)[^/ ]+/n 如何/ryv";
		
		String huibuhui_filter = "( |^)[^/ 不没]+/(?!n|v|ryv)[a-z0-9]*";
		String shi_ma_pattern = "是.*?吗";    // 需要进一步用实体抽取的方法处理。
		/* String shi_ma_filter = "( |^)[^/ ]+/(?!n|v|ryv)[a-z0-9]*"; */
		
		String shibushi_pattern = "是/vshi 不/d 是/vshi.*";
		
		String nengbuneng_pattern = "能/v (不/d 能/v|不能/v).*";
		
		String youmeiyou_filter = "( |^)[^/ ]+/(?!n|v|d)[a-z0-9]*";
		String youmeiyou_noun_behind = "有/vyou 没/d 有/vyou [^/ ]+/n";
		String youmeiyou_verb_behind = "有/vyou 没/d 有/vyou [^/ ]+/v";
		String youmeiyou_noun_ahead = "( |^)[^/ ]+/n 有/vyou 没/d 有/vyou";
		
		String ma_filter = "( |^)[^/ ]+/(?!n|v|a|m|y)[a-z0-9]*";
		
//		fenci("C:\\Users\\Fire\\Desktop\\问句\\能不能_24");
//		fenci("C:\\Users\\Fire\\Desktop\\问句\\能否11");
//		fenci("C:\\Users\\Fire\\Desktop\\问句\\能...吗61");
//		fenci("C:\\Users\\Fire\\Desktop\\问句\\如何46");
//		fenci("C:\\Users\\Fire\\Desktop\\问句\\会不会23");
//		fenci("C:\\Users\\Fire\\Desktop\\问句\\有没有_10");
//		fenci("C:\\Users\\Fire\\Desktop\\问句\\多少18");
//		fenci("C:\\Users\\Fire\\Desktop\\问句\\怎样29");
		fenci("C:\\Users\\Fire\\Desktop\\问句\\吗123");
	}
	
	static void test1() {
		String ss = "藏红花能和大枣。雪域玛咖一起泡酒喝吗/糖尿病能吃藏红花吗/。";
		String[] sss = ss.split("[，。]");
		ArrayList<String> arr = new ArrayList<String>();
		for(String str : sss ) {
			t.debug("ii: "+str);
			Pattern p = Pattern.compile("(.*?(?:吗|呢|$))");
			Pattern p1 = Pattern.compile("((?:请问|问一下|不知道).*$)");
			Matcher m = p.matcher(str);
			while(m.find()) {
				String sent = m.group(1);
//				t.debug(sent);
				Matcher m1 = p1.matcher(sent);
				if(m1.find()) {
//						if(isQuestion(m.group(1))) {
					arr.add(m.group(1));
				} else {
//						if(isQuestion(sent))
					arr.add(sent);
				}
			}
		}
		for (String s : arr) {
			t.debug(s);
		}
	}
	
	static String getOrigQues(String anno_ques) {
		return "";
	}
	
	static void parseQuestion(String orig_ques, String anno_ques ) throws Exception {
		List<QT> qts = extractBody(anno_ques);
		List<Q> qs = new ArrayList<Q>();
		for(QT qt : qts ) {
			if(qt.interrogative.equals("什么")) {
				new QuestionParser();
				qt.entity = QuestionParser.structualRepresent(qt.question);
				Word[][] postags = Platform.segment("orig_ques");
				for(Word[] postag: postags) {
					for(Word w: postag) {
						if(w.pos.equals("v") 
								|| w.pos.equals("vn")
								|| w.pos.equals("vl")
								|| w.pos.equals("vi")
								|| w.pos.equals("n")
								|| w.pos.equals("nl")
								|| w.pos.equals("ns")
								|| w.pos.equals("nsf")
								|| w.pos.equals("nt")
								|| w.pos.equals("nz")
								|| w.pos.equals("a"))
							qt.keyword.add(w);
					}
				}
			}
//			Map<Integer, Word> map = new HashMap<Integer, Word>();
			Word[] words = new Word[qt.question.length()];
			boolean[] occupy = new boolean[qt.question.length()];
			
			/*
			 * 处理医学实体
			 */
			JSONObject o = qt.entity;
			if(o.has("disease")) {
				JSONArray a = o.getJSONArray("disease");
				for( int i=0; i< a.length(); i++) {
					JSONObject b = a.getJSONObject(i);
					String s = b.getString("d");
					int index = qt.question.indexOf(s);
					boolean isOccupied = false;
					for( int j = index; j<index+s.length(); j++) {
						occupy[j] = true;
					}
					words[index] = new Word(null, s, "D", null, null, null); 
//					map.put(index, );
				}
			}
			if(o.has("drug")) {
				JSONArray a = o.getJSONArray("drug");
				for( int i=0; i< a.length(); i++) {
					JSONObject b = a.getJSONObject(i);
					String s = b.getString("m");
					int index = qt.question.indexOf(s);
					for( int j = index; j<index+s.length(); j++) {
						occupy[j] = true;
					}
					words[index] = new Word(null, s, "M", null, null, null);
				}
			}
			if(o.has("check")) {
				JSONArray a = o.getJSONArray("check");
				for( int i=0; i< a.length(); i++) {
					JSONObject b = a.getJSONObject(i);
					String s = b.getString("c");
					int index = qt.question.indexOf(s);
					for( int j = index; j<index+s.length(); j++) {
						occupy[j] = true;
					}
					words[index] = new Word(null, s, "C", null, null, null);
				}
			}
			if(o.has("treat")) {
				JSONArray a = o.getJSONArray("treat");
				for( int i=0; i< a.length(); i++) {
					JSONObject b = a.getJSONObject(i);
					String s = b.getString("t");
					int index = qt.question.indexOf(s);
					for( int j = index; j<index+s.length(); j++) {
						occupy[j] = true;
					}
					words[index] = new Word(null, s, "T", null, null, null);
				}
			}
			if(o.has("symptom")) {
				JSONArray a = o.getJSONArray("symptom");
				for( int i=0; i< a.length(); i++) {
					JSONObject b = a.getJSONObject(i);
					String so = b.getString("o");
					String sos = b.getString("os");
					int index = qt.question.indexOf(so);
					int index1 = qt.question.indexOf(sos);
					for( int j = index; j<index1+sos.length(); j++) {
						occupy[j] = true;
					}
					words[index] = new Word(null, so+"//"+sos, "OOS", null, null, null);
				}
			}
			if(o.has("indicator")) {
				JSONArray a = o.getJSONArray("indicator");
				for( int i=0; i< a.length(); i++) {
					JSONObject b = a.getJSONObject(i);
					String si = b.getString("i");
					String sis = b.getString("is");
					int index = qt.question.indexOf(si);
					int index1 = qt.question.indexOf(sis);
					for( int j = index; j<index1+sis.length(); j++) {
						occupy[j] = true;
					}
					words[index] = new Word(null, si+"//"+sis, "IIS", null, null, null);
				}
			}
			if(o.has("remain")) {
				JSONObject oo = o.getJSONObject("remain");
				if(o.has("o")) {
					JSONArray a = o.getJSONArray("o");
					for( int i=0; i< a.length(); i++) {
						String b = a.getString(i);
						int index = qt.question.indexOf(b);
						for( int j = index; j<index+b.length(); j++) {
							occupy[j] = true;
						}
						words[index] = new Word(null, b, "O", null, null, null);
					}
				}
				if(o.has("os")) {
					JSONArray a = o.getJSONArray("os");
					for( int i=0; i< a.length(); i++) {
						String b = a.getString(i);
						int index = qt.question.indexOf(b);
						for( int j = index; j<index+b.length(); j++) {
							occupy[j] = true;
						}
						words[index] = new Word(null, b, "OS", null, null, null);
					}
				}
				if(o.has("s")) {
					JSONArray a = o.getJSONArray("s");
					for( int i=0; i< a.length(); i++) {
						String b = a.getString(i);
						int index = qt.question.indexOf(b);
						for( int j = index; j<index+b.length(); j++) {
							occupy[j] = true;
						}
						words[index] = new Word(null, b, "O", null, null, null);
					}
				}
				if(o.has("ss")) {
					JSONArray a = o.getJSONArray("ss");
					for( int i=0; i< a.length(); i++) {
						String b = a.getString(i);
						int index = qt.question.indexOf(b);
						for( int j = index; j<index+b.length(); j++) {
							occupy[j] = true;
						}
						words[index] = new Word(null, b, "OS", null, null, null);
					}
				}
				if(o.has("i")) {
					JSONArray a = o.getJSONArray("i");
					for( int i=0; i< a.length(); i++) {
						String b = a.getString(i);
						int index = qt.question.indexOf(b);
						for( int j = index; j<index+b.length(); j++) {
							occupy[j] = true;
						}
						words[index] = new Word(null, b, "I", null, null, null);
					}
				}
			}
			
			/*
			 * 处理动词名词
			 */
			for(Word w: qt.keyword) {
				int index = qt.question.indexOf(w.cont);
				boolean isOccupied = false;
				for( int j = index; j<index+w.cont.length(); j++) {
					if(occupy[j]) {
						isOccupied = true;
						break;
					}
				}
				if(!isOccupied) {
					for( int j = index; j<index+w.cont.length(); j++) {
						occupy[j] = true;
					}
					words[index] = new Word(null, w.cont, w.pos.substring(0, 1), null, null, null);
				}
			}
			
			
			/*
			 * 处理疑问词
			 */
			String[] temp = qt.interrogative.split("//");
			String inter = temp[0];
			String inter_type = temp[1];
			int ii = qt.question.indexOf(inter);
			for( int j = ii; j<ii+inter.length(); j++) {
				occupy[j] = true;
			}
			words[ii] = new Word(null, inter, inter_type, null, null, null);
			
			/*
			 * 探测能够已知的问题类型
			 */
			
			// -------------------> add 为什么
			
			
			/*
			 * 实体从整个问题中抽取，词性关键词和预定义关键词只在疑问句中抽取。
			 */
			if(inter_type.equals("shenme")) {
				/*
				 * 包含预定义关键词
				 */
//				String disease = findDiseaseBefore(words, ii);  
//				if(contains(words, ii, "症状，表现")) {
//					qs.add(new Q(7, disease, "症状"));
//				}
//				if(contains(words, ii, "病因，原因")) {
//					qs.add(new Q(8, disease, "病因"));
//				}
//				if(contains(words, ii, "方法，办法") || containsBefore(words, ii, "用，使用，注意")) {
//					qs.add(new Q(10, disease, "治疗"));
//				}
//				if(contains(words, ii, "药，药物，水果，菜，食物") || contains(words, ii, "吃，服用，喝")) {
//					qs.add(new Q(11, disease, "用药"));
//				}
//				if(contains(words, ii, "科室，科")) {
//					qs.add(new Q(12, disease, "科室"));
//				}
//				if(contains(words, ii, "危害，害")) {
//					qs.add(new Q(13, disease, "危害"));
//				} 
//				if(contains(words, ii, "检查")) {
//					qs.add(new Q(14, disease, "检查"));
//				}
//				if(contains(words, ii, "并发症，后遗症")) {
//					qs.add(new Q(15, disease, "并发症"));
//				}
//			
//				String drug = findDrugBefore(words, ii);
//				if(contains(words, ii, "剂量，量")) {
//					qs.add(new Q(3, drug, "剂量"));
//				}
//				if(containsAfter(words, ii, "用处，功效，用，作用，药效，效果")) {
//					qs.add(new Q(4, drug, "功效"));
//				}
//				if(contains(words, ii, "副作用，坏处，危害，害处")) {
//					qs.add(new Q(5, drug, "副作用"));
//				}
//				
//				String check = findCheckBefore(words, ii);
//				qs.add(new Q(17, check, "是什么"));
//				
//				String treat = findTreat(words, ii);
//				qs.add(new Q(20, treat, "是什么");
//				
//				//include symptom, include indicator
//				String symptom  = findSymptomAndIndictor (words, ii);
//				if(containsAfter(words, ii, "病，疾病，毛病")) {
//					qs.add(new Q(26, symptom, "可能疾病"));
//				}
//				if(contains(words, ii, "病因，原因")) {
//					qs.add(new Q(8, symptom, "病因"));
//				}
//				if(contains(words, ii, "方法，办法") || containsBefore(words, ii, "用，使用，注意")) {
//					qs.add(new Q(10, symptom, "治疗"));
//				}
//				if(contains(words, ii, "药，药物，水果，菜，食物") || contains(words, ii, "吃，服用，喝")) {
//					qs.add(new Q(11, symptom, "用药"));
//				}
//				if(contains(words, ii, "检查")) {
//					qs.add(new Q(14, symptom, "检查"));
//				}
				
			} else if(inter_type.equals("zenme") || inter_type.equals("zenyang")||inter_type.equals("ruhe")) {
				
			} else if(inter_type.equals("na")) {
				
			} else if(inter_type.equals("duoshao")) {
				
			} else if(inter_type.equals("shibushi")) {
				/*
				 * 包含的词性关键词
				 */
			} else if(inter_type.equals("huibuhui")) {
				
			} else if(inter_type.equals("nengbuneng")) {
				
			} else if(inter_type.equals("youmeiyou")) {
				
			} else if(inter_type.equals("end")) {
				
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		test1();
		
		BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream("D:\\input_a_ansi.txt"), "GBK"));
		String line = null;
		int count = 1;
		String f = "D:\\answer\\dbanswer.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(f+".1"));
		while( (line=br.readLine()) != null ) {
			if((count++) % 10000 ==0 ) {
				bw.close();
				bw = new BufferedWriter(new FileWriter(f+"."+count/10000));
			}
			bw.write(line);
			bw.newLine();
		}
		bw.close();
//		extractBody("")

	}

}
