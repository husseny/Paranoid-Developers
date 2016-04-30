import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class Select {

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
	public static void main(String args[]) throws GeneralSecurityException, IOException{
		ArrayList<ArrayList<String>> row = Select.selectFromTable("Name", "Mina");
		System.out.println(row);
		Delete.deleteFromTable("Name", "Mina");
		}
}
