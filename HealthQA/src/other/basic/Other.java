package other.basic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import platform.db.database.ISHCDBConfig;
import platform.db.database.ISHCDataOperator;
import platform.util.log.Trace;

public class Other {
	
	static void question_for_test() throws IOException, SQLException {
		ISHCDBConfig config = new ISHCDBConfig( "jdbc:mysql://127.0.0.1:3308/ishc_data",
				"root", "123qwe");
		ISHCDataOperator op = new ISHCDataOperator(config);
		ResultSet rs = op
				.query("select `ID`, `content` from `question` order by `ID` limit 5000");
		rs.last();
		int total_questions = rs.getRow();
		rs.beforeFirst();

		Trace t2 = new Trace(total_questions, total_questions / 10);
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"./io/off-questions.txt"));
		while (rs.next()) {
			t2.debug("loading questions", true);
			bw.write(rs.getString(1)+"\t"+rs.getString(2));
			bw.newLine();
		}
		bw.close();
	}
	
	
	public static void main(String[] args) throws IOException, SQLException {
//		question_for_test();
		
//		String a = "　　第一，饮食控制。　　第二，也是压力。　　糖尿病人可以";
//		System.out.println(a.replaceAll("\\p{Space}", "") + " -- " + (int)(a.charAt(0)));
		
		String s = "虿/n 　 　 Ｏ/x";
		for( int i=0;i<s.length();i++) {
			System.out.print((int)s.charAt(i)+""+s.charAt(i)+"--");
		}
	}

}
