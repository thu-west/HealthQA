package application.qav2.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import platform.GlobalSettings;
import platform.util.database.ValueTransfer;
import platform.util.log.Trace;

public class EntityMap {
	
	static final Trace t = new Trace().setValid(true, true);
	
	public static Connection sqlite = null;
	
	public static Connection getSQLiteConnection() throws SQLException {
		Connection c = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+GlobalSettings.contextDir("index/entitymap.db"));
	      c.setAutoCommit(false);
	      t.remind("Opened database successfully");
	    } catch ( Exception e ) {
	      t.error( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    return c;
	}
	
	static{
		try {
			sqlite = getSQLiteConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	Map<String, EntityAppearance> map;

	public EntityMap() {
		map = new HashMap<String, EntityAppearance>();
	}
	
	void addAll(Set<String> set, String str) {
		if(str==null || str.equals(""))
			return;
		String[] ns = str.split(";;");
		for(String n: ns) {
			set.add(n);
		}
	}
	
	public EntityMap(String[] entities) throws SQLException {
		map = new HashMap<String, EntityAppearance>();
		for(String e: entities) {
			if(map.containsKey(e)) continue;
			EntityAppearance ea = new EntityAppearance();
			Statement st = null;
			ResultSet rs = null;
			st = sqlite.createStatement();
			rs = st.executeQuery("select * from entity where name="
							+ ValueTransfer.SqlValueFor(e));
			if (rs.next()) {
				addAll(ea.piece, rs.getString("piece"));
				addAll(ea.neighbor, rs.getString("neighbor"));
				addAll(ea.answer, rs.getString("answer"));
				addAll(ea.context, rs.getString("context"));
				addAll(ea.qa, rs.getString("qa"));
			}
			rs.close();
			st.close();
			rs = null;
			st = null;
		}
	}
	
	void put(Entity entity, String id, String unit) throws SQLException {
		Statement st = sqlite.createStatement();
		String tname = ValueTransfer.SqlValueFor(entity.name);
		st.executeUpdate("UPDATE entity SET "+unit+" = "+unit+" || '"+id+";;', tag = tag || '"+entity.tag+";;' where name="+tname);
		st.executeUpdate("INSERT OR IGNORE INTO entity (name, "+unit+", tag) VALUES ("+tname+", '"+id+";;', '"+entity.tag+";;')");
		st.close();
		st =null;
	}

	public void putPiece(Entity entity, String id) throws SQLException {
		put(entity, id, "piece");
	}
	
	public void putNeighbor(Entity entity, String id) throws SQLException {
		put(entity, id, "neighbor");
	}

	public void putAnswer(Entity entity, String id) throws SQLException {
		put(entity, id, "answer");
	}

	public void putContext(Entity entity, String id) throws SQLException {
		put(entity, id, "context");
	}

	public void putQA(Entity entity, String id) throws SQLException {
		put(entity, id, "qa");
	}
	
	public void putTag(Entity entity) throws SQLException {
		put(entity, entity.tag, "tag");
	}

	public void toDB() throws SQLException {
		sqlite.commit();
	}
	
	public void clear() throws SQLException {
		Statement st1 = sqlite.createStatement();
		st1.executeUpdate("delete from entity");
		st1.executeUpdate("delete from sqlite_sequence");
		st1.close();
		st1 = null;
		sqlite.commit();
	}
	
	public void initializeFromFile() throws IOException, SQLException {
		File fff = new File("D:\\Project\\healthqa\\index\\entity");
		int total = 0;
		for( File ff : fff.listFiles() ) {
			for( File f: ff.listFiles() ) {
				total++;
			}
		}
		System.out.println(total);
		Trace tt  = new Trace(total, 100);
		for( File ff : fff.listFiles() ) {
			for( File f: ff.listFiles() ) {
				String name = f.getName();
				tt.debug(" --------- finished ", true);
//				t.debug("reading "+name);
				File[] fs  = f.listFiles();
				if(fs==null || fs.length<1){
					t.error("none: "+name);
					continue;
				}
				boolean flag = true;
				for(File u: f.listFiles()) {
					BufferedReader br = new BufferedReader(new FileReader(
							u));
					StringBuffer a = new StringBuffer();
					String line = null;
					while( (line=br.readLine()) != null ) {
						a.append(line).append(";;");
					}
					br.close();
					Statement st = sqlite.createStatement();
					String tname = ValueTransfer.SqlValueFor(name);
					String unit = u.getName();
					if(flag)
						st.executeUpdate("INSERT INTO entity (name, "+unit+") VALUES ("+tname+", '"+a.toString()+"')");
					else
						st.executeUpdate("UPDATE entity SET "+unit+" = '"+a.toString()+"' where name="+tname);
					flag = false;
					st.close();
					st =null;
				}
			}
			sqlite.commit();
		}
	}

	public static void main(String[] args) throws Exception {
		EntityMap em = new EntityMap();
//		em.test2();
		em.clear();
		em.initializeFromFile();
	}
}
