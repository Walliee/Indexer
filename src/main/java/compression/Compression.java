package compression;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements code for delta encoding and var-byte compression
 *
 */
public class Compression {
	static int mask8bit = (1 << 8) - 1;
	
	/**
	 * Method for delta compression
	 * 
	 * @param docVector
	 * @return
	 */
	public static int[]  deltaCompress(List<Integer> docVector) {
		int[] returnList = new int[docVector.size()];
		returnList[0] = docVector.get(0).intValue();

		for (int i = 1; i < docVector.size(); i++) {
			returnList[i]=docVector.get(i).intValue()-docVector.get(i-1).intValue();
		}
		return returnList;
	}

	/**
	 * Method for delta decompression
	 * 
	 * @param compressedList
	 * @return
	 */
	public static ArrayList<Integer> deltaDecompress(int[] compressedList) {
		int[] decompressedArray = new int[compressedList.length];
		ArrayList<Integer> decompressedList = new ArrayList<Integer>(compressedList.length);
		decompressedArray[0] = compressedList[0];
		decompressedList.add(decompressedArray[0]);
		for (int i = 1; i < compressedList.length; i++) {
			decompressedArray[i] = compressedList[i] + decompressedArray[i - 1];
			decompressedList.add(decompressedArray[i]);
		}
		return decompressedList;
	}
	
	/**
	 * Helper method for var-byte encoding 
	 * 
	 * @param num
	 * @param resultList
	 */
	private static void innerEncode(int num, List<Byte> resultList) {
		int headNum = resultList.size();
		while (true) {
			byte n = (byte) (num % 128);
			resultList.add(headNum, n);
			if (num < 128)
				break;
			num = num >>> 7;
		}

		int lastIndex = resultList.size() - 1;
		Byte val = resultList.get(lastIndex);
		val = (byte) (val.byteValue() - 128);
		resultList.remove(lastIndex);
		resultList.add(val);
	}

	/**
	 * Method for Var-Byte encoding
	 * 
	 * @param list
	 * @return
	 */
	public static byte[] encode(List<Integer> list) {
		List<Byte> resultList = new ArrayList<Byte>();
		for (Integer num:list) {
			innerEncode(num.intValue() , resultList);
		}
		int listNum = resultList.size();
		byte[] resultArray = new byte[listNum + 4];
		int num = list.size();

		resultArray[0] = (byte) ((num >> 24) & mask8bit);
		resultArray[1] = (byte) ((num >> 16) & mask8bit);
		resultArray[2] = (byte) ((num >> 8) & mask8bit);
		resultArray[3] = (byte) (num & mask8bit);

		for (int i = 0; i < listNum; i++)
			resultArray[i + 4] = resultList.get(i);

		return resultArray;
	}

	/**
	 * Method for Var-Byte decoding
	 * 
	 * @param encodedArray
	 * @return
	 */
	public static List<Integer> decode(byte[] encodedArray) {
		ArrayList<Integer> decodedArray = new ArrayList<Integer>();
		int n = 0;
		for (int i = 4; i < encodedArray.length; i++) {

			if (0 <= encodedArray[i])
				n = (n << 7) + encodedArray[i];
			else {
				n = (n << 7) + (encodedArray[i] + 128);
				decodedArray.add(n);
				n = 0;
			}
		}
		return decodedArray;

	}
}