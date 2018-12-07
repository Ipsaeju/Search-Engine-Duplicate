package cecs429.index;

import cecs429.documents.DocumentCorpus;
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
    private DocumentCorpus corpus;
    private final byte OFFSET = 2;

    public DiskIndexWriter(Index index, String path, DocumentCorpus corp){
        mIndex = index;
        mVocab = mIndex.getVocabulary();
        mPath = path;
        mPostingsHmap = new HashMap<String, Long>();
        mVocabsHmap = new HashMap<String, Long>();
        corpus = corp;
    }

    public void writeIndex(){
        DefaultWeight dw = new DefaultWeight(mPath, corpus);
        TFIDFWeight tfidf = new TFIDFWeight(mPath, corpus);
        OkapiWeight okapi = new OkapiWeight(mPath, corpus);
        WackyWeight wacky = new WackyWeight(mPath, corpus);
        createPostingsBin();
        createVocabBin();
        createVocabTableBin();
        dw.createDocWeightBin();
        tfidf.createDocWeightBin();
        okapi.createDocWeightBin();
        wacky.createDocWeightBin();
    }

    public void createPostingsBin(){
        try {
            // create binary file
            DataOutputStream out = new DataOutputStream(new FileOutputStream(mPath + "postings.bin"));
            // get a term from vocab list
            for(String vocab : mVocab){
                int pID = 0;
                int prevID = 0;
                int currPos = 0;
                int prevPos = 0;
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
                    
                    // write posting id into binary file
                    out.writeInt(pID);
                    
                    // get postings positions
                    ArrayList<Integer> positions = p.getPositions();
                    
                    // store tf in a variable
                    int tf = positions.size();
                    
                    // write tf into binary file
                    out.writeInt(tf);

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

            long sumval = 0;
            
            // get a term from vocab list
            for(String vocab : mVocab){
                // store term and where it started
                mVocabsHmap.put(vocab, sumval);
                
                byte[] vbytes = vocab.getBytes();
                sumval += (vbytes.length + OFFSET);
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