package lexicon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Lexicon implements Serializable {
	private static final long serialVersionUID = 5995840378295938710L;
	private Map<Integer, LexiconValue> map;

	public Lexicon()  {
		this.map = new HashMap<Integer, LexiconValue>();
	}

	public List<BlockInfo> get(Object arg0) {
		return map.get(arg0).getBlockInfos();
	}

	public BlockInfo get(Object arg0, int fileNumber){
		for(BlockInfo blockInfo: map.get(arg0).getBlockInfos()){
			if(blockInfo.getFileNumber()==fileNumber)
				return blockInfo;
		}
		return null;
	}
	
	public synchronized void put(Integer termId,int freq ,BlockInfo blockInfo){
		if(!map.containsKey(termId))
			map.put(termId, new LexiconValue( new LinkedList<BlockInfo>(),0));
		map.get(termId).getBlockInfos().add(blockInfo);
		map.get(termId).incrementFrequency(freq);
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
