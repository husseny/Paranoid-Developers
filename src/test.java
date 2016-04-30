import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

// 
// Decompiled by Procyon v0.5.30
// 

public class test
{
    public static Boolean sameh(final String s, final String s2) throws GeneralSecurityException, IOException {
        final FileEncryption fileEncryption = new FileEncryption();
        final File file = new File("decryptedData");
        fileEncryption.loadKey(new File("encryptAES"), new File("private.der"));
        fileEncryption.decrypt(new File("output.txt"), file);
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        final String[] split = bufferedReader.readLine().split(",");
        final ArrayList<Integer> list = new ArrayList<Integer>();
        int n = 1;
        int n2 = -1;
        for (int i = 0; i < split.length; ++i) {
            if (s.equalsIgnoreCase(split[i])) {
                n2 = i;
                break;
            }
        }
        if (n2 != -1) {
            String line;
            while ((line = bufferedReader.readLine()) != null && line.contains(",")) {
                if (line.split(",")[n2].equalsIgnoreCase(s2)) {
                    list.add(n);
                }
                ++n;
            }
        }
        final BufferedReader bufferedReader2 = new BufferedReader(new FileReader(file));
        int n3 = 0;
        String string = "";
        String line2;
        while ((line2 = bufferedReader2.readLine()) != null) {
            if (!list.contains(n3++)) {
                string = String.valueOf(string) + line2 + "\n";
            }
        }
        final PrintWriter printWriter = new PrintWriter(file);
        printWriter.print(string);
        printWriter.close();
        bufferedReader2.close();
        fileEncryption.saveKey(new File("encryptAES"), new File("public.der"));
        fileEncryption.encrypt(file, new File("output.txt"));
        return true;
    }
    
    public static void main(final String[] array) throws GeneralSecurityException, IOException {
        sameh("Gender", "Male");
    }
}