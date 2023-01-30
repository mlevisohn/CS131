package cs131.pa2.filter.concurrent;

/**
 * Implements head command - overrides necessary behavior of SequentialFilter
 * 
 * @author Chami Lamelas & Maya Levisohn
 *
 */
public class HeadFilter extends ConcurrentFilter {

	/**
	 * number of lines read so far
	 */
	private int numRead;

	/**
	 * number of lines passed to output via head
	 */
	private static int LIMIT = 10;

	/**
	 * Constructs a head filter.
	 */
	public HeadFilter() {
		super();
		numRead = 0;
	}

	/**
	 * Overrides {@link ConcurrentFilter#process()} to only add up to 10 lines to
	 * the output queue. Stops if interrupted. 
	 */
	@Override
	public void process() {
		try { //only runs if not interrupted
			while (!isDone() && numRead < LIMIT) {
				String line;
				line = input.take();
				if (line.contains(poisonPill)) { //if passed the poison pill it exits the loop
					done = true; 
				}
				output.put(line);
				numRead++;
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
