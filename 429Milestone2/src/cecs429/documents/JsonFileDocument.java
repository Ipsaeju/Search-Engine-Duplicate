package cecs429.documents;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;

import com.google.gson.Gson;

/**
 * Represents a document saved as a JSON file on the local file system.
 */
public class JsonFileDocument implements FileDocument {
	private int mDocumentId;
	private String mTitle;
	private Path mFilePath;
	
	/**
	 * Constructs a JsonFileDocument with the given document ID representing the file at the given
	 * absolute file path.
	 */
	public JsonFileDocument(int id, Path absoluteFilePath) {
		mDocumentId = id;
		mFilePath = absoluteFilePath;
	}
	
	@Override
	public int getId() {
		return mDocumentId;
	}

	@Override
	public Reader getContent() {
		Gson gson = new Gson();
		
		try (Reader reader = new FileReader(mFilePath.toString())){
			JSONDocument p = gson.fromJson(reader, JSONDocument.class);
			StringReader sreader = new StringReader(p.body);
			return sreader;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}

	@Override
	public String getTitle() {
		Gson gson = new Gson();
		
		try (Reader reader = new FileReader(mFilePath.toString())){
			JSONDocument p = gson.fromJson(reader, JSONDocument.class);
			mTitle = p.title;
			return mTitle;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	@Override
	public Path getFilePath() {
		return mFilePath;
	}
	
	public static FileDocument loadJSONFileDocument(Path absolutePath, int documentId) {
		return new JsonFileDocument(documentId, absolutePath);
	}

}