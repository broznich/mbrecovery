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
public class JHash {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String fileName = args[0],
                start = args[1],
                finish = args[2],
                fileContent = Crypto.readFileAsText(fileName);
        
        
        int saltLen = Crypto.salt.length();
        
        byte[] content = Base64.decodeBase64(fileContent);
        byte[] bytesToDecode = new byte[content.length - saltLen];
        
        System.arraycopy(content, saltLen, bytesToDecode, 0, content.length - saltLen);
        
        byte[] newSalt = new byte[saltLen];
        
        System.arraycopy(bytesToDecode, 0, newSalt, 0, saltLen);
        
        byte[] cipherBytes = new byte[bytesToDecode.length - saltLen];
        System.arraycopy(bytesToDecode, saltLen, cipherBytes, 0, bytesToDecode.length - saltLen);
        
        System.out.println(calculate(Long.parseLong(start), Long.parseLong(finish), newSalt, cipherBytes));    
    }
    
    public static String calculate (long start, long finish, byte[] newSalt, byte[] cipherBytes) {
        boolean found = false;

        String result = "";
        long ts = System.currentTimeMillis();
        long tsr = 0,
                tsr2 = 0,
                tsr3 = 0;
        
        for (long i = start;i < finish;i++) {
            long ts2 = System.currentTimeMillis();
            try {                
                char[] next = Crypto.getNext2(i);
                tsr += System.currentTimeMillis() - ts2;
                found = true;
                byte[] decryptedBytes = Crypto.decrypt(next, newSalt, cipherBytes);
                tsr2 += (System.currentTimeMillis() - ts2);
                
                int dLen = decryptedBytes.length;
                
                while (dLen-- > 0) {
                    if (decryptedBytes[dLen] == 0 || decryptedBytes[dLen] == 10) {
                        continue;
                    }
                    break;
                }
                
                if (decryptedBytes[dLen] != 90) {
                    throw new Exception("next");
                }
                
                StringReader strReader = new StringReader(new String(decryptedBytes));
                Scanner scanner = new Scanner(strReader);
                
                String keyhash = scanner.next();
                String date = scanner.next();
                
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                formatter.parse(date);
                result = keyhash + ";" + date + ";" + String.valueOf(next);
                
            } catch (Exception e) {
                tsr2 += (System.currentTimeMillis() - ts2);
                found = false;
            } finally {
                if (found) {
                    break;
                }
            }
        }
        
        long tss = System.currentTimeMillis() - ts;

        double rate = tss > 0 ? (1000 *(finish - start)) / tss: 0;
        
        System.out.println("RATE: " + rate + " ALL: " + (finish - start) + " TR: " + tss + " TSR: " + tsr + " TSR2: " + tsr2);
        
        return result;
    }
}
