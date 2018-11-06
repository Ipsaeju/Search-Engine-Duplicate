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

    public DiskIndexWriter(Index index, String path, DocumentCorpus corp){
        mIndex = index;
        mVocab = mIndex.getVocabulary();
        mPath = path;
        corpus = corp;
        mPostingsHmap = new HashMap<String, Long>();
        mVocabsHmap = new HashMap<String, Long>();
    }

    public void writeIndex(int weightChoice){
        createPostingsBin();
        createVocabBin();
        createVocabTableBin();
        createDocWeightsBin(weightChoice);
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

                int docFreq = post.size();
                out.writeInt(docFreq);
                // get a posting from postings list
                for(Posting p : post){	
                    // get postings id
                    pID = p.getDocumentId() - prevID;
                    prevID = p.getDocumentId();
                    // get postings positions
                    ArrayList<Integer> positions = p.getPositions();

                    // write posting id into binary file
                    out.writeInt(pID);

                    // get a position from positions list
                    for(int pos: positions){
                        currPos = pos - prevPos;
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

    public void createDocWeightsBin(int choice){
        switch(choice){
            case 1: DefaultWeight defW = new DefaultWeight();
            defW.docWeight(mPath, corpus);
            break;
            case 2: TFIDFWeight tfidfW = new TFIDFWeight();
            tfidfW.docWeight(mPath, corpus);
            break;
            case 3: OkapiWeight okapiW = new OkapiWeight();
            okapiW.docWeight(mPath, corpus);
            break;
            case 4: WackyWeight wackyW = new WackyWeight();
            wackyW.docWeight(mPath, corpus);
            break;
            default: System.out.println("Please choose a valid weighting system");
            break;
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