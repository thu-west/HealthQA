package knowledge;

import java.io.*;
import java.sql.ResultSet;
import java.util.*;

import platform.Platform;
import platform.nlp.ao.Word;
import platform.util.database.DBConfig;
import platform.util.database.MysqlConnection;

public class DictProcess {

	static String path = "E:\\Project\\Eclipse\\healthqa\\dict\\final\\";

	/*
	 * File and Database Transfering
	 */
	static void extractDictFromDB() throws Exception {
		MysqlConnection con = new MysqlConnection(new DBConfig(
				"jdbc:mysql://localhost:3308/ishc_data", "ishc",
				"West_ishc2013"));
		con.connect();
		{
			ResultSet rs = con.query("select name from `inspection`");
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					"D:\\inspection.dict"));
			while (rs.next()) {
				bw.write(rs.getString("name"));
				bw.newLine();
			}
			bw.close();
		}
	}

	/*
	 * Operation between 2 dicts
	 */

	// A minus B
	static void minus(String A, String B) throws Exception {
		BufferedReader br_from = new BufferedReader(new FileReader(B));
		HashSet<String> s_from = new HashSet<String>();
		String line = null;
		while ((line = br_from.readLine()) != null) {
			s_from.add(line);
		}

		BufferedReader br_target = new BufferedReader(new FileReader(A));
		HashSet<String> s_target = new HashSet<String>();
		String line2 = null;
		while ((line2 = br_target.readLine()) != null) {
			if (!s_from.contains(line2))
				s_target.add(line2);
			else
				System.out.println(line2);
		}

		Iterator<String> it = s_target.iterator();
		BufferedWriter bw_target = new BufferedWriter(new FileWriter(A));
		while (it.hasNext()) {
			bw_target.write(it.next());
			bw_target.newLine();
		}

		br_from.close();
		br_target.close();
		bw_target.close();

		orderAndRemoveDup(A);
	}

	// join the dicts ( any number )
	static void merge(String[] fs) throws Exception {

		Set<String> set = new TreeSet<String>();
		String line = null;
		for (String f : fs) {
			BufferedReader br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				set.add(line);
			}
			br.close();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\merge.dic"));
		for (String s : set) {
			bw.write(s);
			bw.newLine();
		}
		bw.close();
	}

	/*
	 * Operation in a single dict
	 */

	// order the words
	static void orderAndRemoveDup(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = null;
		Set<String> set = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("//") && !line.isEmpty())
				set.add(line);

		}
		br.close();
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			bw.write(it.next());
			bw.newLine();
		}
		bw.close();
	}

	static String reverse(String a) {
		StringBuffer sb = new StringBuffer();
		for (int i = a.length() - 1; i >= 0; i--)
			sb.append(a.charAt(i));
		return sb.toString();
	}

	// order the words by the suffix direction
	static void orderBySuffix(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = null;
		Set<String> set = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return reverse(o2).compareTo(reverse(o1));
			}
		});
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("//") && !line.isEmpty())
				set.add(line);

		}
		br.close();
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			bw.write(it.next());
			bw.newLine();
		}
		bw.close();
	}

	/*
	 * Operation 
	 */
	
	// 分离inspection.dict
	static void f5(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = null;
		Set<String> set1 = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
		Set<String> set2 = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
		Set<String> set3 = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});

		while ((line = br.readLine()) != null) {
			if (line.isEmpty())
				continue;
			String temp = line;
			if (line.endsWith("检查") || line.endsWith("试验")
					|| line.endsWith("测定") || line.endsWith("检测")
					|| line.endsWith("法") || line.endsWith("筛查")
					|| line.endsWith("诊") || line.endsWith("显像")
					|| line.endsWith("图") || line.endsWith("造影")
					|| line.endsWith("定位") || line.endsWith("造影")
					|| line.endsWith("镜") || line.endsWith("仪")
					|| line.endsWith("泳") || line.endsWith("超")
					|| line.endsWith("检") || line.endsWith("分析")
					|| line.endsWith("术") || line.endsWith("验")
					|| line.endsWith("常规") || line.endsWith("测")
					|| line.endsWith("扫描") || line.endsWith("成像")
					|| line.endsWith("检验") || line.endsWith("查")
					|| line.endsWith("培养"))
				set1.add(temp);
			else if (line.endsWith("酶") || line.endsWith("蛋白")
					|| line.endsWith("糖") || line.endsWith("酸")
					|| line.endsWith("肽") || line.endsWith("物质")
					|| line.endsWith("素") || line.endsWith("细胞")
					|| line.endsWith("抗体") || line.endsWith("抗原"))
				set2.add(temp);
			else {
				line = line.split("[（(]+")[0];
				if (line.endsWith("检查") || line.endsWith("试验")
						|| line.endsWith("测定") || line.endsWith("检测")
						|| line.endsWith("法") || line.endsWith("筛查")
						|| line.endsWith("诊") || line.endsWith("显像")
						|| line.endsWith("图") || line.endsWith("造影")
						|| line.endsWith("定位") || line.endsWith("造影")
						|| line.endsWith("镜") || line.endsWith("仪")
						|| line.endsWith("泳") || line.endsWith("超")
						|| line.endsWith("检") || line.endsWith("分析")
						|| line.endsWith("术") || line.endsWith("验")
						|| line.endsWith("常规") || line.endsWith("测")
						|| line.endsWith("扫描") || line.endsWith("成像")
						|| line.endsWith("检验") || line.endsWith("查")
						|| line.endsWith("培养"))
					set1.add(temp);
				else if (line.endsWith("酶") || line.endsWith("蛋白")
						|| line.endsWith("糖") || line.endsWith("酸")
						|| line.endsWith("肽") || line.endsWith("物质")
						|| line.endsWith("素") || line.endsWith("细胞")
						|| line.endsWith("抗体") || line.endsWith("抗原"))
					set2.add(temp);
				else
					set3.add(temp);
			}
		}
		br.close();
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename
					+ ".method"));
			Iterator<String> it = set1.iterator();
			while (it.hasNext()) {
				bw.write(it.next());
				bw.newLine();
			}
			bw.close();
		}
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename
					+ ".indicator"));
			Iterator<String> it = set2.iterator();
			while (it.hasNext()) {
				bw.write(it.next());
				bw.newLine();
			}
			bw.close();
		}
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename
					+ ".not"));
			Iterator<String> it = set3.iterator();
			while (it.hasNext()) {
				bw.write(it.next());
				bw.newLine();
			}
			bw.close();
		}
	}

	/*
	 * 取词典中所有词的后两个字符（后缀）
	 */
	static void f6(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = null;
		Set<String> set = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
		while ((line = br.readLine()) != null) {
			System.out
					.println(line.substring(line.length() - 2, line.length()));
			set.add(line.substring(line.length() - 2, line.length()));
		}
		br.close();
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename
				+ ".stat"));
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			bw.write(it.next());
			bw.newLine();
		}
		bw.close();
	}

	/*
	 * 将词典中的词语分类
	 */
	static boolean in(String target, String[] suffix) {
		for (String s : suffix) {
			if (target.endsWith(s))
				return true;
		}
		return false;
	}

	static void classifyHelper(String filename, String[][] in_suffix,
			String[] in_suffix_name) throws Exception {
		String[][] suffix = new String[in_suffix.length + 1][];
		String[] suffix_name = new String[in_suffix_name.length + 1];
		for (int i = 0; i < suffix.length - 1; i++) {
			suffix[i] = in_suffix[i];
			suffix_name[i] = in_suffix_name[i];
		}
		suffix[suffix.length - 1] = new String[] { "" };
		suffix_name[suffix.length - 1] = "其他";
		Set<String>[] sets = new Set[suffix.length];
		for (int i = 0; i < suffix.length; i++)
			sets[i] = new TreeSet<String>();

		int len = suffix.length;
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = null;
		while ((line = br.readLine()) != null) {
			line = line.replaceAll("[ \t]+", "");
			for (int i = 0; i < len; i++) {
				if (in(line, suffix[i])) {
					sets[i].add(line);
					break;
				}
			}
		}
		br.close();

		for (int i = 0; i < len; i++) {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename
					+ "." + suffix_name[i]));
			Iterator<String> it = sets[i].iterator();
			while (it.hasNext()) {
				bw.write(it.next());
				bw.newLine();
			}
			bw.close();
			orderBySuffix(filename + "." + suffix_name[i]);
		}
	}

	static void classifyDTZIQMC(String filename) throws Exception {
		String[] disease_suffix = new String[] { "病", "症", "炎", "瘤", "癌", "疹",
				"痣", "征", "结核", "紫癜", "癫痫", "白内障", "肿", "骨折", "不良", "伤", "瘫",
				"坏死", "斑", "肿大", "脱位", "不全", "痛", "出血", "腹泻", "中毒", "畸形", "感染",
				"溃疡", "麻痹", "痴呆", "异常", "过多", "过高", "过少", "过低", "穿孔", "息肉",
				"狭窄", "缺陷", "发热", "障碍", "白痴", "粘连", "震颤", "陷", "阻", "闭锁", "过速",
				"减退", "昏迷", "亢进", "不足", "并趾", "平足", "斜视", "失调", "失语", "裂",
				"贫血", "积血", "带血", "充血", "萎缩", "瘘管", "衰竭", "便秘", "瘘", "黄疸", "疣",
				"增生", "出血热", "滞", "积水", "梅毒", "梗死", "缺损", "劳损", "痉挛", "并指",
				"疾患", "血尿", "蛋白尿", "良好", "缺如", "缺失", "肥大", "增多", "闭塞", "哮喘",
				"白喉", "呕吐", "癌变", "病变", "硬变", "脱发", "惊厥", "高血压", "钙化", "软化",
				"纤维化", "老化", "硬化", "无力", "膨出", "脱出", "突出", "凸", "休克", "紊乱",
				"缺乏", "低下" };
		String[] treat_suffix = new String[] { "术", "治疗", "缝合", "移植", "截断",
				"离断", "植入", "复位", "结扎", "修补", "栓塞", "除", "麻醉", "止血", "引流",
				"灌注", "疗法", "透析", "眩晕", "切断", "注射", "穿刺" };
		String[] zhongyi_suffix = new String[] { "证", "虚", "藤", "穴" };
		String[] inspect_suffix = new String[] { "查", "测", "验", "测定", "诊",
				"显像", "成像", "图", "造影", "定位", "泳", "超", "检", "分析", "常规", "扫描",
				"多普勒" };
		String[] equip_suffix = new String[] { "仪", "镜", "器", "钳", "剪", "针",
				"计", "钉", "钢板", "螺纹", "针头", "夹板", "盒", "支架", "柄", "机", "桌",
				"钛", "锯", "镊", "袋", "衬", "装置", "口罩", "纸", "玻璃管", "流管", "插管",
				"连接管", "输气管", "尿管", "导管", "灯", "泵", "板", "杯", "纸条", "敷料", "套",
				"夹", "刀", "具" };
		String[] medicine_suffix = new String[] { "颗粒", "霜", "软膏", "膏", "胶囊",
				"肠溶胶囊", "肠溶片", "糖片", "糖浆", "滴鼻液", "滴眼液", "滴丸", "溶液", "液",
				"涂膜剂", "洗剂", "注射液", "注射剂", "气雾剂", "止痛膏", "栓", "搽剂", "干糖浆",
				"喷雾剂", "咀嚼片", "合剂", "口服液", "口服乳剂", "口服乳", "剂", "分散片", "凝胶",
				"乳膏", "丸", "片", "糖衣", "薄膜衣", "贴", "灵", "唑", "吲哚" };
		String[] chemicals_suffix = new String[] { "酶", "醚", "胺", "酚", "酯",
				"碱", "脲", "酮", "酸", "霉素", "烯", "蛋白", "醇", "甘油", "脂", "酐", "抗体",
				"抗原", "疫苗", "病毒", "苷", "啶", "钠", "腈", "嘌呤", "锌", "醚", "蛋白原",
				"盐", "醌", "素", "酰", "烷", "苯", "氧化物", "腙", "呔", "糖", "铵", "银",
				"铵", "铨", "铝", "铜", "铋", "铅", "铂", "铁", "钾", "钼", "钙", "醛",
				"因子" };
		String[][] suffix = new String[][] { disease_suffix, treat_suffix,
				zhongyi_suffix, inspect_suffix, equip_suffix, medicine_suffix,
				chemicals_suffix };
		String[] suffix_name = new String[] { "疾病", "治疗", "中医", "检查", "器械",
				"药物", "化合物" };
		classifyHelper(filename, suffix, suffix_name);
	}

	/*
	 * 提取人体指标和其他化合物
	 */
	static void extractIndicator(String filename) throws Exception {
		String[] indicator_suffix = new String[] { "酶", "抗体", "蛋白", "抗原",
				"蛋白原", "因子" };
		String[][] suffix = new String[][] { indicator_suffix };
		String[] suffix_name = new String[] { "人体指标" };
		classifyHelper(filename, suffix, suffix_name);
	}

	/*
	 * 将症状和疾病区分开来
	 */
	static void extractDisease(String filename) throws Exception {
		String[] disease_suffix = new String[] { "痴呆", "瘘", "黄疸", "疣", "梅毒",
				"哮喘", "白喉", "呕吐", "疾患", "血尿", "蛋白尿", "脱发", "高血压", "休克", "并趾",
				"平足", "白痴", "昏迷", "发热", "病", "症", "炎", "瘤", "癌", "疹", "痣", "征",
				"结核", "紫癜", "癫痫", "白内障", "斑", "腹泻", "中毒", "瘘管", "斜视" };
		String[] sym_suffix = new String[] { "畸形", "感染", "溃疡", "麻痹", "异常",
				"过多", "过高", "过少", "过低", "穿孔", "息肉", "狭窄", "缺陷", "障碍", "粘连",
				"震颤", "陷", "阻", "闭锁", "过速", "减退", "亢进", "不足", "失调", "失语", "裂",
				"贫血", "积血", "带血", "充血", "萎缩", "衰竭", "便秘", "增生", "出血热", "滞",
				"积水", "梗死", "缺损", "劳损", "痉挛", "并指", "良好", "缺如", "缺失", "肥大",
				"增多", "闭塞", "癌变", "病变", "硬变", "脱发", "惊厥", "钙化", "软化", "纤维化",
				"老化", "硬化", "无力", "膨出", "脱出", "突出", "凸", "紊乱", "缺乏", "低下", "肿",
				"骨折", "不良", "伤", "瘫", "坏死", "肿大", "脱位", "不全", "痛", "出血" };
		BufferedReader br = new BufferedReader(new FileReader(filename));
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(filename
				+ ".dis"));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(filename
				+ ".sym"));
		BufferedWriter bw3 = new BufferedWriter(
				new FileWriter(filename + ".ot"));
		String line = null;
		while ((line = br.readLine()) != null) {
			if (in(line, disease_suffix)) {
				bw1.write(line);
				bw1.newLine();
			} else if (in(line, sym_suffix)) {
				bw2.write(line);
				bw2.newLine();
			} else {
				bw3.write(line);
				bw3.newLine();
			}
		}
		bw1.close();
		bw2.close();
		bw3.close();
		br.close();
	}

	/*
	 * 显示每一行的关键词
	 */
	static void f8(String filename) throws Exception {
		Set<String> dict = DictReadWrite
				.loadDictInStringSet(DictGallary.final_path + "jiepou.dict");
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String word = null;
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename
				+ ".keyword"));
		while ((word = br.readLine()) != null) {
			StringBuffer sb = new StringBuffer(word);
			sb.append("\t");
			for (int i = 0; i < word.length(); i++) {
				for (int j = i + 1; j <= word.length(); j++) {
					String temp = word.substring(i, j);
					if (dict.contains(temp)) {
						sb.append(temp);
						sb.append(";;");
					}
				}
			}
			bw.write(sb.toString());
			bw.newLine();
		}
		br.close();
		bw.close();
	}

	/*
	 * 词典分词
	 */
	static void segmentDict(String dict) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(dict));
		BufferedWriter bw = new BufferedWriter(new FileWriter(dict + ".seg"));
		String line = null;
		while ((line = br.readLine()) != null) {
			Word[][] seg = Platform.segment(line);
			bw.write(line);
			bw.write("\t");
			bw.write(Word.toSimpleString(seg));
			bw.newLine();
		}
		br.close();
		bw.close();
	}

	/*
	 * 去除括号
	 */
	static void f9(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename + ".no"));
		String line = null;
		while ((line = br.readLine()) != null) {
			bw.write(line.replaceAll("[(（]+.*[)）]+", "").replace(" ", ""));
			bw.newLine();
		}
		br.close();
		bw.close();
	}

	public static void main(String[] args) throws Exception {

//		orderBySuffix("D:\\temp\\jib");
//		segmentDict("D:\\temp\\jib.ot");
//
//		f8("D:\\temp\\jib");
//
//		extractDisease("D:\\temp\\jib");
//
//		minus(DictGallary.medicine, DictPersistence.organ);
//		minus(DictGallary.organ, DictPersistence.sign);
//
//		orderAndRemoveDup(DictGallary.indicator);
//		orderAndRemoveDup(DictGallary.indicator_des);
//		orderAndRemoveDup(DictGallary.organ);
//		orderAndRemoveDup(DictGallary.sign);
//		orderAndRemoveDup(DictGallary.organ_des);
//		orderAndRemoveDup(DictGallary.disease);
//		orderAndRemoveDup(DictGallary.medicine);
//		orderAndRemoveDup(DictGallary.food);
		
//		orderAndRemoveDup("C:\\Users\\Fire\\Desktop\\哪里");
		
//		orderAndRemoveDup("C:\\Users\\Fire\\Desktop\\问句\\shenme\\bv");
//		orderAndRemoveDup("C:\\Users\\Fire\\Desktop\\问句\\shenme\\bn");
//		orderAndRemoveDup("C:\\Users\\Fire\\Desktop\\问句\\shenme\\pv");
//		orderAndRemoveDup("C:\\Users\\Fire\\Desktop\\问句\\zenme\\bv");
		
//		orderAndRemoveDup("C:\\Users\\Fire\\Desktop\\问句\\c-shibushi\\shi_ma\\full");
		
//		minus(DictGallary.medicine, DictGallary.food);
//		orderAndRemoveDup(DictGallary.food);
		
		String[] fs = new String[]{ DictGallary.disease, DictGallary.indicator, DictGallary.food, DictGallary.medicine, DictGallary.treat, DictGallary.equipment };
		merge(fs);
		
	}

}
