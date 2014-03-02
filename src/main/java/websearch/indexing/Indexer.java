package websearch.indexing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
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
					log4j.debug(file.getName());
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
