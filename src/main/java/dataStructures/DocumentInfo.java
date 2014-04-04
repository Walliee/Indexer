package dataStructures;

/**
 * This class is used to store URL and length of a web page
 *
 */
public class DocumentInfo {
	private String url;
	private int documentLength;

	public DocumentInfo(String url, int documentLength) {
		this.url = url;
		this.documentLength = documentLength;
	}

	public String getUrl() {
		return url;
	}

	public int getDocumentLength() {
		return documentLength;
	}

	@Override
	public String toString() {
		return "DocumentInfo [url=" + url + ", documentLength="
				+ documentLength + "]";
	}
}
