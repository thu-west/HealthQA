package other;

public class DBPreprocess {
	
//	static Trace t = new Trace(); 
//	static LTPJNI ltp = null;
//	
//	public static void transferPostToQuestion () throws SQLException {
//		
//		ISHCDBConfig config = new ISHCDBConfig(
//				GlobalSettings.database_url,
//				GlobalSettings.database_username, GlobalSettings.database_password);
//		config.sql_cache_size = 1000;
//		ISHCDataOperator op = new ISHCDataOperator(config);
//		
////		t.debug("deleting old data");
////		op.update("delete from `question`");
//		
//		t.debug("fetching data");
//		ResultSet rs = op.query("select `ID`, `title`, `content`, `root` from `post`  where"
//				+ " `root` = 'ask.39.net'"
//				+ " or `root` = 'club.xywy.com'"
//				+ " or `root` = '120ask.com'"
//				+ " order by `ID`"
////				+ " limit 10110, 100"
//				);
//		int total = rs.getRow();
//		t.debug("total record: "+total);
//		rs.beforeFirst();
//
//		Trace t2 = new Trace( total, total/100 );
//		while( rs.next() ) {
//			t2.debug("process result set", true);
//			String id = rs.getString(1);
//			String title = rs.getString(2);
//			String content = rs.getString(3);
//			String root = rs.getString(4);
//			String origin_content = DBContentUtil.json_to_plaintext(content);
//			if( title.length()>3 && !origin_content.contains( title.substring(0, title.length()-2)))
//				title = DBContentUtil.preprocess_common(title);
//			else
//				title = "";
//			content = DBContentUtil.preprocess_common(DBContentUtil.json_to_plaintext(content));
//			content = title + content;
//			content = DBContentUtil.preprocess_specified_questions(content, root);
//			if( content==null )
//				continue;
//			content = DBContentUtil.preprocess_common(content);
//			if( content.isEmpty() )
//				continue;
//			op.update("insert into `question` values ("+id+", "+ValueTransfer.SqlValueFor( content )+")");
//		}
//		op.emptyCache();
//		op.disconnect();
//	}
//	
//	public static void transferReplyPostToAnswer () throws SQLException {
//		ISHCDBConfig config = new ISHCDBConfig(
//				GlobalSettings.database_url,
//				GlobalSettings.database_username, GlobalSettings.database_password);
//		config.sql_cache_size = 1000;
//		ISHCDataOperator op = new ISHCDataOperator(config);
//		
////		t.debug("deleting old data");
////		op.update("delete from `answer`");
//		
//		t.debug("fetching data");
//		/* reply_ID range:
//		 * 00040100000000000001 : ask.39.net
//		 * 00050100000000000001 : club.xywy.com
//		 * 00080100000000000001 : 120ask.com  */
//		ResultSet rs = op.query("select `ID`, `reply_ID`, `content` from `reply_post`  where "
//				+ " (reply_ID >= 40100000000000000 and reply_ID <= 40199999999999999 )"
//				+ " or (reply_ID >= 50100000000000000 and reply_ID <= 50199999999999999 )"
//				+ " or (reply_ID >= 80100000000000000 and reply_ID <= 80199999999999999 ) "
//				+ " order by `ID`"
//				+ "");
//		rs.last();
//		int total = rs.getRow();
//		t.debug("total record: "+total);
//		rs.beforeFirst();
//
//		Trace t2 = new Trace( total, total/100 );
//		while( rs.next() ) {
//			t2.debug("process result set", true);
//			String id = rs.getString(1);
//			String reply_id = rs.getString(2);
//			String content = rs.getString(3);
//			content = DBContentUtil.json_to_plaintext(content);
//			content = DBContentUtil.preprocess_common(content);
//			content = DBContentUtil.preprocess_specified_answer(content, reply_id);
//			if(content==null)
//				continue;
//			content = DBContentUtil.preprocess_common(content);
//			if( content.isEmpty() ){
//				continue;
//			}
//			op.update("insert into `answer` values ("+id+", "+reply_id+", "+ValueTransfer.SqlValueFor( content )+")");
//		}
//		op.emptyCache();
//		op.disconnect();
//	}
//	
//	public static void preSegmentQuestion () throws SQLException {
//		if( ltp == null ){
//			ltp = new LTPJNI();
//			ltp.initNER();
//		}
//		
//		ISHCDBConfig config = new ISHCDBConfig(
//				GlobalSettings.database_url,
//				GlobalSettings.database_username, GlobalSettings.database_password);
//		config.sql_cache_size = 1000;
//		ISHCDataOperator op = new ISHCDataOperator(config);
//		
////		t.debug("deleting old data");
////		op.update("delete from `question_after_seg`");
//		
//		t.debug("fetching data");
//		ResultSet rs = op.query("select `ID`, `content` from `question` order by `ID`");
//		rs.last();
//		int total = rs.getRow();
//		t.debug("total record: "+total);
//		rs.beforeFirst();
//		
//		Trace t2 = new Trace( total, total/100 );
//		while( rs.next() ) {
//			t2.debug("process result set", true);
//			String id = rs.getString(1);
//			String content = rs.getString(2);
//			String seg_content = ltp.segNer( content );
//			op.update("insert into `question_after_seg` values ("+id+", "+ValueTransfer.SqlValueFor( seg_content )+")");
//		}
//		op.emptyCache();
//		op.disconnect();
//	}
//	
//	public static void preSegmentAnswer () throws SQLException {
//		if( ltp == null ){
//			ltp = new LTPJNI();
//			ltp.initNER();
//		}
//		
//		ISHCDBConfig config = new ISHCDBConfig(
//				GlobalSettings.database_url,
//				GlobalSettings.database_username, GlobalSettings.database_password);
//		config.sql_cache_size = 1000;
//		ISHCDataOperator op = new ISHCDataOperator(config);
//		
////		t.debug("deleting old data");
////		op.update("delete from `answer_after_seg`");
//		
//		t.debug("fetching data");
//		
//		ResultSet rs = op.query("select `ID`, `reply_ID`, `content` from `answer`"
//				+ " where ID>80300000000067299"
//				+ " order by `ID`");
//		rs.last();
//		int total = rs.getRow();
//		t.debug("total record: "+total);
//		rs.beforeFirst();
//
//		Trace t2 = new Trace( total, total/100 );
//		while( rs.next() ) {
//			t2.debug("process result set", true);
//			String id = rs.getString(1);
//			String reply_id = rs.getString(2);
//			String content = rs.getString(3);
//			String seg_content = ltp.segNer( content );
//			op.update("insert into `answer_after_seg` values ("+id+", "+reply_id+", "+ValueTransfer.SqlValueFor( seg_content )+")");
//		}
//		op.emptyCache();
//		op.disconnect();
//	}
//	
//	public static void main(String[] args) throws SQLException {
////		transferPostToQuestion();
////		transferReplyPostToAnswer();
////		preSegmentQuestion();
//		preSegmentAnswer();
//	}

}
