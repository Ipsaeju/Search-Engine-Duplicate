package cecs429.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class DiskPositionalIndex implements Index{

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

                // if term is equal to term in vocabBin return it's postings position
                if(term.equals(tempTerm)){
                    // store postings position
                    long posPostings = inVocabTable.readLong();

                    // return postings position
                    return posPostings;
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
	
}