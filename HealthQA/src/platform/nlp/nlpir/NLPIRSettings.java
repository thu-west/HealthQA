package platform.nlp.nlpir;

import platform.GlobalSettings;
import platform.util.log.Trace;

public class NLPIRSettings {
	
	static Trace t = new Trace().setValid(true, true);
	
	static String NLPIR_DLL_SO = GlobalSettings.contextDir("lib/ICTCLAS2015/lib/win64/NLPIR");
	
	static String NLPIR_ROOT = GlobalSettings.contextDir("lib/ICTCLAS2015");
	
	static {
		if( GlobalSettings.OS.equals("win32") ) {
			NLPIR_DLL_SO = GlobalSettings.contextDir("lib/ICTCLAS2015/lib/win32/NLPIR");
		} else if( GlobalSettings.OS.equals("win64") ) {
			NLPIR_DLL_SO = GlobalSettings.contextDir("lib/ICTCLAS2015/lib/win64/NLPIR");
		} else if( GlobalSettings.OS.equals("linux32") ) {
			NLPIR_DLL_SO = GlobalSettings.contextDir("lib/ICTCLAS2015/lib/linux32/libNLPIR.so");
		} else if( GlobalSettings.OS.equals("linux64") ) {
			NLPIR_DLL_SO = GlobalSettings.contextDir("lib/ICTCLAS2015/lib/linux64/libNLPIR.so");
		} else if( GlobalSettings.OS.equals("macos") ) {
			NLPIR_DLL_SO = GlobalSettings.contextDir("lib/ICTCLAS2015/lib/mac-o/libNLPIR.dylib");
		}
		t.remind("Loading library: "+NLPIR_DLL_SO);
	}

}
