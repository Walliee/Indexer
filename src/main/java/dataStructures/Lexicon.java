package dataStructures;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the Lexicon data structure
 *
 */
public class Lexicon implements Serializable {
	private static final long serialVersionUID = 5995840378295938710L;
	private Map<Integer, LexiconValue> map;

	public Lexicon()  {
		//Integer is the wordID and LexiconValue is the data structure that stores 
		//further information as to where to find the word in the index files.
		this.map = new HashMap<Integer, LexiconValue>();
	}
	
	//returns list of all the blockInfos for all the indexfiles
	public List<BlockInfo> get(Object arg0) {
		return map.get(arg0).getBlockInfos();
	}

	//returns the blockInfo for a particular index file
	public BlockInfo get(Object arg0, int fileNumber){
		for(BlockInfo blockInfo: map.get(arg0).getBlockInfos()){
			if(blockInfo.getFileNumber()==fileNumber)
				return blockInfo;
		}
		return null;
	}
	
	// Adds entry to Lexicon
	public synchronized void put(Integer wordId,int freq ,BlockInfo blockInfo){
		if(!map.containsKey(wordId))
			map.put(wordId, new LexiconValue( new LinkedList<BlockInfo>(),0));
		map.get(wordId).getBlockInfos().add(blockInfo);
		map.get(wordId).incrementFrequency(freq);
	}

	public int size() {
		return map.size();
	}

	public boolean containsKey(Object arg0) {
		return map.containsKey(arg0);
	}

	public Set<Integer> keySet() {
		return map.keySet();
	}

	public LexiconValue remove(Object arg0) {
		return map.remove(arg0);
	}
	
	@Override
	public String toString() {
		return "Lexicon [map=" + map + "]";
	}
}
