package cs131.pa2.filter.concurrent;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import cs131.pa2.filter.Message;

/**
 * The main implementation of the REPL loop (read-eval-print loop). It reads
 * commands from the user, parses them, executes them and displays the result.
 * 
 * @author cs131a & Maya Levisohn
 *
 */
public class ConcurrentREPL {
	/**
	 * the path of the current working directory
	 */
	static String currentWorkingDirectory;

	/**
	 * pipe string
	 */
	static final String PIPE = "|";

	/**
	 * redirect string
	 */
	static final String REDIRECT = ">";
	
	static boolean interrupt = false; 

	/**
	 * The main method that will execute the REPL loop
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {

		// set cwd here so that it can be reset by tests that run main() function
		currentWorkingDirectory = System.getProperty("user.dir");

		Scanner consoleReader = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		
		//map is used to store background threads, foreMap is used to store foreground threads
		LinkedHashMap<String, ArrayList> map = new LinkedHashMap<String, ArrayList>();
		LinkedHashMap<String, ArrayList> foreMap = new LinkedHashMap<String, ArrayList>();
		ArrayList<String> commands = new ArrayList<String>(); 


		// whether or not shell is running
		boolean running = true;
		

		do {
			System.out.print(Message.NEWCOMMAND);
			boolean background = false; 

			// read user command, if its just whitespace, skip to next command
			String cmd = consoleReader.nextLine();
			//System.out.println("command is"+cmd);
			if (cmd.isBlank()) {
				continue;
			}
			try {
				//parses kill command by taking the number of the command being killed and interrupting all the threads in that command
				if (cmd.contains("kill ")) { 
					String num = cmd.substring(cmd.indexOf("kill ")+5);
					int n = Integer.parseInt(num);
					ArrayList<Thread> threads = map.get(commands.get(n-1));
					for (int i=0; i<threads.size(); i++) {
						threads.get(i).interrupt();
					}
					
				}
				//repl_jobs prints out the background command threads that are alive
				else if (cmd.contains("repl_jobs")) {
					for (int i=1; i<map.size()+1; i++) {
						ArrayList<Thread> line = map.get(commands.get(i-1));
						if (line.get(line.size()-1).isAlive()) {
							System.out.println("\t"+i+". "+commands.get(i-1));
						}
					}
				} 
				else {
					// parse command into sub commands, then into Filters, add final PrintFilter if
					// necessary, and link them together - this can throw IAE so surround in
					// try-catch so appropriate Message is printed (will be the message of the IAE)
					List<ConcurrentFilter> filters = ConcurrentCommandBuilder.createFiltersFromCommand(cmd);
	
					// if we have only an ExitFilter, that means user typed "exit" or "exit"
					// surrounded by ws, stop the shell
					if (filters.size() == 1 && filters.get(0) instanceof ExitFilter) {
						running = false;
					} 
					else {	// otherwise, call process on each of the filters to have them execute
						
						//if the command is a background command
						if (cmd.contains("&")) {
							background = true; 
						}
						
						//iterate through filters and create threads, add them to the arrayList and run them
						ArrayList<Thread> line = new ArrayList<Thread>(); 
						for (ConcurrentFilter filter : filters) {
							Thread t = new Thread(filter);
							line.add(t);
							t.start();
						}
						
						//join foreground threads and add them to the map
						if (!background) {
							try {
								line.get(line.size()-1).join(); 
							} catch (InterruptedException e) {
							}
							foreMap.put(cmd, line);
						}
						//add background commands to the map and to the list of background command strings
						if (background) {
							map.put(cmd, line);
							commands.add(cmd);
						}
					}
				}
			} catch (IllegalArgumentException e) {
				System.out.print(e.getMessage());
			} 

		} while (running);
		System.out.print(Message.GOODBYE);

		consoleReader.close();

	}

}
