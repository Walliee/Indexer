package idGenerators;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public class DocIdGenerator implements Serializable {
	private static final long serialVersionUID = 4130462893849001999L;
	private int docCounter;
  private FileWriter fileWriter;
    
	/**
	 * @param path
	 * @throws IOException 
	 */
	public DocIdGenerator(String path) throws IOException {
		this.fileWriter = new FileWriter(path);
		docCounter = 0;
	}

	public void close(){
		try {
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized int register(String url, int documentLength) throws IOException{
		docCounter++;
		fileWriter.write(url+"|@|"+String.valueOf(docCounter)+"|@|"+documentLength);
		fileWriter.write("\n");
		return docCounter;
	}
}
