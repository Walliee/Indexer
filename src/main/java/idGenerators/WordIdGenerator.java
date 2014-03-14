package idGenerators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WordIdGenerator implements Serializable {
	private static final long serialVersionUID = -1800257312679737729L;
	public int wordRegistrarBaseSize = 1000000;
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
			fileWriter.write((word+" $$ "+invertedWordMap.get(word)));
			fileWriter.write("\n");
		}
		fileWriter.close();
	}
  
  public synchronized int register(String word){
  	if(!invertedWordMap.containsKey(word)){
  		wordID++;
  	  invertedWordMap.put(word, wordID);
  	  return wordID;
  	}
  	return invertedWordMap.get(word);
  }
}
