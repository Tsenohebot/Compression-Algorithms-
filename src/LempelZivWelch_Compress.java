import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LempelZivWelch_Compress {


    private HashMap<String,Integer> dictionary;
    public int uncompressed = 0, dictkey = 255,dict_len = 0,bytelength =0 ;
    public int uncompressedbytes, compressedbytes;
    String prefix = "";
    StringBuilder binst = new StringBuilder();


    //change from native -127/127 notation to 0-255 notation
    private int bytobin(byte b){
        int i = b;
        if (i < 0) i+= 256;
        return i;
    }

    private byte[] Compress (File file){
        List<Integer> Lstl = new ArrayList<>();
        try{
            prefix = "";
            binst = new StringBuilder();

            dictionary = new HashMap<String, Integer>();
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            //intialize dictionary
            for (int i = 0; i < 256; i++) {
                if(i > 127)dictionary.put("" + (char) (i-256), i);
                else dictionary.put("" + (char) i, i);
            }

            //build dictionary and add chracters to output list
            while(true){
                try{
                    char c = (char) dataInputStream.readByte();
                    //uncompressed++;
                    String comb = prefix + c;
                    if(dictionary.containsKey(comb))
                        prefix = comb;
                    else{
                        //System.out.print(dictionary.get(prefix) + " ");
                        Lstl.add(dictionary.get(prefix));
                        dictionary.put(comb,++dictkey);
                        //System.out.println(comb + " " + dictkey + " " );
                        prefix = ""+c;
                    }
                }catch (EOFException eof){
                    break;
                }

            }
            dataInputStream.close();
            fileInputStream.close();

        }catch (Exception e){
            System.out.println("Error occurred");
            System.exit(-1);
        }

        if(!prefix.isEmpty()){
            //System.out.println(dictionary.get(prefix));
            Lstl.add(dictionary.get(prefix));
        }

        bytelength =(int) Math.ceil(Math.log(dictionary.size())/Math.log(2));
        //System.out.println(bytelength);

        //System.out.println(dictionary.get(prefix));
        return Encodebitset(Srbuilder(Lstl));
    }


    //convert integers to binary strings according to size of dictinary
    private String Srbuilder(List<Integer>Lstl ){
        binst = new StringBuilder();
        //System.out.println(bytelength);
        for(int i = 0; i < Lstl.size();i++){
            //System.out.print(Lstl.get(i) + " ");
            String binary = Integer.toBinaryString(Lstl.get(i));
            if(binary.length() < bytelength){
                int zero = bytelength-binary.length();
                for (int j = 0; j < zero ; j++)
                    binary = "0" + binary;
            }

            binst.append(binary);
        }
        System.out.println();
        return binst.toString();
    }

    //unused
    private byte[] Encode_dict (HashMap<String,Integer> dict){
        String[] str = new String[dict.size() - 256];
        for(Map.Entry<String,Integer> entry: dict.entrySet()){
            if(entry.getValue() > 255){
                str[(entry.getValue() - 256)] = entry.getKey();

            }
        }
        StringBuilder sb = new StringBuilder();
        for (int l = 0; l < str.length;l++){sb.append(str[l]);sb.append(",");}
        String str2 = sb.toString();
        return str2.getBytes(StandardCharsets.UTF_8);
    }



    //convert binary string to bitset, then return the equivalent binary array
    private byte[] Encodebitset (String binstr){
        BitSet b = new BitSet(binstr.length());
        for(int i = 0; i < binstr.length(); i++){
            if(binstr.charAt(i) == '1') b.set(i);
        }
        binst = new StringBuilder();
        uncompressed = binstr.length();
        return b.toByteArray();
    }



    public void Encode(String filename){
        try{
            uncompressed = 0;
            File f =new File(filename);
            uncompressedbytes = (int)f.length();
            byte[] Compressed = Compress(f);
            File file = new File(filename+".lzw");
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeInt(bytelength);
            dos.writeInt(uncompressed);
            fos.write(Compressed);
            dos.close();
            fos.close();
            compressedbytes = Compressed.length;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}

