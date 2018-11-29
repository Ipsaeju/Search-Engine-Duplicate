package cecs429.index;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiskIndexWriter{
	
    private List<String> mVocab;
    private String mPath;
    private Index mIndex;
    private HashMap<String, Long> mPostingsHmap;
    private HashMap<String, Long> mVocabsHmap;
    private WeightingStrategy mStrat;

    public DiskIndexWriter(Index index, String path, WeightingStrategy strat){
        mIndex = index;
        mVocab = mIndex.getVocabulary();
        mPath = path;
        mPostingsHmap = new HashMap<String, Long>();
        mVocabsHmap = new HashMap<String, Long>();
        mStrat = strat;
    }

    public void writeIndex(){
        createPostingsBin();
        createVocabBin();
        createVocabTableBin();
        mStrat.createDocWeightBin();
    }

    public void createPostingsBin(){
        try {
            // create binary file
            DataOutputStream out = new DataOutputStream(new FileOutputStream(mPath + "postings.bin"));
            int pID = 0;
            int prevID = 0;
            int currPos = 0;
            int prevPos = 0;
            // get a term from vocab list
            for(String vocab : mVocab){
                // store term and where it started
                mPostingsHmap.put(vocab, (long) out.size());

                // store postings for term
                List<Posting> post = mIndex.getPostings(vocab);

                // get document frequency
                int docFreq = post.size();
                // write document frequency onto postings file
                out.writeInt(docFreq);
                
                // get a posting from postings list
                for(Posting p : post){	
                    // get postings id with gap
                    pID = p.getDocumentId() - prevID;
                    
                    // store latest posting id
                    prevID = p.getDocumentId();
                    
                    // get postings positions
                    ArrayList<Integer> positions = p.getPositions();

                    // write posting id into binary file
                    out.writeInt(pID);

                    // get a position from positions list
                    for(int pos: positions){
                    	// get position with gap
                        currPos = pos - prevPos;
                        
                        // store latest position
                        prevPos = pos;
                        
                        // write position into binary file
                        out.writeInt(currPos);
                    }
                }	
            }
            // close binary file
            out.close();
        } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
    }

    public void createVocabBin(){
        try {
            // create binary file
            DataOutputStream out = new DataOutputStream(new FileOutputStream(mPath + "vocab.bin"));

            // get a term from vocab list
            for(String vocab : mVocab){
                // store term and where it started
                mVocabsHmap.put(vocab, (long) out.size());
                // write term to file encoded using UTF-8
                out.writeUTF(vocab);	
            }

            // close binary file
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createVocabTableBin(){
        try {
            // create binary file
            DataOutputStream out = new DataOutputStream(new FileOutputStream(mPath + "vocabTable.bin"));

            // get a term from vocab list
            for(String vocab : mVocab){
                // write byte position of term in vocab.bin
                out.writeLong(mVocabsHmap.get(vocab));
                // write byte position of term in postings.bin
                out.writeLong(mPostingsHmap.get(vocab));
            }

            // close binary file
            out.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}