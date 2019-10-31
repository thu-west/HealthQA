package platform.mltools.crfpp;

import java.io.*;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import platform.util.log.Trace;

public class CRFPPDataHandler {
	
	/**
	 * Two type file:
	 * 1. AggregateFile (AggFile, *.af) : file in which entity is labeled in aggregate form, for example:
	 * 			糖尿病\d 是一种 血糖\i 引起的疾病
	 * 2. SingleSegregateFile (SingleSegFile, *.ssf) : file in which entity is labeled in segregate form which starts with "B" or "I" and "O" for others, for example:
	 * 			糖	Bd 
	 * 			尿	Id
	 * 			病	Id 
	 * 			是	O 
	 * 			疾	O 
	 * 			病	O
	 * 3. MultiSegregateFile (MultiSegFile, *.msf) : file similar to SingleSegFile, but with multi collumn labels
	 *			糖	Bd	Bn 
	 * 			尿	Id	In
	 * 			病	Id	In
	 * 			是	O	Bvshi
	 * 			疾	O	Bn
	 * 			病	O	Bn
	 */
	public String FOLDER = "";
	
	public CRFPPDataHandler(String _FOLDER) {
		FOLDER = _FOLDER;
	}
	
	static final Trace t = new Trace().setValid(false, true);
	
	private void _transferToSingleSegFile(File input) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(
						input), "utf8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(
						input.getAbsolutePath()+".ssf.tmp"), "utf8"));
		String line = null;
		int count = 0;
		try {
			while ((line = br.readLine()) != null) {
				count++;
				line.replaceAll("[ ]+", " ");
				String[] tokens = line.split(" ");
				for (String token : tokens) {
					if (token.contains("\\")) {
						String[] seq = token.split("\\\\");
						if (seq.length != 2) {
							System.err.println(count + " /1/ " + token + " : "
									+ line);
							break;
						}
						String word = seq[0];
						String tag = seq[1];
//						if( word.charAt(0) == '，' || word.charAt(0) == '。' || word.charAt(0) == '：') {
//							System.err.println(count+" /2/ "+token+" : "+line);
//							break;
//						}
//						if(!Pattern.matches("^[a-z]{1,4}$", tag)) {
//							System.err.println(count+" /3/ "+token+" : "+line);
//							break;
//						}
						for (int i = 0; i < word.length(); i++) {
							if (i == 0) {
								bw.write(word.charAt(i) + "\tB" + tag);
							} else {
								bw.write(word.charAt(i) + "\tI" + tag);
							}
							bw.newLine();
						}
					} else {
						for (int i = 0; i < token.length(); i++) {
							bw.write(token.charAt(i) + "\tO");
							bw.newLine();
						}
					}
				}
				bw.newLine();
			}
			bw.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("line is:"+line);
		}
	}
	
	private File[] _getAllSingleSegFiles() throws IOException {
		File folder  = new File(FOLDER);
		int num = 0;
		for( File f : folder.listFiles() ) {
			if( f.getName().endsWith(".ssf.tmp") )
				num++;
		}
		File[] fs = new File[num];
		num = 0;
		for( File f : folder.listFiles() ) {
			if( f.getName().endsWith(".ssf.tmp") ) {
				t.debug("detect file: "+f.getName());
				fs[num++] = f; //new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf8"));
			}
		}
		// FIXME
		File tmp = fs[0];
		fs[0] = fs[1];
		fs[1] = tmp;
		System.out.println(fs[0]);
		return fs;
	}
	
	private File[] _getAllAggFiles() throws IOException {
		File folder  = new File(FOLDER);
		int num = 0;
		for( File f : folder.listFiles() ) {
			if( f.getName().endsWith(".af") )
				num++;
		}
		File[] fs = new File[num];
		num = 0;
		for( File f : folder.listFiles() ) {
			if( f.getName().endsWith(".af") ) {
				t.debug("detect file: "+f.getName());
//				System.out.println("detect file: "+ f.getName());
				fs[num++] = f;
			}
		}
		return fs;
	}
	
	private String[] _readLines(BufferedReader[] brs ) throws IOException {
		boolean flag = true;
		String[] temp = new String[brs.length];
		for(int i=0; i<brs.length; i++) {
			temp[i] = brs[i].readLine();
			if(temp[i]!=null) flag = false;
		}
		if(flag) return null;
		return temp;
	}
	
	private boolean _isLinesMatch(String[] lines) {
		for(int i=1; i<lines.length; i++) {
			if(lines[i-1].length()*lines[i].length()==0 && (lines[i-1].length()!=lines[i].length()))
				return false;
			if(lines[i].length()>0 && ( lines[i-1].charAt(0) != lines[i].charAt(0) ))
				return false;
		}
		return true;
	}
	
	private String _joinLines(String[] lines) {
		if(lines[0].length()==0)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append(lines[0].charAt(0));
		for( String l : lines ) {
			sb.append(l.substring(1));
		}
		return sb.toString();
	}

	public void combineAllSingleSegFilesToMultiSegFile(String output) throws Exception {
		File[] fs = _getAllSingleSegFiles();
		BufferedReader[] brs = new BufferedReader[fs.length];
		for(int i=0; i<fs.length; i++) {
			brs[i] = new BufferedReader(new InputStreamReader(new FileInputStream(fs[i]), "utf8"));
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(FOLDER+"/"+output), "utf8"));
		String[] lines = null;
		int count = 0;
		while((lines = _readLines(brs))!=null) {
			count++;
			if(!_isLinesMatch(lines)) {
				throw new Exception("Lines not match at "+count+" : "+StringUtils.join(lines, " | ")+"in folder "+FOLDER);
			}
			bw.write(_joinLines(lines));
			bw.newLine();
		}
		bw.close();
		for(BufferedReader br : brs) {
			br.close();
		}
		for(File f: fs ) {
			t.debug("deleting "+f.getAbsolutePath());
			f.deleteOnExit();
		}
	}
	
	public void transferAllAggFilesToSingleSegFiles() throws IOException {
		File[] fs = _getAllAggFiles();
		for( File f: fs) 
			_transferToSingleSegFile(f);
	}
	
	/**
	 * 将SingleSegFile还原成没有任何标签的生文件
	 */
	public static void restoreSingleSegFileToRawFile(String input, String output) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(input), "utf8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(output), "utf8"));
		String line = null;
		while( (line=br.readLine())!= null ) {
			if(line.length()==0) {
				bw.newLine();
				continue;
			}
			if(line.contains("\\"))
				System.out.println(line);
			String[] t = line.split("\t");
			bw.write(t[0]);
		}
		br.close();
		bw.close();
	}
	
	
	
	
	
	public static void main(String[] args) throws Exception {
		
//		restoreSingleSegFileToRawFile("D:/Project/healthqa/res/data/answer/EntityWithPunc/train/1.dbanswer.dict.auto.AggFile.SingleSegFile.tmp", "D:/Project/healthqa/res/data/answer/EntityWithPunc/train/raw");
	}

	
}
