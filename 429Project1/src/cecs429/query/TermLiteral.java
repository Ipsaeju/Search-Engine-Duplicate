package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.NonAlphaProcessor;

import java.util.List;

/**
 * A TermLiteral represents a single term in a subquery.
 */
public class TermLiteral implements QueryComponent {
	private String mTerm;
	
	public TermLiteral(String term) {
		mTerm = term;
	}
	
	public String getTerm() {
		return mTerm;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		return index.getPostings(NonAlphaProcessor.stemToken(mTerm));
	}
	
	@Override
	public String toString() {
		return mTerm;
	}

}