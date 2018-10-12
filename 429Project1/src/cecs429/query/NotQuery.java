package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotQuery implements QueryComponent{
    private List<QueryComponent> mComponents;
	
    public NotQuery(List<QueryComponent> components) {
            mComponents = components;
    }
    @Override
    public List<Posting> getPostings(Index index) {
        List<Posting> result = new ArrayList<>();
        List<List<Posting>> allPostings = new ArrayList<List<Posting>>();
        PhraseLiteral pl = null;
        
        boolean containsMinus = false;
        boolean containsMinusPhrase = false;
        String removeQuery = "";
        for(int i = 0; i < mComponents.size(); i++){
            QueryComponent q = mComponents.get(i);
            String query = q.toString();
            System.out.println("Query: " + query);
            if(query.contains("-")){
                removeQuery = query;
                removeQuery = removeQuery.replace("-", "");
                containsMinus = true;
                
            }
            else if(query.contains("\"")){
                removeQuery = query.replaceAll("\"", "");
                System.out.println("Query to remove: " + removeQuery);
                pl = new PhraseLiteral(removeQuery);
                containsMinusPhrase = true;
            }
            else{
                allPostings.add(index.getPostings(query));
            }
        }
        
        if(containsMinus){
            List<Posting> postingsToRemove = index.getPostings(removeQuery);
            List<Posting> tempResult = allPostings.get(0);
            List<List<Posting>> tempAllPostings = allPostings;
            for(int i = 0; i < tempAllPostings.size(); i++){
                for(int j = 0; j < tempAllPostings.get(i).size(); j++){
                    if(containsRemove(tempResult, postingsToRemove)){
                        Posting removal = tempAllPostings.get(i).get(j);
                        tempResult.remove(removal);
                    }
                    
                }
            }
            result = tempResult;

        }
        else if(containsMinusPhrase){
            List<Posting> postingsToRemove = pl.getPostings(index);
            List<Posting> tempResult = allPostings.get(0);
            List<List<Posting>> tempAllPostings = allPostings;
            for(int i = 0; i < tempAllPostings.size(); i++){
                for(int j = 0; j < tempAllPostings.get(i).size(); j++){
                    if(containsRemove(tempResult, postingsToRemove)){
                        Posting removal = tempAllPostings.get(i).get(j);
                        tempResult.remove(removal);
                    }
                }
                
            }
            result = tempResult;
        }
        return result;

    }
    public static boolean containsRemove(List<Posting> p1, List<Posting> remove){
		
		// ArrayLists to store each postings positions
                ArrayList<Integer> p1IDs = new ArrayList<>();
                ArrayList<Integer> rIDs = new ArrayList<>();
		for(Posting p : p1){
                    p1IDs.add(p.getDocumentId());
                }
                for(Posting r : remove){
                    rIDs.add(r.getDocumentId());
                }
		
		
		// iterate through each of the postings doc ids
		for (int m : p1IDs) {
			for (int y : rIDs) {
				//check if they have identical doc ids
				if (m == y){
                                    return true;
                                }
			}
		}
		// returns false if the postings don't share doc ids
		return false;
	}
    
    @Override
    public String toString() {
        return
        String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
    }


}