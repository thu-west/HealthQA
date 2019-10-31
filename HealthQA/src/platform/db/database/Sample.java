package platform.db.database;

import java.math.BigInteger;
import java.sql.ResultSet;

import org.json.JSONObject;

import platform.db.dao.ArticleDAO;


public class Sample {

	public static void deleteSample(){
		
		System.out.println("\n---------delete sample---------\n");
		
		/*
		 * 连接crawled_data数据库
		 */
		ISHCDataOperator op = new ISHCDataOperator();
		
		try{
			/*
			 * 删除101命名空间下，相对ID处于区间[23,100]的记录
			 */
			op.delete(new ArticleDAO(), "101", "23", "1000");
			
			/*
			 * 一定是在所有插入操作完成之后调用！执行缓存中剩下的指令。
			 */
			op.emptyCache();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			/*
			 * 必须执行，以便能够在程序异常终止时，进入到finally语句，输出当前命令生成的情况，以便下一次从此处开始。
			 * 【因为采用了batch写入机制，最终可能有一部分命令还保存在cache中，最后必须要执行下cache中的命令】
			 */
			ISHCDataOperator.printTerminatePoint();
		}
		
		/*
		 * 关闭数据库的连接
		 */
		op.disconnect();
	}
	
	
	
	public static void insertSample(){
		
		System.out.println("\n---------insert sample---------\n");
		
		/*
		 * 连接crawled_data数据库
		 */
		ISHCDataOperator op = new ISHCDataOperator();
		
		/*
		 * 设置root，命名空间和起始ID（其实真正计数是从ID+1开始），root和命名空间对同一网站而言，所有文章都一样，所以在这里一并设置。
		 */
		ArticleDAO.configure("糖尿病", "101", "23");
		
		try{
			/*
			 * ArticleDAO类是数据库访问对象，你只需要设置ArticleDAO对象就行，不需要关心数据库底层是怎么存储的。
			 */
			ArticleDAO a = new ArticleDAO();
			
			/*
			 * 设置ArticleDAO的url和path，具体说明见uniform data format.xls（以后简称udf）
			 */
			a.setPosition("http://www.baidu.com", "首页>你好>haha");
			
			/*
			 * 设置ArticleDAO的元信息，具体说明见udf和setMetadata的参数表
			 */
			a.setMetadata(null, "2013-9-11", "11:9:003", "预防fdfas治疗",
					"你好;;哈fdsfa;哈;;我靠", "www.baidu.com/134", "糖尿病治疗,ohyeah");
			
			/*
			 * 设置ArticleDAO的内容，内容存储是JSON格式，你只需要关心怎么添加一对key-value即可，具体说明见udf-①
			 */
			a.setAddContent("治疗情况", "嘿嘿");
			a.setAddContent("得到帮助", "我爱卡,jfadjslfadsj,，djaflj \"索拉卡的经费我");
			
			/*
			 * ArticleDAO对象a已经赋值完成，只要调用ISHCDataOperator对象op的insert函数插入到数据库即可。
			 */
			op.insert(a);
			
			/*
			 * 同上。
			 */
			op.emptyCache();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			/*
			 * 同上。
			 */
			ISHCDataOperator.printTerminatePoint();
		}
		
		/*
		 * 同上。
		 */
		op.disconnect();
	}
	
	
	
	public static void querySample(){
		
		System.out.println("\n---------query sample---------\n");
		
		/*
		 * 同上。
		 */
		ISHCDataOperator op = new ISHCDataOperator();
		
		/*
		 * 直接执行一条查询语句
		 */
		ResultSet rs = op.query("select * from article");   // 直接使用命令查询
		
		try{
			/*
			 * 遍历结果集的方法：调用next()，将游标指向下一个记录。
			 */
			rs.next();
			
			/*
			 * 如果游标指向最后一条记录的后面，那么代表结果集遍历完成
			 */
			while(!rs.isAfterLast()){
				
				/*
				 * 获取记录的content字段的内容
				 */
				String c = rs.getString("content");
				
				/*
				 * content是以json字符串存储在数据库中，读出来之后，需要转换成JSON对象
				 */
				JSONObject content = new JSONObject(c);
				
				/*
				 * JSONObject的获取键值的方法 
				 */
				System.out.println(content.get("得到帮助"));
				
				/*
				 * JSONObject转换成json字符串的方法
				 */
				System.out.println(content.toString());
				
				/*
				 * 结果集遍历，将游标指向下一个记录。
				 */
				rs.next();
			}
			
			/*
			 * 将结果集游标恢复到初始位置
			 */
			rs.beforeFirst();    //将ResultSet指针回位。
			
			/*
			 * 同上。
			 */
			rs.next();
			
			while(!rs.isAfterLast()){
				
				/*
				 * 获取BigInteger值的方法，需要使用BigDecimal->BigInteger的转换函数。
				 */
				BigInteger id = rs.getBigDecimal("ID").toBigInteger();
				System.out.println(id.toString());
				
				rs.next();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		/*
		 * 同上。
		 */
		op.disconnect();
	}
	
	
	
	/*
	 * 执行任意一条update类型的语句，包括insert, delete, update。本函数涉及到底层数据库的结构，所以不建议使用。
	 */
	public static void updateSample(){
		
		System.out.println("\n---------update sample---------\n");
		
		/*
		 * 同上。
		 */
		ISHCDataOperator op = new ISHCDataOperator();
		
		/*
		 * 这是一条update类型的指令，这里是insert语句，当然也可以是delete和update语句
		 */
		String cmd = "insert into article (`ID`,`url`,`root`,`title`,`content`) values (1237,\"da\",\"da\",\"da\",\"da\")";
		
		/*
		 * 执行这条指令
		 */
		op.update(cmd);
		
		/*
		 * 同上。
		 */
		op.disconnect();
	}
	
	
	
	public static void main(String[] args) {
		deleteSample();
		insertSample();
		querySample();
		updateSample();
	}
}
