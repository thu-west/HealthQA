package platform.db.database;

import java.math.BigInteger;
import java.sql.SQLException;

import platform.db.dao.*;


public class Test {
	void testUserInsert(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			UserDAO u = new UserDAO();
			UserDAO.configure("糖尿病网", "101", "120");
			u.setBaseInfo("M", 15, "beijing", null, null);
			u.setUrl("www.user.com");
			u.setName("nihao");
			op.insert(u);
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testUserDelete(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			op.delete(new UserDAO(), "101", null, null);
			op.emptyCache();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testUserTianmijiayuanInsert(){
		ISHCDataOperator op = new ISHCDataOperator();
		UserTianmijiayuanDAO ut = new UserTianmijiayuanDAO();
		try{
			UserDAO.configure("usertianmijiayuan.com", "101", "19");
			ut.setUrl("tian.com/haha");
			ut.setName("nihao");
			ut.setBaseInfo(null, -1, null, null, null);
			ut.setExtendInfo(12, 13, 4, 6, 7, 8, 9, 20, 9, 8, 9, 7, 9);
			ut.setFriends("nihao;;haha");
			ut.setVisitors("ni;;wo;;ta");
			op.insert(ut);
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testUserTianmijiayuanDelete(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			op.delete(new UserTianmijiayuanDAO(), "101", null, "19");
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testUserTianyaInsert(){
		ISHCDataOperator op = new ISHCDataOperator();
		UserTianyaDAO ut = new UserTianyaDAO();
		try{
			UserDAO.configure("usertianmijiayuan.com", "101", "19");
			ut.setUrl("tian.com/haha");
			ut.setName("nihao");
			ut.setBaseInfo(null, -1, null, null, null);
			ut.setExtendInfo(12, 13, 4, 6);
			ut.setFriends("nihao;;haha");
			ut.setVisitors("ni;;wo;;ta");
			op.insert(ut);
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testUserTianyaDelete(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			op.delete(new UserTianyaDAO(), "101", null, "19");
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testArticleInsert(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			ArticleDAO a = new ArticleDAO();
			ArticleDAO.configure("article.com", "101", "45");
			a.setPosition("haha.url", "fad>d>dfa");
			a.setMetadata(null, "1990-1-1", null, "haha", "a;;b;;c", null, "title of article");
			a.setAddContent("全文", "quanwen");
			op.insert(a);
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
	}
	
	void testArticleDelete(){
		ISHCDataOperator op = new ISHCDataOperator();
		try {
			op.delete(new ArticleDAO(), "101", "1", null);
			op.emptyCache();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ISHCDataOperator.printTerminatePoint();
		}
	}
	
	void testPostInsert(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			PostDAO p = new PostDAO();
			PostDAO.configure("post.com", "101", "45");
			p.setPosition("haha.url", "fad>d>dfa");
			p.setMetadata(null, "1990-1-2", "12:00:99", "haha", "nihao", "title");
			p.setAddContent("全文", "quanwen");
			op.insert(p);
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
	}
	
	void testPostDelete(){
		ISHCDataOperator op = new ISHCDataOperator();
		try {
			op.delete(new PostDAO(), "101", "1", null);
			op.emptyCache();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ISHCDataOperator.printTerminatePoint();
		}
	}
	
	void testReplyArticleInsert(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			ReplyArticleDAO ra = new ReplyArticleDAO();
			ReplyArticleDAO.configure("101", "34");
			ra.setReplyObject(new BigInteger("01010000000000000045"), null);
			ra.setMetadata(null, "1990-09-3", "12:0:00");
			ra.setStatistic(12, 12);
			ra.setAddContent("你好", "呵呵呵呵呵");
			ra.setAddContent("wokao", "yingyu");
			op.insert(ra);
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testReplyArticleDelete(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			op.delete(new ReplyArticleDAO(), "101", null, null);
			op.emptyCache();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testReplyPostInsert(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			ReplyPostDAO ra = new ReplyPostDAO();
			ReplyPostDAO.configure("101", "34");
			ra.setReplyObject(new BigInteger("01010100000000000045"), null);
			ra.setMetadata(null, "1990-09-3", "12:0:00", null);
			ra.setStatistic(12, 12);
			ra.setAddContent("你好", "呵呵呵呵呵");
			ra.setAddContent("wokao", "yingyu");
			op.insert(ra);
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testReplyPostDelete(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			op.delete(new ReplyPostDAO(), "101", null, null);
			op.emptyCache();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testVideoInsert(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			VideoDAO v = new VideoDAO();
			VideoDAO.configure("video.com", "101", "34");
			v.setPosition(null, null);
			v.setMetadata(null, "1990-1-1", null, null, null, null, "wokao", null, -1);
			op.insert(v);
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
	}
	
	void testVideoDelete(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			op.delete(new VideoDAO(), "101", null, null);
			op.emptyCache();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testUserBaidutiebaInsert(){
		ISHCDataOperator op = new ISHCDataOperator();
		UserBaidutiebaDAO ut = new UserBaidutiebaDAO();
		try{
			UserDAO.configure("baidutieba.com", "101", "19");
			ut.setUrl("baidutieba.com/haha");
			ut.setName("nihao");
			ut.setBaseInfo(null, -1, null, null, null);
			//ut.setExtendInfo(_like_bars_count, _fans_count, _zhidao_reply, _zhidao_accepted, _zhidao_grade, _space_arcticle_count, _space_fans_count)
			ut.setExtendInfo(12, 13, 6, 0.2f, 8, 9, 10);
			op.insert(ut);
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testUserBaidutiebaDelete(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			op.delete(new UserBaidutiebaDAO(), "101", null, "19");
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	
	void testUser39doctorInsert(){
		ISHCDataOperator op = new ISHCDataOperator();
		User39doctorDAO ut = new User39doctorDAO();
		try{
			UserDAO.configure("user39doctor.com", "110", "19");
			ut.setUrl("tian.com/haha");
			ut.setName("nihao");
			ut.setBaseInfo(null, -1, null, null, null);
			ut.setDoctorInfo("major", "fdajsl", "fadsf", "haha");
			ut.setFamousDoctorInfo("yiyuan", null, "nihao", "home");
			op.insert(ut);
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	void testUser39doctorDelete(){
		ISHCDataOperator op = new ISHCDataOperator();
		try{
			op.delete(new User39doctorDAO(), "110", null, "19");
			op.emptyCache();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			ISHCDataOperator.printTerminatePoint();
		}
		op.disconnect();
	}
	
	public static void main(String[] args){
		Test t = new Test();
		
		/*
		 * 测试用户表的执行
		 */
//		t.testUserInsert();
//		t.testUserDelete();
		
		/*
		 * 测试用户扩展表的运行（使用到级联删除）
		 */
//		t.testUserTianmijiayuanInsert();
//		t.testUserTianmijiayuanDelete();
		
		/*
		 * 测试reply?DAO的运行状况
		 */
//		t.testPostDelete();
//		t.testArticleDelete();
//		t.testReplyArticleDelete();
//		t.testReplyPostDelete();
//		t.testArticleInsert();
//		t.testReplyArticleInsert();
//		t.testPostInsert();
//		t.testReplyPostInsert();
		
		/*
		 * 测试文章和评论，主贴和跟帖的级联删除
		 */
//		t.testPostDelete();
//		t.testArticleDelete();
		
		/*
		 * 测试user_tianya
		 */
//		t.testUserTianyaInsert();
//		t.testUserTianyaDelete();
		
		/*
		 * 测试user_baidutieba
		 */
//		t.testUserBaidutiebaInsert();
//		t.testUserBaidutiebaDelete();
		
		/*
		 * 测试user_39doctor
		 */
//		t.testUser39doctorInsert();
		t.testUser39doctorDelete();
	}
}
