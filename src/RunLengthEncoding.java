import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class RunLengthEncoding {

    char flag = '$';
    byte flagb = (byte)flag;
    public int compressed = 0, uncompressed;
    //General Method
    //scan file and get byte array from data
    private byte[] getFileBinary(String filename) {

        byte[] Filebits=new byte[0];
        try {
            File file = new File(filename);
            Filebits = Files.readAllBytes(file.toPath());
        }catch (IOException e ){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return Filebits;
    }

    ////////////////////////////////////Compression//////////////////////////////////////////////////

    private void RunLengthEncoder(byte[] bin,String Filename){
        try{
            compressed = 0;
            File f = new File(Filename);
            uncompressed = (int) f.length();
            FileOutputStream fio = new FileOutputStream(f.getPath()+".rlc");
            for(int i = 0; i < bin.length; i++){
                Integer runlength = 1;
                while (i < bin.length - 1 && bin[i] == bin[i+1]&& runlength<255){
                    runlength++;
                    i++;
                }
                if(bin[i] == flagb){
                    fio.write(flagb);
                    fio.write(runlength.byteValue());
                    fio.write(bin[i]);
                    compressed+=3;
                }else if (runlength > 1){
                    fio.write(flagb);
                    fio.write(runlength.byteValue());
                    fio.write(bin[i]);
                    compressed += 3;
                }else{
                    fio.write(bin[i]);
                    compressed++;
                }
            }
            fio.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ;
        }
    }

    public void compress(String Fname){
        byte[] bin = getFileBinary(Fname);
        RunLengthEncoder(bin,Fname);
    }


//////////////////////////////////Decompression/////////////////////////////////////////////////

    public void decompress(String Fname){
        byte[] binary = getFileBinary(Fname);
        RunLengthDecoder(binary,Fname);
    }

    private void RunLengthDecoder(byte[] binary, String fname) {
        try{
            File f = new File(fname.substring(0,fname.length()-4));
            FileOutputStream fos = new FileOutputStream(f.getName());
            for(int i = 0; i < binary.length; i++){
                if(binary[i] == flagb){
                    //System.out.println(flagb + " : " +binary[i] + " : " + (binary[i] == flagb));
                    i++;

                    int runlength = (int)binary[i];

                    //System.out.println(runlength);
                    i++;
                    byte cand = binary[i];

                    for (int n = 0; n < runlength;n++){
                        fos.write(cand);
                    }

                }else{
                    fos.write(binary[i]);
                }
            }

            fos.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ;
        }
    }
}
