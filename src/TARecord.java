/**
 * Kirsten Tapalla 
 * ktapalla@brandeis.edu
 * May 9, 2021 
 * PA 8 
 * Creates/Stores start and end records for TAs using a map 
 * No known bugs/errors
 */

import java.util.*;

public class TARecord {

	String name;
	Map<Integer, String> map;
	ArrayList<Integer> keyArr;
	// and any other feilds you need

	/*
	 * Constructor
	 */
	public TARecord (String name) {
		//TODO: Implement me!
		this.name = name;
		this.map = new HashMap<Integer, String>();
		this.keyArr = new ArrayList<Integer>();
	}

	/**
	 * Adds/Puts a key,value pair into the map
	 * @param k - key to a value being added to a map
	 * @param v - value being added to the map
	 */
	public void put(Integer k, String v) {
		this.map.put(k, v);
	}

	/**
	 * Returns a string of the TA record, meaning a string of the map
	 */
	public String toString() {
		return this.map.toString();
	}

	/**
	 * Returns the key of a value within the map
	 * @param value - String of a value being searched for
	 * @return - returns an int 
	 */
	public int getKey(String value) {
		int k = -1;
		Set<Integer> keys = this.map.keySet();
		Iterator<Integer> it = keys.iterator();
		while(it.hasNext()) {
			int key = it.next();
			String val = this.map.get(key);
			if (val.equals(value)) {
				return key;
			}
		}
		return k;	
	}

	/**
	 * Returns a Set of keys mapping to the value parameter being searched for
	 * @param value - String of a value being searched for
	 * @return - returns a set 
	 */
	public Set<Integer> getKeys(String value) {
		Set<Integer> keys = this.map.keySet();
		Iterator<Integer> it = keys.iterator();
		while(it.hasNext()) {
			int key = it.next();
			String val = this.map.get(key);
			if (!val.equals(value)) {
				it.remove();;
			}
		}
		return keys;
	}
}







