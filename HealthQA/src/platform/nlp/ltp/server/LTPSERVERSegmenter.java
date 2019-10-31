package platform.nlp.ltp.server;
import platform.nlp.Segmenter;
import platform.nlp.ao.Word;
import platform.nlp.ltp.LTPSettings;
import platform.nlp.ltp.server.xml.Xml2Bean;
import platform.nlp.ltp.xml.bean.LTPParagraph;
import platform.nlp.ltp.xml.bean.LTPSentence;
import platform.nlp.ltp.xml.bean.LTPWord;
import platform.util.log.Trace;
import platform.util.net.HttpRequest;



public class LTPSERVERSegmenter implements Segmenter {
	// 分词/ws, 词性标注/pos, 命名实体识别/ner, 依存句法分析/dp, 语义角色标注/srl, 全部任务/all
	public static final String SEGWORD = "ws";
	public static final String POSTAG = "pos";
	public static final String NER = "ner";
	public static final String DEPENDENCY = "dp";
	
	static String server = LTPSettings.ltp_server;
	
	static float time;
	static float segtime;
	
	static Trace trace = new Trace().setValid(false, false);
	
//	public LTPDocument documentParse(String document) {
//		String param = "s="+document+"&x=n&t=all";
//		String s1 = HttpRequest.sendPost(server, param);
//		return Xml2Bean.xml2Bean( s1.replace("/n", "\n").replaceAll("^[\n \r]+","") ).getDoc();
//	}
//	
//	public LTPParagraph  paragraphParse( String paragraph ) {
//		paragraph.replaceAll("[\n\r]+", "。");
//		String param = "s="+paragraph+"&x=n&t=all";
//		String s1 = HttpRequest.sendPost(server, param);
//		return Xml2Bean.xml2Bean( s1.replace("/n", "\n").replaceAll("^[\n \r]+","") ).getDoc().paragraph(0);
//	}
//	
//	public String wordSegmentation( String text, boolean if_seg_paragraph ) {
//		if ( if_seg_paragraph ){
//			return LTPUtil.getWordSegmentationFromDocument( documentParse(text) );
//		} else {
//			return LTPUtil.getWordSegmentationFromParagraph( paragraphParse(text) );
//		}
//	}
	
	// 提供主方法，测试发送GET请求和POST请求
//	public static void main(String args[]) throws MalformedURLException {
//		LTPSegmenter parser = new LTPSegmenter();
//		String target = "病情。二型糖尿病的饮食控制没有一型糖尿病严格，但也是很重要的，。意见。平时需要注意戒烟酒，少吃油腻食品，淀粉类主食每天不超过6两，如果有饥饿的情况可以用白蛋白类食品或者蔬菜代替，水果中的糖多为果糖，可以少量食用，还注意需要";
//		Word[][] para = parser.segParagraph( target );
//		System.out.println( Word.toString( para ));
//	}
	
	String function;
	
	public LTPSERVERSegmenter( String FUNCTION ) {
		function = FUNCTION;
	}
	
	public Word[][] seg( String aParagraph ) throws Exception {
		aParagraph.replaceAll("[\n\r]+", "。");
		String param = "s="+aParagraph+"&x=n&t="+function;
		long stime = System.currentTimeMillis();
		String s1 = HttpRequest.sendPost(server, param);
		long etime = System.currentTimeMillis();
		LTPParagraph ltp_para = Xml2Bean.xml2Bean( s1 ).getDoc().paragraph(0);
		Word[][] para = new Word[ltp_para.size()][];
		int i=-1;
		for( LTPSentence sent : ltp_para.getSent() ){
			para[++i] = new Word[sent.size()];
			int j=-1;
			for( LTPWord word : sent.getWord() ) {
				para[i][++j] = new Word( word.getId(), word.getCont(), word.getPos(), word.getNe(), word.getParent(), word.getRelate() );
			}
		}
		time = time + (System.currentTimeMillis()-etime)/(float)1000.0;
		segtime += (System.currentTimeMillis()-stime)/(float)1000;
		for( Word[] ws : para ){
			for(Word w:ws){
				if( w.pos==null){
					throw new Exception("Failed to get the postagger information");
				}
			}
		}
		return para;
	}
	
