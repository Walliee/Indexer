package dataStructures;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * 
 * Simple class for testing lexicon loading
 *
 */
public class LexiconReader {
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		System.out.println("Now Loading the Lexicon");
		
		ObjectInputStream inputStream = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(
						"/Users/Walliee/Documents/workspace/Indexing/temp50/FinalIndexLexicon")));
		Lexicon lexicon = (Lexicon) inputStream
				.readObject();
		System.out.println(lexicon);
		
		inputStream.close();
		System.out.println("Done Loading the Lexicon");
	}
}
