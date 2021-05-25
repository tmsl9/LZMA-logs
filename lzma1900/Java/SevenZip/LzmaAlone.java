package SevenZip;

import org.apache.commons.io.FileUtils;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class LzmaAlone
{
	/*static public class CommandLine
	{
		public static final int kEncode = 0;
		public static final int kDecode = 1;
		public static final int kBenchmak = 2;
		
		public int Command = -1;
		public int NumBenchmarkPasses = 10;
		
		public int DictionarySize = 1 << 23;
		public boolean DictionarySizeIsDefined = false;
		
		public int Lc = 3;
		public int Lp = 0;
		public int Pb = 2;
		
		public int Fb = 128;
		public boolean FbIsDefined = false;
		
		public boolean Eos = false;
		
		public int Algorithm = 2;
		public int MatchFinder = 1;
		
		public String InFile;
		public String OutFile;
		
		boolean ParseSwitch(String s)
		{
			if (s.startsWith("d"))
			{
				DictionarySize = 1 << Integer.parseInt(s.substring(1));
				DictionarySizeIsDefined = true;
			}
			else if (s.startsWith("fb"))
			{
				Fb = Integer.parseInt(s.substring(2));
				FbIsDefined = true;
			}
			else if (s.startsWith("a"))
				Algorithm = Integer.parseInt(s.substring(1));
			else if (s.startsWith("lc"))
				Lc = Integer.parseInt(s.substring(2));
			else if (s.startsWith("lp"))
				Lp = Integer.parseInt(s.substring(2));
			else if (s.startsWith("pb"))
				Pb = Integer.parseInt(s.substring(2));
			else if (s.startsWith("eos"))
				Eos = true;
			else if (s.startsWith("mf"))
			{
				String mfs = s.substring(2);
				if (mfs.equals("bt2"))
					MatchFinder = 0;
				else if (mfs.equals("bt4"))
					MatchFinder = 1;
				else if (mfs.equals("bt4b"))
					MatchFinder = 2;
				else
					return false;
			}
			else
				return false;
			return true;
		}
		
		public boolean Parse(String[] args) throws Exception
		{
			int pos = 0;
			boolean switchMode = true;
			for (int i = 0; i < args.length; i++)
			{
				String s = args[i];
				if (s.length() == 0)
					return false;
				if (switchMode)
				{
					if (s.compareTo("--") == 0)
					{
						switchMode = false;
						continue;
					}
					if (s.charAt(0) == '-')
					{
						String sw = s.substring(1).toLowerCase();
						if (sw.length() == 0)
							return false;
						try
						{
							if (!ParseSwitch(sw))
								return false;
						}
						catch (NumberFormatException e)
						{
							return false;
						}
						continue;
					}
				}
				if (pos == 0)
				{
					if (s.equalsIgnoreCase("e"))
						Command = kEncode;
					else if (s.equalsIgnoreCase("d"))
						Command = kDecode;
					else if (s.equalsIgnoreCase("b"))
						Command = kBenchmak;
					else
						return false;
				}
				else if(pos == 1)
				{
					if (Command == kBenchmak)
					{
						try
						{
							NumBenchmarkPasses = Integer.parseInt(s);
							if (NumBenchmarkPasses < 1)
								return false;
						}
						catch (NumberFormatException e)
						{
							return false;
						}
					}
					else
						InFile = s;
				}
				else if(pos == 2)
					OutFile = s;
				else
					return false;
				pos++;
				continue;
			}
			return true;
		}
	}
	
	
	static void PrintHelp()
	{
		System.out.println(
				"\nUsage:  LZMA <e|d> [<switches>...] inputFile outputFile\n" +
				"  e: encode file\n" +
				"  d: decode file\n" +
				"  b: Benchmark\n" +
				"<Switches>\n" +
				// "  -a{N}:  set compression mode - [0, 1], default: 1 (max)\n" +
				"  -d{N}:  set dictionary - [0,28], default: 23 (8MB)\n" +
				"  -fb{N}: set number of fast bytes - [5, 273], default: 128\n" +
				"  -lc{N}: set number of literal context bits - [0, 8], default: 3\n" +
				"  -lp{N}: set number of literal pos bits - [0, 4], default: 0\n" +
				"  -pb{N}: set number of pos bits - [0, 4], default: 2\n" +
				"  -mf{MF_ID}: set Match Finder: [bt2, bt4], default: bt4\n" +
				"  -eos:   write End Of Stream marker\n"
				);
	}

	public static void main(String[] args) throws Exception
	{
		System.out.println("\nLZMA (Java) 4.61  2008-11-23\n");
		
		if (args.length < 1)
		{
			PrintHelp();
			return;
		}
		
		CommandLine params = new CommandLine();
		if (!params.Parse(args))
		{
			System.out.println("\nIncorrect command");
			return;
		}
		
		if (params.Command == CommandLine.kBenchmak)
		{
			int dictionary = (1 << 21);
			if (params.DictionarySizeIsDefined)
				dictionary = params.DictionarySize;
			if (params.MatchFinder > 1)
				throw new Exception("Unsupported match finder");
			SevenZip.LzmaBench.LzmaBenchmark(params.NumBenchmarkPasses, dictionary);
		}
		else if (params.Command == CommandLine.kEncode || params.Command == CommandLine.kDecode)
		{
			java.io.File inFile = new java.io.File(params.InFile);
			java.io.File outFile = new java.io.File(params.OutFile);
			
			java.io.BufferedInputStream inStream  = new java.io.BufferedInputStream(new java.io.FileInputStream(inFile));
			java.io.BufferedOutputStream outStream = new java.io.BufferedOutputStream(new java.io.FileOutputStream(outFile));
			
			boolean eos = false;
			if (params.Eos)
				eos = true;
			if (params.Command == CommandLine.kEncode)
			{
				SevenZip.Compression.LZMA.Encoder encoder = new SevenZip.Compression.LZMA.Encoder();
				if (!encoder.SetAlgorithm(params.Algorithm))
					throw new Exception("Incorrect compression mode");
				if (!encoder.SetDictionarySize(params.DictionarySize))
					throw new Exception("Incorrect dictionary size");
				if (!encoder.SetNumFastBytes(params.Fb))
					throw new Exception("Incorrect -fb value");
				if (!encoder.SetMatchFinder(params.MatchFinder))
					throw new Exception("Incorrect -mf value");
				if (!encoder.SetLcLpPb(params.Lc, params.Lp, params.Pb))
					throw new Exception("Incorrect -lc or -lp or -pb value");
				encoder.SetEndMarkerMode(eos);
				encoder.WriteCoderProperties(outStream);
				long fileSize;
				if (eos)
					fileSize = -1;
				else
					fileSize = inFile.length();
				for (int i = 0; i < 8; i++)
					outStream.write((int)(fileSize >>> (8 * i)) & 0xFF);
				encoder.Code(inStream, outStream, -1, -1, null);
			}
			else
			{
				int propertiesSize = 5;
				byte[] properties = new byte[propertiesSize];
				if (inStream.read(properties, 0, propertiesSize) != propertiesSize)
					throw new Exception("input .lzma file is too short");
				SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();
				if (!decoder.SetDecoderProperties(properties))
					throw new Exception("Incorrect stream properties");
				long outSize = 0;
				for (int i = 0; i < 8; i++)
				{
					int v = inStream.read();
					if (v < 0)
						throw new Exception("Can't read stream size");
					outSize |= ((long)v) << (8 * i);
				}
				if (!decoder.Code(inStream, outStream, outSize))
					throw new Exception("Error in data stream");
			}
			outStream.flush();
			outStream.close();
			inStream.close();
		}
		else
			throw new Exception("Incorrect command");
		return;
	}*/

	private static DecimalFormat df;

	private static final String originalFile     = "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\Java\\SevenZip\\TestFile\\test.txt";
	private static final String compressedFile   = "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\Java\\SevenZip\\TestFile\\test_comp";
	private static final String decompressedFile = "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\Java\\SevenZip\\TestFile\\test_decomp.txt";

	public static void compressFile(String path) throws Exception {

		java.io.File inFile = new java.io.File(path);
		java.io.File outFile = new java.io.File(CorpusSilesia.compressedFile(path));

		java.io.BufferedInputStream inStream  = new java.io.BufferedInputStream(new java.io.FileInputStream(inFile));
		java.io.BufferedOutputStream outStream = new java.io.BufferedOutputStream(new java.io.FileOutputStream(outFile));

		SevenZip.Compression.LZMA.Encoder encoder = new SevenZip.Compression.LZMA.Encoder();

		encoder.WriteCoderProperties(outStream);
		long fileSize;
		fileSize = inFile.length();

		for (int i = 0; i < 8; i++)
			outStream.write((int)(fileSize >>> (8 * i)) & 0xFF);
		encoder.Code(inStream, outStream, -1, -1, null);

		outStream.flush();
		outStream.close();
		inStream.close();
	}

	public static void decompressFile(String path) throws Exception {

		java.io.File inFile = new java.io.File(CorpusSilesia.compressedFile(path));
		java.io.File outFile = new java.io.File(CorpusSilesia.decompressedFile(path));

		java.io.BufferedInputStream inStream  = new java.io.BufferedInputStream(new java.io.FileInputStream(inFile));
		java.io.BufferedOutputStream outStream = new java.io.BufferedOutputStream(new java.io.FileOutputStream(outFile));

		int propertiesSize = 5;
		byte[] properties = new byte[propertiesSize];
		if (inStream.read(properties, 0, propertiesSize) != propertiesSize)
			throw new Exception("input .lzma file is too short");
		SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();
		if (!decoder.SetDecoderProperties(properties))
			throw new Exception("Incorrect stream properties");
		long outSize = 0;
		for (int i = 0; i < 8; i++)
		{
			int v = inStream.read();
			if (v < 0)
				throw new Exception("Can't read stream size");
			outSize |= ((long)v) << (8 * i);
		}
		if (!decoder.Code(inStream, outStream, outSize))
			throw new Exception("Error in data stream");

		outStream.flush();
		outStream.close();
		inStream.close();
	}

	public static long sizeFile_bytes(String path){
		java.io.File file = new java.io.File(path);
		return FileUtils.sizeOf(file);
	}

	public static float b_to_Mb(long bytes){
		long MEGABYTE = 1024L * 1024L;
		return (float) bytes / MEGABYTE;
	}

	public static double ns_to_s(long nanoseconds){
		return (double) TimeUnit.NANOSECONDS.toMillis(nanoseconds) / 1000;
	}

	public static float[] compress_decompress_details(String path, long fileSize, boolean compression) throws Exception {
		System.out.println((compression ? "Compression:" : "Decompression:"));

		long begin = System.nanoTime();

		if(compression) {
			compressFile(path);
		}else
			decompressFile(path);

		long end = System.nanoTime();

		long resultFileSize = sizeFile_bytes(compression ? CorpusSilesia.compressedFile(path) : CorpusSilesia.decompressedFile(path));

		double seconds = ns_to_s(end - begin);
		float Mb_per_s = (float) (b_to_Mb(fileSize) / seconds);

		System.out.println("\tSize (" 	  + (compression ? "" : "de") + "comp): " + df.format(b_to_Mb(resultFileSize)) + " Mb");
		System.out.println("\tTime: " 	  + seconds 				  + " seconds");
		System.out.println("\tBit rate: " + df.format(Mb_per_s) 	  + " Mb/s");

		float compressionRatio = .00F;

		if(compression){
			compressionRatio = (float) (fileSize / resultFileSize);
			System.out.println("\tCompression ratio: " + compressionRatio + ":1 bps");
		}

		return new float[]{Mb_per_s, compressionRatio};
	}

	public static void main(String[] args) throws Exception
	{
		System.out.println("\nLZMA (Java) 4.61  2008-11-23\n");

		df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		float bitRateCompression = .00F;
		float bitRateDecompression = .00F;
		float compressionRatio = .00F;
		for(String path: CorpusSilesia.paths) {
			String[] files = path.split("\\\\");
			System.out.println("File: " + files[files.length - 1]);
			long fileSize = sizeFile_bytes(path);
			System.out.println("Size: " 	+ df.format(b_to_Mb(fileSize)) 		+ " Mb");
			float[] result_comp = compress_decompress_details(path, fileSize, true);
			float[] result_decomp = compress_decompress_details(path, fileSize, false);
			bitRateCompression += result_comp[0];
			bitRateDecompression += result_decomp[0];
			compressionRatio += result_comp[1];
			System.out.println("\n\n");
		}

		float averageBitRateCompression = bitRateCompression / CorpusSilesia.paths.length;
		float averageBitRateDecompression = bitRateDecompression / CorpusSilesia.paths.length;
		float averageCompressionRatio = compressionRatio / CorpusSilesia.paths.length;
		System.out.println("Overall:");
		System.out.println("\tCompression bit rate: " + df.format(averageBitRateCompression) + " Mb/s");
		System.out.println("\tDecompression bit rate: " + df.format(averageBitRateDecompression) + " Mb/s");
		System.out.println("\tCompression ratio: " + df.format(averageCompressionRatio) + " bps");

	}

}