	String breakParagraph( String para ){
		String[] ps = para.split("。");
		int maxSentLen = 0, maxSentIndex = 0;
		int i=0, j=0;
		for(i=0; i<ps.length; i++ ){
			if(ps[i].length()>maxSentLen){
				maxSentLen = ps[i].length();
				maxSentIndex = i;
			}
		}
		int break_low_bound = maxSentLen/2-10;
		StringBuffer pb = new StringBuffer(ps[maxSentIndex]);
		boolean flag = false;
		for( j=0; j<maxSentLen; j++){
			if( pb.charAt(j)=='，' || pb.charAt(j)==',' && j>=break_low_bound ){
				pb.setCharAt(j, '。');
				flag = true;
				break;
			}
		}
		if( !flag ){
			flag = false;
			for( j=0; j<maxSentLen; j++){
				if( pb.charAt(j)=='，' || pb.charAt(j)==',' || pb.charAt(j)=='：' || pb.charAt(j)==':' && j>=0 ){
					pb.setCharAt(j, '。');
					flag = true;
					break;
				}
			}
			if(!flag) {
				pb.insert(break_low_bound+10, '。');
			}
		}
		ps[maxSentIndex] = pb.toString();
		String temp = "";
		for(String s : ps ){
			temp += s + "。";
		}
		return temp;
	}
	
	@Override
	public Word[][] segParagraph(String aParagraph) {
		aParagraph += "。";
		aParagraph = aParagraph.replaceAll("[\n\r。？！!?；;]+", "。");
		for( int i=0; i<100; i++ ){
			try{
				Word[][] temp = seg(aParagraph);
				if( i>0 ){
					trace.remind("piece too long, break "+i+" times! As following: ");
					trace.remind(aParagraph);
				}
				return temp;
			}catch (Exception e ){
//				e.printStackTrace();
				trace.debug("piece too long, to be broken, break times: "+(i+1));
				aParagraph = breakParagraph(aParagraph);
			}
		}
		return null;
	}
	
	public String toFormatString(Word[][] para) {
		String paras = "";
		for( Word[] sent : para ){
			String sents = "";
			for( Word word : sent ){
				sents += word.cont+"<>"+word.pos+"<>"+word.ne+"\t";
			}
			paras += sents + "\n";
		}
		return paras;
	}
	
	public static void main(String[] args) {
		LTPSERVERSegmenter seg = new LTPSERVERSegmenter(LTPSERVERSegmenter.NER);
		String aParagraph = "健康咨询描述。议你采用传统中药山茱萸、玉竹、生地、知母、荔枝核、青果、石斛、肉苁蓉、玄参、益智仁、沙菀子、补骨脂、鹿茸、海南陈、人参、枸杞子、蜂胶、金精粉、乌术粉、茯苓、怀山药、桑葚、苦瓜等配合治疗，见效快，疗效确切。 这些药物安全无毒副作用，适合长期服用，无需节食，无需忌口，无需服用其它降糖药物，单独服用即可，可以有效保卫胰岛，降低血糖，提高免疫。 麻烦问下这个怎么用药 每次用量多少 是单独服用还是磨面一起服用啊。谢谢。曾经的治疗情况和效果。刚刚检查出事糖尿病4个加号。想得到怎样的帮助。想知道您的药怎么服用（感谢医生为我快速解答——该如何治疗和预防。）。附件。点击查看大图。";
//		Word[][] w = seg.segParagraph(a);
//		System.out.println( Word.toSimpleString(w));
//		String aParagraph = "糖尿病如果正确治疗的话饮食上不必忌口，中医中药长期临床实践积累了许多非常有效的治疗方法，建议你采用传统中药山茱萸、玉竹、生地、知母、荔枝核、青果、石斛、肉苁蓉、玄参、益智仁、沙菀子、补骨脂、鹿茸、海南陈、人参、枸杞子、蜂胶、金精粉、乌术粉、茯苓、怀山药、桑葚、苦瓜等配合治疗。见效快，疗效确切。这些药物安全无毒副作用，适合长期服用，无需节食，无需忌口，无需服用其它降糖药物，单独服用即可，可以有效保卫胰岛，降低血糖，提高免疫。这些药物克服了传统产品治疗上严格忌口的弊端，使病人的营养能得到有效的吸收和利用，从而提高人体的自身免疫功能和抗病防病能力，防止了系列并发症的发生，真正做到了综合治疗。这些药物配合使用可以修复胰腺功能，调节自身代谢平衡，消除、缓解各种并发症，具有阻断遗传和杜绝复发的可能。希望你正确治疗，早日康复。这个单独服用即可，是指这20多种药可单独服用吗。这20多种药每天都是吃吗。还是选择性吃。怎样吃能具体指导下吗。有合成一起的方子吗。发病时间。不清楚。";
//		aParagraph = aParagraph.replaceAll("[\n\r。？！!?；;]+", "。");
//		System.out.println(aParagraph.split("。").length);
//		for( String t:aParagraph.split("。"))
//			System.out.println(t);
//		for( int i=0; i<2; i++ ){
//			aParagraph = seg.breakParagraph(aParagraph);
//		}
//		System.out.println(aParagraph.split("。").length);
//		for( String t:aParagraph.split("。"))
//			System.out.println(t);
		
		
	}
}
