package cecs429.index;

import cecs429.documents.DocumentCorpus;


public interface WeightingStrategy {
    //Weighs every document in the corpus and stores it in a bin file
    public void docWeight(String path, DocumentCorpus corp);
    //Weighs query based on the terms in the query
    public double queryWeight(String query, DocumentCorpus corp, Index index);
    //Gets the wdt for a particular document
    public double getWDT(Posting p);
    //Gets the Ld for a particular document
    public double getLd(Posting p);
}
