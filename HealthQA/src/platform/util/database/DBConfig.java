package platform.util.database;

public class DBConfig {
	/*
	 * database parameter
	 */
	public String url;
	public String username;
	public String password;
	
	public DBConfig(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public DBConfig() {
	}
}
