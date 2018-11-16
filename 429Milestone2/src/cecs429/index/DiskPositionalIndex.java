package cecs429.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DiskPositionalIndex implements Index{

    private HashMap<String, ArrayList<Posting>> mHmap;

    public DiskPositionalIndex() {
        mHmap = new HashMap<String, ArrayList<Posting>>();
    }

    @Override
    public List<Posting> getPostings(String term) {
        // list to store postings
        List<Posting> results = new ArrayList<>();
        // variable to store docID without gap
        int docIDGapless = 0;
        // variable to store position without gap
        int posGapless = 0;
        // postings position for term
        long posPostings = binSearchVocab(term);
        // file object for postings file
        File postingsFile = new File("index//postings.bin");

        try {
            // RandomAccessFile object
            RandomAccessFile inPostings = new RandomAccessFile(postingsFile, "rb");
            // seek to postings position
            inPostings.seek(posPostings);
            // store document frequency
            int df = inPostings.readInt();
            // loop for number of documents
            for(int i = 0; i < df; i++){
                // list to store positions
                ArrayList<Integer> positions = new ArrayList<Integer>(); 
                // store document id
                int docID = inPostings.readInt();
                // store document id without gap
                docIDGapless += docID;
                // store term frequency
                int tf = inPostings.readInt();
                // loop for getting positions
                for(int j = 0; j < tf; j++){
                    // get position
                    int tempPosition = inPostings.readInt();
                    // store position without gap
                    posGapless += tempPosition;
                    // store position in list
                    positions.add(posGapless);
                }
                // create posting
                Posting p = new Posting(docIDGapless, positions);
                // add posting to list of postings
                results.add(p);
            }
            // close postings file
            inPostings.close();
        } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }

        return results;
    }

    public List<Posting> getPostingsTftd(String term) {
        // list to store postings
        List<Posting> results = new ArrayList<>();
        // variable to store docID without gap
        int docIDGapless = 0;
        // postings position for term
        long posPostings = binSearchVocab(term);
        // file object for postings file
        File postingsFile = new File("index//postings.bin");

        try {
            // RandomAccessFile object
            RandomAccessFile inPostings = new RandomAccessFile(postingsFile, "rb");
            // seek to postings position
            inPostings.seek(posPostings);
            // store document frequency
            int df = inPostings.readInt();
            // loop for number of documents
            for(int i = 0; i < df; i++){
                // store document id
                int docID = inPostings.readInt();
                // store document id without gap
                docIDGapless += docID;
                // store term frequency
                int tf = inPostings.readInt();
                // change tf to 4 byte value and convert to long
                long tfByte = Long.valueOf(tf*4);
                // use seek to skip past positions
                inPostings.seek(tfByte);
                // create posting
                Posting p = new Posting(docIDGapless, tf);
                // add posting to list of postings
                results.add(p);
                }
                // close postings file
                inPostings.close();
        } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }

        return results;
    }

    @Override
    public List<String> getVocabulary() {
        // get keySet from HashMap and convert to list of string
        List<String> vocab = new ArrayList<String>(mHmap.keySet());
        // sorts the vocabulary list
        Collections.sort(vocab);

        return vocab;
    }

    public long binSearchVocab(String term){
        // create file objects
        File vocabTableFile = new File("index//vocabTable.bin");
        File vocabFile = new File("index//vocab.bin");

        try {
            // create RandomAccessFile objects
            RandomAccessFile inVocabTable = new RandomAccessFile(vocabTableFile, "rb");
            RandomAccessFile inVocab = new RandomAccessFile(vocabFile, "rb");
            // get length of vocabTableFile
            long len = vocabTableFile.length()/16;
            // variables to assist iterating
            long i = 0;
            long j = len-1;
            
            // loop to iterate through VocabTableFile
            while (i <= j){
                // m is the position of a vocab term; m is in the middle
                long m = (i+j)/2;
                // seek to m position
                inVocabTable.seek(m);
                // store byte position of vocab term
                long posVocab = inVocabTable.readLong();
                // seek to vocab term position
                inVocab.seek(posVocab);
                // store vocab term
                String tempTerm = inVocab.readUTF();
                
                // compare terms if 0 then there equal
                if(term.compareTo(tempTerm) == 0){
                        // store postings position
                        long posPostings = inVocabTable.readLong();
                        // return postings position
                        return posPostings;
                } else if (term.compareTo(tempTerm) < 0){	// term comes before tempTerm so adjust j
                        j=m;
                } else {					// term comes after tempTerm so adjust i
                        i=m;
                }		
            }
            // close files
            inVocabTable.close();
            inVocab.close();
        } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        return 0; 
    }
    
    public void addTerm(String term, int documentId, int position) {
		// ArrayList to get the values for the key (term)
	    ArrayList<Posting> values = mHmap.get(term);

	    // if term isn't in HashMap
	    if(values == null) {
	    	// create an empty ArrayList
	    	values = new ArrayList<Posting>();
	    	// create a posting
	    	Posting p = new Posting(documentId,position);
	    	// add posting to ArrayList
	    	values.add(p);
	    	// insert posting into HashMap
	    	mHmap.put(term, values);
	    } 
	    // term is in index
	    else {
	        // if documentId isn't in values add it
	        if(values.get(values.size() - 1).getDocumentId() != documentId){
	        	// create a posting
		    	Posting p = new Posting(documentId,position);
	        	mHmap.get(term).add(p);
	        }
	        // term and documentId already exists -> add new position
	        else {
	        	mHmap.get(term).get(values.size()-1).addPosition(position);
	        }
	    }
	}
	
}
