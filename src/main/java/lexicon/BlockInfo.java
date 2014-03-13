
package lexicon;

import java.io.Serializable;

public class BlockInfo implements  Serializable{
	private static final long serialVersionUID = -7989954884023179545L;
	private int fileNumber;
	private int startOffset;
	private int endOffset;

	public BlockInfo(int fileNumber, int startOffset, int endOffset) {
		super();
		this.fileNumber = fileNumber;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}

	public int getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(int fileNumber) {
		this.fileNumber = fileNumber;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	@Override
	public String toString() {
		return "BlockInfo [fileNumber=" + fileNumber + ", startOffset="
				+ startOffset + ", endOffset=" + endOffset + "]";
	}
	
}
