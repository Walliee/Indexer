package dataStructures;

/**
 * Data structure for storing page URL, page content and status.
 *
 */
public class Page {
  private String url, content, status;

  public Page(String url, String content, String status) {
		this.url = url;
		this.content = content;
		this.status = status;
  }

  public String getUrl() {
      return url;
  }

  public String getContent() {
      return content;
  }

  public String getStatus() {
      return status;
  }

  @Override
  public String toString() {
  	return "Page [url=" + url + ", content length=" + content.length() + ", status=" + status+ "]";
  }
}
