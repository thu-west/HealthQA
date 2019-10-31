package platform.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import platform.GlobalSettings;
import platform.util.log.Trace;

public class MyProperties extends Properties {
	
	static final Trace t = new Trace().setValid(false, true);
	
	public MyProperties(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(GlobalSettings.contextDir("conf/healthqa.properties"))));
		super.load(br);
		br.close();
	}
	
	public int getInt(String key) throws Exception {
		return Integer.parseInt(super.getProperty(key));
	}
	
	public float getFloat(String key) throws Exception {
		return Float.parseFloat(super.getProperty(key));
	}

	public String getString(String key) throws Exception {
		return super.getProperty(key);
	}
	
	public void print() {
		t.remind(super.toString());
	}
	
}
