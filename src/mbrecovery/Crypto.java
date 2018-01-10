/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbrecovery;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.bouncycastle.crypto.BufferedBlockCipher;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
/**
 *
 * @author mitt
 */
public class Crypto {
    static String salt = "Salted__";
    static String[] words = {"a", "b", "c", "1", "2", "3"};

    static final int NUMBER_OF_ITERATIONS = 1;
    static final int KEY_LENGTH = 256;
    static final int IV_LENGTH = 128;
    static final int SALT_LENGTH = 8;

    public static String readFileAsText (String fileName) throws FileNotFoundException, IOException {
        BufferedReader file = new BufferedReader(new FileReader(fileName));
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("");
        
        String line;
        
        while ((line = file.readLine()) != null) {
            strBuilder.append(line);
            strBuilder.append(System.getProperty("line.separator"));
        }
        
        return strBuilder.toString();
    }
    
    public static CipherParameters getAESPasswordKey(char[] password, byte[] salt) throws Exception {
        try {
            PBEParametersGenerator generator = new OpenSSLPBEParametersGenerator();
            generator.init(PBEParametersGenerator.PKCS5PasswordToBytes(password), salt, NUMBER_OF_ITERATIONS);

            ParametersWithIV key = (ParametersWithIV) generator.generateDerivedParameters(KEY_LENGTH, IV_LENGTH);

            return key;
        } catch (Exception e) {
            throw new Exception("Could not generate key from password of length");
        }
    }
    
    public static char[] convertToCharArray(String charSequence) {
        if (charSequence == null) {
            return null;
        }

        char[] charArray = new char[charSequence.length()];
        for(int i = 0; i < charSequence.length(); i++) {
            charArray[i] = charSequence.charAt(i);
        }
        return charArray;
    }
    
    /*public static char[] getNext (long index) {
        String converted = Long.toString(index, words.length);
        
        int i = 0,
                cLen = converted.length();
        
        char[] result = new char[cLen];
        
        for(;i < cLen;i++) {
            result[i] = words[Integer.parseInt("" + converted.charAt(i), words.length)];
        }
        
        return result;
    }*/
    
    public static char [] getNext2 (long index) {
        int wLen = words.length;
        String buf = "";
        int pos = 0;
        boolean cont = true;
        
        while (cont) {
            long base = index / wLen;
            int oth = (int)(index % wLen);
            
            index = base;
            
            buf = words[oth] + buf;
            
            if (base <= 0) {
                cont = false;
            }
        }
        
        int iLen = buf.length();
        char[] result = new char[iLen];

        for (int i = 0;i < iLen;i++) {
            result[i] = buf.charAt(i);
        }
        
        return result;
    }
    
    public static byte[] decrypt (char[] password, byte[] buffer, byte[] cipherBytes) throws Exception {
        ParametersWithIV key = (ParametersWithIV) getAESPasswordKey(password, buffer);
                
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
        cipher.init(false, key);

        byte[] decryptedBytes = new byte[cipher.getOutputSize(cipherBytes.length)];
        final int processLength = cipher.processBytes(cipherBytes, 0, cipherBytes.length, decryptedBytes, 0);
        cipher.doFinal(decryptedBytes, processLength);
        
        return decryptedBytes;
    }
    
    public static byte[] decrypt (String password, byte[] buffer, byte[] cipherBytes) throws Exception {
        return decrypt(convertToCharArray(password), buffer, cipherBytes);
    }
    
    public static String[] getWords () {
        return words;
    }
    
}
