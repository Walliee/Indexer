package lexicon;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

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

	public void setBlockInfos(List<BlockInfo> blockInfos) {
		this.blockInfos = blockInfos;
	}

	public void incrGlobalFreq(int freq) {
		globalFrequency+= freq;
		
	}

	public int getGlobalFrequency() {
		return globalFrequency;
	}

	public void setGlobalFrequency(int globalFrequency) {
		this.globalFrequency = globalFrequency;
	}

	@Override
	public String toString() {
		return "LexiconValue [globalFrequency=" + globalFrequency + "]";
	}
}
