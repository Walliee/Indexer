
package websearch.indexing;

import writers.PostingWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import mergeUtils.IndexReader;
import mergeUtils.WrapperIndexEntry;
import dataStructures.BlockInfo;
import dataStructures.Lexicon;

/**
 * Main class for merging intermediate index
 *
 */
public class IndexMerger {
	private String inputDirectory, outputDirectory;
	private Lexicon inputLexicon, outputLexicon;
	private Map<Integer, IndexReader> readerMap;

	public IndexMerger(String inputDirectory, String outputDirectory, Lexicon lexicon)  {
		this.inputDirectory = inputDirectory;
		this.outputDirectory = outputDirectory;
		this.inputLexicon = lexicon;
		this.outputLexicon = new Lexicon();
		
		File folder = new File(inputDirectory);
		String[] filenames = folder.list();
		readerMap = new HashMap<Integer, IndexReader>(filenames.length);
		
		// Load readers for all the intermidiate index files.
		for(String temp: filenames){
			try{
				readerMap.put(Integer.valueOf(temp),new IndexReader(lexicon, Integer.valueOf(temp), inputDirectory, false)) ;
				//System.out.println(temp);
			}catch(NumberFormatException e){} catch (IOException e) {
				e.printStackTrace();
			}
		}
			System.out.println("Done Loading the readers.");
			
	}

	// k-way merge implementation for merging indexes.
	public boolean merge() throws FileNotFoundException, IOException {
		int fileNumber = 1;
		File folder = new File(outputDirectory, "FinalIndex");
		folder.mkdir();
		outputDirectory = folder.getAbsolutePath();
		File outputFile = new File(outputDirectory,String.valueOf(fileNumber));
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(outputFile));
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(outputDirectory+"Lexicon")));
		int currentFileOffset = 0;
		
		List<Integer> docVector = new ArrayList<Integer>();
		List<Integer> freqVector = new ArrayList<Integer>();

		for (Integer termID : inputLexicon.keySet()) {
			//System.out.println("Now Merging for term :"+termID);
			List<BlockInfo> list = inputLexicon.get(termID);
			PriorityQueue<WrapperIndexEntry> pQueue = new PriorityQueue<WrapperIndexEntry>(
					list.size(), new Comparator<WrapperIndexEntry>() {

						@Override
						public int compare(WrapperIndexEntry o1, WrapperIndexEntry o2) {
							if(o1.getDocID(o1.getCurrentPosition())==o2.getDocID(o2.getCurrentPosition()))
								return 0;
							return( ( (o1.getDocID(o1.getCurrentPosition())-o2.getDocID(o2.getCurrentPosition())) )>0)?1:-1;
							
						}
					});
			int docVectorSize = 0;
			for(BlockInfo blockInfo:list){
				WrapperIndexEntry indexEntry = new WrapperIndexEntry(readerMap.get(blockInfo.getFileNumber()).openList(termID));
				pQueue.add(indexEntry);
				docVectorSize += indexEntry.getDocVectorSize();
			}

			while(!pQueue.isEmpty()){
				WrapperIndexEntry indexEntry = pQueue.poll();
				docVector.add(WrapperIndexEntry.currentDoc(indexEntry));
				freqVector.add(WrapperIndexEntry.currentFrequency(indexEntry));

				if(WrapperIndexEntry.incrIndex(indexEntry, docVectorSize))
					pQueue.add(indexEntry);
			}
			
			currentFileOffset = PostingWriter.writePosting(termID, docVector, freqVector, stream, currentFileOffset, fileNumber, outputLexicon);
		}
		
		// Serializing lexicon to disk
		objectOutputStream.writeObject(outputLexicon);
		objectOutputStream.close();
		stream.close();
		System.gc();
		return true;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		ObjectInputStream inputStream = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(
						"/Users/Walliee/Documents/workspace/Indexing/temp50/Lexicon")));
		Lexicon lexicon = (Lexicon) inputStream
				.readObject();
		inputStream.close();
		System.out.println("Done Loading the Lexicon");
		IndexMerger indexMerger = new IndexMerger("/Users/Walliee/Documents/workspace/Indexing/temp50/index",
				"/Users/Walliee/Documents/workspace/Indexing/temp50", lexicon);
		Long start = System.currentTimeMillis();
		indexMerger.merge();
		Long stop  = System.currentTimeMillis();

		System.out.println("Merging done in "+(stop-start)+" milliSeconds");
		

	}

}
