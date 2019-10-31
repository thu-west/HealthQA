package platform.nlp.ltp.jni;

import platform.nlp.ltp.LTPSettings;
import platform.util.log.Trace;

public class LTPJNI {

	static Trace trace = new Trace().setValid(false, false);
	
	int segmentor_ptr;
	int postagger_ptr;
	int ner_ptr;
	int parser_ptr;
	
	String cws_model = LTPSettings.cws_model;
	String pos_model = LTPSettings.pos_model;
	String ner_model = LTPSettings.ner_model;
	String parser_model = LTPSettings.parser_model;
	String lexicon = LTPSettings.lexicon;

	static {
		System.loadLibrary("LTPJNI");
	}

	public LTPJNI() {
		segmentor_ptr = 0;
		postagger_ptr = 0;
		ner_ptr = 0;
		parser_ptr = 0;
	}
	
	public void set(int seg, int pos, int ner, int parser) {
		segmentor_ptr = seg;
		postagger_ptr = pos;
		ner_ptr = ner;
		parser_ptr = parser;
	}

	public boolean initNER() {
		String ptr_s = this.nCreateSegmentor(cws_model, pos_model,
				ner_model, null, lexicon);
		trace.debug(ptr_s);
		if( ptr_s == null ){
			return false;
		}else{
			String[] ptr_a = ptr_s.split("\\|");
			set( Integer.valueOf(ptr_a[0], 16),
					Integer.valueOf(ptr_a[1], 16),
					Integer.valueOf(ptr_a[2], 16),
					Integer.valueOf(ptr_a[3], 16));
			return true;
		}
	}
	
	public boolean initDependency() {
		String ptr_s = this.nCreateSegmentor(cws_model, pos_model,
				ner_model, parser_model, lexicon);
		trace.debug(ptr_s);
		if( ptr_s == null ){
			return false;
		}else{
			String[] ptr_a = ptr_s.split("\\|");
			set( Integer.valueOf(ptr_a[0], 16),
					Integer.valueOf(ptr_a[1], 16),
					Integer.valueOf(ptr_a[2], 16),
					Integer.valueOf(ptr_a[3], 16));
			return true;
		}
	}
	
	public boolean destroy() {
		if( 0 == nReleaseSegmentor(segmentor_ptr, postagger_ptr,
			ner_ptr, parser_ptr))
			return true;
		else
			return false;
	}
	
	public String segNer(String piece){
		long st = System.currentTimeMillis();
		String a = this.nNer(segmentor_ptr, postagger_ptr, ner_ptr, parser_ptr, piece);
		long et = System.currentTimeMillis();
		trace.debug("parse Ner: "+((et-st)/1000.0)+"s");
		return a;
	}
	
	public String segDependency(String piece){
		long st = System.currentTimeMillis();
		String a = this.nDependency(segmentor_ptr, postagger_ptr, ner_ptr, parser_ptr, piece);
		long et = System.currentTimeMillis();
		trace.debug("parse Dependency: "+((et-st)/1000.0)+"s");
		return a;
	}
	
	public native String nCreateSegmentor(String cws_model, String pos_model,
			String ner_model, String parser_model, String lexicon);

	public native int nReleaseSegmentor(int segmentor_ptr, int postagger_ptr,
			int ner_ptr, int parser_ptr);

	
	public native String nNer(int segmentor_ptr, int postagger_ptr,
			int ner_ptr, int parser_ptr, String piece);
	
	public native String nDependency(int segmentor_ptr, int postagger_ptr,
			int ner_ptr, int parser_ptr, String piece);
	
	public static void main(String[] args) {
		LTPJNI ltp = new LTPJNI();
		ltp.initNER();
		System.out.println( ltp.segNer("你好，糖尿病诊断标准是任何时刻血糖大于11.1或者是空腹血糖大于6.9以上就可以诊断。格列美脲是属于酰脲类抗糖尿病药，具有抑制肝葡萄糖合成、促进肌肉组织对外周葡萄糖的摄取及促进胰岛素分泌的作用。") );
		ltp.destroy();
	}
}
