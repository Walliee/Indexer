
package dataStructures;

import java.io.Serializable;

/**
 * Data structure to store index file number, start offset and end offset for each word in each intermediate index file.
 *
 */
public class BlockInfo implements  Serializable{
	private static final long serialVersionUID = -7989954888390481175L;
	private int fileNumber;
	private int startOffset;
	private int endOffset;

	public BlockInfo(int fileNumber, int startOffset, int endOffset) {
		this.fileNumber = fileNumber;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}

	public int getFileNumber() {
		return fileNumber;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	@Override
	public String toString() {
		return "BlockInfo [fileNumber=" + fileNumber + ", startOffset="
				+ startOffset + ", endOffset=" + endOffset + "]";
	}
	
}
