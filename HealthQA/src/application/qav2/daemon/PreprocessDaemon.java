/*
 * Deamon for application.qav2.process_data.Preprocess
 */

package application.qav2.daemon;

import platform.daemon.Daemon;

public class PreprocessDaemon extends Daemon {

	public PreprocessDaemon(String cmd) {
		super(cmd);
	}
	
	public static void main(String[] args) throws Exception {
		new PreprocessDaemon("java -jar Preprocess.tmp.jar").start();
	}
	
}
