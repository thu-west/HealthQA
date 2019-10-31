package platform.nlp.nlpir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import platform.nlp.Segmenter;
import platform.nlp.ao.Word;
import platform.nlp.ltp.LTPSettings;
import platform.util.log.Trace;

public class NLPIRSegmenter implements Segmenter{

	
	static String system_charset = "UTF-8";
	static int charset_type = 1;
	static boolean init_flag = true;
	
	static Trace t = new Trace().setValid(true, true);
	
	static {
		open();
	}
	
	static void open() {
		t.debug(NLPIRSettings.NLPIR_ROOT);
		int init = CLibrary.Instance.NLPIR_Init(NLPIRSettings.NLPIR_ROOT, charset_type, "0");
		if (0 == init) {
			t.error("NLPIRSegmenter message: Failed to initialize");
			init_flag = false;
		}
		if ( LTPSettings.whether_use_user_dict ) {
			t.remind("NLPIRSegmenter message: Loading user dictionary "+LTPSettings.user_dict);
			try {
				BufferedReader br = new BufferedReader( new FileReader( LTPSettings.user_dict ));
				String line = null;
				while( (line=br.readLine()) !=null ){
					CLibrary.Instance.NLPIR_AddUserWord(line);
				}
				br.close();
			} catch (IOException e) { e.printStackTrace(); }
			t.debug("Done");
//			CLibrary.Instance.NLPIR_ImportUserDict(Config.user_dict);
		}
	}
	
	public static void close() {
		CLibrary.Instance.NLPIR_Exit();
	}
	
	Word[][] parseFromString( String result ) throws Exception {
//		result = result.replaceAll(" [^ ]+/w[a-z] $", "");
//		String[] rs = result.split("(( [^ ]+?wt)|( [^ ]+?ww)|( [^ ]+?wj)|( [^ ]+?wf))+");
//		Word[][] para = new Word[rs.length][];
		
		String[] rs = new String[]{result};
		Word[][] para = new Word[1][];
		
		for ( int i=0; i<rs.length; i++ ) {
			rs[i] = rs[i].replaceAll("^ ", "").replaceAll(" $", "");
			String[] ws = rs[i].split("[ ]+");
			para[i] = new Word[ws.length];
			for( int j=0; j<ws.length; j++ ) {
				int p = ws[j].lastIndexOf("/");
				String[] wp = null;
				try{
					wp = new String[] { ws[j].substring(0, p), ws[j].substring(p+1) };
				}catch(Exception e) {
					throw new Exception("Error when parseFromString: "+result);
				}
//				if(ws[j].equals("//w"))
//					wp = new String[] {"/", "w"};
//				else
//					wp = ws[j].split("/");
				try{
					para[i][j] = new Word(j, wp[0], wp[1], null, null, null);
				}catch(Exception e ){
					para[i][j] = new Word(j, "null", "null", null, null, null);
					t.error("Exception: special character in \""+result+"\"");
					e.printStackTrace();
				}
			}
		}
		return para;
	}
	
	@Override
	public Word[][] segParagraph(String aParagraph) throws Exception {
		
		if( !init_flag ) {
			t.error("Failed to load NLPIR library.");
			return null;
		}
//		String result = CLibrary.Instance.NLPIR_ParagraphProcess(aParagraph.replace("/", "").replace("　", ""), 1);
//		System.out.println(aParagraph.replace("　", ""));
		String result = CLibrary.Instance.NLPIR_ParagraphProcess(aParagraph.replace("　", ""), 1);
//		System.out.println("-> "+result);
		result.replace("　", "");
//		System.out.println(result);
		Word[][] para = parseFromString(result);
		return para;
	}
	
	public static void main(String[] args) throws Exception {
		NLPIRSegmenter seg = new NLPIRSegmenter();
		long time = System.currentTimeMillis();
		Word[][] a = seg.segParagraph("白矾泡脚C5mol/l功能/不全一扫光药膏毛孔的作用，可减少汗液的分泌。每天临睡自动化配血试剂的水中加入明矾3-6克，待明矾溶化在水中以后，泡脚10-15分钟，坚持每晚泡一次，5-6天以后就可以缓解汗脚的症状。　　此方法并不适合所有人，需要谨慎采用，老人和小孩慎用。");
		long time1 = System.currentTimeMillis();
//		System.out.println(time1-time);
//		System.out.println(Word.toSimpleString(a));
		a = seg.segParagraph("白矾泡脚C5功能不全一扫光药膏毛孔的作用，可减少汗液的分泌。龟甲养阴血试剂的水中加入明矾3-6克，待明矾溶化在水中以后，泡脚10-15分钟，坚持每晚泡一次，5-6天以后就可以缓解汗脚的症状。　　此方法并不适合所有人，需要谨慎采用，老人和小孩慎用。");
		long time2 = System.currentTimeMillis();
//		System.out.println(time2-time1);
//		System.out.println(Word.toSimpleString(a));
	}

}
