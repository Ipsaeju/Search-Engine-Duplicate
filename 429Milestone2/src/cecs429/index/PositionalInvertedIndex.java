package cecs429.index;

import java.util.*;

public class PositionalInvertedIndex implements Index {

	private HashMap<String, ArrayList<Posting>> mHmap;
	
	public PositionalInvertedIndex() {
		mHmap = new HashMap<String, ArrayList<Posting>>();
	}
	
	@Override
	public List<Posting> getPostings(String term) {
		// list of Postings for returning
		List<Posting> results = new ArrayList<>();
				
		// checks if term is in the mHmap
		if(mHmap.containsKey(term)){
			// store list of Postings
			results = mHmap.get(term);
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
	
	public void addTerm(String term, int documentId, int position) {
		// ArrayList to get the values for the key (term)
	    ArrayList<Posting> values = mHmap.get(term);

	    // if term isn't in HashMap
	    if(values == null) {
	    	// create an empty ArrayList
	    	values = new ArrayList<Posting>();
	    	// create a posting
	    	Posting p = new Posting(documentId,position);
	    	// add posting to ArrayList
	    	values.add(p);
	    	// insert posting into HashMap
	    	mHmap.put(term, values);
	    } 
	    // term is in index
	    else {
	        // if documentId isn't in values add it
	        if(values.get(values.size() - 1).getDocumentId() != documentId){
	        	// create a posting
		    	Posting p = new Posting(documentId,position);
	        	mHmap.get(term).add(p);
	        }
	        // term and documentId already exists -> add new position
	        else {
	        	mHmap.get(term).get(values.size()-1).addPosition(position);
	        }
	    }
	}

	@Override
	public List<Posting> getPostingsTftd(String term) {
		// TODO Auto-generated method stub
		return null;
	}
	
}