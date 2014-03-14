package compression;

import java.util.ArrayList;
import java.util.List;

public class Compression {
	static int mask8bit = (1 << 8) - 1;
	
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

//	public static byte[] encode(int[] numbers){
//		ArrayList<Integer> arrayList = new ArrayList<Integer>(numbers.length);
//		for(Integer temp:numbers)
//			arrayList.add(temp);
//		return encode(arrayList);
//	}

	public static byte[] encode(List<Integer> numbers) {
		List<Byte> resultList = new ArrayList<Byte>();
		int beforeNum = 0;
		for (Integer num:numbers) {
			innerEncode(num.intValue() , resultList);
			beforeNum = num.intValue();
		}
		int listNum = resultList.size();
		byte[] resultArray = new byte[listNum + 4];
		int num = numbers.size();

		resultArray[0] = (byte) ((num >> 24) & mask8bit);
		resultArray[1] = (byte) ((num >> 16) & mask8bit);
		resultArray[2] = (byte) ((num >> 8) & mask8bit);
		resultArray[3] = (byte) (num & mask8bit);

		for (int i = 0; i < listNum; i++)
			resultArray[i + 4] = resultList.get(i);

		return resultArray;
	}

//	public static int decode(byte[] encodedValue, int offset){
//		int dataNum = ((encodedValue[0] & mask8bit) << 24 | (encodedValue[1] & mask8bit) << 16)
//				| ((encodedValue[2] & mask8bit) << 8 | (encodedValue[3] & mask8bit));
//		int n = 0;
//		for (int i = 4; i < encodedValue.length; i++) {
//
//			if (0 <= encodedValue[i])
//				n = (n << 7) + encodedValue[i];
//			else {
//				n = (n << 7) + (encodedValue[i] + 128);
//				if(offset==0)
//					return n;
//				n = 0;
//			}
//
//		}
//		throw new InvalidParameterException();
//	}

	public static List<Integer> decode(byte[] encodedValue) {
		int dataNum = ((encodedValue[0] & mask8bit) << 24 | (encodedValue[1] & mask8bit) << 16)
				| ((encodedValue[2] & mask8bit) << 8 | (encodedValue[3] & mask8bit));
		//	int[] decode = new int[dataNum];
		ArrayList<Integer> decode=new ArrayList<Integer>();
		//	int id = 0;
		int n = 0;
		for (int i = 4; i < encodedValue.length; i++) {

			if (0 <= encodedValue[i])
				n = (n << 7) + encodedValue[i];
			else {
				n = (n << 7) + (encodedValue[i] + 128);
				decode.add(n);
				//decode[id++] = n;
				n = 0;
			}
		}
		//		if (useGapList)
		//			for (int j = 1; j < dataNum; j++)
		//				decode[j] += decode[j - 1];
		return decode;

	}

//	public static int[] decodeDocId(byte[] encodedValue) {
//
//		int dataNum = ((encodedValue[0] & mask8bit) << 24 | (encodedValue[1] & mask8bit) << 16)
//				| ((encodedValue[2] & mask8bit) << 8 | (encodedValue[3] & mask8bit));
//		int[] decode = new int[dataNum];
//		//    ArrayList<Integer> decode=new ArrayList<Integer>();
//		int id = 0;
//		int n = 0;
//		for (int i = 4; i < encodedValue.length; i++) {
//
//			if (0 <= encodedValue[i])
//				n = (n << 7) + encodedValue[i];
//			else {
//				n = (n << 7) + (encodedValue[i] + 128);
//				//    decode.add(n);
//				decode[id++] = n;
//				n = 0;
//			}
//
//		}
//		//		if (useGapList)
//		//			for (int j = 1; j < dataNum; j++)
//		//				decode[j] += decode[j - 1];
//		return decode;
//
//	}

	public static int[]  deltaCompress(List<Integer> docVector) {
		int[] returnList = new int[docVector.size()];
		returnList[0] = docVector.get(0).intValue();

		for (int i = 1; i < docVector.size(); i++) {
			returnList[i]=docVector.get(i).intValue()-docVector.get(i-1).intValue();
		}
		return returnList;
	}

	public static ArrayList<Integer> deltaDecompress(int[] compressedList) {
		int[] decompressedarray = new int[compressedList.length];
		ArrayList<Integer> deCompressedList = new ArrayList<Integer>(compressedList.length);
		decompressedarray[0] = compressedList[0];
		deCompressedList.add(decompressedarray[0]);
		for (int i = 1; i < compressedList.length; i++) {
			decompressedarray[i] = compressedList[i] + decompressedarray[i - 1];
			deCompressedList.add(decompressedarray[i]);
			// compressedList[i] = deCompressedList[i];
		}
		return deCompressedList;
	}

	/*public static void Write(int value, OutputStream stream) throws IOException {
		do {
			byte byteCode = (byte) (value & 127);
			value = value >> 7;
			if (value != 0) {
				byteCode |= 128;
				stream.write(byteCode);
			} else
				stream.write(byteCode);
		} while (value != 0);
	}
	public static int Read(InputStream stream) throws IOException {
		int value = 0;
		long byteCode = 0;
		int shift = 0;
		do {
			byteCode = stream.read();
			value += (byteCode & 127) << shift;
			shift += 7;
		} while (byteCode > 127);

		return value;
	}*/
}