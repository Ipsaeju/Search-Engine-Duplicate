package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.text.EnglishTokenStream;
import cecs429.text.NonAlphaProcessor;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public class BetterTermDocumentIndexer {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter a name for the directory: ");
		String articlesDirectory = scan.nextLine();
		LoadDirectory(articlesDirectory);
		scan.close();
	}

	/* Searches for terms in corpus. If query contains ":" key for special queries, calls SpecialQueries method
    to perform specific command*/
	public static void queryCorpus(Index indx, DocumentCorpus corp){
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

	private static Index indexCorpus(DocumentCorpus corpus) {
		long start = System.currentTimeMillis();

		NonAlphaProcessor processor = new NonAlphaProcessor();

		// Get all the documents in the corpus by calling GetDocuments().
		Iterable<Document> allDocs = corpus.getDocuments();

		// Construct an PositionalInvertedIndex
		PositionalInvertedIndex index = new PositionalInvertedIndex();

		// addTerms to the PositionalInvertedIndex
		for (Document d : allDocs) {
			EnglishTokenStream ets = new EnglishTokenStream(d.getContent());
			Iterable<String> tokens = ets.getTokens();
			int position = 1;
			for (String t : tokens) {
				ArrayList<String> processorTokenList = new ArrayList<String>();
				
				int preTokenListSize = processor.getTokenListSize();
				processorTokenList = processor.processToken(t);
				int postTokenListSize = processor.getTokenListSize();
				
				for (int i = preTokenListSize-1; i < postTokenListSize; i++){					
					index.addTerm(processorTokenList.get(i),d.getId(),position);
					position++;
				}
			}

		}

		long end = System.currentTimeMillis();
		System.out.println("Finished indexing in " + ((end - start)/1000) + " seconds.");

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
			String stemmedToken = NonAlphaProcessor.stemToken(parsedCommand);
			System.out.println("The stem of " + parsedCommand + " is: " + stemmedToken);
		}
		else if(command.contains(":index")){
			int spaceIndex = command.indexOf(" ");
			String parsedCommand = command.substring(spaceIndex + 1);
			LoadDirectory(parsedCommand);  
		}
		else if(command.equalsIgnoreCase(":vocab")){
			System.out.println("Vocab");
			List<String> vocab = indx.getVocabulary();
			Collections.sort(vocab);
			for(int i = 0; i < 1000 && i < vocab.size(); i++){
				System.out.println(vocab.get(i));
			}
			System.out.println("Total vocabulary values in vocab list: " + vocab.size());
		}
	}
}
