package knowledge;

import java.io.*;
import java.sql.ResultSet;
import java.util.*;

import platform.Platform;
import platform.nlp.ao.Word;
import platform.util.database.DBConfig;
import platform.util.database.MysqlConnection;
import platform.util.log.Trace;

class ValueComparator implements Comparator<Map.Entry<String,Stat>>  
{  
    @Override
	public int compare(Map.Entry<String,Stat> m,Map.Entry<String,Stat> n)  
    {  
        return n.getValue().t-m.getValue().t;
    }  
}  

class Stat {
	int q; // question stat
	int a; // answer stat
	int t; // total stat
	
	public Stat() {
		t = a = q = 0;
	}
	
	public Stat( int q, int a) {
		this.q = q;
		this.a = a;
		this.t = a+q;
	}
	
	public void qi() {
		q++;
		t++;
	}
	
	public void ai() {
		a++;
		t++;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(q);
		sb.append("\t");
		sb.append(a);
		sb.append("\t");
		sb.append(t);
		return sb.toString();
	}
}

public class Statistic {
	
	static Map<String, Stat> disease;
	static Map<String, Stat> body;
	static Map<String, Stat> body_sym;
	static Map<String, Stat> common;
	static Map<String, Stat> medicine;
	static Map<String, Stat> symptom;
	static Map<String, Stat> jiepou;
	static Map<String, Stat> zhongyi;
	static Map<String, Stat> med_all;
	
	public static void loadAllDict() throws Exception {
		t.remind("Loading dict...");
		disease = DictReadWrite.loadStatisticInStringStatMap(DictGallary.disease);
		body = DictReadWrite.loadStatisticInStringStatMap(DictGallary.organ);
		body_sym = DictReadWrite.loadStatisticInStringStatMap(DictGallary.organ_des);
		medicine = DictReadWrite.loadStatisticInStringStatMap(DictGallary.medicine);
//		common = DictReadWrite.loadStatisticInStringStatMap(DictGallary.other);
//		symptom = DictReadWrite.loadStatisticInStringStatMap(DictGallary.symptom);
//		jiepou = DictReadWrite.loadStatisticInStringStatMap(DictGallary.jiepou);
//		zhongyi = DictReadWrite.loadStatisticInStringStatMap(DictGallary.zhongyi);
//		med_all = DictReadWrite.loadStatisticInStringStatMap(DictGallary.med_all);
	}
	
	public static void saveAllDict() throws Exception {
		t.remind("Saving dict...");
		DictReadWrite.saveStatisticInStringStatMap(body, DictGallary.organ+".xls");
		DictReadWrite.saveStatisticInStringStatMap(body_sym, DictGallary.organ_des+".xls");
		DictReadWrite.saveStatisticInStringStatMap(medicine, DictGallary.medicine+".xls");
//		DictReadWrite.saveStatisticInStringStatMap(common, DictGallary.other+".xls");
//		DictReadWrite.saveStatisticInStringStatMap(symptom, DictGallary.symptom+".xls");
//		DictReadWrite.saveStatisticInStringStatMap(jiepou, DictGallary.jiepou+".xls");
//		DictReadWrite.saveStatisticInStringStatMap(zhongyi, DictGallary.zhongyi+".xls");
//		DictReadWrite.saveStatisticInStringStatMap(med_all, DictGallary.med_all+".xls");
	}
	
	static Trace t = new Trace();
	
	/*
	 * 统计
	 */
	public static void statisticInQuestionSet() throws Exception {
		
		MysqlConnection con  = new MysqlConnection(new DBConfig("jdbc:mysql://127.0.0.1:3308/ishc_data", "ishc", "West_ishc2013"));
		con.connect();
		ResultSet rs = con.query("select count(*) from TEMP_processed_question");
		rs.next();
		int total = rs.getInt(1), i=0;
		Trace tt = new Trace(total, total/1000);
		int start = 0, unit = 1000;
		BufferedWriter bw0 = new BufferedWriter( new FileWriter( DictGallary.stat_path+"body-sym.q.txt") );
		BufferedWriter bw1 = new BufferedWriter( new FileWriter( DictGallary.stat_path+"body.q.txt") );
//		total = 1000;
		while( start+unit <= total) {
			rs = con.query("select * from TEMP_processed_question limit "+start+", "+unit);
			start += unit;
			while(rs.next()) {
				String content = rs.getString("content");
				Word[][] ss = Platform.segment(content);
				int a = 0;
				StringBuffer sb = new StringBuffer();
				StringBuffer sb_other = new StringBuffer();
				for( Word[] s : ss) {
					boolean has_body = false;
					boolean has_sym = false;
					StringBuffer sb_body = new StringBuffer();
					for( Word w : s) {
						Stat temp = null;
						if( (temp=body.get(w.cont)) != null ) {
							a++;
							sb_body.append(w.cont+", ");
							has_body = true;
							temp.qi();
						};
						if( (temp=body_sym.get(w.cont)) != null ) {
							a++;
							sb_body.append(w.cont+", ");
							has_sym = true;
							temp.qi();
						};
						if( (temp=jiepou.get(w.cont)) != null ) {
							a++;
							sb_body.append("["+w.cont+"jiep], ");
							has_body = true;
							temp.qi();
						};
						if( (temp=symptom.get(w.cont)) != null ) {
							a++;
							sb_body.append("["+w.cont+"sym], ");
							has_sym = true;
							temp.qi();
						};
						if( (temp=disease.get(w.cont)) != null ) {
							a++;
							sb.append(w.cont+"jib, ");
							temp.qi();
						};
						if( (temp=common.get(w.cont)) != null ) {
							a++;
							sb.append(w.cont+"com, ");
							temp.qi();
						};
						if( (temp=zhongyi.get(w.cont)) != null ) {
							a++;
							sb.append(w.cont+"zhong, ");
							temp.qi();
						};
						if( (temp=medicine.get(w.cont)) != null ) {
							a++;
							sb.append(w.cont+"med, ");
							temp.qi();
						};
						if( (temp=med_all.get(w.cont)) != null ) {
							a++;
							temp.qi();
						};
					}
					sb_body.append(";;; ");
					sb_body.append(sb);
					if( has_body && has_sym )  {
						bw0.write(sb_body.toString() + " ---> " + Word.toSimpleString(s));
						bw0.newLine();
						bw0.newLine();
					}
					if (has_body) {
						bw1.write( sb_body.toString() + " ---> " + Word.toSimpleString(s) );
						bw1.newLine();
						bw1.newLine();
					}
				}
				tt.debug("processing question", true);
//				String s = sb.toString();
//				if(!s.isEmpty()) {
//					t.remind("detect keyword: "+s.toString());
//				}
			}
			bw0.flush();
			bw1.flush();
		}
		
		bw0.close();
		bw1.close();
		con.disconnect();
	}
	
