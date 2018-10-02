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
    	int firstAlpha = 0;
    	int lastAlpha = 0;
    	int lastIndex = token.length()-1;
    	
    	if(!Character.isLetterOrDigit(token.charAt(0)) || !Character.isLetterOrDigit(token.charAt(lastIndex))){
    		for (int i = 0; i < token.length(); i++){
    			char c = token.charAt(i);
    			if(Character.isLetterOrDigit(c)){
    				firstAlpha = i;
    				break;
    			}
    		}
    		for (int i = token.length() - 1; i >= 0; i--){
    			char c = token.charAt(i);
    			if(Character.isLetterOrDigit(c)){
    				lastAlpha = i+1;
    				break;
    			}
    		}
    		processedToken = token.substring(firstAlpha,lastAlpha);
    	} 
    	else{
    		processedToken = token;
    	}

    	processedToken = processedToken.replaceAll("\'", "");
    	processedToken = processedToken.replaceAll("\"", "");

    	if (processedToken.contains("-")){
    		String hyphenSplit = processedToken;
    		String[] arrSplit = hyphenSplit.split("-");
    		for(String i : arrSplit){
    			tokensList.add(i);
    		}
    		processedToken = processedToken.replaceAll("-", "");
    	}
    	
    	tokensList.add(processedToken);
    	
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
