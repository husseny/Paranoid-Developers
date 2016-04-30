import java.io.IOException;
import java.security.GeneralSecurityException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;

// 
// Decompiled by Procyon v0.5.30
// 

public class c
{
    public static ArrayList sameh(final String s, final String s2) throws GeneralSecurityException, IOException {
        final ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        final FileEncryption fileEncryption = new FileEncryption();
        final File file = new File("decryptedData");
        fileEncryption.loadKey(new File("encryptAES"), new File("private.der"));
        fileEncryption.decrypt(new File("output.txt"), file);
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        final String[] split = bufferedReader.readLine().split(",");
        int n = -1;
        for (int i = 0; i < split.length; ++i) {
            if (s.equalsIgnoreCase(split[i])) {
                n = i;
                break;
            }
        }
        if (n != -1) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final ArrayList<String> list2 = new ArrayList<String>();
                final String[] split2 = line.split(",");
                if (split2[n].equalsIgnoreCase(s2)) {
                    for (int j = 0; j < split2.length; ++j) {
                        list2.add(split2[j]);
                    }
                    list.add(list2);
                }
            }
        }
        return list;
    }
    
    public static void main(final String[] array) throws GeneralSecurityException, IOException {
        System.out.println(sameh("Name", "Mina"));
        a.sameh("Name", "Mina");
    }
}
