package parserUtils;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import parserUtils.Parser;
import org.apache.log4j.Logger;
import dataStructures.Page;

/**
 * Class for reading untarred .gz data and index files and ensuring correctness of input.
 *
 */
public class FolderReader {
	private File folderFile;
	private Map<String, File> indexFileMap, dataFileMap;
	private GzipReader gzipReader;
	static Logger log4j = Logger.getLogger(FolderReader.class);

	public FolderReader(File folderFile) throws IOException {
		this.folderFile = folderFile;
		if (null == folderFile)
			throw new IOException("Cannot take a null parameter as folderFile");
		indexFileMap = new HashMap<String, File>();
		dataFileMap = new HashMap<String, File>();
		for (File temp : folderFile.listFiles()) {
			if (temp.getPath().contains("_index"))
				indexFileMap.put(temp.getPath().replaceAll("_index", ""), temp);
			else if (temp.getPath().contains("_data"))
				dataFileMap.put(temp.getPath().replaceAll("_data", ""), temp);
		}
		if (indexFileMap.isEmpty() || dataFileMap.isEmpty()
				|| indexFileMap.size() != dataFileMap.size())
			throw new IOException(
					"Unequal number of index files.");
		if (!openGzipReader())
			throw new IOException("No Gzipped file found!");
	}

	public Page next() throws IOException {
		Page page;
		if (null != (page = gzipReader.next()))
			return page;
		else {
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

	@Override
	public String toString() {
		return "FolderReader [folderFile=" + folderFile + ", indexFileMap="
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