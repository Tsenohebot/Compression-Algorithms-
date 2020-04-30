import java.io.*;
import java.util.*;

public class LempelZivWelch_Decompress {

    private TreeMap<Integer,String> dictionary;
    private int uncompressed = 0, dictkey = 255,dict_size = 0,bytelen=0;
    String prefix = "";

    public void decode(String file) {
        try {
            dictionary = new TreeMap<>();
            File f = new File(file);
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);

            bytelen = dis.readInt();
            uncompressed = dis.readInt();
            dict_size = dis.readInt();
            int filesize = (int)f.length()-8;
            fis.close();
            dis.close();
            byte[] dict_enc = new byte[dict_size];
            byte[] encoded_data = new byte[filesize];
            FileInputStream fis2 = new FileInputStream(file);
            fis2.getChannel().position(8);
            fis2.read(encoded_data,0,filesize);
            fis2.close();

            RetDict();
            BitSet encodeddata = BitSet.valueOf(encoded_data);
            List<Integer> dat= decodebitset(encodeddata);
            writeout(dat,file.substring(0,file.length()-4));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeout(List<Integer> dat, String filename) {
        File out = new File(filename);
        try {
            FileOutputStream outs = new FileOutputStream(filename);
            DataOutputStream dos = new DataOutputStream(outs);
            String prefix = "" + (char)(int)dat.remove(0);
            String str3 = prefix;
            //System.out.println(str3);

            //build dictionary back up
            for(int i = 0; i < dat.size(); i++){
                String val = "";
                if(dictionary.containsKey(dat.get(i)))
                    val = dictionary.get(dat.get(i));
                else if(dictionary.size() == dat.get(i))
                    val = prefix + prefix.charAt(0);
                else
                    throw new Exception("bad compression");

                str3 = str3 + val;
                dictionary.put(++dictkey,prefix+val.charAt(0));
                prefix = val;
            }
            char[] s = str3.toCharArray();
            str3 = "";
            for(int o = 0; o < s.length; o++)
                dos.writeByte((byte)s[o]);
            dos.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private List<Integer> decodebitset(BitSet encoded_data) {
        ArrayList<Integer> ress = new ArrayList<>();
        String st = "";
        for(int i = 0; i < uncompressed; i++ ){
            if(st.length() == bytelen){
                ress.add(Integer.parseInt(st,2));
                //System.out.print(Integer.parseInt(st,2) + " ");
                st = "";
            }
            if(encoded_data.get(i)) st = st + "1";
            else if(!encoded_data.get(i)) st = st+"0";
        }

        if(!st.isEmpty()){
            ress.add(Integer.parseInt(st,2));
            //System.out.println(Integer.parseInt(st,2) + " ");
            st = "";
        }
        return ress;
    }

    public void decodebitsettest(BitSet encoded_data) {
        String st = "";
        for(int i = 0; i <uncompressed; i++ ){
            if(st.length() == 12){
                //System.out.print(Integer.parseInt(st,2) + " ");

                st = "";
            }
            if(encoded_data.get(i)) st = st + "1";
            else if(!encoded_data.get(i)) st = st+"0";
        }

        if(!st.isEmpty()){
            //System.out.print(Integer.parseInt(st,2) + " ");
            st = "";
        }
    }

    private void RetDict(){
        dictionary = new TreeMap<>();

        for (int i = 0; i < 256; i++) {
            if(i > 127)dictionary.put(i,"" + (char) (i-256));
            else dictionary.put(i,"" + (char) i);
        }
    }


}
