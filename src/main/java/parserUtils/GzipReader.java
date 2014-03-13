package parserUtils;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.log4j.Logger;

public class GzipReader {
	//private File indexFile, dataFile;
  //private StringBuilder indexStream, dataStream;
  BufferedReader indexFileReader;
  InputStreamReader dataInputStreamReader;
  //private int offset = 0;
  //public List<String> validCodes;
  public static int totalFile=0;
  public static int rejectFile=0;
  static Logger log4j = Logger.getLogger(GzipReader.class);

  public GzipReader(File indexFile, File dataFile) throws FileNotFoundException, IOException {
		try{
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
		totalFile++;
		String indexLine = indexFileReader.readLine();
		if (null != indexLine) {
		    String[] indexParams = indexLine.split(" ");
		    String domain = indexParams[0];
		    String status = indexParams[6];
		    int charlength = Integer.parseInt(indexParams[3]);
		    char[] dataBuffer = new char[charlength];
		    int readStatus = dataInputStreamReader.read(dataBuffer, 0,
			    charlength);

		    String pageString = new String(dataBuffer);

		    // if page was fetched. Else we move to the next page.
		    if (status.equalsIgnoreCase("ok") && pageString.length()>0
		    		&& pageString.substring(9, 10).equals("2")) {
		    		//return new Page((domain + page), pageString, pageString.substring(9, 12));
		    		return new Page((domain), pageString,
				    		pageString.substring(9, 12));
		    }
		    // A page with null as pagestring is returned to indicate improper
		    // download or fetching
		    rejectFile++;
		    //return new Page((domain + page), null, null);
		    return new Page((domain), null, null);
		}
		// Null Page is returned at the end of file to indicate end of file
		else
		    return null;
  }

	public static void main(String[] args) throws FileNotFoundException,IOException {
		File indexFile = new File("1_index");
		System.out.println(indexFile.getAbsolutePath());
		File dataFile = new File("1_data");
		GzipReader gzipReader = new GzipReader(indexFile, dataFile);
		Page page;
		int counter = 0;

		Long starttime = System.currentTimeMillis();
		while ((null != (page = gzipReader.next()))) {
		    if (null != page.getContent()) {
		    	log4j.debug(page);
					StringBuilder stringBuilder = new StringBuilder();
					Parser.parseDoc(page.getUrl(), page.getContent(), stringBuilder);
					counter++;
		    }
		}
		System.out.println((System.currentTimeMillis() - starttime) + " ms");
		System.out.println("Done. Parsed " + counter + " files.");
  }
}