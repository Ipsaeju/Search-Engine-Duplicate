package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	@Override
	public List<Posting> getPostings(Index index) {
		// TODO: program this method. Retrieve the postings for the individual terms in the phrase,
		// and positional merge them together.
		List<Posting> result = new ArrayList<>();
		List<Posting> shareDocID = new ArrayList<>();
		List<List<Posting>> allPostings = new ArrayList<List<Posting>>();
		
		for(String s : mTerms){
			allPostings.add(index.getPostings(s));
		}
		
		shareDocID = allPostings.get(0);
        for(int i = 1; i < allPostings.size(); i++){
            int k = 0;
            List<Posting> tempResult = new ArrayList<>();
            for(int j = 0; j < allPostings.get(i).size() && k < shareDocID.size();){
                if(shareDocID.get(k).getDocumentId() < allPostings.get(i).get(j).getDocumentId()){
                    k++;
                }
                else if(shareDocID.get(k).getDocumentId() > allPostings.get(i).get(j).getDocumentId()){
                    j++;
                }
                else{
                    tempResult.add(new Posting(shareDocID.get(k).getDocumentId(),shareDocID.get(k).getPositions()));
                    k++;
                    j++;
                }
            }
            shareDocID = tempResult;
        }
        
        ArrayList<Integer> positions = new ArrayList<Integer>();
        int t = 0;
        
        for(Posting p: shareDocID){
        	positions.addAll(p.getPositions());
        	t++;
        	if(t % mTerms.size() == 0){
        		if(longestConsecutive(positions) == mTerms.size()){
        			result.add(new Posting(p.getDocumentId(),positions));
        		}
        		positions.clear();
        	}
        }
        
		return result;
		
	}
	
	@Override
	public String toString() {
		return "\"" + String.join(" ", mTerms) + "\"";
	}
	
	public static int longestConsecutive(ArrayList<Integer> l) {
        Set<Integer> set = new HashSet<>();
        for (int i : l) {
            set.add(i);
        }
        int max = 0;
        for (int i : l) {
            if (!set.contains(i)) {
                continue;
            }
            int cnt = 1;
            int k = i;
            while (set.contains(++k)) {
                cnt++;
            }
            k = i;
            while (set.contains(--k)) {
                cnt++;
            }
            max = Math.max(max, cnt);
        }
        return max;
    }
}
