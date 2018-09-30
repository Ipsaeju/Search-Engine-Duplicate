package cecs429proj1;
import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.documents.JSONDocument;
import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.PositionalInvertedIndex1;
import cecs429.index.Posting;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.Stemmer;
import cecs429.text.BasicTokenProcessor1;
import cecs429.text.EnglishTokenStream;
import cecs429.text.NonAlphaProcessor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import java.util.Collections;
import java.util.List;


public class BetterTermDocumentIndexer {
	
	public ArrayList<JSONDocument> documents;
	
	public static void main(String[] args) {
            Gson gson = new Gson();
            Scanner scan = new Scanner(System.in);
            System.out.print("Enter a name for the directory: ");
            String articlesDirectory = scan.nextLine();
                LoadDirectory(articlesDirectory);
//		Scanner scan = new Scanner(System.in);
//		
//		System.out.print("Enter a File Path or File Name of the Corpus: ");
//		String directoryFile = scan.nextLine();
//		String articlesDirectory = "";
//		
//		try(Reader reader = new FileReader(directoryFile)){
//			BetterTermDocumentIndexer p = gson.fromJson(reader, BetterTermDocumentIndexer.class);
//			ArrayList<JSONDocument> d = p.documents;
//			int i = 1;
//			System.out.print("Enter a name for the directory: ");
//			articlesDirectory = scan.nextLine();
//			Path path = Paths.get(articlesDirectory);
//			
//			if(Files.notExists(path)){
//				Files.createDirectories(path);
//			}
//			
//			System.out.println("Creating directory with articles...");
//			for(JSONDocument a : d){
//				try(FileWriter writer = new FileWriter(articlesDirectory + "\\article" + i + ".json")){
//					gson.toJson(a, writer);
//					i++;
//				}
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		System.out.println("Finished creating directory for articles.\n");
//		
//		DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(articlesDirectory).toAbsolutePath(), ".json");
//		
//		System.out.println("Indexing directories files...");
//		Index index = indexCorpus(corpus);
//		
//		System.out.println("Finished indexing.\n");
//                queryCorpus(index, corpus);
		
		
	}
        
        /* Searches for terms in corpus. If query contains ":" key for special queries, calls SpecialQueries method
        to perform specific command
        */
        public static void queryCorpus(Index indx, DocumentCorpus corp){
            // We aren't ready to use a full query parser; for now, we'll only support single-term queries.
		String query = "";
                Scanner scan = new Scanner(System.in);
		
		while(query.equals("quit") == false){				
			System.out.print("Enter a term: ");
			query = scan.nextLine();
			if(query.contains(":")){
			    SpecialQueries(query, indx);
			}
                        else{
                            if(indx.getPostings(query).size() <= 0 ){
                            System.out.println("No documents contain the query: " + query); 
                            
                            }
                            else{
                              for (Posting p : indx.getPostings(query)) {
				System.out.println("Document " + corp.getDocument(p.getDocumentId()).getTitle() + "Positions: " + p.getPositions());
                                }  
                            } 
                        }
		}
	
		scan.close();
        }
	
        //Temporary list to view what vocab list should look like, currently interferes with :index query command
        private static List<String> vocab = new ArrayList<String>();
	private static Index indexCorpus(DocumentCorpus corpus) {
		BasicTokenProcessor1 processor = new BasicTokenProcessor1();
                NonAlphaProcessor naProcessor = new NonAlphaProcessor();
                
		
		// Get all the documents in the corpus by calling GetDocuments().
		Iterable<Document> allDocs = corpus.getDocuments();
		
		// Construct an PositionalInvertedIndex
		PositionalInvertedIndex1 index = new PositionalInvertedIndex1();
		
		// addTerms to the PositionalInvertedIndex
		for (Document d : allDocs) {
			EnglishTokenStream ets = new EnglishTokenStream(d.getContent());
			Iterable<String> tokens = ets.getTokens();
			int position = 1;
			for (String t : tokens) {
				index.addTerm(processor.processToken(t),d.getId(),position);
                                //Temp call to add term to vocab
                                vocab.add(t);
				position++;
                                ets.close();
			}
                        
		}
		
		return index;
	}
        
        /*Loads all .json files in the directory and calls indexCorpus to index all tokens. Once finished, calls
        queryCorpus to begin querying for terms or special queries */
        public static void LoadDirectory(String dirName){
            DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(dirName).toAbsolutePath(), ".json");
		
            System.out.println("Indexing directories files...");
            Index index = indexCorpus(corpus);
            
            System.out.println("Finished indexing.\n");
            queryCorpus(index, corpus);
        }
        
        //Handles all specified special queries and performs the tasks
        public static void SpecialQueries(String command, Index indx){
            if(command.equalsIgnoreCase(":q")){
                System.exit(0);
            }
            else if(command.contains(":stem")){
                int spaceIndex = command.indexOf(" ");
                String parsedCommand = command.substring(spaceIndex + 1);
                Stemmer vocabStem = new Stemmer();
                for(int i = 0; i < parsedCommand.length(); i++){
                    vocabStem.add(parsedCommand.charAt(i));   
                }
                vocabStem.stem();
                System.out.println("The stem of " + parsedCommand + " is: " + vocabStem.toString());
            }
            else if(command.contains(":index")){
                int spaceIndex = command.indexOf(" ");
                String parsedCommand = command.substring(spaceIndex + 1);
                LoadDirectory(parsedCommand);
                
            }
            
            //TODO: Change assignments to use method getVocabulary() rather than global list
            else if(command.equalsIgnoreCase(":vocab")){
                int totalVocab = vocab.size();
                Collections.sort(vocab);
                System.out.println("The first 1000 vocabulary terms are");
                for(int i = 0; i < 1000; i++){
                    String vocabTerm = vocab.get(i);
                    System.out.println(vocabTerm);
                }
                System.out.println("Total vocabulary values in vocab list: " + totalVocab);
            }
        }
}
