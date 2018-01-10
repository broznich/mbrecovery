package mbrecovery;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import mbrecovery.Crypto;
/**
 *
 * @author mitt
 */
public class UTests {
    Crypto crypto;
    int wLen = 7;
    public UTests () {
        crypto = new Crypto();
    }
    
    public static void main(String[] args) throws Exception {
        UTests ut = new UTests();
        for (int i = 0;i < 5;i++) {
            ut.getNextTest();
        }
    }   

    /*public void cryptoGetNextTest() throws Exception {
        for (int i = 0;i < 1000000;i++) {
            String first = String.valueOf(Crypto.getNext(i));
            String second = String.valueOf(Crypto.getNext(i));
            if (first.equals(second)) {
                System.out.println("OK: " + first + " " + second);
            } else {
                throw new Exception("Fail " + i);
            }
        }
    }
    
    public void cryptoGetNext2Test() throws Exception {
        long ts = System.currentTimeMillis();
        for (int i = 0;i < 10000000;i++) {
            String first = String.valueOf(Crypto.getNext(i));
        }
        System.out.println("First: " + (System.currentTimeMillis() - ts));
        ts = System.currentTimeMillis();
        for (int i = 0;i < 10000000;i++) {
            String first = String.valueOf(Crypto.getNext2(i));
        }
        System.out.println("Second: " + (System.currentTimeMillis() - ts));
    }*/
    
    public boolean getNextTest() {
        String randVal = getRandValue();
        long maxVal = 171000000;
        
        for (long i = 50;i < maxVal;i++) {
            if (String.valueOf(crypto.getNext2(i)).equals(randVal)) {
                System.out.println("ID: " + i + " PASS: " + randVal);
                return true;
            }
        }
        
        System.out.println("Failed ID: " + maxVal + " PASS: " + randVal);
        return false;
    }
    
    public String getRandValue() {
        
        return "pass007@";
    }
}