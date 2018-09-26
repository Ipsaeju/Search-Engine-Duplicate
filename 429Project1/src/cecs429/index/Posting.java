package cecs429.index;

import java.util.ArrayList;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
	private ArrayList<Integer> mPositions;
	
	public Posting(int documentId, int position) {
		mDocumentId = documentId;
		mPositions = new ArrayList<Integer>();
		mPositions.add(position);
	}
	
	public int getDocumentId() {
		return mDocumentId;
	}
	
	public ArrayList<Integer> getPositions(){
		return mPositions;
	}
	
	public void addPosition(int position){
		mPositions.add(position);
	}
}
