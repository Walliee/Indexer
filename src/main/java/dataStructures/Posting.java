package dataStructures;

public class Posting implements Comparable<Posting> {
	private int wordID, docID, offset = -1;
	private String context;
	
	public Posting(int docID, int wordID, int offset, String context) {
		this.wordID = wordID;
		this.docID = docID;
		this.offset = offset;
		this.context = context;
	}
	
	public Posting(int docID, int wordID, String context) {
		this.wordID = wordID;
		this.docID = docID;
		this.context = context;
	}
	
	public int compareTo(Posting o) {
		if(this.getWordID()!=o.getWordID())
			return (this.getWordID()>o.getWordID()? 1:-1);
		if(this.getDocID()!=o.getDocID())
			return (this.getDocID()>o.getDocID()? 1:-1);
		
		return 0;
	}

	public int getWordID() {
		return wordID;
	}

	public int getDocID() {
		return docID;
	}

	public int getOffset() {
		return offset;
	}

	public String getContext() {
		return context;
	}

	@Override
	public String toString() {
		return "Posting [wordID=" + wordID + ", docID=" + docID + ", offset="
				+ offset + ", context=" + context + "]";
	}

}
