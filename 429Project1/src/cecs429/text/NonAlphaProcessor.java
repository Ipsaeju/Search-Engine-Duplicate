package cecs429.text;

import cecs429.text.TokenProcessor;
import java.util.ArrayList;

/**
 *
 * @author Jonalyn Razon
 */
public class NonAlphaProcessor implements TokenProcessor{

    private String processedToken = "";
    private ArrayList<String> tokensList = new ArrayList<String>();

    @Override
    public ArrayList<String> processToken(String token) {
    	tokensList.clear();
    	
    	processedToken = token.replaceAll("^(\\W+)|(\\W+)$|'|\"", "").toLowerCase();
    	
    	if (processedToken.contains("-")){
    		String hyphenSplit = processedToken;
    		String[] arrSplit = hyphenSplit.split("-");
    		for(String i : arrSplit){
    			tokensList.add(stemToken(i));
    		}
    		processedToken = processedToken.replaceAll("-", "");
    	}
    	
    	tokensList.add(stemToken(processedToken));
    	
    	return tokensList;

    }
    
    public int getTokenListSize(){
		return tokensList.size();
    	
    }
    
    public static String stemToken(String token){
        
    	Stemmer vocabStem = new Stemmer();
		for(int i = 0; i < token.length(); i++){
			vocabStem.add(token.charAt(i));   
		}
		vocabStem.stem();
        
		return vocabStem.toString();
    }

}