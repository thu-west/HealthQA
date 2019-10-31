package platform.nlp.nlpir;

import platform.util.log.Trace;

public class Sample {

	static String input = "范德萨范德萨发.阿巴卡擦泪那我给你发的刷卡了韦双夫定。龑做累龙一哈燊,千万科学家;据悉；质检总局已将最新有关情况再次通报美方,要求美方加强对输华玉米的产地来源、运输及仓储等环节的管控措施，有效避免输华玉米被未经我国农业部安全评估并批准的转基因品系污染。";
	static String libroot = "lib/ICTCLAS2014u0105";
	static String system_charset = "UTF-8";
	static int charset_type = 1;
	static boolean init_flag = true;

	static {
		int init = CLibrary.Instance.NLPIR_Init(libroot, charset_type, "0");
		if (0 == init) {
			System.err.println("初始化失败！");
			init_flag = false;
		}
	}

	public static void add_single_user_word() throws Exception {
		if (!init_flag)
			return;
		CLibrary.Instance.NLPIR_AddUserWord("我靠");
		CLibrary.Instance.NLPIR_Exit();
	}

	public static void add_user_word_from_file() throws Exception {
		if (!init_flag)
			return;
		CLibrary.Instance.NLPIR_ImportUserDict("res/ext.dic");
		CLibrary.Instance.NLPIR_Exit();
	}

	public static void delete_user_word() {
		if (!init_flag)
			return;
		CLibrary.Instance.NLPIR_ImportUserDict("res/ext.dic");
		CLibrary.Instance.NLPIR_DelUsrWord("要求美方加强对输");
		CLibrary.Instance.NLPIR_Exit();
	}

	public static void get_key_word() {
		if (!init_flag)
			return;
		try {
//			CLibrary.Instance.NLPIR_ImportUserDict("res/ext.dic");
			String nativeByte = CLibrary.Instance.NLPIR_GetKeyWords(input, 10,
					false);
//			nativeByte = CLibrary.Instance.NLPIR_GetFileKeyWords("res/ext.dic", 10,
//					true);
			System.out.println(nativeByte);
			CLibrary.Instance.NLPIR_Exit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	public static void test_with_user_word() throws Exception {
		if (!init_flag)
			return;
		String nativeBytes = null;

		try {
			Trace t = new Trace();
			t.debug("loading ext.dic");
			CLibrary.Instance.NLPIR_ImportUserDict("res/ext.dic");
			t.debug("loaded ext.dic");
			nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(input, 1);
			System.out.println(nativeBytes);
			CLibrary.Instance.NLPIR_Exit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void test_without_user_word() throws Exception {

		if (!init_flag)
			return;

		String nativeBytes = null;
		try {
			CLibrary.Instance.NLPIR_AddUserWord("范德萨咖啡卡洛斯的经费");
			nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(input, 1);
			System.out.println("分词结果为： " + nativeBytes);
			CLibrary.Instance.NLPIR_Exit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		Sample.input = "你好吗范德萨咖啡卡洛斯的经费fasfasd";
//		test_with_user_word();
		test_without_user_word();
//		get_key_word();

	}
}
