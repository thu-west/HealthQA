package platform.nlp.ltp;

import java.util.*;
import platform.GlobalSettings;
import platform.nlp.ltp.id.LTPIDSegmenter;
import platform.nlp.ltp.jni.LTPJNISegmenter;
import platform.nlp.ltp.server.LTPSERVERSegmenter;

public class LTPSettings {
	
	
	/* =====================================
	 |            选择分词器类型：                      |
	 =======================================*/
	
	public static enum SEGMENTORTYPE {LTP_ID, LTP_JNI, LTP_SERVER, OFFLINE, NLPIR, NONE};					// 分词器类型[枚举类型]
	public static final SEGMENTORTYPE segmentor = SEGMENTORTYPE.NLPIR;	// 有两种分词器：LTP_JNI和LTP_SERVER
	public static final boolean enable_offline_segmentor = false;
	
	/* =======================================
	 | 选择NLPIR分词器时，可配置如下项目： |
	 =========================================*/
	
//	public final static String user_dict = "res/ext.dic";
	public final static String user_dict = GlobalSettings.contextDir("dict/ext.dict");
	//public final static String user_dict_with_df = "res/full-med-with-df.dic";
	public final static String user_dict_with_df = GlobalSettings.contextDir("res/删减后的药.txt");
	//public final static String user_dict_with_df_sym = "res/symptom-0.dic";
	public final static String user_dict_with_df_sym = GlobalSettings.contextDir("res/症状-final.txt");
	public final static boolean whether_use_user_dict = true;
	
	public static Map<String, Integer> ext_dict_df = new HashMap<String, Integer>();
	public static Map<String, Integer> ext_dict_df_sym = new HashMap<String, Integer>();
	
	/* =======================================
	 | 选择  LTP_ID  分词器时，可配置如下项目： |
	 =========================================*/
	
	/*
	 *  配置分词的粒度  ->  LTPJNI_Function取值如下：
	 *     = LTPIDSegmenter.NER ： 分词 + 标注词性 + 识别命名实体;
	 *     = LTPIDSegmenter.DEPENDENCY ：分词 + 标注词性 + 识别命名实体 + 分析句法依赖关系;
	 */
	public final static int LTPID_Function = LTPIDSegmenter.NER;
	public static String dict_path = "E:\\ltp_data\\dic";
	public static String seg_model_path = "E:\\ltp_data\\cws.model";
	public static String pos_model_path = "E:\\ltp_data\\pos.model";
	public static String ner_model_path = "E:\\ltp_data\\ner.model";
	public static String par_model_path = "E:\\ltp_data\\parser.model";
	
	/* =======================================
	 | 选择LTP_SERVER分词器时，可配置如下项目： |
	 =========================================*/
	
	/* 
	 * 配置分词的粒度  ->  LTPSERVER_Function取值如下：
	 *    = TPSERVERSegmenter.SEGWORD ： 仅分词;
	 *    = LTPSERVERSegmenter.POSTAG ： 分词 + 标注词性;
	 *    = LTPSERVERSegmenter.NER ： 分词 + 标注词性 + 识别命名实体;
	 *    = LTPSERVERSegmenter.DPENDENCY ：分词 + 标注词性 + 识别命名实体 + 分析句法依赖关系;
	 */
	public final static String LTPSERVER_Function = LTPSERVERSegmenter.NER;
	public final static String ltp_server = "http://127.0.0.1:12345/ltp";
	public final static int piece_process_unit = 2;			// 对段落进行处理时，多少个句子为一组，为了防止LTP_SEVER出现错误设置，-1为不分组。
	
	/* =======================================
	 | 选择  LTP_JNI  分词器时，可配置如下项目： |
	 =========================================*/
	
	/*
	 *  配置分词的粒度  ->  LTPJNI_Function取值如下：
	 *     = LTPJNISegmenter.NER ： 分词 + 标注词性 + 识别命名实体;
	 *     = LTPJNISegmenter.DEPENDENCY ：分词 + 标注词性 + 识别命名实体 + 分析句法依赖关系;
	 */
	public final static int LTPJNI_Function = LTPJNISegmenter.DEPENDENCY;
	public final static String cws_model = "E:\\ltp_data\\cws.model";
	public final static String pos_model = "E:\\ltp_data\\pos.model";
	public final static String ner_model = "E:\\ltp_data\\ner.model";
	public final static String parser_model = "E:\\ltp_data\\parser.model";
	public final static String lexicon = "E\\ltp_data\\dic";
	
	
	/* =======================================
	 | 选择 NLPIR  分词器时，可配置如下项目： |
	 =========================================*/
	
	
	
	 /* =======================================
	 |                其他配置选项                           |
	 =========================================*/
//	public final static boolean debug = false;			// 是否打印debug信息
//	public static boolean[] debugger = new boolean[] {false/*0*/, false/*1*/, false/*2*/, false/*3*/, false/*4*/};
	public final static boolean remind = true;			// 是否打印remind信息
	public final static String word_seperator = "|";	// 展示分词后句子的各种分隔符
	public final static String piece_seperator_l = " { ";
	public final static String piece_seperator_r = " }  ";
	public final static int piece_valid_length = 5;
}
