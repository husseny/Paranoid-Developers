import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class Delete {

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
		// System.out.print(to_be_deleted.toString());

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
}
