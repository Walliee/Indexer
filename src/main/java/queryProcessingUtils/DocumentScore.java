package queryProcessingUtils;

/**
 * Data structure to store docID and its score during query processing
 *
 */
public class DocumentScore implements Comparable<DocumentScore>{
	private int docID;
	private double score;

	public DocumentScore(int docID, double  score) {
		this.docID = docID;
		this.score = score;
	}

	public int getDocID() {
		return docID;
	}

	public double  getScore() {
		return score;
	}
	
	@Override
	public String toString() {
		return "DocumentScore [docID=" + docID + ", score=" + score + "]";
	}

	@Override
	public int compareTo(DocumentScore o) {
		if(this.getScore()!=o.getScore())
			return (this.getScore()>o.getScore()? 1:-1);
		
		return 0;
	}
	

}
