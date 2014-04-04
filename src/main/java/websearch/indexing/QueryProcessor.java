package websearch.indexing;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import queryProcessingUtils.BM25Scorer;
import queryProcessingUtils.DocumentScore;
import queryProcessingUtils.WordFrequencyEntry;
import dataStructures.Lexicon;
import mergeUtils.IndexReader;
import dataStructures.DocumentInfo;
import mergeUtils.IndexEntry;
import dataStructures.CorpusStatistics;
import mergeUtils.WrapperIndexEntry;

public class QueryProcessor {
	IndexReader indexReader;
	Lexicon lexicon;
	Map<String,Integer> wordMap ;
	Map<Integer, DocumentInfo> documentMap ;
	double avgDocumentLength;

	public QueryProcessor(IndexReader indexReader,
			Lexicon lexicon, Map<String,Integer> wordMap,Map<Integer, DocumentInfo> documentMap, double avgDocumentLength) {
		super();
		this.indexReader = indexReader;
		this.lexicon = lexicon;
		this.wordMap = wordMap;
		this.documentMap = documentMap;
		this.avgDocumentLength = avgDocumentLength;
	}
	
	public DocumentScore[] search(List<String> tokens, int numberOfResults) throws FileNotFoundException, IOException {
		List<WordFrequencyEntry> tokenList = new ArrayList<WordFrequencyEntry>(tokens.size());
		List<String> newTokens = new ArrayList<String>(tokens.size());
		for(String token:tokens){
			newTokens.add(token);
			if(wordMap.containsKey(token)) {
				int wordID = wordMap.get(token);
				//System.out.println(wordID);
				IndexEntry indexEntry = indexReader.openList(wordID);
				//System.out.println(indexEntry);
				tokenList.add(new WordFrequencyEntry(wordID, indexEntry.getDocVectorSize()));
			} else {
				newTokens.remove(token);
			}
		}
		if(tokenList.size() == 0) {
			return new DocumentScore[0];
		}
		if(tokens.size() != newTokens.size() && newTokens.size() != 0) {
			System.out.print("Some words not found!! Showing results for: \"");
			for(String token:newTokens) {
				System.out.print(token + " ");
			}
			System.out.print("\" instead.\n");
		}
		Collections.sort(tokenList);
		return processQuery(tokenList, numberOfResults);
	}


	private DocumentScore[] processQuery(List<WordFrequencyEntry> wordFrequencyEntries, int numberOfResults) throws FileNotFoundException, IOException{
		PriorityQueue<DocumentScore> documentHeap = new PriorityQueue<DocumentScore>(
				numberOfResults, new Comparator<DocumentScore>() {

					@Override
					public int compare(DocumentScore o1, DocumentScore o2) {
						if(o1.getScore()==o2.getScore())
							return 0;
						return(  (o1.getScore()-o2.getScore())>0	)?1:-1;
						
					}
				});
		
		
		//Assumes a sorted array of wordIDs
		ArrayList<WrapperIndexEntry> indexEntries = new ArrayList<WrapperIndexEntry>(wordFrequencyEntries.size());
		for( WordFrequencyEntry wordFrequencyEntry : wordFrequencyEntries ){
			int wordID = wordFrequencyEntry.getWordID();
			IndexEntry temp =  indexReader.openList(wordID);
			if(temp!=null){
				indexEntries.add(new WrapperIndexEntry(temp) );
			}
		}
			
		int maxdocID  = indexEntries.get(0).getMaxDocID();
		
		//for(WrapperIndexEntry indexEntry:indexEntries)
		//	System.out.println(indexEntry.toString());
		
		int documentID = 0; 
		 while (documentID <= maxdocID) 
		 { 
		 /* get next post from shortest list */ 
			 documentID = WrapperIndexEntry.nextGEQ(indexEntries.get(0), documentID); 
		 /* see if you find entries with same docID in other lists */ 
			 int d=0;
			for (int i=1; (i<indexEntries.size()) && ((d=WrapperIndexEntry.nextGEQ(indexEntries.get(i), documentID)) == documentID); i++); 
		
			 if (d > documentID) {
				 documentID = d; /* not in intersection and we move to the next possible id*/
			 } else { 
				 	List<Integer> frequency = new ArrayList<Integer>(indexEntries.size()) ;
				 	List<Integer> globalFrequencies = new ArrayList<Integer>(indexEntries.size());
				 	BM25Scorer scorer = new BM25Scorer(1.2, 0.75);
				 	/* docID is in intersection; now get all frequencies */ 
				 	for (int i=0; i<indexEntries.size(); i++) {
				 		//frequency[i] = indexEntries.get(i).getFrequency(did);}
				 		frequency.add( WrapperIndexEntry.currentFrequency(indexEntries.get(i)));
				 		globalFrequencies.add(indexEntries.get(i).getDocVectorSize());
				 		}
				 	
				 	/* compute BM25 score from frequencies and other data */ 
				 	double score = scorer.score(documentMap.size(), globalFrequencies, frequency, documentMap.get(documentID).getDocumentLength(), avgDocumentLength);	
				 	//Get Total Documents N from the documentRegistrar
				 	//int globalFrequency = lexiconSingleton.ge
				 	//Get Totol Frequency F from the LexiconRegistrar or the indexEntity
				 	//Length of document d from the documentRegistrar
				 	//AverageDocumentLenght |d| from the documentRegistrar
				 	//k & b are constants
				 	
				 	//add it to the heap
				 	documentHeap.offer(new DocumentScore(documentID, score));
				 	if(documentHeap.size()>numberOfResults)
				 		documentHeap.remove();
				 	documentID++; /* and increase did to search for next post */ 
			 	} 
		 } 
		 DocumentScore[] a = {};
		 return documentHeap.toArray(a);
		 // return heap results.
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException{
		System.out.println("Loading Lexicon...");
		ObjectInputStream objectInputStream = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(
						"/Users/Walliee/Documents/workspace/indexing/temp50/FinalIndexLexicon")));
		Lexicon lexicon = (Lexicon) objectInputStream.readObject();
		objectInputStream.close();
		System.out.println("Lexicon loading complete");
		
