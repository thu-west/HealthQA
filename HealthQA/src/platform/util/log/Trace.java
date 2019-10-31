package platform.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class Trace {
	static boolean is_shutdown = false;
	public int count;
	public int total;
	public int period;
	private boolean debug_valid;
	private boolean remind_valid;
	private long start_time;
	private long curr_time;
	private boolean first_run = true;
	static SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm:ss");
	Queue<Long> time_queue = new LinkedList<Long>();
	static int evaluation_remain_time_period = 500;

	public static void shutdown() {
		is_shutdown = true;
	}

	public static void startwork() {
		is_shutdown = false;
	}

	public Trace(int _total, int _period) {
		count = 0;
		total = _total;
		period = _period;
		debug_valid = true;
		remind_valid = true;
	}

	public Trace() {
		debug_valid = true;
		remind_valid = true;
	}

	public Trace setValid(boolean debug_valid, boolean remind_valid) {
		this.debug_valid = debug_valid;
		this.remind_valid = remind_valid;
		return this;
	}

	public void increase() {
		count++;
	}

	public void reset(int _total, int _period) {
		count = 0;
		total = _total;
		period = _period;
	}

	public void debug(String text, boolean increase) {
		if (!debug_valid || is_shutdown)
			return;
		if (increase) {
			count++;
			if(time_queue.size()<evaluation_remain_time_period) {
				time_queue.offer(new Date().getTime());
			} else {
				time_queue.offer(new Date().getTime());
				time_queue.poll();
			}
		}
		if( first_run ) {
			start_time = new Date().getTime();
		}
		if (count % period == 0) {
			// caculate time
			curr_time = new Date().getTime();
			long costed_time = ( curr_time - start_time );
			long remain_time = (long) ((curr_time-time_queue.peek()) / (float)evaluation_remain_time_period )*(total-count);

			String[] out = text.split("\n");
			for (int i = 0; i < out.length - 1; i++)
				System.out.println("[TRACE  " + sd.format(new Date()) + "] "
						+ out[i]);
			System.out.println("[TRACE  " + sd.format(new Date()) + "] "
					+ out[out.length - 1] + ", "
					+ ((float) count / total * 100) + "% Step " + count + "/"
					+ total + " :: costed "+costed_time/1000+"s"+" estimate remaining "+remain_time/1000+"s");
		}
		
		first_run = false;

	}

	public void remind(String text, boolean increase) {
		if (!remind_valid || is_shutdown)
			return;
		if (increase)
			count++;
		if (count % period == 0) {
			String[] out = text.split("\n");
			for (int i = 0; i < out.length - 1; i++)
				System.out.println("[REMIND " + sd.format(new Date()) + "] " + out[i]);
			System.out.println("[REMIND " + sd.format(new Date()) + "] " + out[out.length - 1] + ", "
					+ ((float) count / total * 100) + "% Step " + count + "/"
					+ total);
		}
	}

	public void debug(String text) {
		if (!debug_valid || is_shutdown)
			return;
		String[] out = text.split("\n");
		for (int i = 0; i < out.length; i++)
			System.out.println("[TRACE  " + sd.format(new Date()) + "] "
					+ out[i]);
	}
	
	public void error(String text) {
		if (is_shutdown)
			return;
		String[] out = text.split("\n");
		for (int i = 0; i < out.length; i++)
			System.err.println("[ERROR " + sd.format(new Date()) + "] " + out[i]);
	}

	public void remind(String text) {
		if (!remind_valid || is_shutdown)
			return;
		String[] out = text.split("\n");
		for (int i = 0; i < out.length; i++)
			System.out.println("[REMIND " + sd.format(new Date()) + "] " + out[i]);
	}

	public void print(String text) {
		if (is_shutdown)
			return;
		String[] out = text.split("\n");
		for (int i = 0; i < out.length; i++)
			System.out.println("[RESULT " + sd.format(new Date()) + "] " + out[i]);
	}
}
