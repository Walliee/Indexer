package dataStructures;

import java.io.Serializable;

/**
 * This class is used for storing Corpus information like total documents 
 * and total number of postings generated after indexing
 *
 */
public class CorpusStatistics implements Serializable{
	private static final long serialVersionUID = 114248531690435365L;
	private int totalDocuments, totalPostings;

	public CorpusStatistics(int totalDocuments, int totalPostings) {
		this.totalDocuments = totalDocuments;
		this.totalPostings = totalPostings;
	}

	public int getTotalDocuments() {
		return totalDocuments;
	}

	public int getTotalPostings() {
		return totalPostings;
	}

	@Override
	public String toString() {
		return "CorpusStatistics [totalDocuments=" + totalDocuments
				+ ", totalPostings=" + totalPostings + "]";
	}	
}
