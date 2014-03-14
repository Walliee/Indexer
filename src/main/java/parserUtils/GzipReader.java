package parserUtils;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.log4j.Logger;
import dataStructures.Page;

public class GzipReader {
  BufferedReader indexFileReader;
  InputStreamReader dataInputStreamReader;
  public static int totalFile=0;
  public static int rejectFile=0;
  static Logger log4j = Logger.getLogger(GzipReader.class);
  Pattern contextSplitPattern = Pattern.compile(" ");

  public GzipReader(File indexFile, File dataFile) throws FileNotFoundException, IOException {
		try {
			this.dataInputStreamReader = new InputStreamReader(new GZIPInputStream(new FileInputStream(dataFile)));
			this.indexFileReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(indexFile))));
		} catch (EOFException e) {
			throw e;
		}
  }

	@Override
	protected void finalize() throws Throwable {
		this.close();
	}

	public void close() throws IOException {
		if (null != this.indexFileReader)
		    this.indexFileReader.close();
		if (null != this.dataInputStreamReader)
		    this.dataInputStreamReader.close();
	}

	public Page next() throws IOException {
		String indexLine = indexFileReader.readLine();
		if (null != indexLine) {
				totalFile++;
		    String[] indexParams = indexLine.split(" ");
		    String domain = indexParams[0];
		    String status = indexParams[6];
		    int charlength = Integer.parseInt(indexParams[3]);
		    char[] dataBuffer = new char[charlength];
		    //log4j.debug(charlength);
		    dataInputStreamReader.read(dataBuffer, 0, charlength);

		    String pageString = new String(dataBuffer);
		    //log4j.debug(pageString.substring(0, 50));
		    //log4j.debug(pageString.substring(pageString.length()-30, pageString.length()));
		    // if page was fetched. Else we move to the next page.
		    if (status.equalsIgnoreCase("ok") && pageString.length()>0) {
		    		/*&& pageString.substring(9, 10).equals("2")*/
		    		/*&& pageString.split(" ")[1].equals("200")*/
		    		//return new Page((domain + page), pageString, pageString.substring(9, 12));
		    		return new Page(domain, pageString, "200");
		    }
		    // A page with null as pagestring is returned to indicate improper
		    // download or fetching
		    rejectFile++;
		    //return new Page((domain + page), null, null);
		    return new Page(domain, null, null);
		}
		// Null Page is returned at the end of file to indicate end of file
		else
		    return null;
  }

	public static void main(String[] args) throws FileNotFoundException,IOException {
		File indexFile = new File("2_index");
		System.out.println(indexFile.getAbsolutePath());
		File dataFile = new File("2_data");
		GzipReader gzipReader = new GzipReader(indexFile, dataFile);
		Page page;
		int counter = 0;

		Long starttime = System.currentTimeMillis();
		while ((null != (page = gzipReader.next()))) {
			counter++;
		    if (null != page.getContent()) {
		    	//log4j.debug(page);
					StringBuilder stringBuilder = new StringBuilder();
					Parser.parseDoc(page.getUrl(), page.getContent(), stringBuilder);
		    }
		}
		System.out.println((System.currentTimeMillis() - starttime) + " ms");
		System.out.println("Done. Parsed " + counter + " files.");
		System.out.println(GzipReader.totalFile);
		System.out.println(GzipReader.rejectFile + " Rejected");
  }
}