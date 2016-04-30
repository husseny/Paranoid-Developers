import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class Insert {

	public static void InsertToTable(String new_record) throws GeneralSecurityException, IOException {
		FileEncryption x = new FileEncryption();
		File decryptedData = new File("decryptedData");
		x.loadKey(new File("encryptAES"), new File("private.der"));
		x.decrypt(new File("output.txt"), decryptedData);

		BufferedWriter out = new BufferedWriter(new FileWriter("decryptedData", true));
		out.write("\n" + new_record);
		out.close();

		x.saveKey(new File("encryptAES"), new File("public.der"));
		x.encrypt(decryptedData, new File("output.txt"));
	}
	public static void main(String args[]) throws GeneralSecurityException, IOException{
		Insert.InsertToTable("Mina,23,Female");
		Insert.InsertToTable("Minad,23,Female");
		Insert.InsertToTable("Mina,23,Female");
		Insert.InsertToTable("Minad,23,male");
		Insert.InsertToTable("Mina,23,male");
		}

}
