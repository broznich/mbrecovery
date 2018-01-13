/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import org.junit.*;
import mbrecovery.Words;
/**
 *
 * @author mitt
 */
public class TestWords extends Assert {
    
    @Test
    public void getNextTest () {
        char[] testSymbols = {'1', '2', '3', 'a', 'b', 'c'};
        String[] successResults = {
            "23abc",
            "ac",
            "bcc",
            "3a1bb2",
            "cabcab",
            "cab3bac",
            "1c1c1c11"
        };
        
        for(String res: successResults) {
            boolean status = true;
            Words words = new Words(testSymbols);
            
            while(status) {
                char[] next = words.getNext();
                
                if (res.equals(String.valueOf(next))) {
                    status = false;
                    System.out.println("Found: " + res);
                }
            }
        }
        
        assertTrue(true);
    }
}