		Map<String,Integer> wordMap = new HashMap<String, Integer>(50000);
		Map<Integer, DocumentInfo> documentMap = new HashMap<Integer, DocumentInfo>(500000);
		
		BufferedReader wordIdReader = new BufferedReader( new FileReader("/Users/Walliee/Documents/workspace/indexing/temp50/WordID") );
		System.out.println("Loading DocID references...");
		BufferedReader docIdReader = new BufferedReader( new FileReader("/Users/Walliee/Documents/workspace/indexing/temp50/DocumentID") );
		
		String wordLine;
		while(null != (wordLine = wordIdReader.readLine())){
			String[] split = wordLine.split("\\$\\$");
			wordMap.put(split[0], Integer.valueOf(split[1]));
		}
		
		while(null != (wordLine = docIdReader.readLine())){
			String[] split = wordLine.split("\\$\\$");
			documentMap.put(Integer.valueOf(split[1]),new DocumentInfo(split[0],Integer.valueOf(split[2])));
		}
		 
		objectInputStream = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(
						"/Users/Walliee/Documents/workspace/indexing/temp50/IndexStatistics")));
		CorpusStatistics corpusStatistics = (CorpusStatistics) objectInputStream.readObject();
		
		double avgDocumentLength = ((double)corpusStatistics.getTotalPostings())/((double) corpusStatistics.getTotalDocuments());
		IndexReader indexReader = new IndexReader(lexicon, 1, "/Users/Walliee/Documents/workspace/indexing/temp50/FinalIndex", true);
		QueryProcessor queryProcessor = new QueryProcessor(indexReader, lexicon, wordMap,documentMap,avgDocumentLength);
		boolean toContinue = true;
		while(toContinue){
			try{
				System.out.println("Enter Your Query : ");
				BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
			    String string = bufferReader.readLine();
			    string = string.toLowerCase();
			    //String[] tokens = string.split(" ");
			    List<String> tokens = new ArrayList<String>(Arrays.asList(string.split(" ")));
			    //int[] termIDArray = new int[0];
			    System.out.println("How many pages do you want?");
			    string = bufferReader.readLine();
			    int numberOfResults =Integer.valueOf(string);
				Long startTime = System.currentTimeMillis();
				List<DocumentScore> documentScores = new ArrayList<DocumentScore>(Arrays.asList(queryProcessor.search(tokens, numberOfResults)));
				Long stopTime= System.currentTimeMillis();
				//Collections.reverse(Collections.sort(documentScores));
				Collections.sort(documentScores, Collections.reverseOrder());
				if(documentScores.size()>0){
//					for(DocumentScore documentScore: documentScores){
//					}
				System.out.println("Found " + documentScores.size() + " matching documents in " + (stopTime-startTime) + " milliSeconds");
				System.out.println("####################################################################################");
				for(DocumentScore documentScore:documentScores){
					System.out.println(documentMap.get(documentScore.getDocID()).getUrl()+ "     :   "+documentScore.getScore());
				}
				}
				else
					System.out.println("Sorry!! No Matches Found for the given query");
				System.out.println("####################################################################################");
				//System.out.println("Found "+documentScores.length+" matching documents in "+(stopTime-startTime)+" milliSeconds");
				System.out.println("Do you want to continue? type Y to continue:");
				string = bufferReader.readLine();
			    if(!(string.equalsIgnoreCase("yes")|| string.equalsIgnoreCase("y"))){
			    	System.out.println("Invalid Input. Exiting");
			    	toContinue = false;
			    }
			    	
				
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			finally {
				wordIdReader.close();
				docIdReader.close();
			}
		}

	}

}
