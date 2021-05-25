package SevenZip;

public class CorpusSilesia {

    public static String[] paths = {
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\dickens",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\mozilla",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\mr",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\nci",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\ola",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\ooffice",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\osdb",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\reymont",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\samba",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\sao",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\webster",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\x-ray",
            "C:\\Users\\tmsl9\\GitHub\\LZMA-logs\\lzma1900\\CorpusSilesia\\xml",
    };

    public static String compressedFile(String path){
        return path + "_comp";
    }

    public static String decompressedFile(String path){
        return path + "_decomp";
    }

}
