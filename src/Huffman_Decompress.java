import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Huffman_Decompress {

    private int Total_compressed = 0, Total_uncompressed = 0, total_topology = 0, extrabits = 0, totcharsincomp = 0;
    private byte[] Tree, Encoded_Data;
    private BitSet treebits, databits;
    private Stack st;

    //General Method
    //scan file and get byte array from data
    private byte[] extractTree(File file) {
        try{
            Tree = new byte[0];
            Tree = Files.readAllBytes(file.toPath());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return Tree;
    }

    private void decode(Nodes trees, BitSet octet, String file){
        try{
            Nodes root = trees;
            FileOutputStream fos = new FileOutputStream(file.substring(0,file.length()-4));

            ArrayList<Byte> arr = new ArrayList<>();
            int uncompcounter = 0;
            for(int i = 0; i<octet.size()-extrabits;i++){

                if(octet.get(i)){
                    root = root.right;
                }else{
                    root = root.left;
                }

                if(isleaf(root)){

                    int bto = root.cand;
                    arr.add((byte)bto);
                    uncompcounter+=1;
                    if(uncompcounter == Total_uncompressed){
                        break;
                    }
                    root = trees;
                }
            }

            for(int i = 0; i < arr.size(); i++){
                //System.out.println(arr.get(i));
                fos.write(arr.get(i));
            }
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void decompress(String file){
        try {
            File f = new File(file);
            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);

            Total_compressed = dis.readInt();
            total_topology = dis.readInt();
            Total_uncompressed = dis.readInt();
            extrabits = dis.readInt();
            totcharsincomp = Total_compressed + extrabits;
            int treebytes = (total_topology/8) ;
            //System.out.println(treebytes);
            //treebytes += (total_topology/8);
            //treebytes += total_topology%8;
            fis.close();
            dis.close();
            Tree = new byte[treebytes];
            Encoded_Data = new byte[totcharsincomp/8];
            //System.out.println(total_topology/8);
            System.out.println(total_topology + ": " + Total_uncompressed + " : " + Total_compressed + " : " + extrabits );
            //System.out.println(treebytes);

            FileInputStream fis2 = new FileInputStream(f);
            fis2.getChannel().position(16);
            fis2.read(Tree,0,treebytes);
            fis2.getChannel().position(16+treebytes);
            fis2.read(Encoded_Data,0,totcharsincomp/8);
            fis2.close();


            treebits = BitSet.valueOf(Tree);
            databits = BitSet.valueOf(Encoded_Data);
            Nodes n = Extract_tree(treebits);
            //testset(databits);
            //depthFirst(n);
            decode(n,databits,file);

        }catch (FileNotFoundException e){
            System.out.println("File not found");
        }catch (IOException f){
            System.out.println("could not read from file");
        }
    }

    private Nodes Extract_tree(BitSet b){
        Stack<Nodes> treeStack = new Stack<>();
        for(int i = 0; i < b.size();i++ ){

            if(b.get(i)){
                // System.out.println(/*s.toString()*/ i);
                StringBuilder s = new StringBuilder();
                i+=1;
                int lim = i+8;
                for(;i<lim;i++){

                    if (b.get(i)) {
                        s.append('1');
                    }else{
                        s.append('0');
                    }
                }
                i-=1;
                treeStack.push(new Nodes(Integer.parseInt(s.toString(),2),0));
                //System.out.println(Integer.parseInt(s.toString(),2));
            }else{
                if(!treeStack.isEmpty() && treeStack.size() > 1){
                    Nodes root = new Nodes(0);
                    Nodes right = treeStack.pop();
                    Nodes left = treeStack.pop();
                    root.right = right;
                    root.left = left;
                    treeStack.push(root);
                }
            }
        }
        //System.out.println(treeStack.size());
        return treeStack.pop();
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
        System.out.println();

    }

    private void depthFirst (Nodes root){

        if(!isleaf(root)){
            depthFirst(root.left);
            depthFirst(root.right);
        }else{
            int bt = root.cand;

            System.out.println((byte)bt);}
    }


    private boolean isleaf(Nodes nodes){

        return nodes.left == null && nodes.right == null;
    }
}
