package cecs429.index;

import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WackyWeight implements WeightingStrategy{

    @Override
    public void docWeight(String mPath, DocumentCorpus corpus) {
        try{
            //Set up docWeights.bin file with the default weighing system
            DataOutputStream out = new DataOutputStream(new FileOutputStream(mPath + "docWeights.bin"));
            
            for(Document d : corpus.getDocuments()){
                //Gets contents of document and puts it in a string
                BufferedReader docReader = new BufferedReader(d.getContent());
                String doc = null;
                StringBuilder docToString = new StringBuilder();
                while((doc = docReader.readLine()) != null){
                    docToString.append(doc);
                }
                //Split the document into an array
                String[] docArr = doc.split(" ");
                //Create a new hashmap containing all the vocab in a document and the amount of times it shows on the doc
                HashMap<String, Integer> weight = new HashMap<String, Integer>();
                double ld = 0;
                double tf = 0;
                double avgtf = 0;
                double wdt = 0;
                double byteSize = 0;
                for(int i = 0; i < docArr.length; i++){
                    //if term is already in hashmap, increment frequency by 1
                    //otherwise, put term in and set frequency to 1
                    if(weight.containsKey(docArr[i])){
                        weight.put(docArr[i], weight.get(docArr[i] + 1));
                    }
                    else{
                        weight.put(docArr[i], 1);
                    }
                }
                //Get all terms from that doc and put in set
                Set<String> termSet = weight.keySet();
                //For every term in the document, get the term frequency calculated and calculate wdt
                for(String term : termSet){
                    tf = weight.get(term);
                    avgtf = tf / weight.size();
                    wdt = (1 + Math.log(tf) / (1 + Math.log(avgtf)));
                }
                //Calculate Ld and write it to the file
                byteSize = doc.getBytes().length;
                ld = wdt / Math.sqrt(byteSize);
                out.writeDouble(ld);
                out.writeDouble(wdt);
                out.writeDouble(avgtf);
                out.writeDouble(byteSize);
                //Clear the hashmap for the next document
                weight.clear();
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public double queryWeight(String query, DocumentCorpus corpus, Index index) {
        double corpSize = corpus.getCorpusSize();
        double df = 0;
        double wqt = 0;
        
        //For each term in the query, get the total wqt based on the wacky formula
        String[] splitQuery = query.split(" ");
        for(String term : splitQuery){
            df = index.getPostings(term).size();
            wqt += Math.max(0, Math.log((corpSize - df) / (df)));
        }
        return wqt;
    }

    public double getWDT(Posting p) {
        File weightsFile = new File("index//docWeights.bin");
        double wdt = 0.0;
        int docID = p.getDocumentId();
        try {
            // RandomAccessFile object
            RandomAccessFile inWeights = new RandomAccessFile(weightsFile, "rb");
            //Find the document via doc id
            inWeights.seek(docID);
            //Read value and set it as wdt
            wdt = inWeights.readDouble();
        }   catch (FileNotFoundException ex) {
                Logger.getLogger(DefaultWeight.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (IOException ex) {
            Logger.getLogger(DefaultWeight.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wdt;
    }
    
    @Override
    public double getLd(Posting p) {
        File weightsFile = new File("index//docWeights.bin");
        double wdt = 0.0;
        double ld = 0.0;
        int docID = p.getDocumentId();
        try {
            // RandomAccessFile object
            RandomAccessFile inWeights = new RandomAccessFile(weightsFile, "rb");
            //Find the document via doc id
            inWeights.seek(docID);
            //Read value and set it as wdt
            wdt = inWeights.readDouble();
            //Seek past wdt
            inWeights.seek((long) wdt);
            //Read value and set it as ld
            ld = inWeights.readDouble();
            
        }   catch (FileNotFoundException ex) {
                Logger.getLogger(DefaultWeight.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (IOException ex) {
            Logger.getLogger(DefaultWeight.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ld;
    }

}
