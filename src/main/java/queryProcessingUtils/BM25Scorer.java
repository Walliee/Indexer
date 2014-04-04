package queryProcessingUtils;

import java.util.List;

public class BM25Scorer {
	private double k1, b;

	public BM25Scorer(double k1, double b) {
		super();
		this.k1 = k1;
		this.b = b;
	}
	
	// Implementation of BM25 scorer s described in the paper and in class.
	public double score(int totalDocuments,
			List<Integer> globalDocumentFrequencyVector,
			List<Integer> localDocumentFrequencyVector, int documentLength,
			double avgDocumentLength) {
		double score = 0;

		double constFactorK = k1
				* ((1 - b) + (b * documentLength / avgDocumentLength));

		for (int i = 0; i < globalDocumentFrequencyVector.size(); i++) {
			double factor1 = ((totalDocuments - globalDocumentFrequencyVector.get(i) + 0.5) / (globalDocumentFrequencyVector
					.get(i) + 0.5));
			
			double factor2 = (((k1 + 1) * (double)localDocumentFrequencyVector.get(i)) / (constFactorK +(double) localDocumentFrequencyVector
					.get(i)));
			
			
			score += Math.log(factor1)*factor2; 
		}

		return score;
	}

}