	public static void statisticInAnswerSet() throws Exception {
		
		MysqlConnection con  = new MysqlConnection(new DBConfig("jdbc:mysql://127.0.0.1:3308/ishc_data", "ishc", "West_ishc2013"));
		con.connect();
		ResultSet rs = con.query("select count(*) from TEMP_processed_answer");
		rs.next();
		int total = rs.getInt(1), i=0;
		Trace tt = new Trace(total, total/1000);
		int start = 0, unit = 1000;
		BufferedWriter bw0 = new BufferedWriter( new FileWriter( DictGallary.stat_path+"body-sym.a.txt") );
		BufferedWriter bw1 = new BufferedWriter( new FileWriter( DictGallary.stat_path+"body.a.txt") );
//		total = 1000;
		while( start+unit <= total) {
			rs = con.query("select * from TEMP_processed_answer limit "+start+", "+unit);
			start += unit;
			while(rs.next()) {
				String content = rs.getString("content");
				Word[][] ss = Platform.segment(content);
				int a = 0;
				StringBuffer sb = new StringBuffer();
				for( Word[] s : ss) {
					boolean has_body = false;
					boolean has_sym = false;
					StringBuffer sb_body = new StringBuffer();
					for( Word w : s) {
						Stat temp = null;
						if( (temp=body.get(w.cont)) != null ) {
							a++;
							sb_body.append(w.cont+", ");
							has_body = true;
							temp.ai();
						};
						if( (temp=body_sym.get(w.cont)) != null ) {
							a++;
							sb_body.append(w.cont+", ");
							has_sym = true;
							temp.ai();
						};
						if( (temp=jiepou.get(w.cont)) != null ) {
							a++;
							sb_body.append("["+w.cont+"jiep], ");
							has_body = true;
							temp.ai();
						};
						if( (temp=symptom.get(w.cont)) != null ) {
							a++;
							sb_body.append("["+w.cont+"sym], ");
							has_sym = true;
							temp.ai();
						};
						if( (temp=disease.get(w.cont)) != null ) {
							a++;
							sb.append(w.cont+"jib, ");
							temp.ai();
						};
						if( (temp=common.get(w.cont)) != null ) {
							a++;
							sb.append(w.cont+"com, ");
							temp.ai();
						};
						if( (temp=zhongyi.get(w.cont)) != null ) {
							a++;
							sb.append(w.cont+"zhong, ");
							temp.ai();
						};
						if( (temp=medicine.get(w.cont)) != null ) {
							a++;
							sb.append(w.cont+"med, ");
							temp.ai();
						};
						if( (temp=med_all.get(w.cont)) != null ) {
							a++;
							temp.ai();
						};
					}
					
					if( has_body && has_sym )  {
						bw0.write(sb_body.toString() + " ---> " + Word.toPlainString(s));
						bw0.newLine();
						bw0.newLine();
					}
					if (has_body) {
						bw1.write( sb_body.toString() + " ---> " + Word.toPlainString(s) );
						bw1.newLine();
						bw1.newLine();
					}
				}
				tt.debug("processing answer", true);
//				String s = sb.toString();
//				if(!s.isEmpty()) {
//					t.remind("detect keyword: "+s.toString());
//				}
			}
			bw0.flush();
			bw1.flush();
		}
		
		bw0.close();
		bw1.close();
		con.disconnect();
	}
	
	public static void main(String[] args) throws Exception {
		loadAllDict();
		statisticInQuestionSet();
		System.out.println();
		System.out.println();
		statisticInAnswerSet();
		saveAllDict();
	}
	
}
