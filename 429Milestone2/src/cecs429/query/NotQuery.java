package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import java.util.ArrayList;
import java.util.List;

public class NotQuery implements QueryComponent{
	private QueryComponent mComponent;

	public NotQuery(QueryComponent component) {
		mComponent = component;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		List<Posting> result = new ArrayList<>();
		List<Posting> tempResult = new ArrayList<>();
		
		// get all postings using component's getPostings method
		tempResult.addAll(mComponent.getPostings(index));
		
		// add new postings from tempResult into result list
		for(Posting p: tempResult){
			result.add(new Posting(p.getDocumentId()));
		}

		return result;
	}

	@Override
	public String toString() {
		return mComponent.toString();
	}

	@Override
	public boolean getSign() {
		// TODO Auto-generated method stub
		return false;
	}


}