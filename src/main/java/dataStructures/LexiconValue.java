package dataStructures;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Lexicon entry that contains collection of BlockInfo entries, global frequency of each word and its positions in the intermediate index files.
 *
 */
public class LexiconValue implements Serializable{
	private static final long serialVersionUID = -8826253705045189110L;
	private List<BlockInfo> blockInfos;
	private int globalFrequency;
	
	public LexiconValue(LinkedList<BlockInfo> linkedList, int i) {
		this.blockInfos = linkedList;
		this.globalFrequency = i;
	}

	public List<BlockInfo> getBlockInfos() {
		return blockInfos;
	}

	public void incrementFrequency(int freq) {
		globalFrequency+= freq;
	}

	public int getGlobalFrequency() {
		return globalFrequency;
	}

	@Override
	public String toString() {
		return "LexiconValue [GlobalFrequency=" + globalFrequency + "]";
	}
}
