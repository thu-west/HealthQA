package other.doubt;

import java.io.*;

import platform.Platform;
import platform.nlp.ao.Word;

public class Question {
	
	static String[] END = new String[] {"吗", "呢"};
	static String[] BEGIN = new String[] {"请问", "不知道", "问一下"};     // 不作为判断问句的标准
	static String[] BOOL = new String[] {"能不能", "算不算", "是不是", "会不会", "有没有", "是否", "可否", "能否"};
	static String[] SCALE = new String[] {"多高", "多大", "多少", "多长时间", "多久"};
	static String[] METHOD = new String[] {"怎么", "怎么办", "怎么样", "怎样", "如何"};
	static String[] REASON = new String[] {"怎么回事", "怎么会", "咋", "咋回事", "为什么", "为何", "什么回事", "什么原因", "因为什么"};
	static String[] COMMON = new String[] {"什么", "何", "啥", "哪", "哪些", "哪里", "哪种", "哪个", "哪方面"};
	
	static boolean isQuestion( String str )  {
		
		for( String s : END ) {
			if( str.contains(s) )
				return true;
		}
		
		for( String s : BOOL ) {
			if( str.contains(s) )
				return true;
		}
		
		for( String s : SCALE ) {
			if( str.contains(s) && !str.contains("没有"+s) && !str.contains("没"+s) && !str.contains("不"+s) )
				return true;
		}
		
		for( String s : METHOD ) {
			if( str.contains(s) && !str.contains("没有"+s) && !str.contains("没"+s) && !str.contains("不"+s) )
				return true;
		}
		
		for( String s : REASON ) {
			if( str.contains(s) && !str.contains("没有"+s) && !str.contains("没"+s) && !str.contains("不"+s) )
				return true;
		}
		
		for( String s : COMMON ) {
			if( str.contains(s) && !str.contains("没有"+s) && !str.contains("没"+s) && !str.contains("不"+s) )
				return true;
		}
		
		return false;
	}
	
	static String formatQuestion(String str) {
		
		// end boundary
		int end_pos = str.length()-1, temp =0;
		for( String estr: END ) {
			temp = str.indexOf(estr);
			end_pos = temp>-1?temp:end_pos;
		}
		// begin boundary
		int begin_pos = 0;
		for( String bstr : BEGIN) {
			temp = str.indexOf(bstr);
			begin_pos = temp>-1?temp:begin_pos;
		}
		// effective part
		if( end_pos < begin_pos ) {
			return "--";
		}
		str = str.substring(begin_pos, end_pos+1);
		
		// deal with REASON
		for( String s : REASON ) {
//			if( str.contains(s) )
//				return "为什么";
		}
		
		// deal with METHOD
		for( String s : METHOD ) {
//			if( str.contains(s) )
//				return "怎么做";
		}
		
		// deal with
		return str;
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
	
	static void extractQuestion() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Fire\\Desktop\\NER\\question\\dbquestion.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\Fire\\Desktop\\NER\\question\\dbquestion-question.txt"));
		String line = "";
		int count = 1000;
		while( (line=br.readLine()) != null ) {
			if( (count--) < 0 )
				break;
			String[] tokens = line.split("[，。：]+");
			
			for ( String token : tokens ) {
				if( isQuestion(token) ) {
					bw.write(formatQuestion(token));
					bw.write("    ///    ");
					bw.write(token);
					bw.newLine();
				}
			}
		}
		br.close();
		bw.close();
	}
	
	public static void main(String[] args) throws Exception {
		
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

}
