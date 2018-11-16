package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An OrQuery composes other QueryComponents and merges their postings with a union-type operation.
 */
public class OrQuery implements QueryComponent {
	// The components of the Or query.
	private List<QueryComponent> mComponents;
	
	public OrQuery(List<QueryComponent> components) {
		mComponents = components;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		// TODO: program the merge for an OrQuery, by gathering the postings of the composed QueryComponents and
		// union the resulting postings.
        List<Posting> result = new ArrayList<>();
        
        List<List<Posting>> allPostings = new ArrayList<List<Posting>>();
        for(QueryComponent q : mComponents){
            allPostings.add(q.getPostings(index));
        }
        
        result = allPostings.get(0);
        for(int i = 1; i < allPostings.size(); i++){
            int k = 0;
            List<Posting> tempResult = new ArrayList<>();
            for(int j = 0; j < allPostings.get(i).size() || k < result.size();){
                if(k >= result.size()){
                    while(j < allPostings.get(i).size()){
                        tempResult.add(new Posting(allPostings.get(i).get(j++).getDocumentId()));
                    }
                }
                else if(j >= allPostings.get(i).size()){
                    while(k < result.size()){
                        tempResult.add(new Posting(result.get(k++).getDocumentId()));
                    }
                }
                else if(result.get(k).getDocumentId() < allPostings.get(i).get(j).getDocumentId()){
                    tempResult.add(new Posting(result.get(k).getDocumentId()));
                    k++;
                }
                else if(result.get(k).getDocumentId() > allPostings.get(i).get(j).getDocumentId()){
                    tempResult.add(new Posting(allPostings.get(i).get(j).getDocumentId()));
                    j++;
                }
                else{
                    tempResult.add(new Posting(result.get(k).getDocumentId()));
                    k++;
                    j++;
                }
            }
            result = tempResult;
        }
        
        return result;
	}
	
	@Override
	public String toString() {
		// Returns a string of the form "[SUBQUERY] + [SUBQUERY] + [SUBQUERY]"
		return "(" +
		 String.join(" + ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()))
		 + " )";
	}

	@Override
	public boolean getSign() {
		// TODO Auto-generated method stub
		return true;
	}
}
