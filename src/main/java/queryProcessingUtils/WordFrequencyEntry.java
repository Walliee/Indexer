package queryProcessingUtils;


/**
 * Class for storing wordID and frequency during query processing
 *
 */
public class WordFrequencyEntry implements Comparable<WordFrequencyEntry> {
	private int wordID, frequency;

	public WordFrequencyEntry(int wordID, int frequency) {
		this.wordID = wordID;
		this.frequency = frequency;
	}

	public int getWordID() {
		return wordID;
	}

	public int getFrequency() {
		return frequency;
	}

	@Override
	public int compareTo(WordFrequencyEntry wordFrequencyEntry) {
		if (this.frequency == wordFrequencyEntry.getFrequency()) return 0;
		else
			return (this.frequency>wordFrequencyEntry.frequency)?1:-1;
	}

	@Override
	public String toString() {
		return "WordFrequencyEntry [wordID=" + wordID + ", frequency="
				+ frequency + "]";
	};

}
