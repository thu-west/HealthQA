package other.fire;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import platform.util.general.JavaRegex;
import platform.util.log.Trace;

public class DictPreprocess {
	static void preprocessBaiduBaike() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("E:\\ltp_data\\baike-1.dic"));
		BufferedWriter bw1 = new BufferedWriter(new FileWriter("E:\\ltp_data\\baike-1-1.dic"));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter("E:\\ltp_data\\baike-1-2.dic"));
		int c=0;
		while( br.readLine()!=null) {c++;}
		br.close();
		br = new BufferedReader(new FileReader("E:\\ltp_data\\baike-1.dic"));
		
		Pattern p = Pattern.compile("["+JavaRegex.chinese_character+"]");
		
		String line = null;
		String regex = "["+JavaRegex.chinese_character+"]+";
		
		Trace t = new Trace(c, c/100);
		int i=0; int j=0;
		Set<String> s = new TreeSet<String>();
		while( (line=br.readLine()) != null ){
//			if(i>1000)
//				break;
			t.debug(""+i+":"+j, true);
			if( Pattern.matches(regex, line)){
				if(line.length()>6 || line.length()<2 ){
					bw2.write( line );
					bw2.newLine();
					j++;
				}else{
					s.add(line);
					
					i++;
				}
			}else {
				bw2.write( line );
				bw2.newLine();
				j++;
			}
		}
		for( String ss : s) {
			bw1.write( ss );
			bw1.newLine();
		}
		System.out.println(i+":"+j);
		br.close();
		bw1.close();
		bw2.close();
	}
	
	
	public static void filter() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("io\\baike2-6.dic"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("io\\baike2-4.dic"));
		String line = null;
		while( (line=br.readLine()) !=null ) {
			if(line.length()<=4){
				bw.write(line);
				bw.newLine();
			}
		}
		br.close();
		bw.close();
	}
	
	public static void combineFile( ) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		list.add("E:\\ltp_data\\dict\\中外药品名称大全【官方推荐】.txt");
		list.add("E:\\ltp_data\\dict\\医疗器械大全【官方推荐】.txt");
		list.add("E:\\ltp_data\\dict\\医学词汇大全【官方推荐】.txt");
		list.add("E:\\ltp_data\\dict\\中医中药大全【官方推荐】.txt");
		list.add("E:\\ltp_data\\dict\\饮食大全【官方推荐】.txt");
		list.add("E:\\ltp_data\\dict\\症状名.txt");
		
		Set<String> s = new TreeSet<String>();
		
		for( String fn: list) {
			BufferedReader br = new BufferedReader(new FileReader(fn));
			int c=0;
			while( br.readLine()!=null) {c++;}
			br.close();
			br = new BufferedReader(new FileReader(fn));
			String line = null;
			Trace t = new Trace(c, c/10);
			while( (line=br.readLine()) != null ){
				s.add(line);
			}
			br.close();
			System.out.println(fn+"    done!\n=====================================");
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("E:\\ltp_data\\dict\\medical.dic"));
		for( String ss : s) {
			bw.write( ss );
			bw.newLine();
		}
		bw.close();
	}
	
	public static void main(String[] args) throws Exception {
//		combineFile();
		filter();
	}
}
