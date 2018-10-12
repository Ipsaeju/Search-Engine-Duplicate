package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An AndQuery composes other QueryComponents and merges their postings in an intersection-like operation.
 */
public class AndQuery implements QueryComponent {
	private List<QueryComponent> mComponents;
	
	public AndQuery(List<QueryComponent> components) {
		mComponents = components;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		// TODO: program the merge for an AndQuery, by gathering the postings of the composed QueryComponents and
		// intersecting the resulting postings.
		List<Posting> result = new ArrayList<>();

		List<List<Posting>> allPostings = new ArrayList<List<Posting>>();
		for(QueryComponent q : mComponents){
			allPostings.add(q.getPostings(index));
		}

		result = allPostings.get(0);
		for(int i = 1; i < allPostings.size(); i++){
			int k = 0;
			List<Posting> tempResult = new ArrayList<>();
			for(int j = 0; j < allPostings.get(i).size() && k < result.size();){
				if(result.get(k).getDocumentId() < allPostings.get(i).get(j).getDocumentId()){
					k++;
				}
				else if(result.get(k).getDocumentId() > allPostings.get(i).get(j).getDocumentId()){
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
		return
		 String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}
}
