/**
 * 
 */
package parserUtils;



import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//import parsingUtils.FolderReader;
import parserUtils.Parser;
import websearch.indexing.Indexer;
import org.apache.log4j.Logger;

public class FolderReader {
	//private String outputDirectoryPath;
	private File folderFile;
	private Map<String, File> indexFileMap, dataFileMap;
	private GzipReader gzipReader;
	static Logger log4j = Logger.getLogger(FolderReader.class);

	/**
	 * @param folderFile
	 * @throws IOException
	 */
	public FolderReader(File folderFile) throws IOException {
		super();
		this.folderFile = folderFile;
		if (null == folderFile)
			throw new IOException("Cannot take a null parameter as folderFile");
		indexFileMap = new HashMap<String, File>();
		dataFileMap = new HashMap<String, File>();
		for (File temp : folderFile.listFiles()) {
			//System.out.println(temp.getName());
			if (temp.getPath().contains("_index"))
				indexFileMap.put(temp.getPath().replaceAll("_index", ""), temp);
			else if (temp.getPath().contains("_data"))
				dataFileMap.put(temp.getPath().replaceAll("_data", ""), temp);
		}
		if (indexFileMap.isEmpty() || dataFileMap.isEmpty()
				|| indexFileMap.size() != dataFileMap.size())
			throw new IOException(
					"There must be equal number of non-zero index and data files in the folder");
		if (!openGzipReader())
			throw new IOException("Unable to detect any relevant files");
	}
	
	public Page next() throws IOException {
		Page page;
		if ((page = gzipReader.next()) != null)
			return page;
		else {
			// switch to the next file pair and call next() recursively over it.
			// will return null condition mentioned below when reaches end of
			// fileset

			if (openGzipReader())
				return next();
		}
		return null;
	}

	private boolean openGzipReader() throws FileNotFoundException,
			IOException {
		Object[] dataFiles = dataFileMap.keySet().toArray();
		if (dataFiles.length == 0)
			return false;
		// System.out.println(dataFiles[0]);
		if (gzipReader != null)
			gzipReader.close();
		try {
			gzipReader = new GzipReader(indexFileMap.remove(dataFiles[0]),
					dataFileMap.remove(dataFiles[0]));
		} catch (EOFException e) {
			return openGzipReader();
		}

		return true;
	}

	/**
	 * @return the folderFile
	 */
	public File getFolderFile() {
		return folderFile;
	}

	/**
	 * @param folderFile
	 *            the folderFile to set
	 */
	public void setFolderFile(File folderFile) {
		this.folderFile = folderFile;
	}

	/**
	 * @return the indexFileMap
	 */
	public Map<String, File> getIndexFileMap() {
		return indexFileMap;
	}

	/**
	 * @param indexFileMap
	 *            the indexFileMap to set
	 */
	public void setIndexFileMap(Map<String, File> indexFileMap) {
		this.indexFileMap = indexFileMap;
	}

	/**
	 * @return the dataFileMap
	 */
	public Map<String, File> getDataFileMap() {
		return dataFileMap;
	}

	/**
	 * @param dataFileMap
	 *            the dataFileMap to set
	 */
	public void setDataFileMap(Map<String, File> dataFileMap) {
		this.dataFileMap = dataFileMap;
	}

	/**
	 * @return the gzipReader
	 */
	public GzipReader getNzFileReader() {
		return gzipReader;
	}

	/**
	 * @param gzipReader
	 *            the gzipReader to set
	 */
	public void setNzFileReader(GzipReader gzipReader) {
		this.gzipReader = gzipReader;
	}

	@Override
	public String toString() {
		return "NZFolderReader [folderFile=" + folderFile + ", indexFileMap="
				+ indexFileMap + ", dataFileMap=" + dataFileMap
				+ ", gzipReader=" + gzipReader + "]";
	}

	public static void main(String[] args) throws IOException {
		Page page;
		int badPageCount = 0;
		FolderReader folderReader = new FolderReader(new File(
				"/Users/Walliee/Documents/workspace/Indexing/temp/temp"));
		while ((page = folderReader.next()) != null) {
			if (page.getContent() != null) {
				StringBuilder stringBuilder = new StringBuilder();
				try {
					Parser.parseDoc(page.getUrl(), page.getContent(),
							stringBuilder);
				} catch (StringIndexOutOfBoundsException e) {
					badPageCount++;
				}
				log4j.debug(stringBuilder);
			}
		}
		System.out.println(badPageCount);
		
	}
	

}
