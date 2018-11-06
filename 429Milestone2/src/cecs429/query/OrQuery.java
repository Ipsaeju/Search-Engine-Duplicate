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
                List<String> plusQueries = new ArrayList<>();
                boolean containsPlus = false;
                for(int i = 0; i < mComponents.size(); i++){
                    QueryComponent q = mComponents.get(i);
                    String query = q.toString();
                    if(!query.contains("+") && !query.equals("") && !query.equals(" ")){
                        plusQueries.add(query);
                    }
                    else{
                        containsPlus = true;
                    }
                }
                for(int i = 0; i < plusQueries.size(); i++){
                    String plusQuery = plusQueries.get(i);
                    System.out.println(plusQuery);
                    if(containsPlus){
                        TermLiteral tl = new TermLiteral(plusQuery);
                        allPostings.add(tl.getPostings(index));
                    }
                    else{
                        allPostings.add(index.getPostings(plusQuery));
                    }
                }
                for(int k = 0; k < allPostings.size(); k++){
                    for(int j = 0; j < allPostings.get(k).size(); j++){
                        result.add(new Posting(allPostings.get(k).get(j).getDocumentId(), allPostings.get(k).get(j).getPositions()));
                    }
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
}
