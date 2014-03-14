package lexicon;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class LexiconValue implements Serializable{
	private static final long serialVersionUID = -8826253705045189110L;
	private List<BlockInfo> blockInfos;
	private int frequency;
	
	public LexiconValue(LinkedList<BlockInfo> linkedList, int i) {
		this.blockInfos = linkedList;
		this.frequency = i;
	}

	public List<BlockInfo> getBlockInfos() {
		return blockInfos;
	}

	public void incrementFrequency(int freq) {
		frequency+= freq;
	}

	public int getGlobalFrequency() {
		return frequency;
	}

	@Override
	public String toString() {
		return "LexiconValue [Frequency=" + frequency + "]";
	}
}
