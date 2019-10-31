package knowledge.dbdict;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import platform.util.database.DBConfig;
import platform.util.database.MysqlConnection;
import platform.util.database.ValueTransfer;


public class QingboProDictToDB {
	
	MysqlConnection con;
	
	Pattern sym_p = Pattern.compile("^[ ]*(.*)[ ]*（(.*?)）[ ]*$");
	
	class Disease {
		String name;
		ArrayList<String> symptom = new ArrayList<String>();
		ArrayList<String> inspection = new ArrayList<String>();
		ArrayList<String> department = new ArrayList<String>();
		ArrayList<String> part = new ArrayList<String>();
		ArrayList<String> drug = new ArrayList<String>();
		String subject;
		String system;
		
		boolean IE() {
			return name==null;
		}
		
		void print() {
			if(IE()) {
				System.out.println("-----EMPTY------");
				System.out.println();
				return;
			}
			System.out.println("-----------------------");
			System.out.println(name+" / "+subject+" / "+system);
			for(String s:symptom  ) {
				System.out.print(s+" ;; ");
			}
			for(String s:inspection  ) {
				System.out.print(s+" ;; ");
			}
			System.out.println();
			for(String s:department  ) {
				System.out.print(s+" ;; ");
			}
			System.out.println();
			for(String s:part  ) {
				System.out.print(s+" ;; ");
			}
			System.out.println();
			for(String s:drug  ) {
				System.out.print(s+" ;; ");
			}
			System.out.println();
		}
	}
	
	String subject;
	String system;
	
	String C(String s) {
		return s.replaceAll("^[ ]+", "").replaceAll("[ ]+$", "");
	}
	
	ArrayList<Disease> readFile( String filename ) throws IOException {
		ArrayList<Disease> ds = new ArrayList<Disease>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = null;
		Disease d = new Disease();
		int type = 0;
		int count = 0;
		while( (line=br.readLine())!=null ) {
			count ++;
			line = line.replaceAll("^[ ]+", "").replaceAll("[ ]+$", "");
			if(line.startsWith("---")) {
				System.err.println("start with ---: "+line);
			}else if(line.startsWith("--")) {
				system = C(line.substring(2));
				continue;
			} else if(line.startsWith("-")) {
				subject = C(line.substring(1));
				continue;
			}
			d.subject = subject;
			d.system = system;
			if(C(line).length()==0) {
//				d.print();
				ds.add(d);
				type = 0;
				d = new Disease();
				continue;
			}
			if(line.contains("暂无"))
				continue;
			switch (type) {
			case 0:
				d.name = C(line);
				type = 1;
				break;
			case 1:
				if(!line.startsWith("症状")) {
					System.out.println("err at line"+count+" /"+line+"/");
					System.exit(0);
				}
				type = 2;
				break;
			case 2:
				if(C(line).length()==0) {
					System.out.println("err at line"+count+" /"+line+"/");
					System.exit(0);
				}
				else if(line.startsWith("检查项目"))
					type = 3;
				else
					d.symptom.add(C(line));
				break;
			case 3:
				if(C(line).length()==0) {
					System.out.println("err at line"+count+" /"+line+"/");
					System.exit(0);
				}
				else if(line.startsWith("科室"))
					type = 4;
				else
					d.inspection.add(C(line));
				break;
			case 4:
				if(C(line).length()==0) {
					System.out.println("err at line"+count+" /"+line+"/");
					System.exit(0);
				}
				else if(line.startsWith("部位"))
					type = 5;
				else
					d.department.add(C(line));
				break;
			case 5:
				if(C(line).length()==0) {
					System.out.println("err at line"+count+" /"+line+"/");
					System.exit(0);
				}
				else if(line.startsWith("药品"))
					type = 6;
				else
					d.part.add(C(line));
				break;
			case 6:
				if(C(line).length()==0) {
					System.out.println("err at line"+count+" /"+line+"/");
					type = 0;
				} else
					d.drug.add(C(line));
				break;
			}
		}
		br.close();
		return ds;
	}
	
	boolean E( String table, String name ) throws SQLException {
		ResultSet rs = con.query("select * from pro_"+table+" where `name`="+ValueTransfer.SqlValueFor(name));
		if(!rs.next()) {
			rs.close();
			return false;
		}
		return true;
	}
	
