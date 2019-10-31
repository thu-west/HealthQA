package platform.db.dao;

public interface DAO {
	abstract public String[] insert();
	abstract public String getTableName();
	abstract public String getTypeNamespace();
	abstract public String[] delete(String website_namespace, String start_ID, String end_ID);
}
