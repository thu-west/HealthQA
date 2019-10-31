package platform.util.general;

public class JavaRegex {
	public static String chinese_punctuation = "\u3002\uff1b\uff0c\uff1a\u201c\u201d\uff08\uff09\u3001\uff1f\u300a\u300b";
	public static String punctuation = ":.;,\"()?/<>\u3002\uff1b\uff0c\uff1a\u201c\u201d\uff08\uff09\u3001\uff1f\u300a\u300b";
	public static String english_punctuation = ":.;,\"()?/<>\\-";
	/*                                           ： 。 ；  ， ： “ ”（ ） 、 ？ 《 》   */
	public static String chinese_character = "\u4e00-\u9fa5";
	
	public static String email = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
	public static String url_http = "http[s]?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
	public static String url_www = "www\\.([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
	
	public static void main(String[] args) {
//		String a = "斑9.33，2,5小时9,202236我";
//		StringBuffer a_buf = new StringBuffer(a);
//		Pattern p = Pattern.compile("[^.0-9][0-9]+([,，、])[0-9]+(?!(.[0-9]+))");
//		Matcher m = p.matcher(a);
//		System.out.println(m.groupCount());
//		while( m.find() ){
//			System.out.println(m.group());
//			a_buf.setCharAt(m.start(1), '.');
//		}
//		System.out.println(a_buf.toString());
		String str = "https://baidu.com/nihjao/hha?a=1&b=3.2";
		str = str.replaceAll(JavaRegex.url_http, "");
		System.out.println(str);
	}
	
	
	
}
