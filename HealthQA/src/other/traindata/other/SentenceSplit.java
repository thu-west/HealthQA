package other.traindata.other;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SentenceSplit {
	
	static void refine() throws IOException {
		String f = "D:\\Project\\healthqa\\res\\answer\\dbanswer.txt.1.p1";
		BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream(f), "utf8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f+".refine"), "utf8"));
		String line = null;
		StringBuffer sb  =new StringBuffer();
		while( (line=br.readLine()) != null  ) {
			if(!line.replaceAll("[\\s]+", "").isEmpty()) {
				line = line.replace("。", "，");
				line = (line+"。").replaceAll("[，。；：,.:]*。$", "。");
			}
			bw.write(line);
			bw.newLine();
		}
		br.close();
		bw.close();
		new File(f).delete();
		new File(f+".refine").renameTo(new File(f));
	}
	
	static void f1 () throws IOException {
		String f = "D:\\Project\\healthqa\\res\\answer\\dbanswer.txt.24.p1";
		BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream(f), "utf8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f+".tmp"), "utf8"));
		String line = null;
		StringBuffer sb  =new StringBuffer();
		while( (line=br.readLine()) != null  ) {
			if(line.replaceAll("[\\s]+", "").isEmpty()) {
				if(sb.length()==0)
					continue;
				bw.write(sb.toString());
				bw.newLine();
				sb = new StringBuffer();
				continue;
			}
			line = line.replace("。", "，");
			line = (line+"。").replaceAll("[，。；：,.:]*。$", "。");
			System.out.println(line);
			sb.append(line);
		}
		br.close();
		bw.close();
	}
	
	public static void main(String[] args) throws IOException {
		refine();
	}

}
