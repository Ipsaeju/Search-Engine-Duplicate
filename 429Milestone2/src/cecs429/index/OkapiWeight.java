package cecs429.index;

import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.text.EnglishTokenStream;
import cecs429.text.NonAlphaProcessor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class OkapiWeight implements WeightingStrategy{

    private String mPath;
    private DocumentCorpus mCorp;
	
	public OkapiWeight(String path, DocumentCorpus corp){
		mPath = path;
		mCorp = corp;
	}

	@Override
	public void createDocWeightBin() {
		try {
			// create binary file for document weights
			DataOutputStream out = new DataOutputStream(new FileOutputStream(mPath + "okapidocWeights.bin"));
			
			// create binary file for average docLength
			DataOutputStream outAveDocLength = new DataOutputStream(new FileOutputStream(mPath + "aveDocLength.bin"));

			// variable to hold average document length
			double aveDocLength = 0.0;
			
			// list to hold document's length
			List<Double> aveDocLengthList = new ArrayList<>();
			
			// iterate through all of the documents
			for(Document d : mCorp.getDocuments()){
				double ld = 0.0;
				double tf = 0.0;
				int termCounter = 0;						// variable to store # of terms
				double docLength = 0.0;
				double docByteSize = 0.0;
				double aveTF = 0.0;							// variable to store the average tf
				List<Double> tfList = new ArrayList<>();	// variable to store a terms frequency

				//Create a new hashmap containing all the vocab in a document and the amount of times it shows on the doc
				HashMap<String, Integer> weight = new HashMap<String, Integer>();

				// token processor object
				NonAlphaProcessor processor = new NonAlphaProcessor();
				// token stream
				EnglishTokenStream ets = new EnglishTokenStream(d.getContent());
				// token iterable
				Iterable<String> tokens = ets.getTokens();
				
				// for loop to process through each term
				for(String t : tokens){
					// list of processed terms from token
					ArrayList<String> processorTokenList = new ArrayList<String>();
					// store processed terms
					processorTokenList = processor.processToken(t);
					// variable to get # of terms
					int postTokenListSize = processor.getTokenListSize();

					// if statement for more than one term
					if(postTokenListSize > 1){
						for (int i = 0; i < postTokenListSize; i++){
							// if term is already in hashmap, increment frequency by 1
							if(weight.containsKey(processorTokenList.get(i))){
								weight.put(processorTokenList.get(i), weight.get(processorTokenList.get(i)) + 1);
							}
							// otherwise, put term in and set frequency to 1
							else{
								weight.put(processorTokenList.get(i), 1);
							}
							
							// increment term counter
							termCounter += 1;
							
							// increment byte size for the document
							docByteSize += processorTokenList.get(i).getBytes().length;
						}
					}
					// else statement for only one term
					else {
						// if term is already in hashmap, increment frequency by 1
						if(weight.containsKey(processorTokenList.get(postTokenListSize-1))){
							weight.put(processorTokenList.get(postTokenListSize-1), weight.get(processorTokenList.get(postTokenListSize-1)) + 1);
						}
						// otherwise, put term in and set frequency to 1
						else{
							weight.put(processorTokenList.get(postTokenListSize-1), 1);
						}
						
						// increment term counter
						termCounter += 1;
						
						// increment byte size for the document
						docByteSize += processorTokenList.get(postTokenListSize-1).getBytes().length;
					}
				}
				
				// close stream
				ets.close();
				
				// get all terms from that doc and put in set
				Set<String> termSet = weight.keySet();
				
				// for every term in the document, get the term frequency calculated and calculate wdt
				// then square result and add it to total summation of squared wdts
				for(String term : termSet){
					tf = (double) weight.get(term);
					tfList.add(tf);
				}
				
				//Calculate Ld and write it to the file
				ld = 1.0;
				
				// write Ld to weight file
				out.writeDouble(ld);
				
				// cast term counter to double value
				docLength = (double) termCounter;
				
				// add docLength to list to calculate the average later
				aveDocLengthList.add(docLength);

				// write docLength to weight file
				out.writeDouble(docLength);
				
				// write docByteSize to weight file
				out.writeDouble(docByteSize);
				
				// variable to hold sum of all tf's
		        double tfSum = 0.0;
		        
		        // for loop to add total sum of tf
		        for (Double i : tfList) {
		            tfSum += i;
		        }
		        
		        // calculate the aveTF
		        aveTF = tfSum / tfList.size();
		        
		        // write aveTF to weight file
				out.writeDouble(aveTF);
				
				//Clear the hashmap for the next document
				weight.clear();
			}
			
			// variable to hold sum of all docLength's
	        double docLengthSum = 0.0;
	        
	        // for loop to add total sum of tf
	        for (Double i : aveDocLengthList) {
	            docLengthSum += i;
	        }
	        
	        // calculate the aveDocLength
	        aveDocLength = docLengthSum / aveDocLengthList.size();
	        
	        // write aveDocLength to weight file
			outAveDocLength.writeDouble(aveDocLength);

			// close binary file
			out.close();
			
			// close aveDocLength binary file
			outAveDocLength.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public double getWdt(double tftd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getWdt(double tftd, int docID) {
		double docLength = 0.0;
		double aveDocLength = 0.0;
		
        // file object for average document length file
        File aveDocLengthFile = new File("index//aveDocLength.bin");
        
        // file object for document weights file
        File weightsFile = new File("index//okapidocWeights.bin");

        try {
            // RandomAccessFile object
            RandomAccessFile aveDocLengthRF = new RandomAccessFile(aveDocLengthFile, "r");
            
            // RandomAccessFile object
            RandomAccessFile inWeights = new RandomAccessFile(weightsFile, "r");
			
            // change docID to 32 byte value; convert to long; add 8 to get correct position for docLength
			long docIdByte = Long.valueOf((docID*32)+8);

			// get position of docLength
			long pos = docIdByte;

			// seek to that docLength
			inWeights.seek(pos);

			// read docLength from document weight file
			docLength = inWeights.readDouble();
        
			// read aveDocLength from average document file
			aveDocLength = aveDocLengthRF.readDouble();
        
            // close weights file
            aveDocLengthRF.close();

            // close weights file
            inWeights.close();
        
        } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
		
		// calculate wdt
		double wdt = ((2.2*tftd)/(1.2*(0.25+0.75*(docLength/aveDocLength))+tftd));
		
		return wdt;
	}

	public double getWqt(List<Posting> dft) {
		// initiate a variable to store wqt
		double wqt = 0.0;
		
		// variable to store # of documents
		double n = (double) ((Collection<Document>) mCorp.getDocuments()).size();
		
		// store size of dft
		double dftSize = (double) dft.size();

		// calculate wqt
		wqt = Math.max(0.1, Math.log((n - dftSize + 0.5)/(dftSize + 0.5)));
		
		return wqt;
	}

	public double getLd(int docID) {
		// initiate a variable to store ld
		double ld = 0.0;
		
        // file object for postings file
        File weightsFile = new File("index//okapidocWeights.bin");

        try {
            // RandomAccessFile object
            RandomAccessFile inWeights = new RandomAccessFile(weightsFile, "r");
			
            // change docID to 32 byte value and convert to long
			long docIdByte = Long.valueOf(docID*32);

			// get position of docLd
			long pos = docIdByte;

			// seek to that docLd
			inWeights.seek(pos);

			// read ld from document weight file
			ld = inWeights.readDouble();
        
            // close weights file
            inWeights.close();
        
        } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        
		return ld;
	}

}
