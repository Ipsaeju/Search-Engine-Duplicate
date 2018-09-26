package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.documents.JSONDocument;
import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.EnglishTokenStream;
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


public class BetterTermDocumentIndexer {
	
	public ArrayList<JSONDocument> documents;
	
	public static void main(String[] args) {
		Gson gson = new Gson();
		Scanner scan = new Scanner(System.in);
		
		System.out.print("Enter a File Path or File Name of the Corpus: ");
		String directoryFile = scan.nextLine();
		String articlesDirectory = "";
		
		try(Reader reader = new FileReader(directoryFile)){
			BetterTermDocumentIndexer p = gson.fromJson(reader, BetterTermDocumentIndexer.class);
			ArrayList<JSONDocument> d = p.documents;
			int i = 1;
			System.out.print("Enter a name for the directory: ");
			articlesDirectory = scan.nextLine();
			Path path = Paths.get(articlesDirectory);
			
			if(Files.notExists(path)){
				Files.createDirectories(path);
			}
			
			System.out.println("Creating directory with articles...");
			for(JSONDocument a : d){
				try(FileWriter writer = new FileWriter(articlesDirectory + "\\article" + i + ".json")){
					gson.toJson(a, writer);
					i++;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Finished creating directory for articles.\n");
		
		DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(articlesDirectory).toAbsolutePath(), ".json");
		
		System.out.println("Indexing directories files...");
		Index index = indexCorpus(corpus) ;
		
		System.out.println("Finished indexing.\n");
		
		// We aren't ready to use a full query parser; for now, we'll only support single-term queries.
		System.out.print("Enter a term: ");
		String query = scan.next().trim().toLowerCase();
		
		while(query.equals("quit") == false){				
			
			if(index.getPostings(query).size() <= 0){
				System.out.println("No documents contain the query: " + query);
			}
			
			for (Posting p : index.getPostings(query)) {
				System.out.println("Document " + corpus.getDocument(p.getDocumentId()).getTitle() + "Positions: " + p.getPositions());
			}
			
			System.out.print("Enter a term: ");
			query = scan.next().trim().toLowerCase();
		}
	
		scan.close();
	}
	
	private static Index indexCorpus(DocumentCorpus corpus) {
		BasicTokenProcessor processor = new BasicTokenProcessor();
		
		// First, build the vocabulary hash set.
		
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
				index.addTerm(processor.processToken(t),d.getId(),position);
				position++;
			}
		}
		
		return index;
	}
}
