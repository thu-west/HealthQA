package platform.nlp.ltp.jni;

import platform.nlp.Segmenter;
import platform.nlp.ao.Word;
import platform.nlp.ltp.LTPSettings;
import platform.util.log.Trace;

public class LTPJNISegmenter implements Segmenter {
	
	static Trace trace = new Trace().setValid(false,false);
	
	public static final int NER = 1;
	public static final int DEPENDENCY = 2;
	static LTPJNI ltp; 
	static{
		if( LTPSettings.segmentor == LTPSettings.SEGMENTORTYPE.LTP_JNI ) {
			ltp = new LTPJNI();
			if(LTPSettings.LTPJNI_Function == LTPJNISegmenter.NER)
				ltp.initNER();
			else
				ltp.initDependency();
		}
		
	}
	
	int function;
	
	public LTPJNISegmenter( int FUNCTION ) {
		if( FUNCTION>2 || FUNCTION < 1 ){
			System.out.println("choose invalid function");
			return;
		}
		function = FUNCTION;
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
		long stime = System.currentTimeMillis();
		if( aParagraph.isEmpty() )
			return null;
		aParagraph += "。";
		aParagraph = aParagraph.replaceAll("[\n\r?;!？；！。]+", "。");
		String[] sent_array = null;
		switch(function) {
		case NER :
			sent_array = ltp.segNer(aParagraph).split("\n");
			break;
		case DEPENDENCY :
			sent_array = ltp.segDependency(aParagraph).split("\n");
			break;
		default :
			return null;
		}
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
		long etime = System.currentTimeMillis();
		trace.debug("seg["+function+"] cost: "+ (etime-stime)/1000.0+" s");
		return result;
	}
	
	
	public static void main(String[] args) {
		LTPJNISegmenter seg = new LTPJNISegmenter(LTPJNISegmenter.NER);
		String aParagraph = "龑做累龙一哈燊";
		Word[][] w = seg.segParagraph(aParagraph);
		System.out.println(Word.toSimpleString(w));
	}
}
