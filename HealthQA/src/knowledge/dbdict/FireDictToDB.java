package knowledge.dbdict;

import knowledge.DictGallary;
import knowledge.DictReadWrite;

import java.util.*;
import platform.util.database.DBConfig;
import platform.util.database.MysqlConnection;
import platform.util.database.ValueTransfer;
import platform.util.log.Trace;

public class FireDictToDB {
	
	static Trace t = new Trace().setValid(true, true);
	
	MysqlConnection con;
	
	public FireDictToDB() {
		con = new MysqlConnection(new DBConfig("jdbc:mysql://127.0.0.1:3308/MedDict", "root", "123qwe"));
		con.connect();
	}
	
	public void  toDB () throws Exception {
		Set<String> set = null;
		String dict_name = null;
		String dict_table = null;

		dict_name = DictGallary.disease;
		dict_table = "disease";
		t.remind("writing "+dict_table);
		set = DictReadWrite.loadDictInStringSet(dict_name);
		for(String name : set ) {
			con.update("insert into fire_"+dict_table+" (`name`) values ( "
					+ ValueTransfer.SqlValueFor(name) + ")");
		}

		dict_name = DictGallary.medicine;
		dict_table = "drug";
		t.remind("writing "+dict_table);
		set = DictReadWrite.loadDictInStringSet(dict_name);
		for(String name : set ) {
			con.update("insert into fire_"+dict_table+" (`name`) values ( "
					+ ValueTransfer.SqlValueFor(name) + ")");
		}

		dict_name = DictGallary.food;
		dict_table = "food";
		t.remind("writing "+dict_table);
		set = DictReadWrite.loadDictInStringSet(dict_name);
		for(String name : set ) {
			con.update("insert into fire_"+dict_table+" (`name`) values ( "
					+ ValueTransfer.SqlValueFor(name) + ")");
		}

		dict_name = DictGallary.treat;
		dict_table = "treat";
		t.remind("writing "+dict_table);
		set = DictReadWrite.loadDictInStringSet(dict_name);
		for(String name : set ) {
			con.update("insert into fire_"+dict_table+" (`name`) values ( "
					+ ValueTransfer.SqlValueFor(name) + ")");
		}
		

		
		dict_name = DictGallary.indicator;
		dict_table = "indicator";
		t.remind("writing "+dict_table);
		set = DictReadWrite.loadDictInStringSet(dict_name);
		for(String name : set ) {
			con.update("insert into fire_"+dict_table+" (`name`) values ( "
					+ ValueTransfer.SqlValueFor(name) + ")");
		}

		dict_name = DictGallary.check;
		dict_table = "check";
		t.remind("writing "+dict_table);
		set = DictReadWrite.loadDictInStringSet(dict_name);
		for(String name : set ) {
			con.update("insert into fire_"+dict_table+" (`name`) values ( "
					+ ValueTransfer.SqlValueFor(name) + ")");
		}

		dict_name = DictGallary.equipment;
		dict_table = "equipment";
		t.remind("writing "+dict_table);
		set = DictReadWrite.loadDictInStringSet(dict_name);
		for(String name : set ) {
			con.update("insert into fire_"+dict_table+" (`name`) values ( "
					+ ValueTransfer.SqlValueFor(name) + ")");
		}

		dict_name = DictGallary.organ;
		dict_table = "organ";
		t.remind("writing "+dict_table);
		set = DictReadWrite.loadDictInStringSet(dict_name);
		for(String name : set ) {
			con.update("insert into fire_"+dict_table+" (`name`) values ( "
					+ ValueTransfer.SqlValueFor(name) + ")");
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		new FireDictToDB().toDB();
		
	}

}
