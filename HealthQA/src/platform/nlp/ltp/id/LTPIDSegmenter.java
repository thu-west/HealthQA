package platform.nlp.ltp.id;

import platform.nlp.Segmenter;
import platform.nlp.ao.Word;

public class LTPIDSegmenter implements Segmenter {
	
	public static final int NER = 1;
	public static final int DEPENDENCY = 2;
	
	int function;
	
	public LTPIDSegmenter( int function ){  // ner    denpendency
		this.function = function;
		switch(function) {
		case NER :
			LTPID.init("ner");
			break;
		case DEPENDENCY :
			LTPID.init("dependency");
			break;
		default :
			return;
		}
	}
	
	Word parse(String word_str, int word_id) {
		String[] e = word_str.split("<>");
		switch(function) {
		case NER :
			return new Word( word_id, e[0], e[1], e[2], null, null);
		case DEPENDENCY :
			return new Word( word_id, e[0], e[1], e[2], Integer.valueOf(e[3]), e[4]);
		default :
			return null;
		}
	}
	
	@Override
	public Word[][] segParagraph(String aParagraph) {
		if( aParagraph.isEmpty() )
			return null;
		aParagraph += "。";
		aParagraph = aParagraph.replaceAll("[\n\r?;!？；！。]+", "。");
		
		String[] sent_array = LTPID.seg(aParagraph);
		Word[][] result = new Word[sent_array.length][];
		
		int i=-1;
		for( String sent : sent_array ){
			if( sent.isEmpty() )
				continue;
			String[] word_array = sent.split("\t");
			result[++i] = new Word[word_array.length];
			int j=-1;
			for( String w : word_array ){
				if(w.isEmpty())
					continue;
				result[i][++j] = parse(w, j);
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		try{
			LTPIDSegmenter seger = new LTPIDSegmenter( LTPIDSegmenter.DEPENDENCY );
			Word[][] word = seger.segParagraph("{全文:性别： 男。年龄： 45。病情描述: 。一星期前，吃的不合适。出现头晕恶心呕吐。曾经的治疗及用药情况: 。做了血常规检查，尿常规检查都没找出原因，打了止吐针。发病时间： 不清楚。}。");
			System.out.println(Word.toFullString(word));
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
			LTPID.close();
		}
		
	}

}
