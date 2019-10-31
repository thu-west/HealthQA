package platform.db.database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import platform.GlobalSettings;

import com.mchange.v2.c3p0.DataSources;

public class IshcDB {
	
	private static DataSource ds;
	/**
	 * 初始化连接池代码块
	 */
	static {
		initDBSource();
	}

	/**
	 * 初始化c3p0连接池
	 */
	private static final void initDBSource() {
		Properties c3p0_properties = new Properties();
		try {
			// 加载配置文件
			c3p0_properties.load(new FileInputStream(GlobalSettings.contextDir("conf/c3p0.properties")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// 加载驱动类
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Properties jdbc_prop = new Properties();
		jdbc_prop.put("driverClass", "com.mysql.jdbc.Driver");
		jdbc_prop.put("jdbcUrl", GlobalSettings.database_url);
		jdbc_prop.put("user", GlobalSettings.database_username);
		jdbc_prop.put("password", GlobalSettings.database_password);

		try {
			// 建立连接池
			DataSource unPooled = DataSources.unpooledDataSource(
					GlobalSettings.database_url, jdbc_prop);
			ds = DataSources.pooledDataSource(unPooled, c3p0_properties);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取数据库连接对象
	 * 
	 * @return 数据连接对象
	 * @throws SQLException
	 */
	public static synchronized Connection getConnection() throws SQLException {
		final Connection conn = ds.getConnection();
		conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		return conn;
	}
	
	public static void main(String[] args) throws SQLException {
		Connection c = IshcDB.getConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("select count(*) from answer");
		rs.next();
		System.out.println(rs.getString(1));
	}

}
