package platform.util;

import java.io.*;
import org.json.*;

import platform.util.log.Trace;

public class JSONConf {

	Trace t = new Trace();

	private JSONObject conf = null;

	public JSONConf(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = null;
		StringBuffer json_str = new StringBuffer();
		while ((line = br.readLine()) != null) {
			json_str.append(line.replaceAll("^[\\s]*#.*", "").replaceAll("^[\\s]+", ""));
		}
		conf = new JSONObject(json_str.toString());
		br.close();
	}

	public JSONConf(JSONObject object) {
		this.conf = object;
	}

	public boolean isValid() {
		return conf != null;
	}

	public int getInt(String key) throws Exception {
		if (!isValid())
			throw new Exception("JSONConf: Not initialized the conf object");
		return conf.getInt(key);
	}

	public String getString(String key) throws Exception {
		if (!isValid())
			throw new Exception("JSONConf: Not initialized the conf object");
		return conf.getString(key);
	}

	public JSONConf getNestedConf(String key) throws Exception {
		if (!isValid())
			throw new Exception("JSONConf: Not initialized the conf object");
		return new JSONConf(conf.getJSONObject(key));
	}

	public void print() {
		t.remind(conf.toString(2));
	}
	
	public String toString(int indentFactor) {
		return conf.toString(indentFactor);
	}
	
	@Override
	public String toString() {
		return conf.toString(2);
	}
	
	public static void main(String[] args) throws Exception {
		new JSONConf("D:\\tripadvisor.conf").print();
	}
}
