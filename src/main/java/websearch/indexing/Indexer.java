package websearch.indexing;

import idGenerators.DocIdGenerator;
import idGenerators.WordIdGenerator;
import writers.PostingWriter;
import dataStructures.Page;
import dataStructures.Posting;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import lexicon.Lexicon;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;
import parserUtils.FolderReader;
import parserUtils.GzipReader;
import parserUtils.Parser;

public class Indexer 
{
	private String inputPath;
	private String outputPath;
	private int termBatchSize;
	static Logger log4j = Logger.getLogger(Indexer.class);

	public Indexer(String inputPath, String outputPath, int termBatchSize) {
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.termBatchSize = termBatchSize;
	}

	public boolean index() throws FileNotFoundException, IOException {
		new File(outputPath).mkdir();

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

		DocIdGenerator docIdGenerator = new DocIdGenerator( ( outputPath + "/DocumentID") );
		WordIdGenerator wordIdGenerator = new WordIdGenerator(100000, (outputPath));


		for (File temp : folder.listFiles()) {
			TarArchiveInputStream archiveInputStream = new TarArchiveInputStream(new FileInputStream(temp));
			TarArchiveEntry archiveEntry;

			// for removal of first file which results in invalid path exception
			archiveInputStream.getNextTarEntry();
			// Now to copy the tar data to a temporary folder
			while (null != (archiveEntry = archiveInputStream.getNextTarEntry())) {
				if (archiveEntry.isFile()) {
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

			int validPageCount = 0;
			int badPageCount = 0;
			Pattern newLineSplitPattern = Pattern.compile("\n");
			Pattern contextSplitPattern = Pattern.compile(" ");
			int wordID, offset, totalPostingCount = 0;
			String[] myArray;
			List<Posting> postingList = new ArrayList<Posting>(termBatchSize);

			FolderReader folderReader = new FolderReader(new File(tempFolderPath));
			
			while (null != (page = folderReader.next())) {
				if (null != page.getContent()) {
					StringBuilder stringBuilder = new StringBuilder();
					try {
						Parser.parseDoc(page.getUrl(), page.getContent(), stringBuilder);
						validPageCount++;
						String[] lines = newLineSplitPattern.split(stringBuilder);
						//System.out.println(lines.length);
						int docID = docIdGenerator.register(page.getUrl(), page.getContent().length());
						for (String parsedLine : lines) {
							myArray = contextSplitPattern.split(parsedLine);
							if (myArray.length > 1) {
								totalPostingCount++;
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
								if(postingList.size()==termBatchSize){
									System.gc();	
									new PostingWriter(postingList,lexicon, indexPath).write();
									System.out.println("So far "+ (System.currentTimeMillis()-start)+" milliSec have passed since execution.");
									postingList = null;
									System.gc();	

									postingList = new ArrayList<Posting>(termBatchSize);
								}
							}
						}
					} catch (StringIndexOutOfBoundsException e) {
						badPageCount++;
					}
				}
			}

			if(postingList.size()!=0){
				new PostingWriter(postingList,lexicon, indexPath).write();
				System.gc();	
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
		
		System.out.println("Total Postings: " + totalPostingCount);
		System.out.println("Total Time taken: " + (System.currentTimeMillis() - start));
		System.out.println("Done. Parsed " + validPageCount + " files.");
		System.out.println(GzipReader.totalFile);
		System.out.println(GzipReader.rejectFile + " Rejected");
		System.out.println("BadPageCount : " + badPageCount);
		
		return true;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String inputPath = "/Users/Walliee/Documents/workspace/Indexing/nz2";
		String outputPath = "/Users/Walliee/Documents/workspace/Indexing/temp";
		int batchSize= 5000000;
		new Indexer(inputPath,outputPath,batchSize).index();
	}
}
