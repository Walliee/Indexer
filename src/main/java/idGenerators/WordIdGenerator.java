package idGenerators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class assigns each word to its word ID
 *
 */
public class WordIdGenerator implements Serializable {
	private static final long serialVersionUID = -1800257312679737729L;
  private Map<String, Integer> invertedWordMap;
  private int wordID;
  private FileWriter fileWriter;
  
  public WordIdGenerator(int wordIdGeneratorBaseSize, String path) throws IOException {
  	wordID = 0;
		File file = new File(path,"WordID");
		fileWriter = new FileWriter(file);
		invertedWordMap = new HashMap<String, Integer>(wordIdGeneratorBaseSize);
	}
  
  public void close() throws IOException{
  	for(String word: invertedWordMap.keySet() ){
			fileWriter.write((word+"$$"+invertedWordMap.get(word)));
			fileWriter.write("\n");
		}
		fileWriter.close();
	}
  
  public synchronized int register(String word){
  	//checks to see if word already has a wordID, if it has a 
  	//wordID returns wordID else assigns new wordID and returns it
  	if(!invertedWordMap.containsKey(word)){
  		wordID++;
  	  invertedWordMap.put(word, wordID);
  	  return wordID;
  	}
  	return invertedWordMap.get(word);
  }
}
