package mergeUtils;

/**
 * Wrapper for basic index entry which also stores current position when iterating index files.
 *
 */
public class WrapperIndexEntry {
	private IndexEntry indexEntry;
	private int currentPosition;

	public WrapperIndexEntry(IndexEntry indexEntry) {
		this.indexEntry = indexEntry;
		this.currentPosition = 0;
	}

	@Override
	public boolean equals(Object obj) {
		return indexEntry.equals(obj);
	}

	public int getDocID(int position) {
		return indexEntry.getDocID(position);
	}

	public int getFrequency(int position) {
		return indexEntry.getFrequency(position);
	}

	public int getCurrentPosition() {
		return currentPosition;
	}


	public void setCurrentPosition(int lastPositionChecked) {
		this.currentPosition = lastPositionChecked;
	}

	public int getReaderFrequency() {
		return indexEntry.getReaderFrequency();
	}

	public int getDocVectorSize() {
		return indexEntry.getDocVectorSize();
	}

	@Override
	public int hashCode() {
		return indexEntry.hashCode();
	}

	@Override
	public String toString() {
		return "WrapperIndexEntry [indexEntry=" + indexEntry
				+ ", currentPosition=" + currentPosition + "]";
	}

	public static boolean incrIndex(WrapperIndexEntry indexEntry){
		int nextIndex = indexEntry.getCurrentPosition();
		if(!((nextIndex+1)<(indexEntry.getDocVectorSize()) ) )
			return false;
		nextIndex++;
		indexEntry.setCurrentPosition(nextIndex);
		return true;

	}
	
	public static boolean incrIndex(WrapperIndexEntry indexEntry, int docVectorSize){
		int nextIndex = indexEntry.getCurrentPosition();
		if(!((nextIndex+1)<(docVectorSize)) )
			return false;
		if(!((nextIndex+1)<indexEntry.getDocVectorSize())){
			return false;
		}
		nextIndex++;
		indexEntry.setCurrentPosition(nextIndex);
		return true;

	}
	
	//Implementation for nextGEQ
	public static int nextGEQ(WrapperIndexEntry indexEntry, int DocID) {
		int position = indexEntry.getCurrentPosition();
		while (indexEntry.getDocID(position) < DocID) {
			position++;
			if (position >= indexEntry.getDocVectorSize()) {
				position = indexEntry.getDocVectorSize();
				return -1;
			}
		}
		indexEntry.setCurrentPosition(position);
		return indexEntry.getDocID(position);
	}
	
	public static int currentFrequency(WrapperIndexEntry indexEntry){
		return indexEntry.getFrequency(indexEntry.getCurrentPosition());
	}

	public static Integer currentDoc(WrapperIndexEntry indexEntry) {
		return indexEntry.getDocID(indexEntry.currentPosition);
	}

	public int getMaxDocID() {
		return indexEntry.getMaxDocID();
	}


}
