/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbrecovery;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author mitt
 */
public class Manual {
    public static void main(String[] args) throws Exception {
        String fileName = "/home/mitt/one.key",        
            fileContent = Crypto.readFileAsText(fileName);
        
        int saltLen = Crypto.salt.length();
        
        byte[] content = Base64.decodeBase64(fileContent);
        byte[] bytesToDecode = new byte[content.length - saltLen];
        
        System.arraycopy(content, saltLen, bytesToDecode, 0, content.length - saltLen);
        
        byte[] newSalt = new byte[saltLen];
        
        System.arraycopy(bytesToDecode, 0, newSalt, 0, saltLen);
        
        byte[] cipherBytes = new byte[bytesToDecode.length - saltLen];
        System.arraycopy(bytesToDecode, saltLen, cipherBytes, 0, bytesToDecode.length - saltLen);
        
        while (!askPass(newSalt, cipherBytes)) {
            System.out.println("NO");
        }
        
        System.out.println("YES");
    }
    
    public static boolean askPass (byte[] salt, byte[] cipherBytes) {
        System.out.print("Try new password: ");
        Scanner sc = new Scanner(System.in);
        String pass = sc.nextLine();
        System.out.println("Trying pass: " + pass);
        
        boolean found = true;
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        String result = "";
        
        try {
            byte[] decryptedBytes = Crypto.decrypt(pass, salt, cipherBytes);
            String decrypted = new String(decryptedBytes);
            System.out.println(decrypted);
            
            StringReader strReader = new StringReader(decrypted);
            Scanner scanner = new Scanner(strReader);

            String keyhash = scanner.next();
            String date = scanner.next();

            formatter.parse(date);
        } catch (Exception e) {
            found = false;
        }

        return found;
    }
}
