
package mergeUtils;

import mergeUtils.IndexEntry;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import dataStructures.BlockInfo;
import dataStructures.Lexicon;

/**
 * This class handles reading of index files and lexicon from disk
 *
 */
public class IndexReader {
	
	private static int defaultCacheSize = 1000;
	private static Float defaultLoadFactor =  (float) 0.75;
	private static boolean defaultLastAccessOrder = true;
	private  int indexNumber;
	private String rootDirectory;
	private boolean cacheEnabled;
	private  int cacheSize;
	private  Float loadFactor;
	private  boolean lastAccessOrder;
	private LinkedHashMap<Integer, IndexEntry> lruCache;
	
	private Lexicon lexicon;
	private RandomAccessFile file;
	
	public void init() throws IOException {
		this.file = new RandomAccessFile((rootDirectory+"/"+String.valueOf(indexNumber)),"r");
		if(cacheEnabled)
			lruCache = new LinkedHashMap<Integer, IndexEntry>(cacheSize, loadFactor, lastAccessOrder);
	}


	public IndexEntry openList(int wordID) throws FileNotFoundException, IOException
    {
        IndexEntry indexEntry;
        if(cacheEnabled){
        	//Checks if cache contains word
        	if(lruCache.containsKey(wordID)){
        		indexEntry = lruCache.remove(wordID);
        		lruCache.put(wordID, indexEntry);
        		//indexEntry = lruCache.get(wordID);
        		return indexEntry;
        	}
        }
      //Disk is looked up only if Cache is disabled or word was not found in cache
        BlockInfo blockInfo = lexicon.get(wordID, indexNumber);
        	//System.out.println(blockInfo.toString());
        if(blockInfo!=null){
        	file.seek(blockInfo.getStartOffset());
        	int size= (blockInfo.getEndOffset()-blockInfo.getStartOffset());
        	byte[] bytes = new byte[size];
        	file.read(bytes, 0, size);
        	indexEntry = new IndexEntry(wordID, blockInfo, bytes);
        	if(cacheEnabled){
        		if(lruCache.size()<cacheSize)
        			lruCache.put(wordID,indexEntry);
        		else{
        			//Removing the Least Recently used entry
        			int toRemove = lruCache.keySet().iterator().next();
        			lruCache.remove(toRemove);
        			lruCache.put(wordID, indexEntry);
        		}
        	}
        	return indexEntry;
            }
            else return null;
    }

   public void close() throws IOException{
	   file.close();
   }

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + indexNumber;
	result = prime * result
			+ ((rootDirectory == null) ? 0 : rootDirectory.hashCode());
	return result;
	}

@Override
public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexReader other = (IndexReader) obj;
		if (indexNumber != other.indexNumber)
			return false;
		if (rootDirectory == null) {
			if (other.rootDirectory != null)
				return false;
		} else if (!rootDirectory.equals(other.rootDirectory))
			return false;
		return true;
	}
    
public IndexReader(Lexicon lexicon, int indexNumber, String rootDirectory,
		boolean cacheEnabled) throws IOException {
			this.indexNumber = indexNumber;
			this.rootDirectory = rootDirectory;
			this.cacheEnabled = cacheEnabled;
			this.cacheSize = defaultCacheSize;
			this.loadFactor = defaultLoadFactor;
			this.lastAccessOrder = defaultLastAccessOrder;
			this.lexicon = lexicon;
			init();
	}

public IndexReader(Lexicon lexicon, int indexNumber, String rootDirectory, boolean cacheEnabled,
		int cacheSize, Float loadFactor, boolean lastAccessOrder) throws IOException {
			this.indexNumber = indexNumber;
			this.rootDirectory = rootDirectory;
			this.cacheEnabled = cacheEnabled;
			this.cacheSize = cacheSize;
			this.loadFactor = loadFactor;
			this.lastAccessOrder = lastAccessOrder;
			this.lexicon = lexicon;
			init();
	}
}
