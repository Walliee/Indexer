package parserUtils;

public class Page {
  private String url,content,status;

    public Page(String url, String content, String status) {
			this.url = url;
			this.content = content;
			this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
    	return "Page [url=" + url + ", content length=" + content.length() + ", status=" + status+ "]";
    }

    
}
