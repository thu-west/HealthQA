package platform.daemon;

import java.io.*;

import platform.GlobalSettings;
import platform.util.log.Trace;

public class Daemon {
	
	protected static final Trace t = new Trace().setValid(true, true);
	
	String cmd = null;
	
	public Daemon( String cmd ) {
		this.cmd = cmd;
	}

	public void start() throws Exception {
		while (true) {
			t.remind("Starting a new process");
			System.out.println();
			Process child = Runtime.getRuntime().exec(cmd);
			InputStream stdin = child.getInputStream();
			InputStream stderr = child.getErrorStream();
			Thread tIn = new Thread(new ConsoleSimulator(stdin,
					ConsoleSimulator.INFO, GlobalSettings.SystemEncode));
			Thread tErr = new Thread(new ConsoleSimulator(stderr,
					ConsoleSimulator.ERROR, GlobalSettings.SystemEncode));
			tIn.start();
			tErr.start();
			int result = child.waitFor();
			tIn.join();
			tErr.join();
			
			System.out.println();
			
			if (result == 0) {
				t.remind("Finished with success signal");
			} else {
				t.remind("Finished with failed signal");
			}
			t.remind("Waiting for 10 seconds...");
			Thread.sleep(10000);
		}
	}

	public static void main(String[] args) throws Exception {
		new Daemon("java -jar Preprocess.tmp.jar").start();
	}

}
