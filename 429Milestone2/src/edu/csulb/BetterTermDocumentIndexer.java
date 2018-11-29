package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.documents.DocumentScore;
import cecs429.index.DefaultWeight;
import cecs429.index.DiskIndexWriter;
import cecs429.index.DiskPositionalIndex;
import cecs429.index.Index;
import cecs429.index.OkapiWeight;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.index.TFIDFWeight;
import cecs429.index.WackyWeight;
import cecs429.index.WeightingStrategy;
import cecs429.query.BooleanQueryParser;
import cecs429.text.EnglishTokenStream;
import cecs429.text.NonAlphaProcessor;
import java.awt.BorderLayout;
import java.awt.Dimension;

import java.io.File;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class BetterTermDocumentIndexer {
	private static String query = "";
	private static JTextField inputQuery = new JTextField();
	private static DefaultListModel<String> listModel = new DefaultListModel<>();
	private static JList resultsList;
	private static JFrame f = new JFrame();
	private static JPanel panel = new JPanel();
	private static JPanel buttonPanel = new JPanel();
	private static JPanel resultsPanel = new JPanel();
	private static JScrollPane scroller;
	private static List<Posting> results = new ArrayList<>();
	private static WeightingStrategy ws;

	public static void main(String[] args) {
		String articlesDirectory = getDirectoryPathGUI();
		LoadDirectory(articlesDirectory);
	}

	public static void startGUI(Index indx, DocumentCorpus corp){
		f.setSize(1200, 625);

		JButton quitButton = new JButton("Quit");
		JButton queryButton = new JButton("Boolean");
		JButton rankedButton = new JButton("Ranked");
		JButton stemButton = new JButton("Stem");
		JButton indexButton = new JButton("Index");
		JButton vocabButton = new JButton("Vocab");

		class vocabButtonListener implements ActionListener{
			public void actionPerformed (ActionEvent event){
				vocabOption(f, indx);

			}
		}

		class quitButtonListener implements ActionListener{
			public void actionPerformed (ActionEvent event){
				quitOption();
			}
		}

		class indexButtonListener implements ActionListener{
			public void actionPerformed (ActionEvent event){
				String articlesDirectory = getDirectoryPathGUI();
				LoadDirectory(articlesDirectory);
			}
		}

		class stemButtonListener implements ActionListener{
			public void actionPerformed (ActionEvent event){
				submitAction();
				stemOption(panel, query);
			}
		}

		class rankedButtonListener implements ActionListener{
			public void actionPerformed (ActionEvent event){
				submitAction();
				DiskPositionalIndex dpi = new DiskPositionalIndex();
				rankedCorpus(dpi, corp);
			}
		}

		class txtFieldListener implements ActionListener{
			public void actionPerformed (ActionEvent event){
				submitAction();
				queryCorpus(indx, corp);
			}
		}

		class ListSelectionListener implements MouseListener{
			@Override
			public void mouseClicked(MouseEvent e) {
				displayContent(corp.getDocument(results.get(resultsList.getSelectedIndex()).getDocumentId()).getContent());
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		}

		vocabButtonListener vocab = new vocabButtonListener();
		vocabButton.addActionListener(vocab);
		indexButtonListener reIndex = new indexButtonListener();
		indexButton.addActionListener(reIndex);
		quitButtonListener quit = new quitButtonListener();
		quitButton.addActionListener(quit);
		stemButtonListener stem = new stemButtonListener();
		stemButton.addActionListener(stem);
		rankedButtonListener ranked = new rankedButtonListener();
		rankedButton.addActionListener(ranked);


		txtFieldListener txtField = new txtFieldListener();
		queryButton.addActionListener(txtField);
		inputQuery.setPreferredSize(new Dimension(450, 30));

		JTextArea helper = new JTextArea("Press Query for regular query. Press other buttons for special query");
		helper.setEditable(false);

		buttonPanel.add(queryButton);
		buttonPanel.add(quitButton);
		buttonPanel.add(stemButton);
		buttonPanel.add(rankedButton);
		buttonPanel.add(indexButton);
		buttonPanel.add(vocabButton);

		resultsList = new JList<>(listModel);
		scroller = new JScrollPane(resultsList);
		scroller.setPreferredSize(new Dimension(700, 450));
		resultsPanel.add(scroller, BorderLayout.PAGE_START);
		panel.add(helper);
		panel.add(inputQuery);
		f.add(panel, BorderLayout.CENTER);
		f.add(resultsPanel, BorderLayout.PAGE_START);
		f.add(buttonPanel, BorderLayout.PAGE_END);

		resultsList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionListener listListener = new ListSelectionListener();
		resultsList.addMouseListener(listListener);

		resultsPanel.setVisible(true);
		panel.setVisible(true);
		f.setVisible(true);
	}

	/* Searches for terms in corpus by boolean method.*/
	public static void queryCorpus(Index indx, DocumentCorpus corp){
		listModel.removeAllElements();
		resultsList.removeAll();
		BooleanQueryParser bqp = new BooleanQueryParser();

		if(query.equals("quit")){
			System.exit(0);
		}
		else if(query == null){
			final JDialog dialog7 = new JDialog(f, "Query Error", true);
			JLabel queryingErr = new JLabel("Please enter a valid query");
			dialog7.add(queryingErr);
			dialog7.setSize(250, 100);
			dialog7.setLocationRelativeTo(null);
			dialog7.setVisible(true);
			dialog7.setResizable(false);
		}
		else{
			results = bqp.parseQuery(query).getPostings(indx);
			if(results.size() <= 0 ){
				System.out.println("No documents contain the query: " + query); 
			}
			else{
				int c = 1;
				for (Posting p : results) {
					listModel.addElement("Document #"+ c + ": " + corp.getDocument(p.getDocumentId()).getTitle());
					System.out.println("Document #"+ c + ": " + corp.getDocument(p.getDocumentId()).getTitle());
					c++;
				}

			}
			final JDialog dialog3 = new JDialog(f, "Querying Complete", true);
			JLabel queryingDone = new JLabel("Number of documents returned: "+ results.size());
			dialog3.add(queryingDone);
			dialog3.setSize(250, 100);
			dialog3.setLocationRelativeTo(null);
			dialog3.setVisible(true);
			dialog3.setResizable(false);
		}
	}

	/* Searches for terms in corpus by ranked method; only returning the top 10 documents and their score.*/
	public static void rankedCorpus(Index indx, DocumentCorpus corp){
		listModel.removeAllElements();
		resultsList.removeAll();

		if(query.equals("quit")){
			System.exit(0);
		}
		else if(query == null){
			final JDialog dialog7 = new JDialog(f, "Query Error", true);
			JLabel queryingErr = new JLabel("Please enter a valid query");
			dialog7.add(queryingErr);
			dialog7.setSize(250, 100);
			dialog7.setLocationRelativeTo(null);
			dialog7.setVisible(true);
			dialog7.setResizable(false);
		}
		else{
			List<Posting> res = rankedRetrieval(query, indx, ws);

			if(res.size() <= 0 ){
				System.out.println("No documents contain the query: " + query); 
			}
			else{
				for (Posting p : res) {
					listModel.addElement("Document Name: " + corp.getDocument(p.getDocumentId()).getTitle() + " Document Score: "+ p.getPositions());
					System.out.println("Document Name: " + corp.getDocument(p.getDocumentId()).getTitle() + " Document Score: " + p.getPositions());
				}
			}
			final JDialog dialog3 = new JDialog(f, "Ranked Querying Complete", true);
			JLabel queryingDone = new JLabel("Number of documents returned: "+ res.size());
			dialog3.add(queryingDone);
			dialog3.setSize(250, 100);
			dialog3.setLocationRelativeTo(null);
			dialog3.setVisible(true);
			dialog3.setResizable(false);
		}
	}

	public static void submitAction(){
		query = inputQuery.getText();
	}

	private static Index indexCorpus(DocumentCorpus corpus) {
		long start = System.currentTimeMillis();

		JFrame f = new JFrame();

		final JDialog dialog = new JDialog(f, "Indexing the directory...", false);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.setSize(250, 0);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.setResizable(false);

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

				processorTokenList = processor.processToken(t);
				int postTokenListSize = processor.getTokenListSize();

				if(postTokenListSize > 1){
					for (int i = 0; i < postTokenListSize; i++){
						index.addTerm(processorTokenList.get(i),d.getId(),position);
					}	
					position++;
				}
				else {
					index.addTerm(processorTokenList.get(postTokenListSize-1),d.getId(),position);
					position++;
				}

			}
			ets.close();
		}

		dialog.setVisible(false);
		dialog.dispose();

		long end = System.currentTimeMillis();

		final JDialog dialog2 = new JDialog(f, "Indexing Completed", true);
		JLabel indexingDone = new JLabel("Finished indexing in " + ((end - start)/1000) + " seconds.\n");
		dialog2.add(indexingDone);
		dialog2.setSize(250, 100);
		dialog2.setLocationRelativeTo(null);
		dialog2.setVisible(true);
		dialog2.setResizable(false);

		return index;
	}

	/*Loads all .json files in the directory and calls indexCorpus to index all tokens. Once finished, calls
    queryCorpus to begin querying for terms or special queries */
	public static void LoadDirectory(String dirName){
		DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(dirName).toAbsolutePath(), ".json");
		Index index = indexCorpus(corpus);
		chooseRankSystemGUI("index\\", corpus);
		DiskIndexWriter indexWriter = new DiskIndexWriter(index, "index\\", ws);
		indexWriter.writeIndex();
		startGUI(index, corpus);
	}

	public static void chooseRankSystemGUI(String path, DocumentCorpus corp){
		JFrame chooseF = new JFrame();
		chooseF.setSize(400, 175);
		JPanel cPanel = new JPanel();
		JButton defaultButton = new JButton("Default Ranking");
		JButton tfidfButton = new JButton("TF-IDF Ranking");
		JButton okapiButton = new JButton("Okapi BM25 Ranking");
		JButton wackyButton = new JButton("Wacky Ranking");

		JTextArea helper = new JTextArea("Press how you would like to rank documents and queries");
		helper.setEditable(false);

		class defaultButtonListener implements ActionListener{
			public void actionPerformed (ActionEvent event){
				// set weighting strategy type
				ws = new DefaultWeight(path, corp);

				chooseF.setVisible(false);
			}
		}

		class tfidfButtonListener implements ActionListener{
			public void actionPerformed (ActionEvent event){
				// set weighting strategy type
				ws = new TFIDFWeight(path, corp);

				chooseF.setVisible(false);
			}
		}

		class okapiButtonListener implements ActionListener{
			public void actionPerformed (ActionEvent event){
				// set weighting strategy type
				ws = new OkapiWeight(path, corp);

				chooseF.setVisible(false);
			}
		}

		class wackyButtonListener implements ActionListener{
			public void actionPerformed (ActionEvent event){
				// set weighting strategy type
				ws = new WackyWeight(path, corp);

				chooseF.setVisible(false);
			}
		}

		defaultButtonListener def = new defaultButtonListener();
		defaultButton.addActionListener(def);
		tfidfButtonListener tfidf = new tfidfButtonListener();
		tfidfButton.addActionListener(tfidf);
		okapiButtonListener okapi = new okapiButtonListener();
		okapiButton.addActionListener(okapi);
		wackyButtonListener wacky = new wackyButtonListener();
		wackyButton.addActionListener(wacky);

		cPanel.add(helper, BorderLayout.NORTH);
		cPanel.add(defaultButton, BorderLayout.SOUTH);
		cPanel.add(tfidfButton, BorderLayout.SOUTH);
		cPanel.add(okapiButton, BorderLayout.SOUTH);
		cPanel.add(wackyButton, BorderLayout.SOUTH);

		cPanel.setVisible(true);
		chooseF.add(cPanel);
		chooseF.setVisible(true);
	}

	public static void quitOption(){
		System.exit(0);
	}

	public static void stemOption(JPanel panel, String command){
		String stemmedToken = NonAlphaProcessor.stemToken(command);
		final JDialog dialog4 = new JDialog(f, "Finished Stemming", true);
		JLabel stemmingDone = new JLabel("The stem of " + command + " is: " + stemmedToken);
		dialog4.add(stemmingDone);
		dialog4.setSize(250, 100);
		dialog4.setLocationRelativeTo(null);
		dialog4.setVisible(true);
		dialog4.setResizable(false);
	}

	public static void vocabOption(JFrame f, Index indx){
		listModel.removeAllElements();
		resultsList.removeAll();
		ArrayList<String> vocab = new ArrayList<String>(indx.getVocabulary().subList(0, 1000));
		for(int i = 0; i < 1000 && i < vocab.size(); i++){
			listModel.addElement(vocab.get(i));
		}

		final JDialog dialog5 = new JDialog(f, "Vocab List Retrieved", true);
		JLabel vocabList = new JLabel("Total vocabulary values in vocab list: " + indx.getVocabulary().size());
		dialog5.add(vocabList);
		dialog5.setSize(400, 100);
		dialog5.setLocationRelativeTo(null);
		dialog5.setVisible(true);
		dialog5.setResizable(false);

	}

	public static void displayContent(Reader r){

		StringBuilder builder = new StringBuilder();
		EnglishTokenStream ets = new EnglishTokenStream(r);
		Iterable<String> tokens = ets.getTokens();
		for(String s : tokens){
			builder.append(s + " ");
		}

		String stringReadFromReader = builder.toString();
		System.out.println(stringReadFromReader);

		JFrame docFrame = new JFrame();
		docFrame.setTitle("Document Contents");
		docFrame.setSize(600, 600);
		JTextArea txtArea = new JTextArea(stringReadFromReader);
		txtArea.setLineWrap(true);
		txtArea.setWrapStyleWord(true);
		txtArea.setEditable(false);
		JScrollPane docScroll = new JScrollPane(txtArea);
		docFrame.add(docScroll);
		docFrame.setVisible(true);

		ets.close();
	}

	public static String getDirectoryPathGUI() {

		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Choose a directory to index...");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			return selectedFile.getAbsolutePath();
		}

		return null;
	}

	public static List<Posting> rankedRetrieval(String query, Index i, WeightingStrategy strat){
		// results of list postings
		List<Posting> result = new ArrayList<>();

		// split query into separate terms
		String termList[] = query.split(" ");

		// HashMap to store documents accumulator value
		HashMap<Integer, Double> docAcMap = new HashMap<Integer, Double>();

		//iterate through terms
		for(String t: termList){
			// store postings in a list variable
			List<Posting> postingList = i.getPostingsTftd(t);

			// calculate Wqt
			double Wqt = strat.getWqt(postingList);

			// iterate through documents
			for (Posting p : postingList) {
				// initialize an accumulative value
				double Ad = 0.0;

				// if document already has accumulative value then retrieve it
				if (docAcMap.get(p.getDocumentId()) != null) {
					// store accumulative value for document
					Ad = docAcMap.get(p.getDocumentId());
				}

				// calculate term frequency in that document
				double tftd = (double) p.getPositions().get(0);
				
				// variable to store Wdt value
				double Wdt = 0.0;
				
				// use appropriate getWdt method based on the weighting strategy
				if(ws instanceof cecs429.index.DefaultWeight || ws instanceof cecs429.index.TFIDFWeight){
					// calculate Wdt
					Wdt = strat.getWdt(tftd);					
				} 
				else {
					// calculate Wdt
					Wdt = strat.getWdt(tftd, p.getDocumentId());	
				}

				// increase Ad
				Ad += (Wdt*Wqt);

				// store documents Ad
				docAcMap.put(p.getDocumentId(), Ad);
			}
		}

		// construct a priority queue
		PriorityQueue<DocumentScore> pq = new PriorityQueue<>();

		// store DocId's
		ArrayList<Integer> sortedDocId = new ArrayList<Integer>(docAcMap.keySet());

		// sort the DocId's
		Collections.sort(sortedDocId);

		// iterate through docId's to evaluate score
		for (int docId: sortedDocId){
			// read ld from document weight file
			double ld = strat.getLd(docId);

			// get accumulator for that document
			double temAd = docAcMap.get(docId);

			// score value
			double score = 0.0;

			// if accumulator value is not zero
			if (temAd != 0.0){
				// divide accumulator value by ld
				score = temAd/ld;
			}

			// store score value in priority queue
			pq.add(new DocumentScore(score,docId));
		}

		// get top 10 scores
		for(int c = 0;c < 10; c++){
			// get maximum document score
			DocumentScore tempScoreHolder = pq.poll();

			// store posting in a list
			result.add(new Posting(tempScoreHolder.getValue(),tempScoreHolder.getKey()));
		}

		return result;
	}

}