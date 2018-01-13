/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbrecovery;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author mitt
 */
public class MBRecovery {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        /*String fileName = args[0],
                start = args[1],
                finish = args[2],
                fileContent = Crypto.readFileAsText(fileName);
        */
        boolean success = false;
        Options opts = getCliOptions();
        
        DefaultParser parser = new DefaultParser();
        HelpFormatter help = new HelpFormatter();
        CommandLine cli;
        Utils utils = new Utils();
        
        try {
            cli = parser.parse(opts, args);
            
            String fileContent = utils.readEncryptedFileAsText(cli.getOptionValue("file"));
            char[] symbols = utils.parseSymbols(cli.getOptionValue("symbols"));
            
            success = calculate(symbols, fileContent);
        } catch (ParseException e) {
            help.printHelp("mbrecovery", opts);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } finally {
            if (!success) {
                System.exit(1);
            }
        }
    }
    
    protected static Options getCliOptions() {
        Options opts = new Options();
        
        Option type = new Option("t", "type", true, "Type (single/thread/network-thread)");
        type.setRequired(true);        
        opts.addOption(type);
        
        Option file = new Option("f", "file", true, "Encrypted file");
        file.setRequired(true);
        opts.addOption(file);
        
        Option symbs = new Option("s", "symbols", true, "Symbols without separation");
        symbs.setRequired(true);
        opts.addOption(symbs);
        
        return opts;
    }
    
    protected static boolean calculate (char[] symbols, String fileContent) {
        boolean found = false;        
        Words words = new Words(symbols);        
        Crypto crypto = new Crypto();
        byte[][] parsed = crypto.parseContent(fileContent);
        
        byte[] newSalt = parsed[0];
        byte[] cipherBytes = parsed[1];
        
        int counter = 0;
        long lastTs = System.currentTimeMillis();
        
        while (!found) {
            if (counter == 0) {
                counter = 1000000;
            }
            
            if (--counter == 0) {
                long ts = System.currentTimeMillis();
                
                System.out.println("RATE: " + (1000000 * 1000/(ts - lastTs)) + " H/s");
                lastTs = ts;
            }
            
            try {                
                char[] next = words.getNext();
                found = true;
                byte[] decryptedBytes = Crypto.decrypt(next, newSalt, cipherBytes);
                
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
                
                System.out.println("FOUND! PASSWORD: " + String.valueOf(next));
            } catch (Exception e) {
                found = false;
            }
        }

        return found;
    }
}
