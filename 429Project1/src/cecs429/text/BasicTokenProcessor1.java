package cecs429.text;

import java.util.ArrayList;


/**
 * A BasicTokenProcessor creates terms from tokens by removing all non-alphanumeric characters from the token, and
 * converting it to all lowercase.
 */
public class BasicTokenProcessor1 implements TokenProcessor1 {
        private ArrayList<String> tokensList = new ArrayList<String>();
        @Override
	public String processToken(String token) {
            return token.replaceAll("\\W", "").toLowerCase();
	}
}
