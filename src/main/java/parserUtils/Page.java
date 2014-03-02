package parserUtils;

public class Page {
    private String url,content,status;
    
    /**
     * @param url
     * @param content
     * @param status
     */
    public Page(String url, String content, String status) {
			this.url = url;
			this.content = content;
			this.status = status;
    }
    
    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
    	return "Page [url=" + url + ", content length=" + content.length() + ", status=" + status+ "]";
    }

    
}
