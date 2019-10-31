package application.qav2.process_answer.wzyy_crf;

import java.io.*;

public class DataWzyy {
	
	public static final String[] FRONT_WORD = new String[] {
		"如果",
		"若",
		"如",
		"即使",
		"尽管",
		"不管",
		"假如",
		"因为",
		"在"
		};
	public static final String[] FRONT_LABEL = new String[] {
		"rg",
		"rg",
		"rg",
		"js",
		"jg",
		"bg",
		"rg",
		"yw",
		"z"
	};
	
	public static void manualFileToAF(String manual_file, String target_file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(manual_file), "utf8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(target_file), "utf8"));
		String line = null;
		while((line=br.readLine())!=null) {
			bw.write(line.replace("，", " #\\dou ").replace("。", " #\\ju "));
			bw.newLine();
		}
		br.close();
		bw.close();
	}
	
	public static void generateFrontLabel(String orig_file, String target_file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(orig_file), "utf8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(target_file), "utf8"));
		String line = null;
		while((line=br.readLine())!=null) {
			StringBuffer sb = new StringBuffer();
			line = line.replace("，", "#").replace("。", "#");
			for(int i=0; i<line.length(); i++) {
				if(line.charAt(i)=='#') {
					String temp = sb.toString();
					String tt = new String(temp);
					if(temp.length()>4) 
						tt = temp.substring(0, 4);
					boolean flag = false;
					for(int k = 0; k<FRONT_WORD.length; k++) {
						String fs = FRONT_WORD[k];
						if(tt.contains(fs)) {
							temp = temp+" #\\"+FRONT_LABEL[k]+" ";
							flag = true;
							break;
						}
					}
					if(!flag) {
						temp = temp+"#";
					}
					bw.write(temp);
					sb = new StringBuffer();
				} else {
					sb.append(line.charAt(i));
				}
			}
			bw.newLine();
		}
		br.close();
		bw.close();
	}
	public static void main(String[] args) throws IOException {
		
	}

}
