package cs131.pa2.filter.concurrent;

import cs131.pa2.filter.Filter;
import cs131.pa2.filter.Message;

/**
 * Implements pwd command - overrides necessary behavior of SequentialFilter
 * 
 * @author Chami Lamelas
 *
 */
public class WorkingDirectoryFilter extends ConcurrentFilter {

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * Constructs a pwd filter.
	 * @param cmd cmd is guaranteed to either be "pwd" or "pwd" surrounded by whitespace
	 */
	public WorkingDirectoryFilter(String cmd) {
		super();
		command = cmd;
	}

	/**
	 * Overrides {@link ConcurrentFilter#process()} by adding
	 * {@link ConcurrentREPL#currentWorkingDirectory} to the output queue
	 */
	@Override
	public void process() {
		try {
			this.output.put(ConcurrentREPL.currentWorkingDirectory);
		} catch (InterruptedException e) {
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

	/**
	 * Overrides equentialFilter.setPrevFilter() to not allow a {@link Filter} to be
	 * placed before {@link WorkingDirectoryFilter} objects.
	 * 
	 * @throws IllegalArgumentException - always
	 */
	@Override
	public void setPrevFilter(Filter prevFilter) {
		throw new IllegalArgumentException(Message.CANNOT_HAVE_INPUT.with_parameter(command));
	}
}
