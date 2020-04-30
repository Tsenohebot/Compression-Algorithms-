public class Main {

    public static void main(String[] args){
        Huffman_Compress hc = new Huffman_Compress();
        hc.compress("Benchfiles/cc/alice29.txt");
    }

    //TODO: Add method for algorithms to compress/decompress directories.
    //TODO: Implement better version of LZ77.
    //TODO: GUI.

    //Project is still in development
}
