package application.qav2.process_data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import application.qav2.ao.Answer;
import application.qav2.ao.Question;
import application.qav2.process_data.Transform.QAPair;
import platform.db.database.IshcDB;
import platform.util.database.ValueTransfer;
import platform.util.log.Trace;

public class Preprocess {
	
	static final Trace t = new Trace().setValid(true, true);
	
	static String preprocessQuestionContent( String content, String root ) {
		content = DBContentUtil.preprocess_common(DBContentUtil.json_to_plaintext(content));
		content = DBContentUtil.preprocess_specified_questions(content, root);
		if( content==null )
			return "";
		content = DBContentUtil.preprocess_common(content);
		return content;
	}
	
	static String preprocessAnswerContent(String content, String reply_id) {
		content = DBContentUtil.json_to_plaintext(content);
		content = DBContentUtil.preprocess_common(content);
		content = DBContentUtil.preprocess_specified_answer(content, reply_id);
		if(content==null)
			return "";
		content = DBContentUtil.preprocess_common(content);
		return content;
	}
	
	static void fetch (boolean with_stop) throws Exception {
		
		int shutdown_flag = 1000;
		
		Connection c = IshcDB.getConnection();
		
		Statement countst = c.createStatement();
		ResultSet countrs = countst.executeQuery("select count(*) from `post`  where "
					+ " `transformed` = 0 "
					+ " and (`root` = 'ask.39.net'"
					+ " or `root` = 'club.xywy.com'"
					+ " or `root` = '120ask.com' )"
					);
		countrs.next();
		int total = countrs.getInt(1);
		t.remind("total remain records(questions) : "+total);
		countrs.close();
		countst.close();
		
		int start = 0;
		int batch = 50;
//		int total = 1266230;
		Trace t3 = new Trace(total, 5);
		while(!with_stop || shutdown_flag>0) {
			Statement stat = c.createStatement();
			t.remind("fetching data start @ "+start+" + "+batch);
			Thread.sleep(3000);
			t.remind("begin to fetch");
			ResultSet rs_q = stat.executeQuery("select `ID`, `title`, `content`, `root` from `post`  where "
					+ " `transformed` = 0 "
					+ " and (`root` = 'ask.39.net'"
					+ " or `root` = 'club.xywy.com'"
					+ " or `root` = '120ask.com' )"
					+ " order by `ID`"
//					+ " limit "+start+", "+batch
					+ " limit "+batch
					);
			start += batch;
			t.remind("fetch done");
	
			while( rs_q.next() ) {
				shutdown_flag --;
				t3.debug("process result set", true);
				String id = rs_q.getString(1);
				String title = rs_q.getString(2);
				String content = rs_q.getString(3);
				String root = rs_q.getString(4);
				content = preprocessQuestionContent(content, root);
				if(content.isEmpty()) {
					Statement s = c.createStatement();
					s.executeUpdate("update `post` set `transformed`=100 where `ID`="+rs_q.getString("ID"));
					s.close();
					continue;
				}
//				t.debug("fetching answers");
				Statement stat1 = c.createStatement();
				ResultSet rs_a = stat1.executeQuery("select `ID`, `reply_ID`, `content` from `reply_post` where reply_ID="+ValueTransfer.SqlValueFor(id));
				QAPair qa = new QAPair();
				qa.q = new Question(id, title, content);
				while(rs_a.next()) {
					String aid = rs_a.getString("ID");
					String arid = rs_a.getString("reply_ID");
					String acontent = rs_a.getString("content");
					acontent = preprocessAnswerContent(acontent, arid);
					if(acontent.isEmpty()) {
						System.err.println("Become empty: "+rs_a.getString("content"));
						continue;
					}
					qa.alist.add(new Answer(aid, arid, acontent, qa.q));
				}
				rs_a.close();
				stat1.close();
				try {
					Transform.transform(qa);
					Statement s = c.createStatement();
					s.executeUpdate("update `post` set `transformed`=1 where `ID`="+rs_q.getString("ID"));
					s.close();
				} catch (Exception e) {
					e.printStackTrace();
					Statement s = c.createStatement();
					s.executeUpdate("update `post` set `transformed`=100 where `ID`="+rs_q.getString("ID"));
					s.close();
				}
//				t.remind("writing entity map into file");
//				Transform.entity_map.toFile();
				Transform.entity_map.toDB();
			}
			rs_q.close();
			stat.close();
		}
	}

	public static void main(String[] args) throws Exception {
		fetch(true);
	}
	
}
