package cecs429.text;

import cecs429.text.TokenProcessor;
import java.util.ArrayList;

/**
 *
 * @author Jonalyn Razon
 */
public class NonAlphaProcessor implements TokenProcessor{
    private String first = "";
    private String last = "";
    private String processedToken = "";
    private ArrayList<String> tokensList = new ArrayList<String>();
    private int prevHyphenIndex = 0;
    private int currHyphenIndex = 0;

    @Override
    public ArrayList<String> processToken(String token) {
            int tokenLen = token.length();
            if(tokenLen == 1){
                first = token.substring(0, tokenLen);
                processedToken = token;
                if (!first.matches("^.*[^a-zA-Z0-9].*$")){
                processedToken = token.replace(first, "");
                }
                tokensList.add(processedToken);
                return tokensList;
            }
            else{
                first = token.substring(0, 1);
                last = token.substring(token.length(), token.length());
                processedToken = token;
                if (!first.matches("^.*[^a-zA-Z0-9].*$")){
                    processedToken = processedToken.replace(first, "");
                }
                if (!last.matches("^.*[^a-zA-Z0-9].*$")){
                    processedToken = processedToken.replace(last, "");
                }
                if (processedToken.contains("'")){
                    processedToken = processedToken.replaceAll("'", "");
                }
                if (processedToken.contains("\"")){
                    processedToken = processedToken.replaceAll("\"", "");
                }
                if (processedToken.contains("`")){
                    processedToken = processedToken.replaceAll("`", "");
                }
                if (processedToken.contains("-")){
                    for(int i = 0; i < processedToken.length(); i++){
                        if(processedToken.charAt(i) == '-'){
                            prevHyphenIndex = currHyphenIndex;
                            currHyphenIndex = i;
                            String hyphenSplit = processedToken.substring(prevHyphenIndex, currHyphenIndex);
                            hyphenSplit = hyphenSplit.replaceAll("-", "");
                            tokensList.add(hyphenSplit);
                        }
                    }
                    processedToken = processedToken.replaceAll("-", "");
                }
                tokensList.add(processedToken);
                return tokensList;
            }
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
