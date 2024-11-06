/**
 * Kirsten Tapalla 
 * ktapalla@brandeis.edu
 * May 9, 2021 
 * PA 8 
 * Checks if there are fraudulent/suspicious timesheet submissions from TAs 
 * No known bugs/errors
 */

import java.io.*;
import java.util.*;

public class TAChecker {
	
	//Global ArrayList of TA Records and ArrayList for final output
	public static ArrayList<TARecord> records; 
	public static ArrayList<String> output;
	
	/**
	 * Scans through .txt file and creates a TARecord for each unique TA
	 * @throws FileNotFoundException 
	 */
	public static void sortWorkLog() throws FileNotFoundException {
		// Creates scanner object to read file names
		Scanner console = new Scanner(System.in);
		System.out.println("Enter a work log: ");
		String inFile = console.next();
		// Reads input file; file lines to array of strings
		File inputFile = new File(inFile);
		Scanner input = new Scanner(inputFile);
		
		//Creates an ArrayList of lines from the txt file
		ArrayList<String> lines = new ArrayList<String>();
		while (input.hasNextLine()) {
			String line = input.nextLine();
			lines.add(line);
		}
		
		//Creates a Set of TA names 
		Set<String> TAs = new HashSet<String>();
		for (int i = 0; i < lines.size(); i++) {
			String name = lines.get(i);
			int stopInd = name.indexOf(';');
			name = name.substring(0, stopInd);
			TAs.add(name);
		}
		
		//Goes through set and makes a record for each TA, adds to an ArrayList of records
		Iterator<String> it = TAs.iterator();
		while (it.hasNext()) {
			String name = it.next();
			TARecord r = new TARecord(name);
			//Adds lines and events to TA's record
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				if (line.contains(name)) {
					r.put(i+1, line);
				}
			}
			records.add(r);
		}
	}

	/**
	 * Goes through each TARecord to determine for fraud
	 */
	public static void checkValidity() {
		for(int i = 0; i < records.size(); i++) {
			TARecord r1 = records.get(i);
			checkUnstarted(r1);
			for(int ind = i+1; ind < records.size(); ind++) {
				TARecord r2 = records.get(ind);
				checkShortened(r1, r2);
			}
		}
	}
	
	/**
	 * Compares 2 TA records to each other to find shortened jobs
	 * - Also checks for suspicious batches
	 * @param r1 - first TA record
	 * @param r2 - second TA record
	 * 
	 */
	public static void checkShortened(TARecord r1, TARecord r2) {
		//Creates collections of values from TAs' maps
		Collection<String> r1Vals = r1.map.values();
		Iterator<String> it1 = r1Vals.iterator();
		Collection<String> r2Vals = r2.map.values();
		Iterator<String> it2 = r2Vals.iterator();
		String e1 = "";
		String e2 = "";
		int startKey1 = 0;
		int startKey2 = 0;
		int stopKey1 = 0;
		int stopKey2 = 0;
		while(it1.hasNext() || it2.hasNext()) {
			e1 = it1.next();
			e2 = it2.next();
			//if both events are start events
			if (e1.contains("START") && (e2.contains("START"))) {
				//gets key (line of event) mapping to events/values 
				startKey1 = r1.getKey(e1);
				startKey2 = r2.getKey(e2);
			}
			//2nd passed record is at the start event, but the 1st isn't - gets next event (searching for invoice IDs)
			else if(!e1.contains("START") && (e2.contains("START")) ) {
				e2 = it2.next();
				//new string that removes name from event string
				String e1Sub = e1.substring( e1.indexOf(';') + 1);
				String e2Sub = e2.substring( e2.indexOf(';') + 1);
				//gets key mapping to the events (invoice IDs)
				stopKey1 = r1.getKey(e1);
				stopKey2 = r2.getKey(e2);
				//turns string of numbers into an integer (invoice ID strings -> invoice ID integers)
				int inv1 = Integer.parseInt(e1Sub);
				int inv2 = 0;
				//ArrayList of invoice IDs created
				ArrayList<Integer> e2Inv = new ArrayList<Integer>();
				//gets numbers (invoice IDs) separated by commas 
				if (e2Sub.contains(",")) {
					while(e2Sub.contains(",")) {
						e2Sub = e2Sub.substring(0, e2Sub.indexOf(','));
						int inv = Integer.parseInt(e2Sub); 
						e2Inv.add(inv);
					}
					//gets final invoice ID (or only invoice ID if there are no others)
					//no comma returns index of -1, add 1 to begin at index of 0
					e2Sub = e2Sub.substring(e2Sub.indexOf(',') + 1);
					inv2 = Integer.parseInt(e2Sub); 
					e2Inv.add(inv2);
					
				}
				//if there's more than one key and all are less than inv1, and the the 1st event stopped before the 2nd started
				if (((e2Inv.size() > 1 && checkAllShortened(e2Inv, inv1) == true)) && (stopKey1 < startKey2)) {
					Set<Integer> startKeys = r2.getKeys(r2.name + ";START");
					for (int startKey : startKeys) {
						output.add(startKey + ";" + r2.name + ";SHORTENED_JOB");
					}
					
				} 
				//checks if the batch is suspicious and the 1st event started before the 2nd event started
				if (checkSuspicious(e2Inv, inv1) == true && startKey1 < startKey2) {
					output.add(stopKey2 + ";" + r2.name + ";SUSPICIOUS_BATCH");
				}
			} 
			//1st passed record is at the start event, but the 2nd isn't - gets next event (searching for invoice IDs)
			else if(!e2.contains("START") && (e1.contains("START"))) {
				e1 = it1.next();
				//new string that removes name from event string
				String e1Sub = e1.substring( e1.indexOf(';') + 1);
				String e2Sub = e2.substring( e2.indexOf(';') + 1);
				//gets key mapping to the events (invoice IDs)
				stopKey1 = r1.getKey(e1);
				stopKey2 = r2.getKey(e2);
				//turns string of numbers into an integer (invoice ID strings -> invoice ID integers)
				int inv1 = 0;
				int inv2 = Integer.parseInt(e2Sub);
				//ArrayList of invoice IDs created
				ArrayList<Integer> e1Inv = new ArrayList<Integer>();
				//gets numbers (invoice IDs) separated by commas
				if (e1Sub.contains(",")) {
					while(e1Sub.contains(",")) {
						e1Sub = e1Sub.substring(0, e1Sub.indexOf(','));
						inv1 = Integer.parseInt(e1Sub); 
						e1Inv.add(inv1);
					}
					//gets final invoice ID (or only invoice ID if there are no others)
					//no comma returns index of -1, add 1 to begin at index of 0
					e1Sub = e1Sub.substring(e1Sub.indexOf(',') + 1);
					inv1 = Integer.parseInt(e1Sub); 
					e1Inv.add(inv1);
				}
				//if there's more than one key and all are less than inv2, and the the 2nd event started before the 1st started
				if (((e1Inv.size() > 1 && checkAllShortened(e1Inv, inv2) == true)) && (stopKey2 < startKey1)) {
					Set<Integer> startKeys = r1.getKeys(r1.name + ";START");
					for (int startKey : startKeys) {
						output.add(startKey + ";" + r1.name + ";SHORTENED_JOB");
					}
				} 
				//checks if the batch is suspicious and the 2nd event stopped before the 1st event started
				if (checkSuspicious(e1Inv, inv2) == true && startKey2 < startKey1) {
					output.add(stopKey1 + ";" + r1.name + ";SUSPICIOUS_BATCH");
				}
			}
			//if there is only one invoice ID 
			else {
				//new string that removes name from event string
				String e1Sub = e1.substring( e1.indexOf(';') + 1);
				String e2Sub = e2.substring( e2.indexOf(';') + 1);
				//gets key mapping to the events (invoice IDs)
				stopKey1 = r1.getKey(e1);
				stopKey2 = r2.getKey(e2);
				//turns string of numbers into an integer (invoice ID strings -> invoice ID integers)
				int inv1 = Integer.parseInt(e1Sub);
				int inv2 = Integer.parseInt(e2Sub);
				//2nd invoice ID is greater than 1st invoice ID, and 2nd event started before 1st event
				if ((inv2 > inv1) && (startKey2 < startKey1)) {
					output.add(startKey1 + ";" + r1.name + ";SHORTENED_JOB");
				} 
				//1st invoice ID is greater than 2nd invoice ID, and 1st event started before 2nd event
				else if((inv1 > inv2) && (startKey1 < startKey2)) {
					output.add(startKey2 + ";" + r2.name + ";SHORTENED_JOB");
				}
			}
		}
	}

	/**
	 * Compares all invoices in an ArrayList to a separate invoice provided
	 * @param invID - ArrayList of invoice IDs
	 * @param invComp - invoice ID they ones in the ArrayList are being compared to
	 * @return - returns true if all invoices are less that the one they are being compared to
	 *           returns false once one is greater than the one they are being compared to
	 */
	public static boolean checkAllShortened(ArrayList<Integer> invID, int invComp) {
		boolean b = true;
		//loops through arraylist of invoice ID values
		for (int i = 0; i < invID.size(); i++) {
			int inv = invID.get(i);
			//returns false if invoice ID of any within the arraylist is greater than the one it is being compared to
			if (inv > invComp) {
				return false;
			}
		}
		//returns true otherwise
		return b;
	}
	
	/**
	 * checks if TA completed a job they didn't start
	 * @param r - individual TA record
	 */
	public static void checkUnstarted(TARecord r) {
		//collection of values created
		Collection<String> values = r.map.values();
		Iterator<String> it = values.iterator();
		int startCount = 0;
		int stopCount = 0;
		String event = "";
		while(it.hasNext()) {
			//new string that removes name from event string
			event = it.next();
			int eInd = event.indexOf(';');
			String eventCopy = event.substring(eInd+1);
			//if it's a start event, startCount is increased by 1
			if(eventCopy.equals("START")) {
				startCount++;
			} 
			//if it's not a start event (meaning it's an invoice ID) stopCount is increased by 1
			else if (!eventCopy.equals("START")) {
				stopCount++;
				//if there are multiple invoices included in one line
				while (eventCopy.contains(",")) {
					eventCopy = eventCopy.substring(eventCopy.indexOf(',')+1);
					stopCount++;
				}
			}
		}
		//if there are less start events than stop (invoice ID) events, there was an unstarted job
		if (startCount < stopCount) {
			int line = r.getKey(event);
			output.add(line + ";" + r.name + ";UNSTARTED_JOB");
		}
	}
	
	/**
	 * Compares all invoices in an ArrayList to a separate invoice provided
	 * @param invID - ArrayList of invoice IDs
	 * @param invComp - invoice ID they ones in the ArrayList are being compared to
	 * @return - returns true if invComp falls between invoices in the List
	 *           returns false if it doesn't 
	 */
	public static boolean checkSuspicious(ArrayList<Integer> invID, int invComp) {
		boolean b = false;
		//loops through arraylist of invoice ID values
		for (int i = 0; i+1 < invID.size(); i++) {
			int inv1 = invID.get(i);
			int inv2 = invID.get(i+1);
			//returns true if the invoice ID they're being compared to is in the middle, 
			// an event was started late (meaning shortened time, but unclear which event)
			if ((inv1 < invComp && invComp < inv2) || (inv2 < invComp && invComp < inv1)) {
				return true;
			}
		}
		//returns false otherwise
		return b;
	}
	
	public static void main(String [] args) throws FileNotFoundException {
		//TODO: Implement me!
		records = new ArrayList<TARecord>();
		output = new ArrayList<String>();
		sortWorkLog();
		checkValidity();
		for (int i = 0; i < output.size(); i++) {
			System.out.println(output.get(i));
		}
	}
}
