package cs131.pa2.filter.concurrent;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements tail command - overrides necessary behavior of SequentialFilter
 * 
 * @author Chami Lamelas & Maya Levisohn
 *
 */
public class TailFilter extends ConcurrentFilter {

	/**
	 * number of lines passed to output via tail
	 */
	private static int LIMIT = 10;

	/**
	 * line buffer
	 */
	private List<String> buf;

	/**
	 * Constructs a tail filter.
	 */
	public TailFilter() {
		super();
		buf = new LinkedList<String>();
	}

	/**
	 * Overrides {@link ConcurrentFilter#process()} to only add up to 10 lines to
	 * the output queue. Stops if interrupted.
	 */
	@Override
	public void process() {

		// until the input is empty, add line to end of buffer if buffer reached LIMIT,
		// remove the head (LinkedList makes this O(1)), could also use Queue/Deque
		// removing the head removes the oldest line seen so far - this way buf will
		// hold the last 10 lines of the input (or as many lines were in the input if
		// the input had < 10 lines)
		try { //only runs while not interrupted
			while (!isDone()) {
				String line;
				line = input.take();
				if (line.equals(poisonPill)) {
					done=true;
					break;
				}
				buf.add(line);
				if (buf.size() > LIMIT) {
					buf.remove(0);
				}
			}
	
			// once we're done with the input (and have identified the last 10 lines), add
			// them to the output in the order in which they appeared in the input
			while (!buf.isEmpty()) {
				output.put(buf.remove(0));
			}
		} catch (InterruptedException e) {
			output.clear();
		}
	}

	/**
	 * Overrides SequentialFilter.processLine() - doesn't do anything.
	 */
	@Override
	protected String processLine(String line) {
		// TODO Auto-generated method stub
		return null;
	}

}
