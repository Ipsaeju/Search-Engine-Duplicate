/*
package cecs429.index;

import java.util.*;

public class InvertedIndex implements Index {
	
	private HashMap<String, ArrayList<Integer>> mHmap;
	
	public InvertedIndex() {
		mHmap = new HashMap<String, ArrayList<Integer>>();
	}

	@Override
	public List<Posting> getPostings(String term) {
		// list of Postings for returning
		List<Posting> results = new ArrayList<>();
		
		// checks if term is in the mHmap
		if(mHmap.containsKey(term)){
			
			// ArrayList to get the values of docID's
			ArrayList<Integer> values = mHmap.get(term);
			
			// iterate through ArrayList of values
			for (Integer i : values) {
				// create a posting and initialize it's documentId
				Posting p = new Posting(i);
				
				// store posting in the list of Postings
				results.add(p);
			}
		}else{
			System.out.println("Term not found.");
		}
		
		return results;
	}

	@Override
	public List<String> getVocabulary() {
		// get keySet from HashMap and convert to list of string
		List<String> vocab = new ArrayList<String>(mHmap.keySet());
		
		// sorts the vocabulary list
		Collections.sort(vocab);
		
		return vocab;
	}
	
	public void addTerm(String term, int documentId) {
		// ArrayList to get the values for the key (term)
	    ArrayList<Integer> values = mHmap.get(term);

	    // if term isn't in HashMap
	    if(values == null) {
	    	// create an empty ArrayList
	    	values = new ArrayList<Integer>();
	    	// add documentID to ArrayList
	    	values.add(documentId);
	    	// insert into HashMap
	    	mHmap.put(term, values);
	    } else {
	        // if documentId isn't in values add
	        if(values.get(values.size() - 1) != documentId)
	        	mHmap.get(term).add(documentId);
	    }
	}
}
*/