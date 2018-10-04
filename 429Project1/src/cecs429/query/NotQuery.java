package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotQuery implements QueryComponent{
    private String mTerm;
    private List<QueryComponent> mComponents;
	
    public NotQuery(List<QueryComponent> components, String term) {
            mTerm = term;
            mComponents = components;
    }
    @Override
    public List<Posting> getPostings(Index index) {
        List<Posting> result = new ArrayList<>();
        List<Posting> postingsToRemove = index.getPostings(mTerm);
        List<List<Posting>> allPostings = new ArrayList<List<Posting>>();
        
        for(QueryComponent q : mComponents){
            allPostings.add(q.getPostings(index));
        }
        
        List<List<Posting>> tempAllPostings = allPostings;
        for(int i = 1; i < allPostings.size(); i++){
            int k = 0;
            List<Posting> tempResult = new ArrayList<>();
            for(int j = 0; j < allPostings.get(i).size() && k < result.size();){
                if(result.get(k).getDocumentId() < allPostings.get(i).get(j).getDocumentId() && 
                (postingsToRemove.get(k).getDocumentId() == allPostings.get(i).get(j).getDocumentId())){
                    k++;
                }
                else if(result.get(k).getDocumentId() > allPostings.get(i).get(j).getDocumentId() && 
                (postingsToRemove.get(k).getDocumentId() == allPostings.get(i).get(j).getDocumentId())){
                    j++;
                }
                else{
                    tempResult.add(new Posting(result.get(k).getDocumentId(),result.get(k).getPositions()));
                    k++;
                    j++;
                }
                tempAllPostings.get(i).remove(tempResult.get(j));
                tempResult = tempAllPostings.get(i);
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

    @Override
    public boolean isPositive() {
        return false;
    }

}
