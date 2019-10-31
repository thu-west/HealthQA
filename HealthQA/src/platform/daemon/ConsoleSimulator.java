package platform.daemon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class for console simulation
 * 
 * @author lewhwa
 */
public class ConsoleSimulator implements Runnable {
	private volatile boolean isStop = false;

	public static final int INFO = 0;
	public static final int ERROR = 1;

	private InputStream is;
	private int type;
	private String encode;

	/** Creates a new instance of StreamInterceptor */
	public ConsoleSimulator(InputStream is, int type, String encode) {
		this.is = is;
		this.type = type;
		this.encode = encode;
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is, encode);
			BufferedReader reader = new BufferedReader(isr);
			String s;
			while ((!isStop) && (s = reader.readLine()) != null) {
				if (s.length() != 0) {
					if (type == INFO) {
						System.out.println(s);
					} else {
						System.err.println(s);
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void stop() {
		isStop = true;
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		// Process child = Runtime.getRuntime().exec("run.bat");

	}
}