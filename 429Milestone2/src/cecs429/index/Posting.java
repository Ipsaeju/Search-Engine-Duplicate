package cecs429.index;

import java.util.ArrayList;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
	private double mDocumentScore;
	private ArrayList<Integer> mPositions;

	public Posting(int documentId) {
		mDocumentId = documentId;
		mPositions = new ArrayList<Integer>();
	}
	
	public Posting(int documentId, int position) {
		mDocumentId = documentId;
		mPositions = new ArrayList<Integer>();
		mPositions.add(position);
	}
	
	public Posting(int documentId, ArrayList<Integer> positions) {
		mDocumentId = documentId;
		mPositions = positions;
	}
	
	public Posting(int documentId, double score) {
		mDocumentId = documentId;
		mDocumentScore = score;
	}

	public double getDocumentScore() {
		return mDocumentScore;
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