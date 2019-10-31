package platform.util.general;

import java.util.regex.Pattern;

import platform.nlp.ao.Word;

public class StringUtil {
	
	public final static String comma = "，。；“”？！……,;\"?!";
	public final static String NUMBER_REGEX = "^[\\-+]?\\d+(\\.\\d+)?$";
	public final static String TIME_REGEX = "^(\\d+年)|(\\d+月)|(\\d+个月)|(\\d+星期)|(\\d+个星期)|(\\d+周)|(\\d+天)|(\\d+小时)|(\\d+个小时)|(\\d+分钟)$";
	public final static String AGE_REGEX = "^(\\d+岁)|(年龄\\d)$";
	public final static Pattern NUMBER_MATCHER = Pattern.compile(NUMBER_REGEX);
	

	
	public static Word[] reverseWord( Word[] word ) {
		Word[] t = new Word[word.length];
		for( int i=0; i<word.length; i++ ) {
			t[i] = word[word.length-1-i];
		}
		return t;
	}

	public static void main(String[] args) {
//		System.out.println(Pattern.matches(NUMBER_REGEX, "+11043.12"));
		System.out.println(Pattern.matches(TIME_REGEX, "10小时"));
		System.out.println(Pattern.matches(AGE_REGEX, "10岁"));
//		System.out.println("aa".substring(0, 15));
	}
}
