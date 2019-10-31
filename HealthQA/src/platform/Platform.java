package platform;

import java.io.FileNotFoundException;
import java.util.*;
import platform.nlp.Segmenter;
import platform.nlp.ao.Word;
import platform.nlp.ltp.LTPSettings;
import platform.nlp.ltp.id.LTPIDSegmenter;
import platform.nlp.ltp.jni.LTPJNISegmenter;
import platform.nlp.ltp.server.LTPSERVERSegmenter;
import platform.nlp.nlpir.NLPIRSegmenter;
import platform.nlp.special.OfflineSegmenter;

public class Platform {

	static Segmenter seger;
	static {
		switch (LTPSettings.segmentor) {
		case LTP_JNI:
			seger = new LTPJNISegmenter(LTPSettings.LTPJNI_Function);
			break;
		case LTP_SERVER:
			seger = new LTPSERVERSegmenter(LTPSettings.LTPSERVER_Function);
			break;
		case NLPIR:
			seger = new NLPIRSegmenter();
			break;
		case LTP_ID:
			seger = new LTPIDSegmenter(LTPSettings.LTPID_Function);
		default:
			break;
		}
	}
	
	public static String getProjectRoot() throws Exception {
		String s = System.getProperty("user.dir");
		return s.replace("\\", "/");
	}

	/*
	 * 根据要查询的问题，寻找最相似的问题，可以自定义返回的相似问题的个数（platform.Config.maxSearchResultCount）
	 */
//	public static List<Question> findBasicReleventQuestions(
//			String target_question, int maxSearchResultCount, double thres)
//			throws Exception {
//		return QuestionSearcher.searchQuestion(target_question,
//				maxSearchResultCount, thres);
//	}

	/*
	 * 根据问题列表找到所有这些问题的所有答案。
	 */
//	public static Answer[] findAllAnswersForQuestions(List<Question> questions) {
//		return 
//	}

	/*
	 * 给定一段文本，返回文本分词的结果，分词结果的表示方法参照《/doc/使用说明.docx》
	 */
	public static Word[][] segment(String text) throws Exception {
		Word[][] temp = seger.segParagraph(text);
		return temp;
	}

	public static Word[][] offlineSegmentQuestion(String question_id) {
		return OfflineSegmenter.segmentQuestion(question_id);
	}

	public static Word[][] offlineSegmentAnswer(String answer_id) {
		return OfflineSegmenter.segmentAnswer(answer_id);
	}

	public static Set<String> findNounVerb(String s) throws Exception {
		Set<String> a = new TreeSet<String>();
		Word[][] para = Platform.segment(s);
		for (Word[] sent : para) {
			for (Word word : sent) {
				if (word.pos.charAt(0) == 'n' || word.pos.charAt(0) == 'v')
					a.add(word.cont);
			}
		}
		return a;
	}

	public static Set<String> findNoun(String s) throws Exception {
		Set<String> a = new TreeSet<String>();
		Word[][] para = Platform.segment(s);
		for (Word[] sent : para) {
			for (Word word : sent) {
				if (word.pos.charAt(0) == 'n')
					a.add(word.cont);
			}
		}
		return a;
	}

	public static Set<String> findVerb(String s) throws Exception {
		Set<String> a = new TreeSet<String>();
		Word[][] para = Platform.segment(s);
		for (Word[] sent : para) {
			for (Word word : sent) {
				if (word.pos.charAt(0) == 'v')
					a.add(word.cont);
			}
		}
		return a;
	}

	public static void main(String[] args) throws FileNotFoundException {
		try {
//			System.out.println(getOS());
			//Word[][] w = segment("二是到医院皮肤科诊治一下。");
			Word[][] w = segment("糖尿病是各种致病因子作用于机体导致胰岛功能减退、胰岛素抵抗等而引发的，可以用几丁聚糖，注射胰岛素等方式进行治疗，注意饮食控制，无糖饮食，少食多餐，定期监测空腹及餐后血糖。");
			System.out.println(Word.toSimpleString(w[0]));
			// int len = w[0].length + 1;
			// int [][] graph = new int [len][len];
			// for(int i = 0;i < len;i++)
			// {
			// for(int j = 0;j < len;j++)
			// graph[i][j] = -1;
			// }
			// System.out.println("Done!");
			// graph[0][0] = 0;
			// for(int i = 1;i < len;i++)
			// {
			// graph[i][i] = 0;
			// if (w[0][i-1].parent == null){
			// System.out.println(w[0][-1].cont + "." + w[0][i-1].pos + "." +
			// w[0][i-1].ne);
			// }
			// else{
			// System.out.println(w[0][i-1].cont + "." + w[0][i-1].pos + "." +
			// w[0][i-1].ne + "." + w[0][i-1].parent + "." + w[0][i-1].relate);
			// int p = w[0][i-1].parent;
			// graph[i][p] = 1;
			// graph[p][i] = 1;//保证是无向图
			// System.out.println("couple: " + i + " " + p);
			// }
			// }
			// for(int i = 1;i < len;i++)
			// {
			// System.out.print(w[0][i-1].cont + " " + w[0][i-1].parent + ": ");
			// for(int j = 0;j < len;j++)
			// System.out.print(graph[i][j]+" ");
			// System.out.println();
			// }
			// System.out.println("Done~~");
			// int a[] = sp.dijkstra(graph,9,11);
			// System.out.println("路径长：" + a[0]);
			// System.out.println("内部所有节点：");
			// System.out.println(w[0][a[0]].cont);
			// System.out.println(w[0][a[2]].cont);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// LTPID.close();
		}
	}

}
