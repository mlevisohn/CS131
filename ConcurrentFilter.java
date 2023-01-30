package cs131.pa2.filter.concurrent;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa2.filter.Filter;

/**
 * An abstract class that extends the Filter and implements the basic functionality of all filters. Each filter should
 * extend this class and implement functionality that is specific for this filter. 
 * @author cs131a & Maya Levisohn
 *
 */
//Updated to implement Runnable
public abstract class ConcurrentFilter extends Filter implements Runnable {
	/**
	 * The input queue for this filter
	 */
	//Updated to LinkedBlockingQueue data structure
	protected LinkedBlockingQueue<String> input;
	/**
	 * The output queue for this filter
	 */
	//Updated to LinkedBlockingQueue data structure
	protected LinkedBlockingQueue<String> output;
	
	public String poisonPill = "stop";
	
	public boolean done;
	
	/**
	 * Sets the previous filter
	 */
	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}
	
	/**
	 * Sets the next filter 
	 */
	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter){
			ConcurrentFilter sequentialNext = (ConcurrentFilter) nextFilter;
			this.next = sequentialNext;
			sequentialNext.prev = this;
			if (this.output == null){
				this.output = new LinkedBlockingQueue<String>();
			}
			sequentialNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
	}
	/**
	 * Processes the input queue and passes the result to the output queue
	 */
	public void process(){
		try { //only processes if the thread is not interrupted 
			while (!isDone()){
				String line;
				line = input.take();
				if (line.contains(poisonPill)) { //stops processing and leaves the loop if passed the poison pill
					done = true; 
					break;
				}
				String processedLine = processLine(line);
				if (processedLine != null){
					output.put(processedLine);
				}
			}
		} catch (InterruptedException e) {
			output.clear();
		}	
	}
	
	/**
	 * returns a boolean to signify if a filter is done processing
	 */
	@Override
	public boolean isDone() {
		return done;
	}
	
	/**
	 * Called by the {@link #process()} method for every encountered line in the input queue.
	 * It then performs the processing specific for each filter and returns the result.
	 * Each filter inheriting from this class must implement its own version of processLine() to
	 * take care of the filter-specific processing.
	 * @param line the line got from the input queue
	 * @return the line after the filter-specific processing
	 */
	protected abstract String processLine(String line);
	
	/**
	 * The thread method used to run each filter and signify that it is done using the poison pill 
	 */
	@Override
	public void run() {
		process();
		try {
			output.put(poisonPill);
		} catch (InterruptedException e) {
			
		}
	}
	
}
