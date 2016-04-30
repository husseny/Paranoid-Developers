import java.io.IOException;
import java.security.GeneralSecurityException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

// 
// Decompiled by Procyon v0.5.30
// 

public class b
{
    public static void sameh(final String s) throws GeneralSecurityException, IOException {
        final FileEncryption fileEncryption = new FileEncryption();
        final File file = new File("decryptedData");
        fileEncryption.loadKey(new File("encryptAES"), new File("private.der"));
        fileEncryption.decrypt(new File("output.txt"), file);
        final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("decryptedData", true));
        bufferedWriter.write("\n" + s);
        bufferedWriter.close();
        fileEncryption.saveKey(new File("encryptAES"), new File("public.der"));
        fileEncryption.encrypt(file, new File("output.txt"));
    }
    
    public static void main(final String[] array) throws GeneralSecurityException, IOException {
        sameh("Mina,23,Female");
        sameh("Minad,23,Female");
        sameh("Mina,23,Female");
        sameh("Minad,23,male");
        sameh("Mina,23,male");
    }
}
