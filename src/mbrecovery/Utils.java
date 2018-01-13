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
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author mitt
 */
public class Utils {
    public String readEncryptedFileAsText (String fileName) throws FileNotFoundException, IOException {
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
    
    public char[] parseSymbols (String symbs) {
        // @TODO make it better.
        char[] set = symbs.toCharArray();
        HashSet<Character> hs = new HashSet<>();
        
        for (char ch: set) {
            hs.add(ch);
        }
        
        char[] result = new char[hs.size()];
        int index = 0;
        
        for (Character ch: hs) {
            result[index++] = ch.charValue();
        }
   
        return result;
    }
}
