package dataStructures;

/**
 * It is a data structure for storing word ID and context w.r.t each word
 *
 */
public class Word {
	private int wordID;
	private String word;
	private String context;

	public Word(int wordID, String word, String context){
		this.wordID = wordID;
		this.word = word;
		this.context = context;
	}
	
	public int getWordID() {
		return wordID;
	}

	public String getWord() {
		return word;
	}

	public String getContext() {
		return context;
	}

	@Override
	public String toString() {
		return "Word [wordID=" + wordID + ", Word=" + word + ", context=" + context + "]";
	}
}
