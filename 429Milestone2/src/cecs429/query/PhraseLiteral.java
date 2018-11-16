package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.NonAlphaProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a phrase literal consisting of one or more terms that must occur in sequence.
 */
public class PhraseLiteral implements QueryComponent {
	// The list of individual terms in the phrase.
	private List<String> mTerms = new ArrayList<>();
	
	/**
	 * Constructs a PhraseLiteral with the given individual phrase terms.
	 */
	public PhraseLiteral(List<String> terms) {
		mTerms.addAll(terms);
	}
	
	/**
	 * Constructs a PhraseLiteral given a string with one or more individual terms separated by spaces.
	 */
	public PhraseLiteral(String terms) {
		mTerms.addAll(Arrays.asList(terms.split(" ")));
	}
	
	/**
	 * Get the postings of terms that are phrase literals.
	 */
	@Override
	public List<Posting> getPostings(Index index) {
		// TODO: program this method. Retrieve the postings for the individual terms in the phrase,
		// and positional merge them together.
		List<Posting> result = new ArrayList<>();
		List<List<Posting>> allPostings = new ArrayList<List<Posting>>();
		
		// iterate through each term and store it's postings
		for(String s : mTerms){
			if(s.equals("")){
				continue;
			}
			allPostings.add(index.getPostings(NonAlphaProcessor.stemToken(s)));
		}
		
		// set result to the first posting 
        result = allPostings.get(0);
        
        // iterate through each posting and check if there's any matching documentIDs
        for(int i = 1; i < allPostings.size(); i++){
            int k = 0;
            
            // temporary array list to store final result
            List<Posting> tempResult = new ArrayList<>();
            
            // compares the result posting with the rest
            for(int j = 0; j < allPostings.get(i).size() && k < result.size();){
            	
                if(result.get(k).getDocumentId() < allPostings.get(i).get(j).getDocumentId()){
                    k++;
                }
                else if(result.get(k).getDocumentId() > allPostings.get(i).get(j).getDocumentId()){
                    j++;
                }
                // both postings share a documentID
                else{
                	// check if the similar documentIDs positions are of by 1 
                    if(mergePositions(result.get(k), allPostings.get(i).get(j))){
                    	// add posting to tempResult
                        tempResult.add(new Posting(result.get(k).getDocumentId(),result.get(k).getPositions()));
                    }
                    k++;
                    j++;
                }
            }
            // set result to the tempResult
            result = tempResult;
        }

        // return final result
		return result;
		
	}
	
	/**
	 * Checks if two postings have a position they have a difference of 1. Returns a boolean value.
	 */
	public static boolean mergePositions(Posting t1, Posting t2){
		
		// ArrayLists to store each postings positions
		ArrayList<Integer> t1Positions = t1.getPositions();
		ArrayList<Integer> t2Positions = t2.getPositions();
		
		// iterate through each of the postings positions
		for (int p1 : t1Positions) {
			for (int p2 : t2Positions) {
				//check if there is a difference by 1
				if (p1 == p2-1) return true;
			}
		}
		// returns false if the postings don't have position of by 1
		return false;
	}

	@Override
	public String toString() {
		return "\"" + String.join(" ", mTerms) + "\"";
	}

	@Override
	public boolean getSign() {
		// TODO Auto-generated method stub
		return true;
	}
	
}
