package idGenerators;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class handles and assigns doc ID based on the order in which they are read.
 *
 */
public class DocIdGenerator implements Serializable {
	private static final long serialVersionUID = -3574390161623137988L;
	private int docCounter;
  private FileWriter fileWriter;

	public DocIdGenerator(String path) throws IOException {
		this.fileWriter = new FileWriter(path);
		docCounter = 0;
	}

	public void close() throws IOException{
			fileWriter.close();
	}
	
	public synchronized int register(String url, int documentLength) throws IOException{
		docCounter++;
		fileWriter.write(url+"$$"+String.valueOf(docCounter)+"$$"+documentLength);
		fileWriter.write("\n");
		return docCounter;
	}
}
