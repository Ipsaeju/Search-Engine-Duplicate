package cecs429.text;

import java.util.ArrayList;


/**
 * A BasicTokenProcessor creates terms from tokens by removing all non-alphanumeric characters from the token, and
 * converting it to all lowercase.
 */
public class BasicTokenProcessor implements TokenProcessor {
        private ArrayList<String> tokensList = new ArrayList<String>();
        @Override
	public ArrayList<String> processToken(String token) {
            tokensList.add(token.replaceAll("\\W", "").toLowerCase());
            return tokensList;
	}
}
