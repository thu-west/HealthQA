package platform.util.exception;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class DatabaseException extends Exception{
	String command;
	String type;
	static HashSet<String> list;
	static {
		list = new HashSet<String>();
	}
	
	public DatabaseException(String _command, String _type) {
		super(_type+" ["+_command+"]");
		type = _type;
		command = _command;
		list.add(_command+" ["+_type+"]");
	}
	
	public static void save(String filename){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			Iterator<String> i = list.iterator();
			while(i.hasNext()){
				bw.write(i.next());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
