import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.sql.*;
public class sqlConnection {
	Connection conn = null;
	public static Connection dbConnector(){
		try{
			Class.forName("org.sqlite.JDBC");
			Connection  conn = DriverManager.getConnection("jdbc:sqlite:/home/matio/Desktop/GUC/Semester10/Security/milestone2/Paranoid-Developers/paraDev.sqlite");
			return conn;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	private static byte[] convertToBytes(String file) {
        ByteArrayOutputStream bos = null;
        try {
            File f = new File(file);
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1;) {
                bos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e2) {
            System.err.println(e2.getMessage());
        }
        return bos != null ? bos.toByteArray() : null;
    }
	
    public static void uploadCode(int entry,String developer, String filename,String className,boolean encrypt) throws GeneralSecurityException, IOException {
        // update sql
        String updateSQL = "Insert INTO sourceCode (EntryID,Developer,Code,className)"
        		+ " VALUES(?,?,?,?)";
              
        if(encrypt){
        	FileEncryption x = new FileEncryption();
    		File tempFile = new File("Obf_"+filename);
    		x.makeKey();
    		
    		if (!tempFile.exists()) {
    			tempFile.createNewFile();
    		}

    		x.saveKey(new File("encryptAES"), new File("public.der"));
    		x.encrypt(new File(filename), tempFile);
        }
 
        try (Connection conn = sqlConnection.dbConnector();
                PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
 
            // set parameters
            pstmt.setInt(1, entry);
            pstmt.setString(2, developer);
            pstmt.setBytes(3, convertToBytes("Obf_"+filename));
            pstmt.setString(4, className);
            
            pstmt.executeUpdate();
            System.out.println("Stored the file in the BLOB column.");
 
            
            
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * read the picture file and insert into the material master table
     *
     * @param materialId
     * @param filename
     * @throws GeneralSecurityException 
     */
    public static void downloadCode(int materialId, String filename,boolean decrypt) throws GeneralSecurityException {
        // update sql
        String selectSQL = "SELECT Code FROM sourceCode WHERE EntryID=?";
        ResultSet rs = null;
        FileOutputStream fos = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
    	FileEncryption x = new FileEncryption();

 
        try {
            conn =sqlConnection.dbConnector();
            pstmt = conn.prepareStatement(selectSQL);
            pstmt.setInt(1, materialId);
            rs = pstmt.executeQuery();
 
            // write binary stream into file
            File file = new File(filename);
            fos = new FileOutputStream(file);
 
            System.out.println("Writing BLOB to file " + file.getAbsolutePath());
            while (rs.next()) {
                InputStream input = rs.getBinaryStream("Code");
                byte[] buffer = new byte[1024];
                while (input.read(buffer) > 0) {
                    fos.write(buffer);
                }
            }
            fos.flush();
            fos.close();
            rs.close();
            pstmt.close();
            conn.close();
            
            if(decrypt){
            x.makeKey();
            x.loadKey(new File("encryptAES"), new File("private.der"));
    		x.decrypt(file, new File(filename+2));
            }
    		
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
 
                if (conn != null) {
                    conn.close();
                }
                if (fos != null) {
                    fos.close();
                }
 
            } catch (SQLException | IOException e) {
                System.out.println(e.getMessage());
            }
            
        }
        
    }
	
}
