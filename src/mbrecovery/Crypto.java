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
import org.apache.commons.codec.binary.Base64;
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
    static final int NUMBER_OF_ITERATIONS = 1;
    static final int KEY_LENGTH = 256;
    static final int IV_LENGTH = 128;
    static final int SALT_LENGTH = 8;
    
    public byte[][] parseContent (String fileContent) {
        int saltLen = Crypto.salt.length();
        
        byte[] content = Base64.decodeBase64(fileContent);
        byte[] bytesToDecode = new byte[content.length - saltLen];
        
        System.arraycopy(content, saltLen, bytesToDecode, 0, content.length - saltLen);
        
        byte[] newSalt = new byte[saltLen];
        
        System.arraycopy(bytesToDecode, 0, newSalt, 0, saltLen);
        
        byte[] cipherBytes = new byte[bytesToDecode.length - saltLen];
        System.arraycopy(bytesToDecode, saltLen, cipherBytes, 0, bytesToDecode.length - saltLen);
        
        return new byte[][]{newSalt, cipherBytes};
        //System.out.println(calculate(Long.parseLong(start), Long.parseLong(finish), newSalt, cipherBytes));
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
        // @TODO toCharArray
        if (charSequence == null) {
            return null;
        }

        char[] charArray = new char[charSequence.length()];
        for(int i = 0; i < charSequence.length(); i++) {
            charArray[i] = charSequence.charAt(i);
        }
        return charArray;
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
}
