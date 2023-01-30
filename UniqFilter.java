package cs131.pa2.filter.concurrent;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements uniq command - overrides necessary behavior of SequentialFilter
 * 
 * @author Chami Lamelas
 *
 */
public class UniqFilter extends ConcurrentFilter {

	/**
	 * stores unique strings seen in input
	 */
	private Set<String> currUniq;

	/**
	 * Constructs a uniq filter.
	 */
	public UniqFilter() {
		super();
		currUniq = new HashSet<String>();
	}

	/**
	 * Overrides SequentialFilter.processLine() - only returns lines to
	 * {@link ConcurrentFilter#process()} that aren't duplicates.
	 */
	@Override
	protected String processLine(String line) {
		if (!currUniq.contains(line)) {
			currUniq.add(line);
			return line;
		}

		// TODO Auto-generated method stub
		return null;
	}

}