	void writeDB (ArrayList<Disease> ds ) throws Exception {
		con = new MysqlConnection(new DBConfig("jdbc:mysql://127.0.0.1:3308/MedDict", "root", "123qwe"));
		con.connect();
		for( Disease d : ds ) {
			if(d.IE())
				continue;
			
			// insert into pro_disease
			String dept = "";
			for(String s:d.department  ) {
				dept = s+",";
			}
			dept = dept.replaceAll(",$", "");
			String part = "";
			for(String s:d.part  ) {
				part = s+",";
			}
			part = part.replaceAll(",$", "");
//			System.out.println("\t"+dept+"  /  "+part);
			if(!E("disease", d.name))
				con.update("insert into pro_disease (`name`, `subject`, `system`, `dept`, `part`) values ( "
						+ ValueTransfer.SqlValueFor(d.name) + ", "
						+ ValueTransfer.SqlValueFor(d.subject) + ", "
						+ ValueTransfer.SqlValueFor(d.system) + ", "
						+ ValueTransfer.SqlValueFor(dept) + ", "
						+ ValueTransfer.SqlValueFor(part) + ")");
//			
			for(String s:d.symptom  ) {
				s = C(s);
				// insert into pro_symptom
				if(!E("symptom", s)) {
					con.update("insert into pro_symptom (`name`) values ("+ValueTransfer.SqlValueFor(s)+")");
				}
				// insert into pro_disease_symptom
				con.update("insert into pro_disease_symptom (`disease`, `symptom`) values ( "
						+ ValueTransfer.SqlValueFor(d.name) + ", "
						+ ValueTransfer.SqlValueFor(s) + ")");
			}
			
			for(String s:d.inspection  ) {
				
				String name = s, abbr = null;
				Matcher m = sym_p.matcher(s);
				if( m.find() ) {
					name = m.group(1);
					abbr = m.group(2);
				}
				
//				System.out.println(s+":\t"+name+"/"+abbr);
				
				// insert into pro_inspection
				if(!E("inspection", name)) {
					con.update("insert into pro_inspection (`name`, `abbr`) values ("
							+ ValueTransfer.SqlValueFor(name) + ", "
							+ ValueTransfer.SqlValueFor(abbr) + ")");
				}
				
				// insert into pro_disease_inspection
				con.update("insert into pro_disease_inspection (`disease`, `inspection`) values ( "
						+ ValueTransfer.SqlValueFor(d.name) + ", "
						+ ValueTransfer.SqlValueFor(name) + ")");
			}
			
			for(String s:d.drug  ) {

				String name = s, maker = null, source=null, alias=null;
				String[] t = s.split("[ ]*---[ ]*");
				name = t[0];
				try{
					source = t[1];
				} catch (Exception e ) {
					System.err.println("err when split by ---: "+s);
//					throw e;
				}
				name = C(name);
				String[] ss = name.split("[ ]*\\\\[ ]*");
				if(ss.length>2) {
					System.err.println("err when split: "+name);
				} else if(ss.length==2) {
					maker = ss[0];
					name = ss[1];
				}
//				int tt = name.indexOf(" ");
//				if(tt>-1) {
//					maker = name.substring(0, tt);
//					name = name.substring(tt+1);
//				}
				Matcher m = sym_p.matcher(name);
				if( m.find() ) {
					name = m.group(1);
					alias = m.group(2);
					alias = alias.replaceAll("[ ]*[，、]+[ ]*", ",");
				}
				
				
//				System.out.println(s+":\t"+name+"/"+maker+"/"+source+"/"+alias);
				// insert into pro_drug
				if(!E("drug", name)) {
					con.update("insert into pro_drug (`name`, `maker`, `source`, `alias`) values ("
							+ ValueTransfer.SqlValueFor(name) + ", "
							+ ValueTransfer.SqlValueFor(maker) + ", "
							+ ValueTransfer.SqlValueFor(source) + ", "
							+ ValueTransfer.SqlValueFor(alias) + ")");
				} else {
					if( maker != null )
						con.update("update pro_drug set `maker` = concat(ifnull(maker,\"\"), ',"+maker+"') where `name`=\""+name+"\"");
				}
				
				// insert into pro_disease_inspection
				con.update("insert into pro_disease_drug (`disease`, `drug`) values ( "
						+ ValueTransfer.SqlValueFor(d.name) + ", "
						+ ValueTransfer.SqlValueFor(name) + ")");
			}
		}
	}
	
	void test (ArrayList<Disease> ds ) {
		int max_len = 0;
		for( Disease d : ds ) {
			if(d.IE())
				continue;
			for( String s : d.inspection ) 
				max_len = max_len<s.length() ? s.length() : max_len;
		}
		System.out.println(max_len);
	}
	
	void test2() {
		Matcher m = sym_p.matcher(" 阴离子间隙 （A2） ");
		if(m.find()) {
			System.out.println(m.group(0));
			System.out.println(m.group(1));
			System.out.println(m.group(2));
		}
	}
	
	public static void main(String[] args) throws Exception {
		QingboProDictToDB dtd = new QingboProDictToDB();
//		dtd.test(dtd.readFile("C:\\Users\\Fire\\Desktop\\OrigDict"));
//		dtd.test2();
		dtd.writeDB(dtd.readFile("C:\\Users\\Fire\\Desktop\\Professional Dictionary\\OrigDict0408.txt"));
	}

}
