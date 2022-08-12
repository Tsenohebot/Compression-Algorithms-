// Nabeel Akhtar - 2020

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Huffman_Compress {

    public int Total_compressed = 0, Total_uncompressed = 0, total_topology = 0, extrabits = 0;
    public long timeelp;
    private byte[] FileBinary;
    private int counter = 0;
    private Nodes top;
    private StringBuilder strings;


    //General Method
    //generate frequency map from byte array
    private HashMap<Integer,Integer> FrequencyMap(byte[] bytes){

        HashMap<Integer,Integer> WordCount = new HashMap<>();
        counter = 0;

        for(int i = 0; i < bytes.length; i++){
            int w = bytobin(bytes[i]);
            counter++;
            Integer frequency = WordCount.get(w);
            if(frequency != null) WordCount.put(w,frequency+1);
            else WordCount.put(w,1);
        }

        Total_uncompressed = counter;
        return WordCount;
    }

    //General Method
    //convert byte data type to ascii
    public static int bytobin(Byte b) {
        int result = b;
        if (result < 0) {
            result = ~b;
            result = result + 1;
            result = result ^ 255;
            result += 1;
        }
        return result;
    }


    //General Method
    //generate reverse priority queue (Smallest frequency is given highest priority)
    // from byte array using frequency map
    private PriorityQueue<Nodes> buildNodes(byte[] bytes){

        HashMap<Integer,Integer> k = FrequencyMap(bytes);
        PriorityQueue<Nodes> result = new PriorityQueue<>();
        Iterator it = k.entrySet().iterator();

        while(it.hasNext()){
            Map.Entry r = (Map.Entry)it.next();
            Nodes n = new Nodes((Integer) r.getKey(),(Integer) r.getValue());
            result.add(n);
        }

        return result;

    }

    //General Method
    //scan file and get byte array from data
    private byte[] getFileBinary(File file) {
        try {
            FileBinary = new byte[0];
            FileBinary = Files.readAllBytes(file.toPath());
        }catch (IOException e ){
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(101);
        }
        return FileBinary;
    }


    //General Method
    //build the huffman tree, using the priority queue;
    private static  Nodes BuildTree(PriorityQueue<Nodes> r){

        if(r.size() == 1){
            return r.poll();
        }

        while(r.size() > 1){
            Nodes leftleaf = r.poll();
            Nodes rightleaf = r.poll();
            Nodes root = new Nodes(leftleaf.freq + rightleaf.freq);
            root.right = rightleaf;
            root.left  = leftleaf;
            r.add(root);
        }
        return r.poll();
    }

    //to check if a given node is a leaf
    private boolean leaf (Nodes n){
        if(n.left == null && n.right == null)
            return true;
        else return false;
    }

    //General Method
    //build code lookup table
    private Map<Integer,String> CodedTable(Nodes root){
        Map<Integer,String> table = new HashMap<Integer, String>();
        depthFirst(root,"",table);
        return table;
    }

    //recursive Pre-order search of the tree
    private void depthFirst (Nodes root, String buffer, Map<Integer,String> lookup){

        if(!leaf(root)){
            depthFirst(root.left, buffer+'0',lookup);
            depthFirst(root.right,buffer+'1',lookup);
        }else
            lookup.put(root.cand,buffer);
    }

    ////////////////////////////////////////////////////////Compression ////////////////////////////////////////////////
    private void encode(File f, Map<Integer,String> m){
        try {


            byte[] binout = PopulateString(m);
            byte[] bit ;
            FileOutputStream fio = new FileOutputStream(f.getPath()+".huf");
            DataOutputStream datio = new DataOutputStream(fio);


            datio.writeInt(Total_compressed);

            writeTree(top,strings);//encode tree for compressed file
            String sbuilt = strings.toString();
            bit = convToBytes(sbuilt);
            //System.out.println(bit.length);
            datio.writeInt(total_topology);
            datio.writeInt(Total_uncompressed);
            datio.writeInt(extrabits);
            System.out.println(total_topology + ": " + Total_uncompressed + " : " + Total_compressed + " : " + extrabits );
            //System.out.println(sbuilt);
            fio.write(bit);
            fio.write(binout);
            fio.close();
            datio.close();
            Total_compressed+=(96+(total_topology/8));
            timeelp = System.nanoTime();
        }catch (FileNotFoundException e){
            System.out.println("FILE NOT FOUND ! : " + e.getMessage());
        }catch (IOException g){
            System.out.println("WRITEOUT FAILED ! : " + g.getMessage());
        }/*catch (NumberFormatException rq){
            System.out.println(rq.getCause());
        }*/
    }


    //method to store tree in the compressed file.
    private void writeTree(Nodes root, StringBuilder s){

        if(root == null)return;

        writeTree(root.left,s);
        writeTree(root.right,s);

        if(leaf(root)){
            s.append("1");
            String fr = String.format("%8s",Integer.toBinaryString(root.cand & 0xFF)).replace(' ','0');
            s.append(fr);
            total_topology ++;
            //System.out.println(root.cand);
            //System.out.println(fr);
            //System.out.println(Integer.parseInt(fr,2));
        }else{
            s.append('0');}
    }

    //Convert binary string to byte array;
    private byte[] convToBytes(String s){
        total_topology = s.length();
        BitSet b = new BitSet(s.length());
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == '1') b.set(i);
        }
        //testset(b);
        return b.toByteArray();
    }

    private byte[] PopulateString(Map<Integer,String> map) {
        BitSet b = new BitSet(8) ;
        try {
            extrabits = 0;
            String[] binstr = new String[FileBinary.length];
            int totlength = 0;//total length of the bit array;

            //Store as binary strings in sequence.
            for (int i = 0; i < FileBinary.length; i++) {

                binstr[i] = map.get(bytobin(FileBinary[i]));
                totlength += binstr[i].length();
            }

            Total_compressed = totlength;
            //System.out.println(totlength);
            if(totlength%8 != 0) {
                extrabits = (8-(totlength%8));
                totlength += extrabits;
            }
            //System.out.println(totlength);
            int binaryIndex = 0;
            b  = new BitSet(totlength);
            for (int j = 0; j < binstr.length; j++ ){
                for(int lo = 0; lo < binstr[j].length();lo++){
                    if (binstr[j].charAt(lo) == '1') b.set(binaryIndex);
                    binaryIndex++;
                }
            }
            //testset(b);
            return b.toByteArray();
        }catch (NullPointerException e){
            System.out.println(e.getCause());
            System.exit(404);
        }


        return b.toByteArray();
    }

    //initiate zipping process
    public void compress(String f){

        File d = new File(f);
        byte[] r = getFileBinary(d);
        PriorityQueue<Nodes> g =  buildNodes(r);
        Nodes n = BuildTree(g);
        top = n;
        strings = new StringBuilder();
        Map<Integer,String> lpp = CodedTable(n);
        encode(d,lpp);
    }

    private void resetValues(){

        strings =  new StringBuilder();
        top = null;
        total_topology = 0;
        Total_compressed = 0;
        Total_uncompressed = 0;

    }

    private void testset(BitSet b){
        int c = 0;
        for (int o = 0; o < b.length();o++){
            if(b.get(o))System.out.print(1);
            else System.out.print(0);
            c++;

            if(c%8 == 0) System.out.println();
        }
        System.out.println();
    }
}
