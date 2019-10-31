package platform.annotate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import platform.Platform;
import platform.nlp.ao.Word;

public class AnnotateByPostag {
	
	public static void label(String input, String output) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(
						input), "utf8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(
						output), "utf8"));
		String line = null;
		while ( (line=br.readLine()) != null ) {
			if(line.length() ==0 ) {
				bw.newLine();
				continue;
			}
			if(line.contains("\\"))
				System.out.println(line);
			Word[][] www = Platform.segment(line);
			StringBuffer sb = new StringBuffer();
			for( Word[] ww : www ) {
				for( Word w : ww) {
					String temp = w.cont + "\\" + w.pos + " ";
					sb.append(temp);
//					String word = w.cont;
//					String tag = w.pos;
//					for( int i=0; i<word.length(); i++ ) {
//						if(i==0)
//							bw.write(word.charAt(i)+"\tB"+tag);
//						else
//							bw.write(word.charAt(i)+"\tI"+tag);
//						bw.newLine();
//					}
				}
			}
			bw.write(sb.toString().replaceAll("[ ]+", " ").replaceAll("^ ", "").replaceAll(" $", ""));
			bw.newLine();
		}
		bw.close();
		br.close();
	}

	public static void main(String[] args) throws Exception {
//		label("D:/Project/healthqa/res/data/question/train/raw", "D:/Project/healthqa/res/data/question/train/2.postag.mf");
	}
}
