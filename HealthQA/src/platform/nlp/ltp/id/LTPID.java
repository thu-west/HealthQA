package platform.nlp.ltp.id;

import java.io.*;

import platform.nlp.ltp.LTPSettings;

public class LTPID {

	public static Process p;
	public static BufferedReader readstd;
	public static BufferedWriter writestd;

	public static boolean ready = false;

	public static String dict_path = LTPSettings.dict_path;
	public static String seg_model_path = LTPSettings.seg_model_path;
	public static String pos_model_path = LTPSettings.pos_model_path;
	public static String ner_model_path = LTPSettings.ner_model_path;
	public static String par_model_path = LTPSettings.par_model_path;

	public static void init(String func) { // ner denpendency
		try {
			p = Runtime.getRuntime().exec(
					"lib/ltp/LTPEXE.exe" + " " + func + " " + seg_model_path
							+ " " + pos_model_path + " " + ner_model_path + " "
							+ par_model_path + " " + dict_path);
			InputStream in = p.getInputStream();
			readstd = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			OutputStream out = p.getOutputStream();
			writestd = new BufferedWriter(new OutputStreamWriter(out, "GBK"));
			String line = null;
			while ((line = readstd.readLine()) != null) {
				System.out.println(line);
				if (line.contains("ready")) {
					ready = true;
					break;
				}
				if (line.contains("error")) {
					ready = false;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void close() {
		p.destroy();
	}

	public static String[] seg(String paragraph) {
		char c = paragraph.charAt(paragraph.length() - 1);
		String endc = "。？；！";
		if (!endc.contains(c + ""))
			paragraph = paragraph + "。";
		String[] sent_array = null;
		try {
			writestd.write(paragraph);
			writestd.newLine();
			writestd.flush();
			int sent_num = Integer.parseInt(readstd.readLine());
			sent_array = new String[sent_num];
			for (int i = 0; i < sent_num; i++) {
				sent_array[i] = readstd.readLine();
			}
			// 吃掉多余空行
			readstd.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sent_array;
	}

	public static void main(String[] args) {
		try {
			String line = null;
			while ((line = readstd.readLine()) != null) {
				System.out.println(line);
				if (line.contains("input:")) {
					System.out.println("写入");
					writestd.write("二甲双弧。");
					writestd.newLine();
					writestd.flush();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
