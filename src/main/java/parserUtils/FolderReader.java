package parserUtils;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//import parsingUtils.FolderReader;
import parserUtils.Parser;
import org.apache.log4j.Logger;

public class FolderReader {
	//private String outputDirectoryPath;
	private File folderFile;
	private Map<String, File> indexFileMap, dataFileMap;
	private GzipReader gzipReader;
	static Logger log4j = Logger.getLogger(FolderReader.class);

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
		if (null != (page = gzipReader.next()))
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
		if (0 == dataFiles.length)
			return false;
		// System.out.println(dataFiles[0]);
		if (null != gzipReader)
			gzipReader.close();
		try {
			gzipReader = new GzipReader(indexFileMap.remove(dataFiles[0]),
					dataFileMap.remove(dataFiles[0]));
		} catch (EOFException e) {
			return openGzipReader();
		}

		return true;
	}

	public File getFolderFile() {
		return folderFile;
	}

	public void setFolderFile(File folderFile) {
		this.folderFile = folderFile;
	}

	public Map<String, File> getIndexFileMap() {
		return indexFileMap;
	}

	public void setIndexFileMap(Map<String, File> indexFileMap) {
		this.indexFileMap = indexFileMap;
	}

	public Map<String, File> getDataFileMap() {
		return dataFileMap;
	}

	public void setDataFileMap(Map<String, File> dataFileMap) {
		this.dataFileMap = dataFileMap;
	}

	public GzipReader getNzFileReader() {
		return gzipReader;
	}

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
		log4j.debug(folderReader);
		Long starttime = System.currentTimeMillis();
		while (null != (page = folderReader.next())) {
			if (null != page.getContent()) {
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
		System.out.println((System.currentTimeMillis() - starttime) + " ms");

	}


}