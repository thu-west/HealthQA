package platform.mltools.crfpp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import platform.util.log.Trace;

public class ReadableFileHandler {
	
	static final Trace t = new Trace().setValid(false, true);
	
	public static String refineLine_old(String line) throws Exception {
		StringBuffer sb = new StringBuffer();
		line.replaceAll("[ ]+", " ");
		String[] tokens = line.split(" ");
		for (String token : tokens) {
			if (token.contains("\\")) {
				// format
				String[] seq = token.split("\\\\");
				if (seq.length != 2) {
					throw new Exception(token + " ::: " + line);
				}
				String word = seq[0];
				String tag = seq[1];
				
				// word
				
				int sp = 0;
				if(word.length()<=1) {
					// 这时标注对象长度是一，可认为是postag中的标点标注，所以省略下面的标点截断处理
				} else {
					// 在标点处截断，认为标点不应该在一个实体中。
					if( (sp=word.lastIndexOf("。")) > -1) {
						sb.append(word.substring(0, sp+1));
						word = word.substring(sp+1);
					}
					if ((sp=word.lastIndexOf("，")) > -1) {
						sb.append(word.substring(0, sp+1));
						word = word.substring(sp+1);
					}
					if ((sp=word.lastIndexOf("、")) > -1) {
						sb.append(word.substring(0, sp+1));
						word = word.substring(sp+1);
					}
					if ((sp=word.lastIndexOf("：")) > -1) {
						sb.append(word.substring(0, sp+1));
						word = word.substring(sp+1);
					}
					if ((sp=word.lastIndexOf("；")) > -1) {
						sb.append(word.substring(0, sp+1));
						word = word.substring(sp+1);
					}
				}
				sb.append(" "+word+"\\");
				
				// tag
				Pattern p = Pattern.compile("^([a-z]+)([^a-z]*)$");
				Matcher m = p.matcher(tag);
				if(m.find()) {
					String real_tag = m.group(1);
					String tail = m.group(2);
					sb.append(real_tag+" "+tail);
				} else {
					throw new Exception("tag ["+"] is illegal");
				}
			} else {
				sb.append(token);
			}
		}
		return sb.toString().replaceAll("[ ]+", " ").replaceAll("^ ", "").replaceAll(" $", "");
	}
	
	static String EffectiveEntityRegex = "^([^\\\\]+)\\\\([a-z]+)$";
	static Pattern EffectiveEntity = Pattern.compile(EffectiveEntityRegex);

	public static String refineLine(String line) throws Exception {
		line = line.replaceAll("([，。、：；])$", " $1");    // 末尾标点前面加空格
		line = line.replaceAll("([，。、：；])\\\\", " $1\\\\");	 // 标点如果成为被标记对象时（即后面有'\'时），需要在标点前面加空格
		line = line.replaceAll("([，。、：；])([^\\\\])", " $1 $2");	// 标点后面如果不是'\'，说明这个标点是普通标点，给这个标点的前后都添加空格
		line = line.replaceAll("\\\\[a-z]+", "$0 ");	// 在每个标签后面加空格
		String[] tokens = line.split("[ ]+");
		StringBuffer sb = new StringBuffer();
		for (String token : tokens) {
			Matcher m = EffectiveEntity.matcher(token);
			if(m.find()) {
				sb.append(" "+token+" ");
			} else if( token.contains("\\") ) {
				throw new Exception("Error occur at token ["+token+"] in line ["+line+"]"); 
			} else {
				sb.append(token);
			}
		}
		return sb.toString().replaceAll("[ ]+", " ").replaceAll("^ ", "").replaceAll(" $", "");
	}
	
	public static void refine(String aggregate_filename) throws IOException, FileNotFoundException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(
						aggregate_filename), "utf8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(
						aggregate_filename+".tmp"), "utf8"));
		String line = null;
		int count = 0;
		try {
			while ((line = br.readLine()) != null) {
				count++;
				try {
					line = refineLine(line);
				} catch(Exception e) {
					t.error("error in line "+count);
					e.printStackTrace();
				}
				bw.write(line);
				bw.newLine();
			}
			bw.close();
			br.close();
			File f = new File(aggregate_filename);
			f.renameTo(new File(aggregate_filename+".backup."+System.currentTimeMillis()));
			File nf = new File(aggregate_filename+".tmp");
			nf.renameTo(f);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(line);
		}
	}

	public static void main(String[] args) throws Exception {
//		refine("D:/Project/healthqa/res/data/question/train/3.label.man.mf");
		String line = "心力衰竭\\d，\\dou是他fdsaf目前的病情\\ada发大水发fasdfaaf\\ddfa发展难f以dsafasd避免的结果，目前出现胸痛\\d症状，应注意有无心绞痛\\d、心肌梗塞\\d等可能。所以要复查心电图\\c，心肌\\o酶。建议最好一个月到三个月复查。";
		line = "心力衰竭\\d ，\\dou 是他fdsaf目前的 病情\\ada继续发展难f以dsafasd避免的结果， 目前出现胸痛\\d 症状， 应注意有无心绞痛\\d 、 心肌梗塞\\d 等可能。 所以要复查 心电图\\c， 心肌\\o 酶。建议最好一个月到三个月复查。";
		line = "范德萨发  过于频繁。手淫过于频繁，心情不好或是工作劳累都可能导致的，透纳延时喷剂只有暂时缓解的作用，并没有治愈的疗效。早泄\\d 我靠";
		System.out.println(line);
		line = refineLine(line);
		System.out.println(line);
	}
}
