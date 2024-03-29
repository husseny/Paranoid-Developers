import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.ArrayList;

import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Utility class for encrypting/decrypting files.
 * 
 * @author Michael Lones
 */
public class FileEncryption {

	public static final int AES_Key_Size = 128;

	Cipher pkCipher, aesCipher;
	byte[] aesKey;
	SecretKeySpec aeskeySpec;

	/**
	 * Constructor: creates ciphers
	 */
	public FileEncryption() throws GeneralSecurityException {
		// create RSA public key cipher
		pkCipher = Cipher.getInstance("RSA");
		// create AES shared key cipher
		aesCipher = Cipher.getInstance("AES");
	}

	/**
	 * Creates a new AES key
	 */
	public void makeKey() throws NoSuchAlgorithmException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(AES_Key_Size);
		SecretKey key = kgen.generateKey();
		aesKey = key.getEncoded();
		aeskeySpec = new SecretKeySpec(aesKey, "AES");
	}

	/**
	 * Decrypts an AES key from a file using an RSA private key
	 */
	public void loadKey(File in, File privateKeyFile) throws GeneralSecurityException, IOException {
		// read private key to be used to decrypt the AES key
		byte[] encodedKey = new byte[(int) privateKeyFile.length()];
		new FileInputStream(privateKeyFile).read(encodedKey);

		// create private key
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey pk = kf.generatePrivate(privateKeySpec);

		// read AES key
		pkCipher.init(Cipher.DECRYPT_MODE, pk);
		aesKey = new byte[AES_Key_Size / 8];
		CipherInputStream is = new CipherInputStream(new FileInputStream(in), pkCipher);
		is.read(aesKey);
		aeskeySpec = new SecretKeySpec(aesKey, "AES");
	}

	/**
	 * Encrypts the AES key to a file using an RSA public key
	 */
	public void saveKey(File out, File publicKeyFile) throws IOException, GeneralSecurityException {
		// read public key to be used to encrypt the AES key
		byte[] encodedKey = new byte[(int) publicKeyFile.length()];
		new FileInputStream(publicKeyFile).read(encodedKey);

		// create public key
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pk = kf.generatePublic(publicKeySpec);

		// write AES key
		pkCipher.init(Cipher.ENCRYPT_MODE, pk);
		CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), pkCipher);
		os.write(aesKey);
		os.close();
	}

	/**
	 * Encrypts and then copies the contents of a given file.
	 */
	public void encrypt(File in, File out) throws IOException, InvalidKeyException {
		aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);

		FileInputStream is = new FileInputStream(in);
		CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);

		copy(is, os);

		os.close();
	}

	/**
	 * Decrypts and then copies the contents of a given file.
	 */
	public void decrypt(File in, File out) throws IOException, InvalidKeyException {
		aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

		CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
		FileOutputStream os = new FileOutputStream(out);

		copy(is, os);

		is.close();
		os.close();
	}

	/**
	 * Copies a stream.
	 */
	private void copy(InputStream is, OutputStream os) throws IOException {
		int i;
		byte[] b = new byte[1024];
		while ((i = is.read(b)) != -1) {
			os.write(b, 0, i);
		}
	}


	public static Boolean deleteFromTable(String ColumnName, String Value)
			throws GeneralSecurityException, IOException {
		FileEncryption x = new FileEncryption();
		File decryptedData = new File("decryptedData");
		x.loadKey(new File("encryptAES"), new File("private.der"));
		x.decrypt(new File("output.txt"), decryptedData);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(decryptedData));
		String currentLine;
		currentLine = bufferedReader.readLine();
		String[] array = currentLine.split(",");
		ArrayList<Integer> to_be_deleted = new ArrayList<Integer>();
		int column = 1;
		int columnIndex = -1;
		for (int i = 0; i < array.length; i++) {
			if (ColumnName.equalsIgnoreCase(array[i])) {
				columnIndex = i;
				break;
			}
		}
		if (columnIndex != -1) {
			while ((currentLine = bufferedReader.readLine()) != null) {
				String[] tokens = currentLine.split(",");
				if (tokens[columnIndex].equalsIgnoreCase(Value))
					to_be_deleted.add(column);

				column++;
			}
		}
		//System.out.print(to_be_deleted.toString());

		BufferedReader reader = new BufferedReader(new FileReader(decryptedData));

		int counter = 0;
		String new_data = "";
		while ((currentLine = reader.readLine()) != null)
			if (!to_be_deleted.contains(counter++))
				new_data += currentLine + "\n";

		PrintWriter writer = new PrintWriter(decryptedData);
		writer.print(new_data);
		writer.close();
		reader.close();

		x.saveKey(new File("encryptAES"), new File("public.der"));
		x.encrypt(decryptedData, new File("output.txt"));

		return true;

	}

	public static ArrayList<ArrayList<String>> selectFromTable(String ColumnName, String Value)
			throws GeneralSecurityException, IOException {
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
		FileEncryption x = new FileEncryption();
		File decryptedData = new File("decryptedData");
		x.loadKey(new File("encryptAES"), new File("private.der"));
		x.decrypt(new File("output.txt"), decryptedData);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(decryptedData));

		String currentLine;
		currentLine = bufferedReader.readLine();
		String[] array = currentLine.split(",");
		int columnIndex = -1;
		for (int i = 0; i < array.length; i++) {
			if (ColumnName.equalsIgnoreCase(array[i])) {
				columnIndex = i;
				break;
			}
		}
		if (columnIndex != -1) {
			while ((currentLine = bufferedReader.readLine()) != null) {
				ArrayList<String> row = new ArrayList<String>();
				String[] tokens = currentLine.split(",");
				if (tokens[columnIndex].equalsIgnoreCase(Value)) {
					for (int i = 0; i < tokens.length; i++) {
						row.add(tokens[i]);
					}
					rows.add(row);
				}
			}
		}
		return rows;

	}


	public static void InsertToTable(String new_record) throws GeneralSecurityException, IOException {
		FileEncryption x = new FileEncryption();
		File decryptedData = new File("decryptedData");
		x.loadKey(new File("encryptAES"), new File("private.der"));
		x.decrypt(new File("output.txt"), decryptedData);
		 
		BufferedWriter  out = new BufferedWriter(new FileWriter("decryptedData",true));
		out.write( "\n"+new_record);
		out.close();
		
		
		x.saveKey(new File("encryptAES"), new File("public.der"));
		x.encrypt(decryptedData, new File("output.txt"));
	}

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		FileEncryption x = new FileEncryption();

		// make public key
		File rsapublic = new File("public.der");
		File rsaprivate = new File("private.der");

		File encryptedKey = new File("encryptAES");

		File input = new File("input.txt");
		File output = new File("output.txt");
		File decryptedData = new File("decryptedData");

		// if file doesnt exists, then create it
		if (!rsapublic.exists()) {
			rsapublic.createNewFile();
		}
		if (!rsaprivate.exists()) {
			rsaprivate.createNewFile();
		}
		if (!encryptedKey.exists()) {
			encryptedKey.createNewFile();
		}
		if (!input.exists()) {
			input.createNewFile();
		}
		if (!output.exists()) {
			output.createNewFile();
		}
		if (!decryptedData.exists()) {
			decryptedData.createNewFile();
		}
		x.makeKey();

		// Writer writer = new BufferedWriter(new OutputStreamWriter(new
		// FileOutputStream(decryptedData, true), "UTF-8"));
		// writer.append("sxsx");

		x.saveKey(encryptedKey, rsapublic);
		x.encrypt(input, output);

		x.loadKey(encryptedKey, rsaprivate);
		x.decrypt(output, decryptedData);

		InsertToTable("Alice,23,Female");

		ArrayList<ArrayList<String>> row = selectFromTable("Name", "Mina");
		System.out.println(row);
		row = selectFromTable("Age", "23");
		System.out.println(row);


		deleteFromTable("Gender", "Male");
	}
}
