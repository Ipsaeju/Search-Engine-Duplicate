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

		// list to store postings from components of positive sign
		List<List<Posting>> allPostings = new ArrayList<List<Posting>>();
		
		// list to store postings from not components
		List<Posting> notPostings = new ArrayList<>();
		
		// iterate through all components in component list
		for(QueryComponent q : mComponents){
			
			// if component's sign is positive add it's postings into allPostings list
			if(q.getSign()){				
				allPostings.add(q.getPostings(index));
			} 
			// if component's sign is negative add it's postings into notPostings list
			else{
				notPostings.addAll(q.getPostings(index));
			}
		}
		
		System.out.println("In AND allPostings size: "+allPostings.size());

		// list to hold temporary result
		List<Posting> tempResult = new ArrayList<>();
		
		// make result the first element in all postings
		result = allPostings.get(0);
		
		// there exists more than one positive query component
		if(allPostings.size() >= 2){
			// for loop to iterate through allPostings list
			for(int i = 1; i < allPostings.size(); i++){
				int k = 0;
				
				List<Posting> tempResultPos = new ArrayList<>();

				// nested for loop to iterate through the other posting lists of a different component
				for(int j = 0; j < allPostings.get(i).size() && k < result.size();){
					if(result.get(k).getDocumentId() < allPostings.get(i).get(j).getDocumentId()){
						k++;
					}
					else if(result.get(k).getDocumentId() > allPostings.get(i).get(j).getDocumentId()){
						j++;
					}
					// if document id's match then merge into temporary result list
					else{
						tempResultPos.add(new Posting(result.get(k).getDocumentId()));
						k++;
						j++;
					}
				}
				// update result
				result = tempResultPos;
			}
			tempResult = result;
		} 
		// there only exists one positive query component
		else{
			// add a new posting created from result list
			for (Posting p : result){
				tempResult.add(new Posting(p.getDocumentId()));
			}
		}

		
		// check if notPostings list is empty
		if(!notPostings.isEmpty()){
			// iterate through tempResults
			for(int i = 0; i < notPostings.size(); i++)
			{
				// iterate through notPostings list
				for(int j = 0; j < tempResult.size(); j++){
					// if found in the tempResults then remove
					if(tempResult.get(j).getDocumentId() == notPostings.get(i).getDocumentId()){
						tempResult.remove(j);
						break;
					}
				}
			}
			
		}
		
		// make result equal to temporary result
		result = tempResult;

		return result;
	}
	
	@Override
	public String toString() {
		return
		 String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}

	@Override
	public boolean getSign() {
		// TODO Auto-generated method stub
		return true;
	}
}
