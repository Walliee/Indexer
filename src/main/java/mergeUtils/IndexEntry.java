
package mergeUtils;

import java.util.ArrayList;
import java.util.List;

//import writers.OffsetBlock;

import dataStructures.BlockInfo;

import compression.Compression;

/**
 * data structure for managing stores index entries (termID, doc ID, frequencies)
 *
 */
public class IndexEntry {

	private int wordID;
	private List<Integer> docVector;
	private List<Integer> freqVector;
	private int readerFrequency;
	
	//This method reads blockInfo byte array and decodes it into Doc Vector and Frequency Vector
	public IndexEntry(int wordID, BlockInfo blockInfo, byte[] bytes) {
		this.wordID = wordID;		
		List<Integer> list = Compression.decode(bytes);
		
		freqVector = new ArrayList<Integer>(list.size()/2);
		
		int[] tempDocArray = new int[list.size()/2];
		
		int i=0;
		int j = list.size()/2;
		readerFrequency = 0;
		
		for(;i<list.size()/2;i++,j++){
			tempDocArray[i] = list.get(i);
			freqVector.add(list.get(j));
			readerFrequency+= list.get(j);
		}	
		docVector = Compression.deltaDecompress(tempDocArray);

	}

	public int getMaxDocID(){
		return docVector.get(docVector.size()-1);
	}

	public int getDocID(int position) {
		try {
			return this.docVector.get(position);
		} catch (IndexOutOfBoundsException e) {
			return -1;
		}
	}

	public int getFrequency(int position) {
		try {
			return this.freqVector.get(position);
		} catch (IndexOutOfBoundsException e) {
			return -1;
		}
	}

	public int getReaderFrequency() {
		return readerFrequency;
	}
	
	public int getDocVectorSize(){
		return this.docVector.size();
	}

	@Override
	public String toString() {
		return "IndexEntry [wordID=" + wordID + ", docVector=" + docVector
				+ ", freqVector=" + freqVector + ", readerFrequency=" + readerFrequency
				+ "]";
	}
}
