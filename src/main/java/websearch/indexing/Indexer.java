package websearch.indexing;

import idGenerators.DocIdGenerator;
import idGenerators.WordIdGenerator;
import writers.PostingWriter;
import dataStructures.Posting;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import lexicon.Lexicon;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;
import parserUtils.FolderReader;
import parserUtils.Page;
import parserUtils.Parser;

public class Indexer 
{
	private String inputPath;
	private String outputPath;
	private int termBatchSize;
	static Logger log4j = Logger.getLogger(Indexer.class);

	public Indexer(String inputPath, String outputPath, int termBatchSize) {
		//super();
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.termBatchSize = termBatchSize;
	}

	public boolean index() throws FileNotFoundException, IOException {
		boolean outputPathExists = new File(outputPath).mkdir();

		File  tempFolder= new File(outputPath,"temp");

		tempFolder.mkdir();
		String tempFolderPath = tempFolder.getAbsolutePath();

		File indexFolder = new File(outputPath,"index");
		indexFolder.mkdir();
		String indexPath = indexFolder.getAbsolutePath();

		File folder = new File(inputPath);

		Posting posting;
		Lexicon lexicon = new Lexicon();

		Long start = System.currentTimeMillis();
		int myloop=0;
		Set<Integer> activeTaskSet = new HashSet<Integer>();

		DocIdGenerator docIdGenerator = new DocIdGenerator( ( outputPath + "/DocumentID") );
		WordIdGenerator wordIdGenerator = new WordIdGenerator(100000, (outputPath));

		//System.out.println(inputPath);
		//System.out.println(folder.listFiles());

		for (File temp : folder.listFiles()) {
			//System.out.println(temp.getName());
			TarArchiveInputStream archiveInputStream = new TarArchiveInputStream(
					new FileInputStream(temp));
			TarArchiveEntry archiveEntry;
			//System.out.println(archiveInputStream.getNextEntry());

			// for removal of first file which results in invalid path exception
			archiveInputStream.getNextTarEntry();
			//			// Now to copy the tar data to a temporary folder
			while (null != (archiveEntry = archiveInputStream.getNextTarEntry())) {
				if (archiveEntry.isFile()) {
					//System.out.println(archiveEntry.getName());
					String[] folds = archiveEntry.getName().split("/");
					File file = new File(tempFolderPath, folds[(folds.length - 1)]);
					//log4j.debug(file.getName());
					try {
						FileOutputStream fileOutputStream = new FileOutputStream(file);
						IOUtils.copy(archiveInputStream, fileOutputStream);
						fileOutputStream.close();
					} catch (java.io.FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			archiveInputStream.close();
			System.out.println("Done Uncompressing " + temp);
		}

			Page page;

			int validPagecounter = 0;
			int badPageCount = 0;
			Pattern newLineSplitPattern = Pattern.compile("\n");
			Pattern contextSplitPattern = Pattern.compile(" ");
			int wordID, offset, tcount = 0;
			String[] myArray;
			List<Posting> postingList = new ArrayList<Posting>(termBatchSize);

			FolderReader folderReader = new FolderReader(new File(tempFolderPath));
			
			while (null != (page = folderReader.next())) {
				if (null != page.getContent()) {
					StringBuilder stringBuilder = new StringBuilder();
					try {
						Parser.parseDoc(page.getUrl(), page.getContent(),
								stringBuilder);
						//offset = 0;
						validPagecounter++;
						String[] lines = newLineSplitPattern.split(stringBuilder);
						int docID = docIdGenerator.register(page.getUrl(), lines.length);
						for (String parsedLine : lines) {
							myArray = contextSplitPattern.split(parsedLine);
							if (myArray.length > 1) {
								tcount++;
								//offset++;
								wordID = wordIdGenerator.register(myArray[0]);
								String context = myArray[1];
								if (myArray.length > 2) {
									offset = Integer.parseInt(myArray[2]);
									posting = new Posting(docID, wordID, offset, context);
								} else {
									posting = new Posting(docID, wordID, context);
								}

								//log4j.debug(posting);
								postingList.add(posting);
								//for (Posting i:postingList)
									//System.out.println(i);
								if(postingList.size()==termBatchSize){

									System.gc();	
									//String tempIndexPath = new File(tempFolderPath,String.valueOf(myloop)).getAbsolutePath();
									new PostingWriter(postingList,lexicon, indexPath).write();
									System.out.println("So far "+ (System.currentTimeMillis()-start)+" milliSec have passed since execution.");
									postingList = null;
									System.gc();	

									postingList= new ArrayList<Posting>(termBatchSize);
									myloop++;
									System.out.println(myloop+" into the batchSize yet executed.");
									System.out.println(activeTaskSet);
								}
							}
						}
					} catch (StringIndexOutOfBoundsException e) {
						badPageCount++;
					}
				}
			}

			if(postingList.size()!=0){
				//String tempIndexPath = new File(tempFolderPath,String.valueOf(myloop)).getAbsolutePath();
				new PostingWriter(postingList,lexicon, indexPath).write();
				System.gc();	
				myloop++;
			}

			File tempDir = new File(tempFolderPath);
			if (tempDir.listFiles() != null)
				for (File file : tempDir.listFiles()) {
					file.delete();
				}
		
			
		File lexiconObjectFile = new File(outputPath,"Lexicon");
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(lexiconObjectFile));
		objectOutputStream.writeObject(lexicon);
		objectOutputStream.close();	
		
		wordIdGenerator.close();
		docIdGenerator.close();
		//System.out.println(indexPath);
		return true;
	}



	public static void main(String[] args) throws FileNotFoundException, IOException {
		String inputPath = "/Users/Walliee/Documents/workspace/Indexing/nz2";
		String outputPath = "/Users/Walliee/Documents/workspace/Indexing/temp";
		int batchSize= 5000000;
		new Indexer(inputPath,outputPath,batchSize).index();
	}
}
